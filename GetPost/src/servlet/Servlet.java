package servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

    	
		RequestDispatcher dispatcher = request.getRequestDispatcher("getpost.jsp");
		
				
		
		   
    	String a = (String) request.getParameter("valore1");
    	String b = (String) request.getParameter("valore2");
    	
    	if (a==null && b==null)
    	{
    		request.setAttribute("colore", "white");
    	}
    	else
    	{
    		    	
    		request.getAttribute("valore2");
    	
    		int x;
    		int y;
    	
    		try  
    		{
    			x= Integer.parseInt(a); 
    		}
    		catch (Exception e)
    		{
    			x= 2147483647;   			
    		}
    		
    		try  
    		{
    			y= Integer.parseInt(b); 
    		}
    		catch (Exception e)
    		{
    			y= 2147483647;
    			
    		}
    		
    		String errore="ERRORE! impossibile eseguire la somma";
    	
    		if(x==2147483647 || y==2147483647)
    		{
    			request.setAttribute("risultato", errore);
    			
    			request.setAttribute("colore", "yellow");

    		}
    		else 
    		{
    			int res=x+y;
    			request.setAttribute("risultato", res);
    			
    			request.setAttribute("colore", "yellow");
    		}
    	}
    	
    	
		dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	
    	
		RequestDispatcher dispatcher = request.getRequestDispatcher("getpost.jsp");
	   
    	String a = (String) request.getParameter("valore1");
    	String b = (String) request.getParameter("valore2");
    	
    	request.getAttribute("valore2");
    	int x;
    	int y;
    	
    	try  
		{
			x= Integer.parseInt(a); 
		}
		catch (Exception e)
		{
			x= 2147483647;
			
		}
		
		try  
		{
			y= Integer.parseInt(b); 
		}
		catch (Exception e)
		{
			y= 2147483647;
			
		}
    	
    	
		String errore="ERRORE! impossibile eseguire la somma";
    	
    	if(x== 2147483647 || y== 2147483647 )
    	{
    		request.setAttribute("risultato", errore);
    		
    		request.setAttribute("colore", "red");
    	}
    	else 
    	{
    		int res=x+y;
    		request.setAttribute("risultato", res);
    		
    		request.setAttribute("colore", "red");
    	}
    	
    	
		dispatcher.forward(request, response);

		
	}

}
