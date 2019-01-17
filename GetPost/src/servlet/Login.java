package servlet;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name="Login",urlPatterns={"/Login"})
public class Login extends HttpServlet {
 
	public Connection con;
	
	public void init(ServletConfig config)throws ServletException
	{
		super.init(config);
		//carico il driver ed eseguo la connessione al db
		try 
		{
			Class.forName("org.postgresql.Driver");
			con=DriverManager.getConnection ("jdbc:postgresql://localhost:5432/getpost","postgres","postgre");
			System.out.println("Connessione in corso...");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 //Metodo che esegue una query con i parametri passati, 
	 //se presenti nel database ritorna true, altrimenti false

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			String username = request.getParameter("username");
			String password = request.getParameter("password");
			
			ResultSet rs = null;
			//qui ho unificato le due query precedenti in un' unica
			boolean st =false;
		    try
		    {
		   	 //faccio una query per vedere se i dati inseriti sono corretti
		        PreparedStatement ps =con.prepareStatement ("select * from utente where username=? and password=?");
		        ps.setString(1, username);
		        ps.setString(2, password);
		        System.out.println("executing checkUser with usr ("+username+") pwd ("+password+")");
		        rs =ps.executeQuery();
		        st = rs.next();
		       
		     }
		     catch(Exception e)
		     {
		         e.printStackTrace();
		     }
	        
	        //Chiamo il metodo checkUser della classe Validate per controllare che 
	        //i dati passati come parametro dalla form siano presenti nel database
	        //se corretti,reinderizzo l'URL "Servlet" 
			if(st)
			{
				HttpSession session = request.getSession();
				
				//metto dentro utente i dati della riga della tabella
				
				try 
				{
					
					Utente utente = new Utente();
					utente.setUsername(rs.getString("username"));
					utente.setPassword(rs.getString("password"));
					utente.setNome(rs.getString("nome"));
					utente.setCognome(rs.getString("cognome"));
					utente.setId_utente(rs.getInt("id_utente"));
					session.setAttribute("utente", utente);
				} 
				catch (SQLException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			response.sendRedirect("Servlet");	
			}
	        else
	        {
	        	out.println("Username o Password non corretti, reinserire i dati");
	        	RequestDispatcher reDi = request.getRequestDispatcher("index.jsp");
	        	reDi.include(request, response);
	        }
	}
	
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}