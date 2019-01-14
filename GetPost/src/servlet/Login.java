package servlet;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name="Login",urlPatterns={"/Login"})

public class Login extends HttpServlet {
 
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if(Validate.checkUser(username, password))
        {
            RequestDispatcher rs = request.getRequestDispatcher("getpost.jsp");
            rs.forward(request, response);
        }
        else
        {
           out.println("Username o Password non corretti, reinserire i dati");
           RequestDispatcher rs = request.getRequestDispatcher("index.jsp");
           rs.include(request, response);
        }
    }  
}