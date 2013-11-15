package reports;

/**
 * Этот клас должен быть загружен на сервере как:
 * create or replace and compile java source named jreport_api as
**/

/**
 * Также нужно создать пакет pac_load_reports
 *
**/

/*
 * CREATE OR REPLACE PACKAGE pac_load_reports AS

  -- CREATE OR REPLACE TYPE TVARCHAR_ARRAY AS TABLE OF VARCHAR2(4000)

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
  -- 1 - файл удален; 2 - файл не удален
  function removeFile(aFileName in varchar2) return number as language java
    name 'ReportApi.removeFile(java.lang.String) return int';
    
  -- переименовать файл на сервере. aFileName - полный путь к старому файла. aFileName - полный путь к новому файлу. 
  -- 1 - файл переименован; 2 - файл не переименован    
  function renameFile(aFileName in varchar2,aNewFileName in varchar2) return number as language java
    name 'ReportApi.renameFile(java.lang.String, java.lang.String) return int';

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
import java.io.OutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class ReportApi {

    static final String DIRECTORY_NAME = "OSS_DIR";

    private static ArrayList<File> getDir(File aDir,String aTab, int aLvl){
        ArrayList<File> list = new ArrayList<File>();
        if (aLvl == 2){
            aLvl--;
            return list;
        } 
        aLvl++;
        if(aDir.isDirectory()){
            String[] s = aDir.list();
            for (String dirName : s){
                File locFile = new File(aDir+System.getProperty("file.separator")+dirName);
                if(locFile.getName().endsWith("fr3")){
                    list.add(locFile);
                }
                if(locFile.isDirectory()){
                    getDir(locFile,"    "+aTab,aLvl);
                }
            }
        }
        aLvl--;
        return list;
    }
    
    private static oracle.sql.ARRAY getElementsAsArray(Connection aCon, ArrayList<File> aList, String aStructureTypeName)
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
    
    public static int checkFile(String aPath){
        File file = new File(aPath);
        if(file.exists()){
            return 1;
        }else{
            return 0;
        } 
    }
    
    public static int removeFile(String aPath){
        File file = new File(aPath);
        int status = 0;
        if(file.delete()){
          status = 1;
        }
        return status;
        
    }
    
    public static int renameFile(String aPath,String aNewPath){
        int status = 0;
        File file = new File(aPath);
        if(file.exists()){
            File newFile = new File(aNewPath);
            if(file.renameTo(newFile)){
                status = 1;
            }
        }        
        return status;
        
    }
    
    public static String getReportDir() throws SQLException{
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
    }
    
    private static byte[] fileToBytes(String aFilePath) throws IOException {
        byte[] bytes = null;
        File file = new File(aFilePath);
        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        for (int readNum; (readNum = fis.read(buf)) != -1;) {
            bos.write(buf, 0, readNum);
        }        
        bytes = bos.toByteArray();
        return bytes;
    }

    public static BLOB getFile(String aPath) throws SQLException, IOException{
        BLOB blob = null;
        Connection conn = DriverManager.getConnection("jdbc:default:connection");
        try{
            blob = BLOB.createTemporary(conn, true, BLOB.DURATION_SESSION);
            byte[] bytes = null;
            bytes = fileToBytes(aPath);
            if(bytes != null){
                OutputStream os = blob.setBinaryStream(0);
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
       
    public static void putFile(String aPath, BLOB aBlob) throws FileNotFoundException, SQLException, IOException{
        File file = new File(aPath);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        
        try{
            in = new BufferedInputStream(aBlob.binaryStreamValue());
            out = new BufferedOutputStream(new FileOutputStream(file));
            int b = 0;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            out.flush();
        }finally{
            if(in != null) in.close();
            if(out != null) out.close();
        }
    }   
     
    public static oracle.sql.ARRAY getReportList() throws SQLException{
        String dir = getReportDir();
        Connection conn = null; 
        ARRAY result = null; 
        try {
            conn = DriverManager.getConnection("jdbc:default:connection");                  
            if (dir == null){
                throw new SQLException("DIRECTORY_PATH not found");
            }else{
                ArrayList<File> list =
                  getDir(new File(dir),new String("  "),0);
                result = getElementsAsArray(conn,list,"TVARCHAR_ARRAY");
                
            }
        }finally {
            if(conn != null) conn.close();
        }
        return result;
    }
}
