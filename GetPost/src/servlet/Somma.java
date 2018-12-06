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
			return 10000;
		}
	}
	
	public int addizione (int x, int y)
	
	{
		//int x = Integer.parseInt(a);	
		//int y = Integer.parseInt(b);
		
		//int r=x+y;
		
		int res=x+y;
		
		
		return res;
	}

}
