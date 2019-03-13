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

import javax.naming.InitialContext;
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

	
	// qui hai usato una stringa per definire il valore "enable"
	// e poterlo poi controllare con la tringa del file di conf...
	// va bene, ma allora la userei meglio ... guarda	
	static String cookiesParamEnable = "enable";
	static String cookiesParamDisable = "disable";
	// ok .. ? chiaro il motivo di questa scelta ?
	// ?perchè uso più volte la solita stringa
	// ? non ho capito ... c'e' un motivo ben preciso del perche' e' meglio fare cosi ...
	// prova a pensarci un sec ( o forse e' quello che intendevi nella tua risposta ma allora non 
	// l'ho capita .... :-)
	// ... perchè se in futuro il valore del parametro per il quale attivo o disattivo i cookies dovesse
	// cambiare, basta modificare la costante e non tutti i punti dove controllo la stringa
	// ok . .ma anche perche' se tu fai riferimento direttamente alla stringa ( es: "enable" )
	// potresti commettere l'errore di scrivere in un punto "enable"  e in un altro "anable"
	// e tutto va a p...e .... ok ? quella stringa e' usata piu volte ... diventa una variabile
	
	
	// tutte queste sono costanti
	// riguardati cosa significa il modificatore static ( scope delle variabili )
	String errore = "errore";	
	String nomejsp = "getpost.jsp";
	String coloreHome = "white";
	String reqParamNameVal1 = "valore1";
	String reqParamNameVal2 = "valore2";
	String jspParamNameResult = "risultato";
	String jspParamNameColor = "colore";
	String jspParamUserId = "id";
	String nomeSessionList = "lista";
	//------
	
	public Connection con;

	// non sono convinto che queste variabili debbano avere questo scope ...
	// secondo me e' un errore ... poi ci ritorniamo sopra ( me ne sono accorto adesso .. )
	//
	// torniamo qui .... ora ...vorrei tu ripensassi ad alcuni discorsi 
	// fatti originariamente ( i primi giorni in cui abbiamo cominciato a guardare le servlet )
	// se ti ricordi avevamo fatto un discorso in merito a variabili globali o locali 
	// e al rischio che comporta l'utilizzarle male (ancora una volta ritorna il concetto di scope ... )
	// ti ricordi qualcosa in questo senso ?
	// ?si, sul multithreading...
	// esattamente .....
	// queste due informazioni , sono diverse da request a request .. giusto ?
	// ?giusto
	// no , sto dicendo cazzate ..perchè vengano settate una sola volta per tutte le request nell'init?
	// si , esatto .. sto dicendo cazzata .. puo' andar bene che siano cosi ...
	boolean cookiesEnable = false;

	// piu che altro, implicitLogin e' totalmente inutile come variabile
	// globale all'applicazione .. e' una variabile locale al metodo di init..
	// prova a vedere quando la usi...
	// visto ?
	// si,
	
	// spe ... non ho cpaito l'uso di questa ..
	// me la spieghi ?
	// se la configurazione è sbagliata da file, quando catturo l'eccezione la
	// setto false e quando viene eseguito il programma
	// vado in questo blocco:
	// aspetta .. quindi appare una pagina copn scritto "configurazione non corretta " ?
	// ?si
	// mmh 
	
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
		Cookie userCookies[] = null;
		Cookie userCookieLogin = null;
		// nel doGet() (ad ogni request) solo qui e solo per un log
		// questo log , non serve qui .. scrivere ad ogni request che stai
		// lavorando con implicitLoginn enabled o disabled , non serve a nulla
		// dato che e' una informazione di init ... non ha senso continuare a scriverla
		// ad ogni request ... il suo valore non cambiera' mai ...
		// giusto ?giusto
		HttpSession session = request.getSession();
		String session_id = session.getId();
		Utente session_user = (Utente) session.getAttribute("utenteSessione");

		logger.debug("CookiesEnable = " + cookiesEnable);
		if (cookiesEnable) {

			userCookies = request.getCookies();
			userCookieLogin = getCookie(userCookies, "usernameServletGetPost");

			if (session_user != null) {
				logger.debug("Setting cookieUsername=" + session_user.getUsername());
				Cookie cookieUsername = new Cookie("usernameServletGetPost", session_user.getUsername());
				cookieUsername.setMaxAge(300);
				response.addCookie(cookieUsername);
			}
		}
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

					if (cookiesEnable) {
						logger.debug("Setting cookieUsername=" + username);
						Cookie cookieUsername = new Cookie("usernameServletGetPost", username);
						cookieUsername.setMaxAge(300);
						response.addCookie(cookieUsername);
					}

					RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
					dispatcher.forward(request, response);
					return;
				}
			}
			if (userCookieLogin != null) {// se il cookie è pieno, eseguo login
				// implicito
				Utente userLogged = login(userCookieLogin.getValue(), null);
				if (userLogged != null) {
					session.setAttribute("utenteSessione", userLogged);
					request.setAttribute(jspParamUserId, session_id);
					String usernameUtente = userLogged.getUsername();
					Integer id_utente = userLogged.getId_utente();
					setInterface(request, usernameUtente, null, id_utente);
					logger.debug("Setting cookieUsername=" + usernameUtente);

					Cookie cookieUsername = new Cookie("usernameServletGetPost", usernameUtente);
					cookieUsername.setMaxAge(300);
					response.addCookie(cookieUsername);
					RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
					dispatcher.forward(request, response);
					return;
				}
			}
			// se la sessione è vuota, nuova e non c'è un cookie valido indirizzo al login
			// form
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
			return;
		}
		// la sessione è piena
		if (request.getParameter("logout") != null && request.getParameter("logout").equals("t")) {
			// la pagina jsp di login,stamperà un messaggio di logout
			// elimino i cookie
			Cookie cookieUsername = new Cookie("usernameServletGetPost", "");
			cookieUsername.setMaxAge(0);
			response.addCookie(cookieUsername);
			session.invalidate();
			request.setAttribute("logout_message", "Logout effettuato!");
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			logger.debug("Logout effetuato");
			dispatcher.forward(request, response);
			return;
		}
		// se l'utente non fa il logout, eseguirà le operazioni
		logger.trace("Utente in sessione");
		request.setAttribute(jspParamUserId, session_id);
		Utente userLogged = (Utente) session.getAttribute("utenteSessione");
		String utente = userLogged.getUsername();
		Integer id_utente = userLogged.getId_utente();
		setInterface(request, utente, session_id, id_utente);
		logger.debug("Setting cookieUsername=" + utente);

		RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
		dispatcher.forward(request, response);
		return;
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
		String implicitLogin = null;
		
		try {
			ServletContext servletContext = this.getServletContext();

			// ok 
			cookiesEnable = false;
			
			// poi la usi solo qui
			implicitLogin = servletContext.getInitParameter("cookie");
			//se il parametro è vuoto o non esiste, setto i cookies
			if (implicitLogin == null) {
				cookiesEnable = true;
			}
			//se il parametro non appartiene al vocabolario, setto i cookies
			else if(!isImplicitLoginValid(implicitLogin)) {
				cookiesEnable = true;
			}
			//se il parametro appartiene al vocabolario e vale "enable", abilito i cookies
			// vedi che adesso  il codice e' piu' "..discorsivo..." anche per chi non sa niente di informatica ..
			// ora e' uno pseudo-inglese
			
			// qui non capisco perche' controlli ancora se e' valido ...
			// l'hai gia fatto prima .. perche' rifarlo ?
			// ? marco ? si, non serve
			
			else if(implicitLogin.equalsIgnoreCase(cookiesParamEnable)) {
				cookiesEnable = true;
			}
			
			
			InitialContext initialContext = null;
			initialContext = new InitialContext();

			DataSource dataSource = null;
			dataSource = (DataSource) initialContext.lookup("java:/comp/env/jdbc/postgres");

			// se il dataSource non è nullo, quindi corretto, verrà fatta una connessione
			// tramite risorsa
			if (dataSource != null) {
				con = dataSource.getConnection();
				logger.trace("Connessione tramite risorsa");
				return;
			}
			// se il dataSource è nullo, verrà fatta una connessione cablata
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/getpost", "postgres", "postgre");
			logger.trace("Connessione cablata");

		} catch (Exception e) {
		
			// perche' hai usato getName e non getSimpleName() ?
			// va bene eh ! .. ma perche' hai fatto questa scelta ?
			// ?così mi ricordo di guardare la documentazione xD
			// .. mmh speravo l'avessi fatto per un motivo piu' "nobile"
			// non hai per caso verificato se esistono due eccezioni con stesso simpleName
			// in packages diversi !?!?!
			// es :
			// javax.naming.NameNotFoundException e java.lang.NameNotFoundException 
			// ??
			// ( ho sparato un package a caso ... ma il dubbio potrebbe venire ..
			// e in quel caso non sarebbero la stessa eccezione !!
			// ti documenti in questo senso ?
			// ?yes
			if (e.getClass().getName() == "javax.naming.NameNotFoundException") {
				logger.error("Impossibile connettersi al database tramite risorsa, Exception:" + e.toString());
				return;
			}
			if (e.getClass().getName() == "java.lang.ClassNotFoundException") {
				logger.error("Driver per la connessione al database non trovati, Exception:" + e.toString());
				return;
			}
			if (e.getClass().getName() == "java.sql.SQLException") {
				logger.error("Impossibile connettersi al database. Exception:" + e.toString());
				return;
			}
			logger.error("Error found during init method, Exception:" + e.toString());
		}
	}

	// funzione che controlla se il valore passato da conf appartiene al "dizionario"
	
	// qui ... cambierei il nome al metodo ... di solito i metodi che ritornano boolean
	// si chiamano isQualcosa ... rende il codice piu' leggibile
	
	// p.s: i nomi delle variabili, in java , per convenzione iniziano con minuscola
	// se iniziano con maiuscola sono Classi ....
	// ok ?
	//ok
	public boolean isImplicitLoginValid (String parameter) {
		
		if (parameter.equalsIgnoreCase(cookiesParamDisable))
			return true;
		if (parameter.equalsIgnoreCase(cookiesParamEnable))
			return true;
		
		// sempre per dare a ciascun pezzo di codice la propria competenza ...
		// questo metodo si occupa solo di dire se una stringa e' valida o no 
		// non si occupa di stabilire se i cookies sono o non sono abilitati ( lo fa qualcun'altro)
		// quindi non ha senso questo messaggio di log
		
		// ok ?
		//ok
		logger.debug("parameter = " + parameter + ", non appartiene ai valori accettati");	
		return false;
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

	// funzione che interroga il database per controllare se il login è corretto
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
			try {
				logger.debug("Creating utente-> " + "username: " + resultSet.getString("username") + ", nome: "
						+ resultSet.getString("nome") + ", cognome: " + resultSet.getString("cognome") + ", id_utente: "
						+ resultSet.getInt("id_utente"));
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