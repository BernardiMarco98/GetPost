package servlet;

public class Somma {
	
	
	
	public int converti(String a) 
	{
		try  
		{
			return Integer.parseInt(a); 
		}
		catch (Exception e)
		{
			return 2147483647;
			
		}
	}
	
	public int addizione (int x, int y)
	
	{
		
		int res=x+y;
		
		
		return res;
	}

}
