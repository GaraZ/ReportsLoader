/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reports;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;


public class Reports {
    
    private final static Logger LOGGER = Logger.getLogger(Reports.class.getName());
    private String gRootDir = new String();
    private Connection gCon;
    private Config gConfig = new Config();
    
    public Reports(){
        try{
            FileHandler handler = 
                new FileHandler("."+System.getProperty("file.separator")+"report.log",100000,1,true);
            LOGGER.addHandler(handler);
        } catch(IOException e){
            //e.printStackTrace();
        }    
    }
    
    // сохраняем настройки
    void saveProfiles(TreeMap<String,HashMap<String,String>> aProfileMap) throws ParserConfigurationException, TransformerException,
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
            gConfig.setProfiles(aProfileMap);
    }
    
    // загружаем настройки
    TreeMap<String,HashMap<String,String>> getProfiles(){ 
         try{
             return gConfig.getProfiles();
         } catch (SAXException e) {
             LOGGER.log(Level.INFO, e.getMessage(), e);
         } catch (ParserConfigurationException e) {
             LOGGER.log(Level.INFO, e.getMessage(), e);
         } catch (IOException e) {
             LOGGER.log(Level.INFO, e.getMessage(), e);
         }
         return new TreeMap<String,HashMap<String,String>>();
     }
    
    // подключится к БД
    void startCon(String aServerName, String aPort, String aDatabase, String aUsername, String aPassword) throws SQLException{
        gCon = DriverManager.getConnection
                ("jdbc:oracle:thin:@//" + aServerName+":" + aPort + "/" + aDatabase, aUsername, aPassword); 
    }
    
    // отключится от БД
    void closeCon(){
        if(gCon != null){
            try{
                gCon.close();
            }catch(SQLException e){
                LOGGER.log(Level.WARNING,e.getMessage(),e);
            }
        }
    }
    
    // каталог файлов на сервере
    String getRootDir(){
        return gRootDir;
    }
    
    // Копирование потоков.
    //in Входящий поток
    // out Выходящий поток
    private synchronized void copyFile(InputStream in, OutputStream out) throws IOException {
        int b = 0;
        while ((b = in.read()) != -1) {
            out.write(b);
        }
        out.flush();
    }
    
    // получить путь к файлам на сервере
    String initRootDir() throws SQLException{
        gRootDir = new String();
        try(Statement stmt = gCon.createStatement()) {
            ResultSet rset = stmt.executeQuery("select pac_load_reports.getRootDir from dual");
            while (rset.next()){
                gRootDir = rset.getString(1);
            }
        }
        return gRootDir;
    }
    
    // проверить существования файла на сервере
    // 1 - файл существует
    // 0 - файл не существует
    int checkFile(String aPath) throws SQLException{
        int status = 1;
        String QUERY = "select pac_load_reports.checkFile(?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aPath);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                status = rset.getInt(1);
            }
        }
        return status;   
    }
    
    // удаление файла на сервере
    // 1 - файл удален
    // 0 - файл не удален
    int removeFile(String aPath) throws SQLException{
        int status = 0;
        String QUERY = "select pac_load_reports.removeFile(?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aPath);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                status = rset.getInt(1);
            }
        }
        return status;
    }
    
    // переименование файла на сервере
    // 1 - файл переименован
    // 0 - файл не переименован
    int renameFile(String aName, String aNewName) throws SQLException{
        int status = 0;
        String QUERY = "select pac_load_reports.renameFile(?,?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aName);
            pstmt.setString(2, aNewName);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                status = rset.getInt(1);
            }
        }
        return status;
    }
    
    // получить список файлов
    ArrayList<File> getReportList() throws SQLException{
        ArrayList<File> list = new ArrayList<File>();
        try(Statement stmt = gCon.createStatement()) {
            ResultSet rset = stmt.executeQuery("select pac_load_reports.getRootDir from dual");
            while (rset.next()){
                gRootDir = rset.getString(1);
            }
            rset = stmt.executeQuery("select * from table(pac_load_reports.getDirList) order by 1 asc");
            while (rset.next()){
                list.add(new File(rset.getString(1)));
            }
        }
        return list;
    }
    
    // получить поток для чтения указанного файла на сервере
    private InputStream getBlobInputStream(String aFileName) throws SQLException{
        Blob blob = null;
        try(Statement stmt = gCon.createStatement()) {
            ResultSet rset = stmt.executeQuery("select pac_load_reports.getRootDir from dual");
            while (rset.next()){
                gRootDir = rset.getString(1);
            }
            rset = stmt.executeQuery("select pac_load_reports.getFile('"+aFileName+"') from dual");
            while (rset.next()){
                blob = rset.getBlob(1);
                return blob.getBinaryStream();
            }
        }
        return null;
    }
    
    // копировать файл на сервере в указанное место на лок мащине
    // aFileNameFrom - путь к файлу на сервере
    // aFileNameTo - путь к файлу на локальной машине
    File getFile(String aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        File file = null;
        InputStream in = null;
        OutputStream out = null;
        try{
            in = getBlobInputStream(aFileNameFrom);
            out = new FileOutputStream(aFileNameTo);
            copyFile(in,out);
        }finally{
            if(in != null) in.close();
            if(out != null) out.close();
        }
        return file;
    }
    
    // конвертируем файл в blob для передачи
    private Blob getBlob(File aFileNameFrom) throws SQLException, FileNotFoundException, IOException{
        Blob blob = gCon.createBlob();
        InputStream in = null;
        OutputStream out = null;
        try{
            in = new FileInputStream(aFileNameFrom);
            out = blob.setBinaryStream(0);
            copyFile(in,out);
        }finally{
            if(in != null) in.close();
            if(out != null) out.close();
        }
        return blob;
    };
    
    // передаем blob для для сохраненияя на сервере
    // aFileNameTo путь к файлу на сервере
    private void putBlob(Blob aBlob, String aFileNameTo) throws SQLException{
        final String QUERY = "begin pac_load_reports.putFile(?,?); end;";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aFileNameTo);
            pstmt.setBlob(2, aBlob);
            pstmt.executeQuery();
        }
    }
    
    // копировать файл на лок машине в указанное место на сервере
    // aFileNameFrom - путь к файлу на лок машине
    // aFileNameTo - путь к файлу на сервере
    File putFile(File aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        File file = null;
        Blob blob = getBlob(aFileNameFrom);
        putBlob( blob,aFileNameTo);
        return file;
    }
    
    public static void main(String[] args) throws SQLException, IOException{        
        Reports reports = new Reports();
        ReportJFrame reportJFrame = new ReportJFrame(reports);
        reportJFrame.pack();
        reportJFrame.setLocationRelativeTo(null);
        reportJFrame.initReportJFrame();
        reportJFrame.setVisible(true);        
    }
}
