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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

		HttpSession session = request.getSession();

		if (session.isNew()) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
			dispatcher.forward(request, response);
		} else {

			if (session.getAttribute("utente") == null) {
				String username = request.getParameter("username");
				String password = request.getParameter("password");
				Utente utente = login(username, password);
				if (utente != null)
					session.setAttribute("utente", utente);
			}
			if (session.getAttribute("utente") == null) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
				dispatcher.forward(request, response);
			} else {

				request.setAttribute(jspParamUserId, session.getId());
				String sessione = session.getId();
				Utente datiUtente = (Utente) session.getAttribute("utente");
				String utente = datiUtente.getUsername();
				Integer id_utente = datiUtente.getId_utente();
				ArrayList<Risultati> risultati = null;
				String a = (String) request.getParameter(reqParamNameVal1);
				String b = (String) request.getParameter(reqParamNameVal2);
				String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());

				request.setAttribute("data", date);
				request.setAttribute("metodo", request.getParameter("method"));

				if ((a == null) && (b == null)) {
					request.setAttribute(jspParamNameColor, coloreHome);
					request.setAttribute("username", utente);
					try {
						risultati = queryResult(datiUtente.getId_utente());
						request.setAttribute("arraylist", risultati);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					request.setAttribute(jspParamNameColor, request.getParameter("color"));
					String risultato = operazioni(a, b);
					request.setAttribute(jspParamNameResult, risultato);
					request.setAttribute("username", utente);
					try {
						insert(request.getParameter("method"), a, b, risultato, date, sessione, id_utente);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						risultati = queryResult(datiUtente.getId_utente());
						request.setAttribute("arraylist", risultati);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);
				dispatcher.forward(request, response);
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

	/**
	 * @throws IOException
	 * @throws ServletException
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	public void init(ServletConfig config) throws ServletException {

		// Stabilisco la connessione col database
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/getpost", "postgres", "postgre");
			System.out.println("Connessione in corso...");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	// funzione che esegue l'insert dentro risultati per aggiungere l'operazione
	public void insert(String nomeMetodo, String a, String b, String res, String date, String session,
			Integer id_utente) throws SQLException {
		try {
			PreparedStatement ps = con.prepareStatement(
					"INSERT INTO risultati(metodo, add1, add2, risultato, data, sessione, id_utente ) VALUES(?, ?, ?, ?, ?, ?, ?)");

			// eseguo l'insert con i dati passati come parametri della funzione
			ps.setString(1, nomeMetodo);
			ps.setString(2, a);
			ps.setString(3, b);
			ps.setString(4, res);
			ps.setString(5, date);
			ps.setString(6, session);
			ps.setInt(7, id_utente);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// funzione che inserisce dentro un ArrayList tutti i risultati di un utente per
	// poi
	// passarloo alla pagina jsp
	public Utente login(String username, String password) {

		ResultSet rs = null;
		boolean st = false;
		Utente utente = new Utente();

		try {
			// faccio una query per vedere se i dati inseriti sono corretti
			PreparedStatement ps = con.prepareStatement("select * from utente where username=? and password=?");

			ps.setString(1, username);
			ps.setString(2, password);
			System.out.println("executing login with usr (" + username + ") pwd (" + password + ")");
			rs = ps.executeQuery();
			st = rs.next();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (st) {
			// metto dentro utente i dati della riga della tabella
			try {

				utente.setUsername(rs.getString("username"));
				utente.setPassword(rs.getString("password"));
				utente.setNome(rs.getString("nome"));
				utente.setCognome(rs.getString("cognome"));
				utente.setId_utente(rs.getInt("id_utente"));
				return utente;
			} catch (SQLException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	protected String operazioni(String a, String b) {

		int x = 0;
		int y = 0;

		// in questo blocco eseguo la somma castando le stringhe passate nel form
		try {
			x = Integer.parseInt(a);
			y = Integer.parseInt(b);

			int res = x + y;
			String risultato = String.valueOf(res);

			return risultato;

		} catch (NumberFormatException e) {

			// se le stringhe non possono essere castate in interi,il risultato sarà un
			// errore
			String risultato = errore;
			return risultato;
		}
	}

	public ArrayList<Risultati> queryResult(Integer id_utente) throws SQLException {

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
			e.printStackTrace();
		}
		return risultati;
	}
}
