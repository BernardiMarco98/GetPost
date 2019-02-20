package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
	private Logger logger = null;

	/**
	 * qui tu stai dichiarando due variabili a livello di Servlet quindi verranno
	 * condivise da ogni request gestita dalla servlet
	 *
	 * lo scenario con cui stiamo facendo questi esercizi e' poco realistico perche'
	 * generiamo una request alla volta
	 *
	 * mentre nelle servlet di produzione, possono arrivare anche migliaia di
	 * request al secondo
	 *
	 * ammetiamo di essere in grado di generare due request contemporanee: arriva la
	 * prima, entra ad esempio nel doPost e mette in a e b le due stringhe ricevute
	 * dalla form (es 10,20).
	 *
	 * stessa cosa fa la seconda request (es 30,40)
	 *
	 * tutte le volte che arriva una request , il container delega a un thread le
	 * operazioni da fare per quella request , per fare in modo che lui (il
	 * container) sia pronto a gestire sin da subito una nuova request (se non
	 * facesse cosi, la millesima request potrebbe dover attendere secondi prima che
	 * le altre 999 vengano eseguite ...e questo e' troppo inefficiente (boh .. con
	 * o senza i ? credo con si :-)
	 *
	 * quindi ogni request viene eseguita in PARALLELO
	 *
	 * quindi siamo al punto che le due request , eseguite in parallelo , sono allo
	 * setsso punto (valorizzazione di a e b )
	 *
	 * Da a e b, con le parseInt() otterresti x ed y ma qui c'e' l'inghippo x ed y
	 * sono dichiarate a livello di classe (Servlet) e quindi sono COMUNI ai due
	 * threads che stanno gestendo le due request (a differenza di a e b che sono
	 * diciarate a livello di metodo e quindi DISTINTE nei vari threads)
	 *
	 * immagina che il primo thread arrivi qualche microsecondo prima del secondo e
	 * quinda metta dentro in x e y i due valori 10 e 20 ma, subito dopo ( e prima
	 * che il primo thread riesca a fare la int res = x+y; ) il secondo arriva e
	 * mette in x e y 30 e 40... il risultato e' un disastro: la prima request
	 * scriverebbe in res 70 invece di 30 e la seconda request anche lei quindi chi
	 * ha fatto la request 10,20 si vede arrivare una response con dentro 70 invece
	 * di 30
	 *
	 * ecco perche' quelle due variabili non possono essere GLOBALI (comuni a tutte
	 * le request) ma devono essere LOCALI (con visibilita' limitata al metodo e
	 * quindi al thread che lo sta eseguendo)
	 *
	 * questo ci fa capire che una applicazione web oltre a passare i test
	 * funzionali e di regressione (normalmente eseguiti con singol request ) deve
	 * passare anche dei test MULTITHREAD (che sono quelli che simulano la realta )
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 *
	 */

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

		Cookie userCookies[] = request.getCookies();
		Cookie userCookieLogin = getCookie(userCookies, "usernameServletGetPost");
		HttpSession session = request.getSession();
		String session_id = session.getId();
		Utente session_user = (Utente) session.getAttribute("utenteSessione");

		// sessione vuota
		if (session_user == null) {
			logger.trace("Nessun utente in sessione");
			if (!session.isNew() && userCookieLogin == null) {
				// se il cookie è vuoto, chiedo il login
				logger.info("Lettura credenziali login");
				Utente utente = null;
				String username = request.getParameter("username");
				String password = request.getParameter("password");

				if ((username != null && password != null) && (utente = login(username, password)) != null) {

					Integer id_utente = utente.getId_utente();
					session.setAttribute("utenteSessione", utente);

					request.setAttribute(jspParamUserId, session_id);

					setInterface(request, username, null, id_utente);

					logger.debug("Setting cookieUsername=" + username);
					Cookie cookieUsername = new Cookie("usernameServletGetPost", username);
					cookieUsername.setMaxAge(300);
					response.addCookie(cookieUsername);

					RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
					dispatcher.forward(request, response);
					return;
				}

			} else if (userCookieLogin != null) {// se il cookie è pieno, eseguo login implicito
				logger.trace("Trovato un cookie utente valido");
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
			logger.trace("Utente presente in sessione");
			if (request.getParameter("logout") != null && request.getParameter("logout").equals("t")) {
				// la pagina jsp di login,stamperà un messaggio di logout
				// elimino i cookie
				logger.trace("Logout effettuato");
				logger.debug("Deleting cookieUsername");
				Cookie cookieUsername = new Cookie("usernameServletGetPost", "");
				cookieUsername.setMaxAge(0);
				response.addCookie(cookieUsername);
				session.invalidate();
				request.setAttribute("logout_message", "Logout effettuato!");
				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				dispatcher.forward(request, response);
				return;

			} else {
				// se l'utente non fa il logout, eseguirà le operazioni

				request.setAttribute(jspParamUserId, session_id);
				Utente userLogged = (Utente) session.getAttribute("utenteSessione");
				String utente = userLogged.getUsername();
				Integer id_utente = userLogged.getId_utente();

				setInterface(request, utente, session_id, id_utente);

				logger.debug("Setting cookieUsername=" + utente);
				Cookie cookieUsername = new Cookie("usernameServletGetPost", utente);
				cookieUsername.setMaxAge(300);
				response.addCookie(cookieUsername);

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
	public void init(ServletConfig config) throws ServletException {

		logger = Logger.getRootLogger();
		logger.info("Servlet initialized");

		// Stabilisco la connessione col database
		try {
			Class.forName("org.postgresql.Driver");
			logger.debug("Connessione al database in corso... (jdbc:postgresql://localhost:5432/getpost) (postgres) (postgre)");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/getpost", "postgres", "postgre");
			logger.info("Connessione avvennuta con successo!");
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("Connessione al database fallita!");
			e.printStackTrace();
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
			logger.trace("lettura parametri somma");
			String addendo1 = (String) request.getParameter(reqParamNameVal1);
			String addendo2 = (String) request.getParameter(reqParamNameVal2);

			if ((addendo1 == null) && (addendo2 == null)) {
				request.setAttribute(jspParamNameColor, coloreHome);
			} else {
				if (request.getMethod().equals("GET"))
					request.setAttribute(jspParamNameColor, "yellow");
				else
					request.setAttribute(jspParamNameColor, "red");
				logger.debug("addendo1="+addendo1+" , addendo2="+addendo2);
				String risultato = operazioni(addendo1, addendo2);
				request.setAttribute(jspParamNameResult, risultato);
				try {
					insert(metodo, addendo1, addendo2, risultato, date, sessione, id);
				} catch (SQLException e) {
					logger.error("Impossibile inserire il risultato nel db");
					e.printStackTrace();
				}

			}
		}
		try {
			risultati = queryResult(id);
			request.setAttribute("arraylist", risultati);
		} catch (SQLException e) {
			logger.error("Impossibile visualizzare i risultati");
			e.printStackTrace();
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
				logger.debug("Executing login with usr (" + username + ") pwd (" + password + ")");
				resultSet = preparedStatement.executeQuery();
				queryPositiva = resultSet.next();
			} catch (Exception e) {
				logger.error("Impossibile eseguire la query");
				e.printStackTrace();
			}
		else
			try {
				preparedStatement = con.prepareStatement("select * from utente where username=?");
				logger.debug("Executing implicit login");
				preparedStatement.setString(1, username);
				resultSet = preparedStatement.executeQuery();
				queryPositiva = resultSet.next();
			} catch (Exception e) {
				logger.error("Impossibile eseguire la query");
				e.printStackTrace();
			}

		if (queryPositiva) {
			logger.trace("Utente loggato correttamente!");
			// metto dentro utente i dati della riga della tabella
			try {

				utente.setUsername(resultSet.getString("username"));
				utente.setPassword(resultSet.getString("password"));
				utente.setNome(resultSet.getString("nome"));
				utente.setCognome(resultSet.getString("cognome"));
				utente.setId_utente(resultSet.getInt("id_utente"));
				return utente;
			} catch (SQLException e) {
				logger.error("Impossibile settare i dati utente");
				e.printStackTrace();
			}
		}
		logger.warn("Credenziali di login errate");
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
		} catch (Exception e) {
			logger.error("Impossibile inserire il risultato nel db");
			e.printStackTrace();
		}
	}

	// funzione che inserisce dentro un ArrayList tutti i risultati di un utente per
	// poi
	// passarloo alla pagina jsp

	protected String operazioni(String a, String b) {

		logger.trace("Executing method operazioni");

		int x = 0;
		int y = 0;

		// in questo blocco eseguo la somma castando le stringhe passate nel form
		try {
			x = Integer.parseInt(a);
			y = Integer.parseInt(b);

			int res = x + y;
			String risultato = String.valueOf(res);
			logger.debug("Esecuzione somma-> " + a + "+" + b + "=" + risultato);
			return risultato;

		} catch (NumberFormatException e) {

			// se le stringhe non possono essere castate in interi,il risultato sarà un
			// errore
			String risultato = errore;
			logger.debug("Esecuzione somma-> " + a + "+" + b + "=" + risultato);
			return risultato;
		}
	}

	public ArrayList<Risultati> queryResult(Integer id_utente) throws SQLException {

		logger.trace("Executing method queryResult");
		// dichiaro arraylist
		ArrayList<Risultati> risultati = new ArrayList<Risultati>();
		ResultSet resultSet = null;
		PreparedStatement ps;

		// inserisco in una variabile locale l'id_utente
		try {
			ps = con.prepareStatement("select add1, add2, risultato, data, metodo from risultati where id_utente=?");
			ps.setInt(1, id_utente);
			resultSet = ps.executeQuery();

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
			logger.error("Impossibile eseguire la query");
			e.printStackTrace();
		}
		return risultati;
	}
}