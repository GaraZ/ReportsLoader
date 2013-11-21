package reports;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;


/**
 * Релизует интерфейс, а также обрабатывает логику которая 
 * имеет непосредственное отношение к интерфейсу
 * @author GaraZ
 */
public class ReportJFrame extends javax.swing.JFrame {
    private JFileChooser gFileChoose = new JFileChooser();
    private Reports gReports;
    private final static Logger LOGGER = Logger.getLogger(Reports.class.getName());
    
    private Map<String,Map<String,String>> gProfileMap;
    
    private class ProfileSelList implements ListSelectionListener{

        @Override
        public void valueChanged(ListSelectionEvent lse) {
            showParameters(jListProf);
        }
        
    }
    
    private class KeyFieldParam implements KeyListener{

        @Override
        public void keyTyped(KeyEvent ke) {
            
        }

        @Override
        public void keyPressed(KeyEvent ke) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER){
                saveParameters();
            }
        }

        @Override
        public void keyReleased(KeyEvent ke) {
         }

    }
    
    public ReportJFrame(Reports aReports){
        gReports = aReports;
        initComponents();           
        KeyFieldParam keyFieldParam = new KeyFieldParam();
        jTextFieldSrvName.addKeyListener(keyFieldParam);
        jTextFieldPort.addKeyListener(keyFieldParam);
        jTextFieldDB.addKeyListener(keyFieldParam);
        jTextFieldUser.addKeyListener(keyFieldParam);
        jPasswordFieldPass.addKeyListener(keyFieldParam);
        gFileChoose.setFileSelectionMode(JFileChooser.FILES_ONLY);
        gFileChoose.setCurrentDirectory(new File(".")); 
        gFileChoose.setFileFilter(new fr3Filter()); 
        gFileChoose.setAcceptAllFileFilterUsed(false);
        ProfileSelList profileSelList = new ProfileSelList();
        this.jListProf.addListSelectionListener(profileSelList);
    }
    
    void initReportJFrame(){
        try{
            gProfileMap = gReports.loadProfiles();
        }catch(ParserConfigurationException | SAXException | NoSuchAlgorithmException | 
                NoSuchPaddingException | IOException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException e){
            gProfileMap = new TreeMap<String,Map<String,String>>();
            LOGGER.log(Level.WARNING,"Ошибка загрузки настроек",e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка загрузки настроек", 
                    JOptionPane.ERROR_MESSAGE);
        }
        initProfiles();
        selProfile(0);
        showParameters(jListProf);    
    }
    
    private class fr3Filter extends FileFilter{
        
        @Override
        public boolean accept(File file) {
            return file.getName().toLowerCase().endsWith(".fr3")||file.isDirectory();
        }
        
        @Override
        public String getDescription() {
            return "fast report(.fr3)";
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelTo = new javax.swing.JLabel();
        jLabelConPar = new javax.swing.JLabel();
        jTextFieldSrvName = new javax.swing.JTextField();
        jTextFieldPort = new javax.swing.JTextField();
        jTextFieldDB = new javax.swing.JTextField();
        jTextFieldUser = new javax.swing.JTextField();
        jPasswordFieldPass = new javax.swing.JPasswordField();
        jScrollPaneFileTree = new javax.swing.JScrollPane();
        jTreeFiles = new javax.swing.JTree();
        jLabelServName = new javax.swing.JLabel();
        jLabelPort = new javax.swing.JLabel();
        jLabelDB = new javax.swing.JLabel();
        jLabelUser = new javax.swing.JLabel();
        jLabelPass = new javax.swing.JLabel();
        jToggleButtonConnect = new javax.swing.JToggleButton();
        jButtonGetFile = new javax.swing.JButton();
        jButtonDelFile = new javax.swing.JButton();
        jPanelPutFile = new javax.swing.JPanel();
        jLabelFrom = new javax.swing.JLabel();
        jButtonPutFile = new javax.swing.JButton();
        jTextFieldFrom = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListProf = new javax.swing.JList();
        jButtonProfAdd = new javax.swing.JButton();
        jButtonProfDel = new javax.swing.JButton();
        jTextFieldProf = new javax.swing.JTextField();
        jButtonParanSave = new javax.swing.JButton();
        jButtonRenameFile = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabelTo.setText("Список файлов на сервере");

        jLabelConPar.setText("Параметры подключения к БД");

        jTextFieldSrvName.setText("dc-oss02.ug.local");

        jTextFieldPort.setText("2483");

        jTextFieldDB.setText("cable2.ug");

        jTextFieldUser.setText("OSS");

        jPasswordFieldPass.setText("JCC");

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        jTreeFiles.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        jTreeFiles.setToolTipText("");
        jTreeFiles.setRootVisible(false);
        jScrollPaneFileTree.setViewportView(jTreeFiles);

        jLabelServName.setText("Имя сервера");

        jLabelPort.setText("Порт");

        jLabelDB.setText("База данных");

        jLabelUser.setText("Имя пользователя");

        jLabelPass.setText("Пароль");

        jToggleButtonConnect.setText("Подключиться...");
        jToggleButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonConnectActionPerformed(evt);
            }
        });

        jButtonGetFile.setText("Выгрузить");
        jButtonGetFile.setEnabled(false);
        jButtonGetFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGetFileActionPerformed(evt);
            }
        });

        jButtonDelFile.setText("Удалить");
        jButtonDelFile.setEnabled(false);
        jButtonDelFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDelFileActionPerformed(evt);
            }
        });

        jLabelFrom.setText("Выбрать и загрузить файл");

        jButtonPutFile.setText("Загрузить");
        jButtonPutFile.setEnabled(false);
        jButtonPutFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPutFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelPutFileLayout = new javax.swing.GroupLayout(jPanelPutFile);
        jPanelPutFile.setLayout(jPanelPutFileLayout);
        jPanelPutFileLayout.setHorizontalGroup(
            jPanelPutFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPutFileLayout.createSequentialGroup()
                .addComponent(jLabelFrom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(184, 184, 184))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelPutFileLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextFieldFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonPutFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanelPutFileLayout.setVerticalGroup(
            jPanelPutFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelPutFileLayout.createSequentialGroup()
                .addComponent(jLabelFrom)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelPutFileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonPutFile))
                .addGap(0, 6, Short.MAX_VALUE))
        );

        jListProf.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jListProf.setFocusable(false);
        jScrollPane1.setViewportView(jListProf);

        jButtonProfAdd.setText("add");
        jButtonProfAdd.setFocusable(false);
        jButtonProfAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProfAddActionPerformed(evt);
            }
        });

        jButtonProfDel.setText("del");
        jButtonProfDel.setFocusable(false);
        jButtonProfDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonProfDelActionPerformed(evt);
            }
        });

        jTextFieldProf.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTextFieldProfKeyPressed(evt);
            }
        });

        jButtonParanSave.setText("Сохранить");
        jButtonParanSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonParanSaveActionPerformed(evt);
            }
        });

        jButtonRenameFile.setText("Переименовать");
        jButtonRenameFile.setEnabled(false);
        jButtonRenameFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRenameFileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabelConPar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabelTo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPaneFileTree, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButtonGetFile, javax.swing.GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)
                                    .addComponent(jButtonDelFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButtonRenameFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jToggleButtonConnect, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jTextFieldProf)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButtonProfDel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButtonProfAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButtonParanSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jTextFieldSrvName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(jPasswordFieldPass, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldUser, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jTextFieldDB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(jTextFieldPort, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabelServName, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                                    .addComponent(jLabelPort, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelDB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabelPass, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
            .addComponent(jPanelPutFile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabelConPar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldSrvName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelServName)
                    .addComponent(jButtonProfAdd)
                    .addComponent(jTextFieldProf, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelPort)
                            .addComponent(jButtonProfDel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldDB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelDB))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelUser))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabelPass)
                            .addComponent(jPasswordFieldPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonParanSave))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(jToggleButtonConnect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelTo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonGetFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRenameFile)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonDelFile))
                    .addComponent(jScrollPaneFileTree, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jPanelPutFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /** Отображение диалога для выбора файла
    * @return Выбранный файл
    */
    private File chooseFile(){
        File file = null;
        int openRes = gFileChoose.showOpenDialog(this);        
        if (openRes == JFileChooser.APPROVE_OPTION){
            file = gFileChoose.getSelectedFile();
            jTextFieldFrom.setText(file.getAbsolutePath());
        }
        return file;
    }
    
    /**
     * Переименовать выбранный файл
     * @param aTree Дерево с файлами
     */
    private void renameSelFile(javax.swing.JTree aTree){
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) aTree.getLastSelectedPathComponent();
        if (selNode == null) return;
        String name = selNode.getUserObject().toString();
        String newName = (String) JOptionPane.showInputDialog(this,"Введите новое название", "Введите новое название",
                JOptionPane.QUESTION_MESSAGE,null,null, name);
        if(!newName.endsWith(".fr3")){
            newName += ".fr3";
        }
        if(newName != null && !newName.equals(name)){
            try{
                String title;
                String dir = gReports.getRootDir(); 
                gReports.renameFile(dir+name,dir+newName);
                JOptionPane.showMessageDialog(this, "Файл был успешно переименован", "Сообщение", 
                        JOptionPane.INFORMATION_MESSAGE); 
                initFileReportList();
            }catch(SQLException e){
                LOGGER.log(Level.WARNING,"Ошибка переименования файла",e);
                JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка переименования файла", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 
    /**
     * Сохранить выбранный файл
     * @param aTree Дерево с файлами
     */
    private void saveSelFile(javax.swing.JTree aTree){
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) aTree.getLastSelectedPathComponent();
        if (selNode == null) return;
        String dirPath = String.valueOf(aTree.getModel().getRoot());
        String fileName = String.valueOf(selNode.getUserObject());
        try{
            gFileChoose.setSelectedFile(new File(System.getProperty("user.dir")+File.separator+fileName)); 
            int openRes = gFileChoose.showSaveDialog(this);        
            if (openRes == JFileChooser.APPROVE_OPTION){
                String newFile = gFileChoose.getSelectedFile().getAbsolutePath();
                if(!newFile.endsWith(".fr3")){
                    newFile += ".fr3";
                }
                File file = new File(newFile);
                if(file.exists()){
                    int selection = JOptionPane.showConfirmDialog(this, "Файл с таким названием уже существует. Заменить?", "Осторожно", 
                        JOptionPane.OK_CANCEL_OPTION);
                    if(selection == JOptionPane.OK_OPTION){
                        gReports.CopyFileFromServer(dirPath+fileName,newFile).createNewFile();
                    }
                }else{
                     gReports.CopyFileFromServer(dirPath+fileName,newFile).createNewFile();
                }
            }
        }catch(SQLException e){
            LOGGER.log(Level.WARNING,"Ошибка извлечения файла " + dirPath+fileName,e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка извлечения файла " + dirPath+fileName, 
                    JOptionPane.ERROR_MESSAGE);
        }catch(FileNotFoundException e){
            LOGGER.log(Level.WARNING,"Файл не найден",e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Файл не найден", 
                    JOptionPane.ERROR_MESSAGE);
        }catch(IOException e){
            LOGGER.log(Level.WARNING,"Ошибка записи файла " + fileName,e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка записи файла " + fileName, 
                    JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
   
    /**
     * Строит модель дерева файлов в соответствии с переданным списком
     * @param root Корневой елемент дерева
     * @param fList Список файлов
     * @return Модель дерева файлов
     */
    private DefaultMutableTreeNode buildFileTree(DefaultMutableTreeNode root, java.util.List<File> fList) {
        if (!fList.isEmpty()){
            class TreeFile extends File{
                public TreeFile(String path){
                    super(path);
                }
                public String toString(){
                    return super.getName();
                }
            }
            try{     
                DefaultMutableTreeNode treeNodeF;
                for(File file: fList){
                    treeNodeF = new DefaultMutableTreeNode(new TreeFile(file.getPath()));
                    root.add(treeNodeF);
                }
            }catch(Exception e){
                LOGGER.log(Level.WARNING,"Ошибка формирования списка файлов",e);
                root.add(new DefaultMutableTreeNode("Ошибка формирования списка файлов"));
            }
        }
        return root;
    } 
    
    /**
     * Инициализация списка файлов и включение элементов интерфейса 
     * необходимых для редактирования списка
     * @param aTree Дерево файлов
     * @param aRoot Путь к директории файлов 
     * @param aList список файлов
     */
    private void initTreeReports(javax.swing.JTree aTree, String aRoot, List<File> aList){
        jButtonDelFile.setEnabled(true);
        jButtonGetFile.setEnabled(true);
        jButtonPutFile.setEnabled(true);
        jButtonRenameFile.setEnabled(true);
        DefaultMutableTreeNode lRoot = new DefaultMutableTreeNode(aRoot);
        DefaultTreeModel lTreeModel = new DefaultTreeModel(this.buildFileTree(lRoot,aList));
        aTree.setRootVisible(true);
        aTree.setModel(lTreeModel);            
    }
    
    /**
     * Деинициализация списка файлов и выключение элементов интерфейса 
     * необходимых для редактирования списка
     * @param aTree Дерево файлов
     */
    private void deinitTreeReports(javax.swing.JTree aTree){
        jButtonDelFile.setEnabled(false);
        jButtonGetFile.setEnabled(false);
        jButtonPutFile.setEnabled(false);
        jButtonRenameFile.setEnabled(false);
        aTree.setModel(null);
        aTree.setRootVisible(false);
        aTree.removeAll();            
    }

    /**
     * Загрузка выбранного файла на сервер
     */
    private void loadFile(){
        File file = chooseFile();
        if (file == null) return;
        String newName = gReports.getRootDir()+file.getName();
        try{
            if (gReports.checkFile(newName)){
                int selection = JOptionPane.showConfirmDialog(this, "Файл с таким названием уже существует. Заменить?", "Осторожно", 
                        JOptionPane.OK_CANCEL_OPTION);
                if(selection == JOptionPane.OK_OPTION){
                    gReports.putFile(file, newName);
                    initFileReportList(); 
                    JOptionPane.showMessageDialog(this, "Файл был успешно сохранен", "Сообщение", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                gReports.putFile(file, newName);
                initFileReportList(); 
                JOptionPane.showMessageDialog(this, "Файл был успешно сохранен", "Сообщение", 
                        JOptionPane.INFORMATION_MESSAGE);                
            }
        }catch(SQLException e){
            LOGGER.log(Level.WARNING,"Ошибка передачи файла на сервер",e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка передачи файла на сервер", 
                    JOptionPane.ERROR_MESSAGE);
        }catch(IOException e){
            LOGGER.log(Level.WARNING,"Ошибка записи файла" + newName,e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка записи файла " + newName, 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Удаление выбранного файла на сервере
     */
    private void removeFile(javax.swing.JTree aTree){ 
        DefaultMutableTreeNode selNode = 
            (DefaultMutableTreeNode) aTree.getLastSelectedPathComponent();
        if (selNode == null) return;
        String fileName = String.valueOf(selNode.getUserObject());
        try{
            int selection = JOptionPane.showConfirmDialog(this, "Вы уверены что хотите удалить выбранный файл?", "Осторожно", 
                    JOptionPane.OK_CANCEL_OPTION);
            if(selection == JOptionPane.OK_OPTION){
                gReports.removeFile(gReports.getRootDir()+fileName);
                JOptionPane.showMessageDialog(this, "Файл был успешно удален", "Сообщение", 
                        JOptionPane.INFORMATION_MESSAGE); 
                initFileReportList();
            }
        }catch(SQLException e){
            LOGGER.log(Level.WARNING,"Ошибка удаления файла",e);
            JOptionPane.showMessageDialog(this, "Файл удалить не удалось ", "Ошибка удаления файла", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }    
    
    /**
     * Перерисовка списка файлов
     */    
    private void initFileReportList() throws SQLException{
        gReports.initRootDir();
        initTreeReports(jTreeFiles, gReports.getRootDir(), gReports.getReportList());
        jToggleButtonConnect.setText("Отключиться...");
    }
    
    /**
     * Инициализация настроек подключения
     */  
    private void initProfiles(){
        DefaultListModel model = new DefaultListModel();
        for(String key:gProfileMap.keySet()){
            model.addElement(key);
        } 
        jListProf.setModel(model);
    }
    
    private void selProfile(int aIndex){
        if(jListProf.getModel().getSize()>0){
            if(aIndex < 0){
                aIndex = 0;
            }
            if(jListProf.getModel().getSize()> aIndex){
               jListProf.setSelectedIndex(aIndex); 
            }  
        }
    }
    
    private void selProfile(String aName){
        int size = jListProf.getModel().getSize();
        for(int i =0; i< size; i++){
            if(jListProf.getModel().getElementAt(i).equals(aName)){
                jListProf.setSelectedIndex(i);
            }
        }
    }
    
    /**
     * Добавление профиля с настройками
     * @param aJTextField Имя профиля
     */ 
    private void addProfile(JTextField aJTextField){
        if(!aJTextField.getText().trim().equals("")){
            gProfileMap.put(aJTextField.getText(), new HashMap());
            initProfiles();
            selProfile(aJTextField.getText());
        }
    }
    
    /**
     * Удаление выбрынного профиля с настройками
     * @param aJList Список профилей
     */ 
    private void delProfile(JList aJList){
        String prof = aJList.getSelectedValue().toString();
        int index = aJList.getSelectedIndex();
        if (prof == null) return;
        gProfileMap.remove(prof);
        initProfiles();
        selProfile(index-1);
    }
    
    /**
     * Отображение настроек выбранного профиля
     * @param aJList Список профилей
     */ 
    private void showParameters(JList aJList){
        jTextFieldSrvName.setText(null);
        jTextFieldPort.setText(null);
        jTextFieldDB.setText(null);
        jTextFieldUser.setText(null);
        jPasswordFieldPass.setText(null);
        String selProf = String.valueOf(aJList.getSelectedValue());
        if (gProfileMap.containsKey(selProf)){
            for(Entry<String,String> entry:gProfileMap.get(selProf).entrySet()){
                switch (entry.getKey()){ 
                    case "srvname": jTextFieldSrvName.setText(entry.getValue()); break; 
                    case "port": jTextFieldPort.setText(entry.getValue()); break; 
                    case "db": jTextFieldDB.setText(entry.getValue()); break;
                    case "user": jTextFieldUser.setText(entry.getValue()); break;
                    case "pass": jPasswordFieldPass.setText(entry.getValue());
                } 
            }
        }
        
    }
    
    /**
     * Сохранения настроек выбранного профиля
     * @param aJList Список профилей
     */ 
    private void setParameters(JList aJList){
        if(aJList.getModel().getSize() > 0){
            String selProf = String.valueOf(aJList.getSelectedValue());
            Map<String,String> map = new HashMap<String,String>();
            map.put("srvname", jTextFieldSrvName.getText());
            map.put("port", jTextFieldPort.getText());
            map.put("db", jTextFieldDB.getText());
            map.put("user", jTextFieldUser.getText());
            map.put("pass", String.valueOf(jPasswordFieldPass.getPassword()));
            gProfileMap.put(selProf, map);            
        }

    }

    /**
     * Подключение/отключение к серверу
     * @param aJToggleButton Источник события
     */ 
    private void startCon(JToggleButton aJToggleButton){
        if(aJToggleButton.isSelected()){
            try{
                gReports.closeCon();
                gReports.startCon(
                    jTextFieldSrvName.getText(),
                    jTextFieldPort.getText(),
                    jTextFieldDB.getText(),
                    jTextFieldUser.getText(),
                    jPasswordFieldPass.getText()                
                );
                initFileReportList();
            }catch(SQLException e){
                LOGGER.log(Level.INFO, e.getMessage(), e);
                aJToggleButton.setSelected(false);
                JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            gReports.closeCon();
            deinitTreeReports(jTreeFiles);
            aJToggleButton.setText("Подключиться...");
        }
    }
    
    private void saveParameters(){
        setParameters(jListProf);
        try{
            gReports.saveProfiles(gProfileMap);           
        } catch (ParserConfigurationException | TransformerException | UnsupportedEncodingException | 
                NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | 
                IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.log(Level.WARNING,"Ошибка сохранения настроек",e);
            JOptionPane.showMessageDialog(this, e.getMessage(), "Ошибка сохранения настроек", 
                    JOptionPane.ERROR_MESSAGE);
        }
    } 
    
    private void jButtonPutFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPutFileActionPerformed
        gFileChoose.setDialogTitle("Выберите файл отчета для отправки на сервер");
        loadFile();
    }//GEN-LAST:event_jButtonPutFileActionPerformed

    private void jToggleButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonConnectActionPerformed
        startCon(jToggleButtonConnect);
    }//GEN-LAST:event_jToggleButtonConnectActionPerformed

    private void jButtonGetFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGetFileActionPerformed
        gFileChoose.setDialogTitle("Укажите файл для сохранения");
        saveSelFile(jTreeFiles);
    }//GEN-LAST:event_jButtonGetFileActionPerformed

    private void jButtonDelFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDelFileActionPerformed
        removeFile(jTreeFiles);
    }//GEN-LAST:event_jButtonDelFileActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        gReports.closeCon();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void jButtonParanSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonParanSaveActionPerformed
        saveParameters();
    }//GEN-LAST:event_jButtonParanSaveActionPerformed

    private void jButtonProfAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProfAddActionPerformed
            addProfile(jTextFieldProf);
            jTextFieldProf.setText(null);
    }//GEN-LAST:event_jButtonProfAddActionPerformed

    private void jButtonProfDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonProfDelActionPerformed
        delProfile(jListProf);
    }//GEN-LAST:event_jButtonProfDelActionPerformed

    private void jTextFieldProfKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldProfKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER){
            addProfile(jTextFieldProf);
            jTextFieldProf.setText(null);
        }
    }//GEN-LAST:event_jTextFieldProfKeyPressed

    private void jButtonRenameFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRenameFileActionPerformed
        renameSelFile(jTreeFiles);
    }//GEN-LAST:event_jButtonRenameFileActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonDelFile;
    private javax.swing.JButton jButtonGetFile;
    private javax.swing.JButton jButtonParanSave;
    private javax.swing.JButton jButtonProfAdd;
    private javax.swing.JButton jButtonProfDel;
    private javax.swing.JButton jButtonPutFile;
    private javax.swing.JButton jButtonRenameFile;
    private javax.swing.JLabel jLabelConPar;
    private javax.swing.JLabel jLabelDB;
    private javax.swing.JLabel jLabelFrom;
    private javax.swing.JLabel jLabelPass;
    private javax.swing.JLabel jLabelPort;
    private javax.swing.JLabel jLabelServName;
    private javax.swing.JLabel jLabelTo;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JList jListProf;
    private javax.swing.JPanel jPanelPutFile;
    private javax.swing.JPasswordField jPasswordFieldPass;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneFileTree;
    private javax.swing.JTextField jTextFieldDB;
    private javax.swing.JTextField jTextFieldFrom;
    private javax.swing.JTextField jTextFieldPort;
    private javax.swing.JTextField jTextFieldProf;
    private javax.swing.JTextField jTextFieldSrvName;
    private javax.swing.JTextField jTextFieldUser;
    private javax.swing.JToggleButton jToggleButtonConnect;
    private javax.swing.JTree jTreeFiles;
    // End of variables declaration//GEN-END:variables
}
