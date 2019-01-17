package servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Validate
 {
	 //Metodo che esegue una query con i parametri passati, 
	 //se presenti nel database ritorna true, altrimenti false
     public static boolean checkUser(String username,String password, Connection con) 
     {

      boolean st =false;
      try
      {
    	 //faccio una query per vedere se i dati inseriti sono corretti
         PreparedStatement ps =con.prepareStatement ("select username,password from utente where username=? and password=?");
         ps.setString(1, username);
         ps.setString(2, password);
         System.out.println("executing checkUser with usr ("+username+") pwd ("+password+")");
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