package servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.RequestDispatcher;
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
    public Servlet() {
    	super();
    	// TODO Auto-generated constructor stub
    }

	/**
	* @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	*/
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);	
		
		//Richiama la sessione corrente o ne crea una se non esiste
		HttpSession session = request.getSession(); //qui creo la sessione o la ottengo se gia esistente

		//controllo se è il primo accesso alla sessione e se vero creo l'array altrimenti non faccio neiente perchè l'array è già 			//stato creato
		if(session.isNew() == true)
		{
			//creo l'arraylist
			ArrayList<Output> list = new ArrayList<Output>(4);

			//lo metto nella sessione
			session.setAttribute(nomeSessionList, list);
		}


		request.setAttribute(jspParamUserId ,session.getId());
		

		String a = (String) request.getParameter(reqParamNameVal1);
		String b = (String) request.getParameter(reqParamNameVal2);
		

		String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());


		request.setAttribute("data", date);
		request.setAttribute("metodo", nomeMetodoGet);
		
		// qui inulla da dire ... (a parte alconi dettagli che non voglio approfondire adesso)
		if (a == null && b == null)	
		{
			request.setAttribute(jspParamNameColor, coloreHome);
		}
		else
		// siamo nel secondo ( o n-esimo giro )
		{
			ArrayList<Output> risultati = (ArrayList<Output>) session.getAttribute(nomeSessionList);//prendo l'arraylist della sessione

			int x = 0;
			int y = 0;
			
			request.setAttribute(jspParamNameColor, coloreGet);

			try  
			{
				x = Integer.parseInt(a); 
				y = Integer.parseInt(b);
				int res = x + y;
				request.setAttribute(jspParamNameResult, res);
				
				//int i = risultati.size(); //inserisco dentro una variabile locale la dimensione dell'arraylist
				String risultato = a+"+"+b+"="+String.valueOf(res);

				// per adesso saltiamo le considerazioni su questo if .... e sull'else successivo
				// vediamo cosa succede dentro....
				if(session.isNew() != true)
				{
					if(risultati.size() == 4)//Se l'arraylist Ã¨ piena, elimina il primo elemento,
					{	
						risultati.remove(0); 
						risultati.add(new Output(risultato,date,nomeMetodoGet));
						session.setAttribute(nomeSessionList, risultati);
					
					}
					//dimensione lista <=4
					else
					{
					//inserisco il risultato dentro l'arraylist
					risultati.add(new Output(risultato,date,nomeMetodoGet));

					//rimetto l'arraylist in sessione
					session.setAttribute(nomeSessionList, risultati);
					
					}
				}
				else 
				{
					risultati.add(new Output(risultato,date,nomeMetodoGet));
					session.setAttribute(nomeSessionList, risultati);
				}
				request.setAttribute("arraylist", risultati);
			}
			catch (NumberFormatException e)
			{
				request.setAttribute(jspParamNameResult, errore);

                //int i = risultati.size(); //inserisco dentro una variabile locale la dimensione dell'arraylist
				String risultato = a+"+"+b+"="+errore;

                if(risultati.size() == 4)
				{	
					//Se l'arraylist Ã¨ piena, elimina il primo elemento,
					risultati.remove(0); 
					risultati.add(new Output(risultato,date,nomeMetodoGet));
					session.setAttribute(nomeSessionList, risultati);
				}
				//dimensione lista <=4
				else
				{
					//inserisco il risultato dentro l'arraylist
					risultati.add(new Output(risultato,date,nomeMetodoGet));

					//rimetto l'arraylist in sessione
					session.setAttribute(nomeSessionList, risultati);
					
				}
				
				request.setAttribute("arraylist", risultati);

			}			
		}
	
		dispatcher.forward(request, response);
	}
	/**
	* @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	*/
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		RequestDispatcher dispatcher = request.getRequestDispatcher(nomejsp);	
		
		//Richiama la sessione corrente o ne crea una se non esiste
		HttpSession session = request.getSession(); //qui creo la sessione o la ottengo se gia esistente

		//controllo se è il primo accesso alla sessione e se vero creo l'array altrimenti non faccio neiente perchè l'array è già stato creato
		if(session.isNew() == true)
		{
			//creo l'arraylist
			ArrayList<Output> list = new ArrayList<Output>(4);

			//lo metto nella sessione
			session.setAttribute(nomeSessionList, list);
		}


		request.setAttribute(jspParamUserId ,session.getId());
		

		String a = (String) request.getParameter(reqParamNameVal1);
		String b = (String) request.getParameter(reqParamNameVal2);
		

		String date = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss").format(new Date());


		request.setAttribute("data", date);
		request.setAttribute("metodo", nomeMetodoPost);
		

		ArrayList<Output> risultati = (ArrayList<Output>) session.getAttribute(nomeSessionList);//prendo l'arraylist della sessione

		int x = 0;
		int y = 0;
			
		request.setAttribute(jspParamNameColor, colorePost);

		try  
		{
			x = Integer.parseInt(a); 
			y = Integer.parseInt(b);
			int res = x + y;
			request.setAttribute(jspParamNameResult, res);
			
			//int i = risultati.size(); //inserisco dentro una variabile locale la dimensione dell'arraylist
			String risultato = a+"+"+b+"="+String.valueOf(res);

			// per adesso saltiamo le considerazioni su questo if .... e sull'else successivo
			// vediamo cosa succede dentro....
			if(session.isNew() != true)
			{
				if(risultati.size() == 4)//Se l'arraylist Ã¨ piena, elimina il primo elemento,
				{	
					risultati.remove(0); 
					risultati.add(new Output(risultato,date,nomeMetodoPost));
					session.setAttribute(nomeSessionList, risultati);
					
				}
				//dimensione lista <=4
				else
				{
				//inserisco il risultato dentro l'arraylist
				risultati.add(new Output(risultato,date,nomeMetodoPost));

				//rimetto l'arraylist in sessione
				session.setAttribute(nomeSessionList, risultati);
					
				}
			}
			else 
			{
				risultati.add(new Output(risultato,date,nomeMetodoPost));
				session.setAttribute(nomeSessionList, risultati);
			}
			request.setAttribute("arraylist", risultati);
		}
		catch (NumberFormatException e)
		{
			request.setAttribute(jspParamNameResult, errore);
			//int i = risultati.size(); //inserisco dentro una variabile locale la dimensione dell'arraylist
			String risultato = a+"+"+b+"="+errore;

			if(risultati.size() == 4)
			{	
			//Se l'arraylist Ã¨ piena, elimina il primo elemento,
			risultati.remove(0); 
			risultati.add(new Output(risultato,date,nomeMetodoPost));
			session.setAttribute(nomeSessionList, risultati);
			}
			//dimensione lista <=4
			else
			{
				//inserisco il risultato dentro l'arraylist
				risultati.add(new Output(risultato,date,nomeMetodoPost));

				//rimetto l'arraylist in sessione
				session.setAttribute(nomeSessionList, risultati);
					
			}
				
			request.setAttribute("arraylist", risultati);

		}			
		
	dispatcher.forward(request, response);
	}
}