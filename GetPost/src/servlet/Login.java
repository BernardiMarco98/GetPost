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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
			response.setContentType("text/html;charset=UTF-8");
			PrintWriter out = response.getWriter();
			
			String username = request.getParameter("username");
			String password = request.getParameter("password");
	        
	        //Chiamo il metodo checkUser della classe Validate per controllare che 
	        //i dati passati come parametro dalla form siano presenti nel database
	        //se corretti,reinderizzo l'URL "Servlet" 
			if(Validate.checkUser(username, password,con))
			{
				HttpSession session = request.getSession();
				
				//metto dentro utente i dati della riga della tabella
				PreparedStatement ps;
				try 
				{
					ps = con.prepareStatement ("select * from utente where username=?");
					ps.setString(1, username);
					System.out.println(ps);
					ResultSet rs = ps.executeQuery();
					rs.next();
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
	        	//se i dati inseriti nella form non sono corretti, viene stampato il messaggio di errore
	        	//e si ritorna nella pagina di login
	           out.println("Username o Password non corretti, reinserire i dati");
	           RequestDispatcher rs = request.getRequestDispatcher("index.jsp");
	           rs.include(request, response);
	           
	        }
	}
	
	
	protected void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}