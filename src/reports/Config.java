package reports;
    
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class Config {
    static final String FILE_NAME = "config.xml";
    private final static Logger LOGGER = Logger.getLogger(Reports.class.getName());

    private static class Crypt {
        private static final byte[] KEY_DATA = new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8}; 
        private static final String ALGORITHM = "AES"; 
        private static final  String ENCODING = "UTF-8";

        private static SecretKey getKey() throws NoSuchAlgorithmException{
            SecretKeySpec keySpec = new SecretKeySpec(KEY_DATA,ALGORITHM);
            return keySpec;
        }

        private static void crypt(InputStream in, OutputStream out, Cipher cipher) throws IOException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
            int blockSize = cipher.getBlockSize();
            int outputSize = cipher.getOutputSize(blockSize);
            byte[] inBytes = new byte[blockSize];
            byte[] outBytes = new byte[outputSize];
            int inLength = 0;
            boolean more = true;
            while(more){
                inLength = in.read(inBytes);
                if(inLength == blockSize){
                    int outLength =cipher.update(inBytes, 0,blockSize,outBytes);
                    out.write(outBytes,0,outLength);
                }
                else more = false;
            }
            if(inLength > 0){          
                outBytes =cipher.doFinal(inBytes,0,inLength);
            }
            else{
                outBytes =cipher.doFinal();
            }
            out.write(outBytes);
        }

        private static byte[] crypter(byte[] aBytes, int aMode, Key aKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, ShortBufferException, IllegalBlockSizeException, BadPaddingException{
            ByteArrayOutputStream out;
            byte[] bytes;
            try (InputStream in = new ByteArrayInputStream(aBytes)) {
                out = new ByteArrayOutputStream();
                Cipher cipher = Cipher.getInstance(ALGORITHM);
                cipher.init(aMode, aKey);
                crypt(in,out,cipher);
                bytes = out.toByteArray();
            }
            out.close();
            return bytes;
        }
        
        // шифрование строки aString
        static String encrypt(String aString){
            String result = new String();
            if(aString == null || aString.trim().isEmpty()) 
                return result;
            try {
                byte[] bytes = aString.getBytes(ENCODING);
                bytes = crypter(bytes, Cipher.ENCRYPT_MODE, getKey());
                return Base64.encode(bytes);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                    InvalidKeyException | IOException | ShortBufferException |
                    IllegalBlockSizeException | BadPaddingException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
            return result;
        }
        
        // дешифровка строки aString
        static String dencrypt(String aString){
            String result = new String();
            if(aString == null || aString.trim().isEmpty()) 
                return result;
            try {
                byte[] bytes = Base64.decode(aString);
                if (bytes != null){
                    bytes = crypter(bytes, Cipher.DECRYPT_MODE, getKey());
                    return new String(bytes,ENCODING);
                }
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                    InvalidKeyException | IOException | ShortBufferException |
                    IllegalBlockSizeException | BadPaddingException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return result;
        }
    }
    
    private DocumentBuilder getDocBuilder() throws ParserConfigurationException{
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder;
    }
    
    private void TransformFactory(Document doc) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(FILE_NAME));
        transformer.transform(source, result);
    }
    
    // сохраняем настройки подключения
    void setProfiles(TreeMap<String,HashMap<String,String>> aMap) throws ParserConfigurationException, TransformerException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Document doc = getDocBuilder().newDocument();
        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);
        for(Entry<String, HashMap<String, String>> gEntry :aMap.entrySet()){
            Element prof = doc.createElement(String.valueOf(gEntry.getKey()));
            for(Entry<String,String> lEntry :gEntry.getValue().entrySet()){
                Element param = doc.createElement(String.valueOf(lEntry.getKey()));
                if(lEntry.getKey().equals("pass")){
                    param.setAttribute("value", Crypt.encrypt(lEntry.getValue()));
                }else{
                    param.setAttribute("value", String.valueOf(lEntry.getValue()));
                }
                prof.appendChild(param);
            }
            rootElement.appendChild(prof);
        }
        TransformFactory(doc);
    }

    // получаем настройки подключения
    TreeMap<String,HashMap<String,String>> getProfiles() throws SAXException, ParserConfigurationException, IOException{
        TreeMap<String,HashMap<String,String>> map = new TreeMap<String,HashMap<String,String>>();
        Document doc;
        doc = null;
        File file = new File(FILE_NAME);
        if(file.exists()){
            doc = getDocBuilder().parse(FILE_NAME);
        }
        if(doc == null) return map;
        NodeList nodeList =  doc.getFirstChild().getChildNodes();
        int size = nodeList.getLength();
        for(int i = 0; i < size; i++){
            if(nodeList.item(i) instanceof Element){
                NodeList lNodeList = nodeList.item(i).getChildNodes();
                int lSize = lNodeList.getLength();
                HashMap<String,String> lMap = new HashMap<String,String>();
                for(int j = 0; j < lSize; j++){
                    if(lNodeList.item(j) instanceof Element){
                        if(lNodeList.item(j).getNodeName().equals("pass")){
                            lMap.put(lNodeList.item(j).getNodeName(),
                                Crypt.dencrypt(lNodeList.item(j).getAttributes().getNamedItem("value").getNodeValue())); 
                        }else{
                            lMap.put(lNodeList.item(j).getNodeName(),
                                lNodeList.item(j).getAttributes().getNamedItem("value").getNodeValue()); 
                        }
                    }
                }
                map.put(nodeList.item(i).getNodeName(), lMap);
            } 
        }
        return map;
    }
    
}