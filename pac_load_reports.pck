CREATE OR REPLACE PACKAGE pac_load_reports
  AUTHID CURRENT_USER AS
  
  -- �������� ������ ������ (���������������) �� �������
  function getDirList(aRootDir in varchar2, aBlob in blob) return BLOB as
    LANGUAGE JAVA NAME 'ReportApi.getDirList(java.lang.String, oracle.sql.BLOB) return oracle.sql.Blob';

  -- �������� ���� � ���������� � �������
  function getRootDir return varchar2;

  -- �������� ���� � ������� �� ���������� ����. aPath - ����� ���� � �����
  function getFile(aPath in varchar2, aBlob in BLOB) return BLOB as
    LANGUAGE JAVA NAME 'ReportApi.getFile(java.lang.String, oracle.sql.BLOB) return oracle.sql.Blob';

  -- ��������� ���� �� ������ �� ���������� ����. aFileName - ������ ���� � �����
  procedure putFile(aFileName in varchar2, aBlob in blob) is language java
    name 'ReportApi.putFile(java.lang.String, oracle.sql.BLOB)';

  -- ��������� ������� ����� �� �������. aFileName - ������ ���� � �����
  -- ���� ���� ���������� ���������� ���� � �����, ����� null 
  function checkFile(aFileName in varchar2) return varchar2 as language java
    name 'ReportApi.checkFile(java.lang.String) return java.lang.String';

  -- ������� ���� �� �������. aFileName - ������ ���� � �����
  procedure removeFile(aFileName in varchar2) is language java
    name 'ReportApi.removeFile(java.lang.String)';

  -- ������������� ���� �� �������. aFileName - ������ ���� � ������� �����. aFileName - ������ ���� � ������ �����.
  procedure renameFile(aFileName in varchar2,aNewFileName in varchar2) is language java
    name 'ReportApi.renameFile(java.lang.String, java.lang.String)';

END pac_load_reports;
/
create or replace package body pac_load_reports as

  DIR_NAME varchar2(30) := 'OSS_DIR';
  
  function getDirName return varchar2
  is
  begin
    return DIR_NAME;
  end;
  
  procedure setDirName(dirname varchar2)
  is
  begin
    DIR_NAME := dirname;
  end;

  function getRootDir return varchar2  
  is
    dir varchar2(4000);
    sql_stmt varchar2(200);
    sep varchar(1);
  begin
    sql_stmt := 'select t.directory_path from dba_directories t where t.directory_name = :name and rownum <= 1';
    execute immediate sql_stmt into dir using DIR_NAME;
    
    if(instr(dir,'/',1) > 0) then
      sep := '/';
    else
      sep := '\';
    end if;
    
    if(substr(dir,length(dir)) != sep) then
      dir := dir||sep;
    end if;
    
    return dir;
  end;
  
end pac_load_reports;
/
