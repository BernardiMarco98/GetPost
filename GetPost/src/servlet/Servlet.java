package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * Servlet implementation class Servlet
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String errore = "errore";
	String nomejsp = "getpost.jsp";
	String coloreHome = "white";
	String reqParamNameVal1 = "valore1";
	String reqParamNameVal2 = "valore2";
	String jspParamNameResult = "risultato";
	String jspParamNameColor = "colore";
	String jspParamUserId = "id";
	String nomeSessionList = "lista";
	public Connection con;
	String implicitLogin = null;
	private Logger logger = null;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// quando sei qui, sai di essere in doGet()
		logger.trace("Executing method doGet");

		logger.debug("implicitLogin =" + implicitLogin);

		Cookie userCookies[] = request.getCookies();
		Cookie userCookieLogin = getCookie(userCookies, "usernameServletGetPost");
		HttpSession session = request.getSession();
		String session_id = session.getId();
		Utente session_user = (Utente) session.getAttribute("utenteSessione");

		// sessione vuota
		if (session_user == null) {
			if (!session.isNew() && userCookieLogin == null) {
				// se il cookie è vuoto, chiedo il login
				Utente utente = null;
				String username = request.getParameter("username");
				String password = request.getParameter("password");

				logger.debug("Parametri login ricevuti: username:" + username + " password:*******");
				if ((username != null && password != null) && (utente = login(username, password)) != null) {
					Integer id_utente = utente.getId_utente();
					session.setAttribute("utenteSessione", utente);

					request.setAttribute(jspParamUserId, session_id);

					setInterface(request, username, null, id_utente);

					logger.trace("Sessione vuota e cookie valido assente");
					logger.debug("Setting cookieUsername=" + username);
					if (implicitLogin.equals("enable")) {
						Cookie cookieUsername = new Cookie("usernameServletGetPost", username);
						cookieUsername.setMaxAge(300);
						response.addCookie(cookieUsername);
					}
					RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
					dispatcher.forward(request, response);
					return;
				}

			} else if (userCookieLogin != null && implicitLogin.equals("enable")) {// se il cookie è pieno, eseguo login
				// implicito
				Utente userLogged = login(userCookieLogin.getValue(), null);
				if (userLogged != null) {

					session.setAttribute("utenteSessione", userLogged);
					request.setAttribute(jspParamUserId, session_id);
					String usernameUtente = userLogged.getUsername();
					Integer id_utente = userLogged.getId_utente();

					setInterface(request, usernameUtente, null, id_utente);

					RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
					dispatcher.forward(request, response);
					return;
				}

			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
			return;
		}
		// la sessione è piena
		else {
			if (request.getParameter("logout") != null && request.getParameter("logout").equals("t")) {
				// la pagina jsp di login,stamperà un messaggio di logout
				// elimino i cookie
				Cookie cookieUsername = new Cookie("usernameServletGetPost", "");
				cookieUsername.setMaxAge(0);
				response.addCookie(cookieUsername);
				session.invalidate();
				request.setAttribute("logout_message", "Logout effettuato!");
				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				logger.debug("Logout effetuato, cookieUsername cancellato");
				dispatcher.forward(request, response);
				return;

			} else {
				// se l'utente non fa il logout, eseguirà le operazioni

				request.setAttribute(jspParamUserId, session_id);
				Utente userLogged = (Utente) session.getAttribute("utenteSessione");
				String utente = userLogged.getUsername();
				Integer id_utente = userLogged.getId_utente();

				setInterface(request, utente, session_id, id_utente);

				logger.trace("Utente in sessione");
				if (implicitLogin.equals("enable")) {
					logger.debug("Setting cookieUsername=" + utente);
					Cookie cookieUsername = new Cookie("usernameServletGetPost", utente);
					cookieUsername.setMaxAge(300);
					response.addCookie(cookieUsername);
				}
				RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
				dispatcher.forward(request, response);
				return;
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// quando sei qui ... sai di essere in doPost()
		logger.trace("Executing method doPost");
		doGet(request, response);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void init() throws ServletException {

		logger = Logger.getRootLogger();
		logger.info("Servlet initialized");
		ServletContext c = this.getServletContext();
		implicitLogin = c.getInitParameter("cookie");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Stabilisco la connessione col database
		InitialContext cxt = null;
		try {
			cxt = new InitialContext();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.error("Uh oh -- no context!" + e);
		}

		DataSource ds = null;
		try {
			ds = (DataSource) cxt.lookup("java:/comp/env/jdbc/postgres");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			logger.error("Data source not found!" + e);
		}

		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.error("impossibile connettersi al database" + e);
		}
	}

	// funzione che imposta il contenuto della pagina
	public void setInterface(HttpServletRequest request, String username, String sessione, Integer id) {

		logger.trace("Executing method setInterface");

		ArrayList<Risultati> risultati = null;

		String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
		String metodo = request.getMethod();

		request.setAttribute("data", date);
		request.setAttribute("metodo", metodo);
		request.setAttribute("username", username);

		if (sessione != null) {
			String addendo1 = (String) request.getParameter(reqParamNameVal1);
			String addendo2 = (String) request.getParameter(reqParamNameVal2);
			// dovrei farlo qui così vedo anche quando sono vuoti
			// si ( al netto che non ricordo come si comporta se sono null , non so se va in
			// crash .. da provare )
			logger.debug("Parametri somma letti: addendo1=" + addendo1 + " , addendo2=" + addendo2);
			if ((addendo1 == null) && (addendo2 == null)) {
				request.setAttribute(jspParamNameColor, coloreHome);
			} else {
				if (request.getMethod().equals("GET"))
					request.setAttribute(jspParamNameColor, "yellow");
				else
					request.setAttribute(jspParamNameColor, "red");
				String risultato = operazioni(addendo1, addendo2);
				request.setAttribute(jspParamNameResult, risultato);

				try {
					insert(metodo, addendo1, addendo2, risultato, date, sessione, id);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		try {
			risultati = queryResult(id);
			request.setAttribute("arraylist", risultati);
		} catch (SQLException e) {
			logger.error("Impossibile visualizzare i risultati, Exception:" + e);
		}
	}

	// funzione che ritorna il cookie che cerco
	public Cookie getCookie(Cookie cookies[], String nameOfCookie) {

		logger.trace("Executing method getCookie");

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(nameOfCookie)) {
					return cookie;
				}
			}
		}
		return null;
	}

	public Utente login(String username, String password) {

		logger.trace("Executing method login");
		ResultSet resultSet = null;
		boolean queryPositiva = false;
		Utente utente = new Utente();
		PreparedStatement preparedStatement;
		if (password != null)
			try {
				// faccio una query per vedere se i dati inseriti sono corretti
				preparedStatement = con.prepareStatement("select * from utente where username=? and password=?");

				preparedStatement.setString(1, username);
				preparedStatement.setString(2, password);
				logger.debug("Executing login with usr (" + username + ") pwd (*****)");
				resultSet = preparedStatement.executeQuery();
				queryPositiva = resultSet.next();
				// logger.debug("Query eseguita: " + preparedStatement);
			} catch (Exception e) {
				logger.error("Impossibile eseguire la query, Exception:" + e);
			}
		else
			try {
				preparedStatement = con.prepareStatement("select * from utente where username=?");
				logger.debug("Executing implicit login");
				preparedStatement.setString(1, username);
				resultSet = preparedStatement.executeQuery();
				queryPositiva = resultSet.next();
				// logger.debug("Query eseguita: " + preparedStatement);
			} catch (Exception e) {
				logger.error("Impossibile eseguire la query, Exception:" + e);
			}

		if (queryPositiva) {
			// metto dentro utente i dati della riga della tabella
			// qui non servirebbe un log che ti dice cosa stai mettendo in session ?
			// si sarebbe utile
			try {
				utente.setUsername(resultSet.getString("username"));
				utente.setPassword(resultSet.getString("password"));
				utente.setNome(resultSet.getString("nome"));
				utente.setCognome(resultSet.getString("cognome"));
				utente.setId_utente(resultSet.getInt("id_utente"));
				logger.debug("Dati utente messo in sessione: Username=" + utente.getUsername() + ", Nome:"
						+ utente.getNome() + ", Cognome=" + utente.getCognome() + ", ID=" + utente.getId_utente());
				return utente;
			} catch (SQLException e) {
				logger.error("Impossibile settare i dati utente, Exception:" + e);
			}
		}
		logger.trace("wrong username or password for username:" + username);
		return null;
	}

	// funzione che esegue l'insert dentro risultati per aggiungere l'operazione
	public void insert(String nomeMetodo, String addendo1, String addendo2, String result, String date, String session,
			Integer id_utente) throws SQLException {

		logger.trace("Executing method insert");

		try {
			PreparedStatement preparedStatement = con.prepareStatement(
					"INSERT INTO risultati(metodo, add1, add2, risultato, data, sessione, id_utente ) VALUES(?, ?, ?, ?, ?, ?, ?)");

			// eseguo l'insert con i dati passati come parametri della funzione
			preparedStatement.setString(1, nomeMetodo);
			preparedStatement.setString(2, addendo1);
			preparedStatement.setString(3, addendo2);
			preparedStatement.setString(4, result);
			preparedStatement.setString(5, date);
			preparedStatement.setString(6, session);
			preparedStatement.setInt(7, id_utente);
			preparedStatement.executeUpdate();
			logger.debug("Query eseguita: " + preparedStatement);
		} catch (Exception e) {
			logger.error("Impossibile inserire il risultato nel db, Exception:" + e);
		}
	}

	// funzione che inserisce dentro un ArrayList tutti i risultati di un utente per
	// poi
	// passarloo alla pagina jsp

	protected String operazioni(String a, String b) {

		logger.trace("Executing method operazioni");

		int x = 0;
		int y = 0;
		String risultato = null;

		// in questo blocco eseguo la somma castando le stringhe passate nel form
		try {
			x = Integer.parseInt(a);
			y = Integer.parseInt(b);

			int res = x + y;
			risultato = String.valueOf(res);

		} catch (NumberFormatException e) {

			// se le stringhe non possono essere castate in interi,il risultato sarà un
			// errore
			risultato = errore;
		}
		logger.debug("Esecuzione somma-> " + a + "+" + b + "=" + risultato);
		return risultato;
	}

	public ArrayList<Risultati> queryResult(Integer id_utente) throws SQLException {

		logger.trace("Executing method queryResult");
		// dichiaro arraylist
		ArrayList<Risultati> risultati = new ArrayList<Risultati>();
		ResultSet resultSet = null;
		PreparedStatement preparedStatement;

		// inserisco in una variabile locale l'id_utente
		try {
			preparedStatement = con
					.prepareStatement("select add1, add2, risultato, data, metodo from risultati where id_utente=?");
			preparedStatement.setInt(1, id_utente);

			resultSet = preparedStatement.executeQuery();
			logger.debug("Query eseguita: " + preparedStatement);

			// dopo aver fatto la query, inserisco i risultati dentro l'array
			while (resultSet.next()) {
				Risultati user = new Risultati();

				user.setAdd1(resultSet.getString("add1"));
				user.setAdd2(resultSet.getString("add2"));
				user.setRisultato(resultSet.getString("risultato"));
				user.setData(resultSet.getString("data"));
				user.setMetodo(resultSet.getString("metodo"));
				risultati.add(user);
			}
		} catch (Exception e) {
			logger.error("Impossibile eseguire la query, Exception:" + e);
		}
		return risultati;
	}
}