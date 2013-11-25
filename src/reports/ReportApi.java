package reports;

/**
  Этот клас должен быть загружен на сервере как:
  create or replace and compile java source named jreport_api as

  Еще необходимо создать пакет pac_load_reports

 */

import java.sql.*;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Класс отрабатывает на сервере Oracle внутри ее JVM
 * Необходим для получения доступа к каталогам сервера на котором установлен Oracle
 * @author GaraZ
 */
public class ReportApi {  

    private static ArrayList<String> getDir(File aDir){
        ArrayList<String> list = new ArrayList<String>();
        if(aDir.isDirectory()){
            String[] s = aDir.list();
            for (String dirName : s){
                File locFile = new File(aDir+System.getProperty("file.separator")+dirName);
                if(locFile.getName().endsWith("fr3")){
                    list.add(locFile.getAbsolutePath());
                }
            }
        }
        return list;
    }
    
    private static synchronized <T extends OutputStream > void bufferedCopy(BufferedInputStream  in, T out) throws IOException {
        int byte_;
        while ((byte_ = in.read()) != -1){
            out.write(byte_);
        }
    }
    
    /**
     * Проверяет существование файла на сервере
     * @param aPath - полный путь к файлу на сервере
     * @return - если файл существует возвращает путь к файлу, иначк null 
     */
    public static String checkFile(String aPath){
        File file = new File(aPath);
        if(file.exists()){
            return aPath;
        } else
            return null;
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
    
    private static byte[] fileToBytes(String aFilePath) throws IOException {
        byte[] bytes = null;
        File file = new File(aFilePath);
        BufferedInputStream in = null;
        ByteArrayOutputStream out = null;
        try{
            in = new BufferedInputStream(new FileInputStream(file));
            out = new ByteArrayOutputStream();
            bufferedCopy(in,out); 
            bytes = out.toByteArray();
            out.flush();
        }finally{
            if(in != null) in.close();           
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
    public static oracle.sql.BLOB getFile(String aPath, oracle.sql.BLOB aBlob) throws SQLException, IOException{
        byte[] bytes = null;
        bytes = fileToBytes(aPath);
        bytesToBlob(bytes, aBlob);
        BufferedOutputStream bos = null;
        OutputStream os = null;
        if(bytes != null){
            os = aBlob.setBinaryStream(0);
            bos = new BufferedOutputStream(os);
            try{
                bos.write(bytes);
                bos.flush();
            }finally{
                if(bos != null) bos.close();
                if(os != null) os.close();
            }
        }
        
        return aBlob;
    }
    
    private static void bytesToBlob(byte[] aBytes, oracle.sql.BLOB aBlob) throws IOException, SQLException{       
        if(aBytes != null){
            BufferedOutputStream bos = null;
            OutputStream os = null;
            os = aBlob.setBinaryStream(0);
            bos = new BufferedOutputStream(os);
            try{
                bos.write(aBytes);
                bos.flush();
            }finally{
                if(bos != null) bos.close();
                if(os != null) os.close();
            }
        }
    }
       
    /**
     * Извлекает файл из сервер
     * @param aPath - полный путь к файлу на сервере
     * @param aBlob - непосредственно сам файл
     * @throws FileNotFoundException
     * @throws SQLException
     * @throws IOException
     */
    public static void putFile(String aPath, oracle.sql.BLOB aBlob) throws FileNotFoundException, SQLException, IOException{
        File file = new File(aPath);
        BufferedInputStream in = null;
        BufferedOutputStream out = null;   
        try{
            in = new BufferedInputStream(aBlob.binaryStreamValue());
            out = new BufferedOutputStream(new FileOutputStream(file));
            bufferedCopy(in, out);
            out.flush();
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
    public static oracle.sql.BLOB getDirList(String dir, oracle.sql.BLOB aBlob) throws IOException, SQLException{            
            if (dir == null) throw new IOException("DIRECTORY_PATH not found");
            ArrayList<String> list = getDir(new File(dir)); 
            ObjectOutputStream out = null;
            try{
                out = new ObjectOutputStream(aBlob.setBinaryStream(0));
                out.writeObject(list);
                out.flush();
            }finally{
                if(out != null) out.close();
            }
            return aBlob;
    }
}