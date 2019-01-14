package servlet;

import java.sql.*;

public class Validate
 {
     public static boolean checkUser(String username,String password) 
     {
      boolean st =false;
      try
      {
    	 Class.forName("org.postgresql.Driver");
         Connection con=DriverManager.getConnection("jdbc:postgresql:/ /localhost:5432/getpost","postgres","postgre");
         PreparedStatement ps =con.prepareStatement("select * from utente where username=? and password=?");
         ps.setString(1, username);
         ps.setString(2, password);
         ResultSet rs =ps.executeQuery();
         st = rs.next();
      }
      catch(Exception e)
      {
          e.printStackTrace();
      }
         return st;                 
  }   
}