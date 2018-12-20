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

	public String getRisultato() {
		return risultato;
	}

	public void setRisultato(String risultato) {
		this.risultato = risultato;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}
	
	
}
