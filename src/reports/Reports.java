package reports;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;


/**
 * Реализует возможность работы с файлами на удаленной машине 
 * используя для этого доступ к базе данных Oracle и е JVM 
 * @author Garaz
 */
public class Reports {
    
    private final static Logger LOGGER = Logger.getLogger(Reports.class.getName());
    private String gRootDir = new String();
    private Connection gCon;
    private Config gConfig = new Config();
    
    /**
     * Инициализирует файл лога
     * @throws IOException
     */
    void initLogFile() throws IOException{
        FileHandler handler = 
                new FileHandler("."+System.getProperty("file.separator")+"report.log",100000,1,true);
        LOGGER.addHandler(handler);
    }
    
    /**
     * Сохраняет настройки подключения
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
    void saveProfiles(Map<String,Map<String,String>> aProfileMap) throws ParserConfigurationException, 
            TransformerException, UnsupportedEncodingException, NoSuchAlgorithmException, 
            NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        gConfig.setProfiles(aProfileMap);
    }
    
    /**
     * Загружаем настройки подключения
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
    Map<String,Map<String,String>> loadProfiles() throws ParserConfigurationException,
            SAXException, NoSuchAlgorithmException, NoSuchPaddingException, IOException,
            InvalidKeyException, IllegalBlockSizeException, BadPaddingException{ 
        return gConfig.getProfiles();

     }
    
    /**
     * Установка подключения к бд 
     * @param aServerName - имя сервера
     * @param aPort - номер порта
     * @param aDatabase - имя базы данных
     * @param aUsername - имя пользователя
     * @param aPassword - пароль
     * @throws SQLException
     */
    void startCon(String aServerName, String aPort, String aDatabase, String aUsername, String aPassword) throws SQLException{
        gCon = DriverManager.getConnection
                ("jdbc:oracle:thin:@//" + aServerName+":" + aPort + "/" + aDatabase, aUsername, aPassword); 
    }
    
    /**
     * Завершить соединение с бд
     */
    void closeCon(){
        if(gCon != null){
            try{
                gCon.close();
            }catch(SQLException e){
                LOGGER.log(Level.WARNING,e.getMessage(),e);
            }
        }
    }
    
    /**
     * @return Путь к директорри на сервере в которой находятся отчеты
     */
    String getRootDir(){
        return gRootDir;
    }
    
    private synchronized void bufferedCopy(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        int byte_;
        while ((byte_ = in.read()) != -1){
            out.write(byte_);
        }
   }
    
    /**
     * Запрашивает  сервера путь к директорри на сервере в которой находятся отчеты
     * @return Путь к директорри на сервере в которой находятся отчеты
     * @throws SQLException
     */
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
    
    /**
     * Проверка существования файла на сервере
     * @param aPath Полный путь к файлу на сервере
     * @return true - файл существует; false - файл не существует
     * @throws SQLException
     */
    boolean checkFileRemote(String aPath) throws SQLException{
        String status = null;
        String QUERY = "select pac_load_reports.checkFile(?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aPath);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                status = rset.getString(1);
            }
        }
        if(status ==null){
            return false;
        } else{
            return true;
        } 
    }
    
    /**
     * Удаление файла на сервере
     * @param aPath Полный путь к файлу на сервере
     * @throws SQLException
     */
    void removeFileRemote(String aPath) throws SQLException{
        String QUERY = "begin pac_load_reports.removeFile(?); end;";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aPath);
            pstmt.executeQuery();
        }
    }
    
    /**
     * Переименование файла на сервере
     * @param aName Старый путь к файлу
     * @param aNewName Новый путь к файлу
     * @throws SQLException
     */
    void renameFileRemote(String aName, String aNewName) throws SQLException{
        String QUERY = "begin pac_load_reports.renameFile(?,?); end;";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aName);
            pstmt.setString(2, aNewName);
            pstmt.executeQuery();
        }
    }
    
    /**
     * @return Список файлов на сервере
     * @throws SQLException
     */
    List<String> getFileListRemote() throws SQLException, IOException, ClassNotFoundException{
        List<String> list = new ArrayList<String>();
        String QUERY = "select pac_load_reports.getDirList(?,?) from dual";
        Blob blob = null;
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)){
            pstmt.setString(1, gRootDir);
            pstmt.setBlob(2, gCon.createBlob());
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                blob = rset.getBlob(1);
                break;
            }
        }
        if (blob != null){
            try(ObjectInputStream bos = new ObjectInputStream(blob.getBinaryStream())){
                list = (ArrayList<String>) bos.readObject();
                Collections.sort(list);
            }
        }
        return list;
    }
    
    
    
    private Blob getBlobRemote(String aFileName) throws SQLException{
        Blob blob = gCon.createBlob();
        String QUERY = "select pac_load_reports.getFile(?,?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aFileName);
            pstmt.setBlob(2, blob);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                blob = rset.getBlob(1);
                return blob;
            }
        }
        return null;
    }
    
    /**
     * @param aFileNameFrom Путь к файлу на сервере
     * @param aFileNameTo Путь к файлу на локальной машине
     * @return Файл скопированный с сервера
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    File CopyFileToLocal(String aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        File file = new File(aFileNameTo);
        try(BufferedInputStream in = new BufferedInputStream(getBlobRemote(aFileNameFrom).getBinaryStream()); 
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
            bufferedCopy(in,out);
        }
        return file;
    }

    private Blob getBlobLocal(File aFileNameFrom) throws SQLException, FileNotFoundException, IOException{
        Blob blob = gCon.createBlob();        
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(aFileNameFrom));
                BufferedOutputStream out = new BufferedOutputStream(blob.setBinaryStream(0))){
            bufferedCopy(in,out);
        }
        return blob;
    };

    private void putBlobToRemote(String aFileNameTo, Blob aBlob) throws SQLException{
        final String QUERY = "begin pac_load_reports.putFile(?,?); end;";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aFileNameTo);
            pstmt.setBlob(2, aBlob);
            pstmt.executeQuery();
        }
    }

    /**
     * Копирование файла на лок машине в указанное место на сервере
     * @param aFileNameFrom Путь к файлу на локальной машине
     * @param aFileNameTo Путь к файлу на сервере
     * @return
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    void putFileToRemote(File aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        Blob blob = getBlobLocal(aFileNameFrom);
        putBlobToRemote(aFileNameTo, blob);
    }
    
    public static void main(String[] args) throws IOException{        
        Reports reports = new Reports();
        reports.initLogFile();
        ReportJFrame reportJFrame = new ReportJFrame(reports);
        reportJFrame.pack();
        reportJFrame.setLocationRelativeTo(null);
        reportJFrame.initReportJFrame();
        reportJFrame.setVisible(true);        
    }
}
