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
public class Servlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	public Connection con;
	
	String errore = "errore";
	String nomejsp = "getpost.jsp";
	String coloreHome = "white";
	String coloreGet = "yellow";
	String colorePost = "red";
	String reqParamNameVal1 = "valore1";
	String reqParamNameVal2 = "valore2";
	String jspParamNameResult = "risultato";
	String jspParamNameColor = "colore";
	String jspParamUserId = "id";
	String nomeMetodoGet = "GET";
	String nomeMetodoPost = "POST";
	String nomeSessionList = "lista";
	/**
	 * qui tu stai dichiarando due variabili a livello di Servlet
	 * quindi verranno condivise da ogni request gestita dalla servlet
	 * 
	 * lo scenario con cui stiamo facendo questi esercizi e' poco realistico
	 * perche' generiamo una request alla volta
	 * 
	 * mentre nelle servlet di produzione, possono arrivare anche migliaia di request al secondo
	 * 
	 * ammetiamo di essere in grado di generare due request contemporanee:
	 * arriva la prima, entra ad esempio nel doPost e mette in a e b le due stringhe
	 * ricevute dalla form (es 10,20).
	 * 
	 * stessa cosa fa la seconda request (es 30,40)
	 * 
	 *  tutte le volte che arriva una request , il container delega a un thread
	 *  le operazioni da fare per quella request , per fare in modo che lui (il container)
	 *  sia pronto a gestire sin da subito una nuova request
	 *  (se non facesse cosi, la millesima request potrebbe dover attendere secondi prima che le altre 999
	 *  vengano eseguite ...e questo e' troppo inefficiente (boh .. con o senza i ? credo con si :-)
	 *  
	 *  quindi ogni request viene eseguita in PARALLELO
	 *  
	 *  quindi siamo al punto che le due request , eseguite in parallelo , sono allo setsso punto
	 *  (valorizzazione di a e b )
	 *  
	 *  Da a e b, con le parseInt() otterresti x ed y ma qui c'e' l'inghippo
	 *  x ed y sono dichiarate a livello di classe (Servlet) e quindi sono COMUNI ai due threads
	 *  che stanno gestendo le due request (a differenza di a e b che sono diciarate a livello di metodo e
	 *  quindi DISTINTE nei vari threads)
	 *  
	 *  immagina che il primo thread arrivi qualche microsecondo prima del secondo e quinda metta dentro
	 *  in x e y i due valori 10 e 20 ma, subito dopo ( e prima che il primo thread riesca a fare la
	 *  int res = x+y; )
	 *  il secondo arriva e mette in x e y 30 e 40... il risultato e' un disastro:
	 *  la prima request scriverebbe in res 70 invece di 30 e la seconda request anche lei
	 *  quindi chi ha fatto la request 10,20 si vede arrivare una response con dentro 70 invece di 30
	 *  
	 *  ecco perche' quelle due variabili non possono essere GLOBALI (comuni a tutte le request)
	 *  ma devono essere LOCALI (con visibilita' limitata al metodo e quindi al thread che lo sta eseguendo)
	 *  
	 *  questo ci fa capire che una applicazione web oltre a passare i test funzionali e di regressione (normalmente
	 *  eseguiti con singol request ) deve passare anche dei test MULTITHREAD (che sono quelli che simulano
	 *  la realta )
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
    public Servlet() 
    {
    	super();
    	
    	// TODO Auto-generated constructor stub
    }
    /**
	* @throws IOException 
	* @throws ServletException 
	* @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	*/
    public void init(ServletConfig config)throws ServletException
    {	
    	//Stabilisco la connessione col database
    	try 
    	{
			Class.forName("org.postgresql.Driver");
			con=DriverManager.getConnection ("jdbc:postgresql://localhost:5432/getpost","postgres","postgre");
			System.out.println("Connessione in corso...");
    	} 
    	catch (ClassNotFoundException | SQLException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} 
    }
    //funzione che inserisce dentro un ArrayList tutti i risultati di un utente per poi
    //passarloo alla pagina jsp
    public void queryResult(HttpServletRequest request, HttpServletResponse  response) throws ServletException, IOException, SQLException 
    {
    	//dichiaro arraylist
    	ArrayList<Risultati> risultati = new ArrayList<Risultati>();
    	
    	ResultSet resultSet = null;
    	PreparedStatement ps;
    	//Prendo dalla sessione l'oggetto utente
    	HttpSession sessione = request.getSession();
    	Utente datiUtente = (Utente) sessione.getAttribute("utente");
    	//inserisco in una variabile locale l'id_utente
    	Integer id_utente = datiUtente.getId_utente();
    	ps = con.prepareStatement ("select add1, add2, risultato, data, metodo from risultati where id_utente=?");
    	ps.setInt(1, id_utente);          
    	resultSet = ps.executeQuery();
    	
    	//dopo aver fatto la query, inserisco i risultati dentro l'array
    	while (resultSet.next()) 
    	{
    		Risultati user = new Risultati();
    		user.setAdd1(resultSet.getString("add1"));
    		user.setAdd2(resultSet.getString("add2"));
    		user.setRisultato(resultSet.getString("risultato"));
    		user.setData(resultSet.getString("data"));
    		user.setMetodo(resultSet.getString("metodo"));
    		risultati.add(user);
    	}
    	request.setAttribute("arraylist", risultati);
    	System.out.println(risultati);
    	
    }
    //funzione che esegue l'insert dentro risultati per aggiungere l'operazione 
    public void insert(String nomeMetodo,String a, String b, String res, String date, String session, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException
    {	
    	//prendo dalla sessione l'oggetto utente e instanzio delle variabili locali
    	//contenenti i dati che mi servono
    	HttpSession sessione = request.getSession();
    	Utente datiUtente = (Utente) sessione.getAttribute("utente");
    	String utente = datiUtente.getUsername();
    	Integer id_utente = datiUtente.getId_utente();
    	request.setAttribute("username", utente);
    	System.out.println(utente);
    	
    	PreparedStatement ps=con.prepareStatement("INSERT INTO risultati(metodo, add1, add2, risultato, data, sessione, id_utente ) VALUES(?, ?, ?, ?, ?, ?, ?)");   
    	//eseguo l'insert con i dati passati come parametri della funzione
    	ps.setString(1, nomeMetodo);
    	ps.setString(2, a);
    	ps.setString(3, b);
    	ps.setString(4, res);
    	ps.setString(5, date);
    	ps.setString(6, session);
    	ps.setInt(7, id_utente);
    	
    	ps.executeUpdate(); 
    	
    }
    
    protected void operazioni(String coloreMetodo, String nomeMetodo, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException
    {
    	RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);	
    	
    	//Richiama la sessione corrente o ne crea una se non esiste
    	HttpSession session = request.getSession(); //qui creo la sessione o la ottengo se gia esistente	
    	request.setAttribute(jspParamUserId ,session.getId());
    	String sessione = session.getId();
    	//inserisco i parametri del form in variabili
    	String a = (String) request.getParameter(reqParamNameVal1);
    	String b = (String) request.getParameter(reqParamNameVal2);
    	String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());
    	
    	request.setAttribute("data", date);
    	request.setAttribute("metodo", nomeMetodo);
    	
    	//al primo giro setto lo sfondo della pagina bianco
    	if (a == null && b == null)	
    	{
    		request.setAttribute(jspParamNameColor, coloreHome);
    	}
    	else
    	// siamo nel secondo ( o n-esimo giro )
    	{	
    		//prendo l'arraylist della sessione

    		int x = 0;
    		int y = 0;
    		
    		request.setAttribute(jspParamNameColor, coloreMetodo);
    		String risultato = a+"+"+b+"=";
    		
    		//in questo blocco eseguo la somma castando le stringhe passate nel form
    		try  
    		{
    			x = Integer.parseInt(a); 
    			y = Integer.parseInt(b);
    			
    			int res = x + y;

    			risultato = risultato + res;
    			request.setAttribute(jspParamNameResult, res);
    			String result = String.valueOf(res);
    			
    			insert(nomeMetodo, a, b, result, date, sessione, request, response);
    		}
    		catch (NumberFormatException e)
    		{	
    			// se le stringhe non possono essere castate in interi,il risultato sarà un errore
    			risultato = risultato + errore;				
    			request.setAttribute(jspParamNameResult, errore);
    			
    			insert(nomeMetodo, a, b, errore, date, sessione, request, response);
    			//queryResult(request, response);
    		}
    	}
    	queryResult(request,response);
    	dispatcher.forward(request, response);
    }
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	// TODO Auto-generated method stub

    	try 
    	{
    		operazioni(coloreGet,nomeMetodoGet, request, response);
    	}
    	catch (SQLException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    /**
	* @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	*/
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
    	// TODO Auto-generated method stub
    	
    	try 
    	{
    		operazioni(colorePost,nomeMetodoPost, request, response);
    	} 
    	catch (SQLException e) 
    	{
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
}