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
		
				
		Somma ris = new Somma();
		   
    	String a = (String) request.getParameter("valore1");
    	String b = (String) request.getParameter("valore2");
    	
    	if (a==null && b==null)
    	{
    	
    		Colore col = new Colore();
    		request.setAttribute("colore", col.setColore("white"));
    	}
    	else
    	{
    		    	
    		request.getAttribute("valore2");
    	
    		int x = ris.converti(a);
    		int y = ris.converti(b);
    	
    		String errore="ERRORE! impossibile eseguire la somma";
    	
    		if(x==10000 || y==10000)
    		{
    			request.setAttribute("risultato", errore);
    			Colore col = new Colore();
    			request.setAttribute("colore", col.setColore("yellow"));

    		}
    		else 
    		{
    			request.setAttribute("risultato", ris.addizione(x,y));
    			Colore col = new Colore();
    			request.setAttribute("colore", col.setColore("yellow"));
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
		

				
		Somma ris = new Somma();
		   
    	String a = (String) request.getParameter("valore1");
    	String b = (String) request.getParameter("valore2");
    	
    	request.getAttribute("valore2");
    	
    	int x = ris.converti(a);
    	int y = ris.converti(b);
    	
    	
String errore="ERRORE! impossibile eseguire la somma";
    	
    	if(x==10000 || y==10000)
    	{
    		request.setAttribute("risultato", errore);
    		Colore col = new Colore();
    		request.setAttribute("colore", col.setColore("red"));
    	}
    	else 
    	{
    		request.setAttribute("risultato", ris.addizione(x,y));
    		Colore col = new Colore();
    		request.setAttribute("colore", col.setColore("red"));
    	}
    	
    	
		dispatcher.forward(request, response);

		
	}

}
