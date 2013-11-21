package reports;

/**
  Этот клас должен быть загружен на сервере как:
  create or replace and compile java source named jreport_api as

  Еще необходимо создать тип TVARCHAR_ARRAY и пакет pac_load_reports

  CREATE OR REPLACE TYPE TVARCHAR_ARRAY AS TABLE OF VARCHAR2(4000)

 CREATE OR REPLACE PACKAGE pac_load_reports AS

  -- получить список файлов на сервере
  function getDirList return TVARCHAR_ARRAY as
    LANGUAGE JAVA NAME 'ReportApi.getReportList() return oracle.sql.ARRAY';

  -- получить путь к директории с файлами
  function getRootDir return varchar2 as
    LANGUAGE JAVA NAME 'ReportApi.getReportDir() return String';

  -- получить файл с сервера по указанному пути. aPath - поный путь к файлу
  function getFile(aPath in varchar2) return BLOB as
    LANGUAGE JAVA NAME 'ReportApi.getFile(java.lang.String) return oracle.sql.Blob';

  -- звгрузить файл на сервер по указанному пути. aFileName - полный путь к файлу
  procedure putFile(aFileName in varchar2, aBlob in blob) is language java
    name 'ReportApi.putFile(java.lang.String,oracle.sql.BLOB)';

  -- проверить наличие фйла на сервере. aFileName - полный путь к файлу
  -- 1 - файл существует; 2 - файл не существует
  function checkFile(aFileName in varchar2) return number as language java
    name 'ReportApi.checkFile(java.lang.String) return int';

  -- удалить файл на сервере. aFileName - полный путь к файлу
  procedure removeFile(aFileName in varchar2) is language java
    name 'ReportApi.removeFile(java.lang.String)';

  -- переименовать файл на сервере. aFileName - полный путь к старому файла. aFileName - полный путь к новому файлу.
  procedure renameFile(aFileName in varchar2,aNewFileName in varchar2) is language java
    name 'ReportApi.renameFile(java.lang.String, java.lang.String)';

END pac_load_reports;
 */

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BLOB;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.util.List;

/**
 * Класс отрабатывает на сервере Oracle внутри ее JVM
 * Необходим для получения доступа к каталогам сервера на котором установлен Oracle
 * @author GaraZ
 */
public class ReportApi {

    static final String DIRECTORY_NAME = "OSS_DIR";
    

    private static List<File> getDir(File aDir){
        List<File> list = new ArrayList<File>();
        if(aDir.isDirectory()){
            String[] s = aDir.list();
            for (String dirName : s){
                File locFile = new File(aDir+System.getProperty("file.separator")+dirName);
                if(locFile.getName().endsWith("fr3")){
                    list.add(locFile);
                }
            }
        }
        return list;
    }
    
   private static synchronized void bufferedCopy(BufferedInputStream in, BufferedOutputStream out) throws IOException {
        int byte_;
        while ((byte_ = in.read()) != -1){
            out.write(byte_);
        }
   }
    
    private static oracle.sql.ARRAY getElementsAsArray(Connection aCon, List<File> aList, String aStructureTypeName)
            throws SQLException {
        ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor(aStructureTypeName, aCon);
        String[] list = new String[aList.size()];
        {    
             int i = 0;
             for(File file:aList){
                  String s = file.getAbsolutePath();
                  list[i++] = s;
             }
        }
        ARRAY result = new ARRAY(descriptor, aCon, list);  
        return result;
    }
    
    /**
     * Проверяет существование файла на сервере
     * @param aPath - полный путь к файлу на сервере
     * @return - 1 - файл сущетвует; 0 - файл - не существует // с boolean возникли сложности 
     */
    public static boolean checkFile(String aPath){
        File file = new File(aPath);
        return file.exists();
    }
    
    /**
     *Удаление файла на сервере
     * @param aPath - полный путь к файлу на сервере
     * @throws IOException
     */
    public static void removeFile(String aPath) throws IOException{
        File file = new File(aPath);
         if(!file.delete()){
          throw new  IOException("File not deleting");
        }        
    }
    
    /**
     * Переименование файла на сервере
     * @param aPath - полный путь к файлу на сервере
     * @param aNewPath - полный путь к файлу с новым названием на сервере
     * @throws IOException
     */
    public static void renameFile(String aPath,String aNewPath) throws IOException{
        File file = new File(aPath);
        if(file.exists()){
            File newFile = new File(aNewPath);
            if(file.renameTo(newFile)){
                return;
            }
        }        
        throw new IOException("File not renamed");   
    }
    
    /**
     * Получаем путь к директории в которой хранятся отчеты
     * @return - полный путь к дирекории
     * @throws SQLException
     */
/*    public static String getReportDir() throws SQLException{
        String gDir = null;
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        String query = "select t.DIRECTORY_PATH from dba_directories t where t.DIRECTORY_NAME = ? and rownum <= 1";
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, DIRECTORY_NAME);
            try{
                ResultSet rset = pstmt.executeQuery();
                while (rset.next()){
                    gDir = rset.getString(1)+File.separator;
                }
            }finally{
                pstmt.close();
            }
        }finally {
            conn.close();
        }
        return gDir;
    }*/
    
    private static byte[] fileToBytes(String aFilePath) throws IOException {
        byte[] bytes = null;
        File file = new File(aFilePath);
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        BufferedOutputStream bout = null;
        try{
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream();
            bout = new BufferedOutputStream(out);
            bufferedCopy(in,bout);    
            bytes = out.toByteArray();
        }finally{
            if(in != null) in.close();
            if(bout != null) bout.close();            
            if(out != null) out.close();
        }
        return bytes;
    }

    /**
     * Возвращает файл на сервере
     * @param aPath - полный путь к файлу на сервере
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public static BLOB getFile(String aPath) throws SQLException, IOException{
        BLOB blob = null;
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        try{
            blob = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
            byte[] bytes = null;
            bytes = fileToBytes(aPath);
            if(bytes != null){
                BufferedOutputStream os = new BufferedOutputStream(blob.setBinaryStream(0));
                try{
                    os.write(bytes);
                }finally{
                    os.close();
                }
            }
        }finally{
            conn.close();
        }
        return blob;
    }
       
    /**
     * Помещает файл на сервер
     * @param aPath - полный путь к файлу на сервере
     * @param aBlob - непосредственно сам файл
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static void putFile(String aPath, BLOB aBlob) throws FileNotFoundException, SQLException, IOException{
        File file = new File(aPath);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;   
        try{
            in = new BufferedInputStream(aBlob.binaryStreamValue());
            out = new BufferedOutputStream(new FileOutputStream(file));
            bufferedCopy(in, out);
        }finally{
            if(in != null) in.close();
            if(out != null) out.close();
        }
    }   
     
    /**
     * Возвращает список путей к файлам
     * @return - список путей к файлам 
     * @throws SQLException
     */
    public static oracle.sql.ARRAY getReportList(String dir) throws SQLException{
        ARRAY result = null; 
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        try {                
            if (dir == null){
                throw new SQLException("DIRECTORY_PATH not found");
            }else{
                List<File> list = getDir(new File(dir));
                result = getElementsAsArray(conn,list,"TVARCHAR_ARRAY");
                
            }
        }finally {
            conn.close();
        }
        return result;
    }
}
