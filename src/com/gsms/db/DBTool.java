package com.gsms.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBTool {
	
	public static Connection connect(){  
        //Properties pro = new Properties();  
        String url = null;  
        String username = null;  
        String password = null;  
        String driver = null;  
          
        //InputStream is = DBTool.class.getClassLoader().getResourceAsStream("DB.properties"); //��ȡ�ļ�������  
        try {  
           // pro.load(is);//�����ڲ��ļ�  
          /*  username = pro.getProperty(username);//������ݿ��û���  
            password = pro.getProperty(password);//������ݿ�����  
            driver = pro.getProperty(driver);//��ȡ���ݿ�����  
            url = pro.getProperty(url);  */
        	/*url = "jdbc:mysql://localhost:3306/geodb?useUnicode=true&characterEncoding=utf-8";
        	driver = "com.mysql.jdbc.Driver";
        	username ="root";
        	password = "123456";*/
        	url = "jdbc:oracle:thin:@192.168.0.212:1521:gdcic";
        	driver = "oracle.jdbc.driver.OracleDriver";
        	username = "gsmsserver2";
        	password ="123456";
            Class.forName(driver);  
            Connection conn = DriverManager.getConnection(url, username, password);  
            return conn;  
              
        
        }catch (ClassNotFoundException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }catch (SQLException e) {  
            // TODO Auto-generated catch block  
            e.printStackTrace();  
        }  
        return null;  
          
    }  
      
    /**  
     * �ر����ݿ�  
     *   
     * @param conn  
     *            �������ݿ����Ӷ���  
     */  
    public static void close(Connection con) {  
        if (con != null) {  
            try {  
                con.close();  
            } catch (SQLException e) {  
                e.printStackTrace();  
            }  
        }  
    }  
  
}  

