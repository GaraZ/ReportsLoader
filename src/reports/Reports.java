package reports;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public void initLogFile() throws IOException{
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
    public void saveProfiles(Map<String,Map<String,String>> aProfileMap) throws ParserConfigurationException, 
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
    public void startCon(String aServerName, String aPort, String aDatabase, String aUsername, String aPassword) throws SQLException{
        gCon = DriverManager.getConnection
                ("jdbc:oracle:thin:@//" + aServerName+":" + aPort + "/" + aDatabase, aUsername, aPassword); 
    }
    
    /**
     * Завершить соединение с бд
     */
    public void closeCon(){
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
    public String getRootDir(){
        return gRootDir;
    }
    
    private synchronized void bufferedCopy(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        int byte_;
        while ((byte_ = in.read()) != -1){
            out.write(byte_);
        }
   }
    
 /*   private synchronized void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int b = 0;
        while ((b = in.read(buffer)) != -1) {
            out.write(buffer,0,b);
        }
        out.flush();
    }*/
    
    /**
     * Запрашивает  сервера путь к директорри на сервере в которой находятся отчеты
     * @return Путь к директорри на сервере в которой находятся отчеты
     * @throws SQLException
     */
    public String initRootDir() throws SQLException{
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
    public boolean checkFile(String aPath) throws SQLException{
        int status = 1;
        String QUERY = "select pac_load_reports.checkFile(?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aPath);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                status = rset.getInt(1);
            }
        }
        if(status ==1){
            return true;
        } else{
            return false;
        } 
    }
    
    /**
     * Удаление файла на сервере
     * @param aPath Полный путь к файлу на сервере
     * @throws SQLException
     */
    public void removeFile(String aPath) throws SQLException{
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
    public void renameFile(String aName, String aNewName) throws SQLException{
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
    public ArrayList<File> getReportList() throws SQLException{
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
    
    private InputStream getBlobInputStream(String aFileName) throws SQLException{
        Blob blob = null;
        String QUERY = "select pac_load_reports.getFile(?) from dual";
        try(PreparedStatement pstmt = gCon.prepareStatement(QUERY)) {
            pstmt.setString(1, aFileName);
            ResultSet rset = pstmt.executeQuery();
            while (rset.next()){
                blob = rset.getBlob(1);
                return blob.getBinaryStream();
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
    public File CopyFileFromServer(String aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        File file = new File(aFileNameTo);
        try(BufferedInputStream in = new BufferedInputStream(getBlobInputStream(aFileNameFrom)); 
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file))){
            bufferedCopy(in,out);
        }
        return file;
    }
    
    private Blob getBlob(File aFileNameFrom) throws SQLException, FileNotFoundException, IOException{
        Blob blob = gCon.createBlob();        
        try(BufferedInputStream in = new BufferedInputStream(new FileInputStream(aFileNameFrom));
                BufferedOutputStream out = new BufferedOutputStream(blob.setBinaryStream(0))){
            bufferedCopy(in,out);
        }
        return blob;
    };
    
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
    /**
     * Копирование файла на лок машине в указанное место на сервере
     * @param aFileNameFrom Путь к файлу на локальной машине
     * @param aFileNameTo Путь к файлу на сервере
     * @return
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void putFile(File aFileNameFrom,String aFileNameTo) throws SQLException, FileNotFoundException, IOException{
        Blob blob = getBlob(aFileNameFrom);
        putBlob( blob,aFileNameTo);
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
