package servlet;

public class Output {
	
	public String data;
	public String metodo;
	public String risultato;
	
	public Output(String ris, String dat, String met )
	{
		this.data = dat;
		this.metodo = met;
		this.risultato = ris;
		
	}

	public static int conta()
	{
		int i=0;
		
		i++;
		
		return i;
	}
}
