package servlet;

public class Output {
	
	public String risultato;
	public String data;
	public String metodo;

	public Output(String risultato, String data, String metodo )
	{	
		this.risultato = risultato;
		this.data = data;
		this.metodo = metodo;
	
	}

	public static int conta()
	{
		int i=0;
		
		i++;
		
		return i;
	}
}
