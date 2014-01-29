package reports;
    
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.*;
import javax.crypto.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
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


/**
 * Класс для работы с конфигурациионным файлом. 
 * @author GaraZ
 */
public class Config {
    static final String FILE_NAME = "config.xml";

    private static class Crypt {
        private static final byte[] KEY_DATA = new byte[]{1,2,3,4,5,6,7,8,1,2,3,4,5,6,7,8}; 
        private static final String ALGORITHM = "AES"; 
        private static final  String ENCODING = "UTF-8";

        private static SecretKey getKey() throws NoSuchAlgorithmException{
            SecretKeySpec keySpec = new SecretKeySpec(KEY_DATA,ALGORITHM);
            return keySpec;
        }

        private static byte[] crypter(byte[] aBytes, int aMode, Key aKey) throws 
                NoSuchAlgorithmException, NoSuchPaddingException, 
                InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(aMode, aKey);
            byte[] bytes = cipher.doFinal(aBytes);
            return bytes;
        }
        
        /**
        * Шифрование
        * @return - строка которую необходимо зашифровать
        * @throws UnsupportedEncodingException
        * @throws NoSuchAlgorithmException
        * @throws NoSuchPaddingException
        * @throws InvalidKeyException
        * @throws IllegalBlockSizeException
        * @throws BadPaddingException
        */
        static String encrypt(String aString) throws UnsupportedEncodingException, 
                NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
                IllegalBlockSizeException, BadPaddingException {
            if(aString == null || aString.trim().isEmpty()) 
                return new String();
            byte[] bytes = aString.getBytes(ENCODING);
            bytes = crypter(bytes, Cipher.ENCRYPT_MODE, getKey());
            return Base64.encode(bytes);
        }
        
        /**
        * Расшифрование
        * @return - строка которую необходимо расшифровать
        * @throws NoSuchAlgorithmException
        * @throws NoSuchPaddingException
        * @throws InvalidKeyException
        * @throws IllegalBlockSizeException
        * @throws BadPaddingException
        * @throws UnsupportedEncodingException
        */
        static String dencrypt(String aString) throws NoSuchAlgorithmException, 
                NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, 
                BadPaddingException, UnsupportedEncodingException {
            if(aString == null || aString.trim().isEmpty()) 
                return new String();
            byte[] bytes = Base64.decode(aString);
            bytes = crypter(bytes, Cipher.DECRYPT_MODE, getKey());
            return new String(bytes,ENCODING);
        }
    }
    
    private DocumentBuilder getDocBuilder() throws ParserConfigurationException {
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
    
    /**
     * Сохраняет настройки подключения в xml файл
     * @param aMap - коллекция с настройками
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    void setProfiles(Map<String,Map<String,String>> aMap) throws 
            ParserConfigurationException, TransformerException, 
            UnsupportedEncodingException, NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, 
            IllegalBlockSizeException, BadPaddingException {
        Document doc = getDocBuilder().newDocument();
        Element rootElement = doc.createElement("root");
        doc.appendChild(rootElement);
        for(Entry<String, Map<String, String>> gEntry :aMap.entrySet()){
            Element prof = doc.createElement(gEntry.getKey());
            for(Entry<String,String> lEntry :gEntry.getValue().entrySet()){
                Element param = doc.createElement(lEntry.getKey());
                if(lEntry.getKey().equals("pass")){
                    param.setAttribute("value", Crypt.encrypt(lEntry.getValue()));
                }else{
                    param.setAttribute("value", lEntry.getValue());
                }
                prof.appendChild(param);
            }
            rootElement.appendChild(prof);
        }
        TransformFactory(doc);
    }

    /**
     * Парсит настройки подключения из xml файла
     * @return aMap - коллекция с настройками
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IOException
     * @throws InvalidKeyException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    Map<String,Map<String,String>> getProfiles() throws ParserConfigurationException, 
            SAXException, NoSuchAlgorithmException, NoSuchPaddingException, IOException, 
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Map<String,Map<String,String>> map = new TreeMap<String,Map<String,String>>();
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
            if(nodeList.item(i) instanceof Element) {
                NodeList lNodeList = nodeList.item(i).getChildNodes();
                int lSize = lNodeList.getLength();
                Map<String,String> lMap = new HashMap<String,String>();
                for(int j = 0; j < lSize; j++) {
                    if (lNodeList.item(j) instanceof Element){
                        if(lNodeList.item(j).getNodeName().equals("pass")){
                            lMap.put(lNodeList.item(j).getNodeName(),
                                Crypt.dencrypt(lNodeList.item(j).getAttributes().getNamedItem("value").getNodeValue())); 
                        } else {
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