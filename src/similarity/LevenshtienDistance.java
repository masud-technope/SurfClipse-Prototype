package similarity;

public class LevenshtienDistance {

	/**
	 * @param args
	 */
	int cost=0;
	public LevenshtienDistance()
	{
		//default constructor
	}
	
	protected int getLevenshteinDistance(String s, String t)
	{
	  int len_s = s.length();
	  int len_t = t.length();
	  
	  /* test for degenerate cases of empty strings */
	  if (len_s == 0) return len_t;
	  if (len_t == 0) return len_s;
	 
	  /* test if last characters of the strings match */
	  if (s.charAt(len_s-1) == t.charAt(len_t-1)) cost = 0;
	  else cost = 1;
	 
	  /* return minimum of delete char from s, delete char from t, and delete char from both */
	  return minimum(getLevenshteinDistance(s.substring(0,len_s-1), t) + 1,getLevenshteinDistance(s, t.substring(0,len_t-1)) + 1,getLevenshteinDistance(s.substring(0,len_s-1), t.substring(0,len_t-1) + cost));
	}
	
	protected int minimum(int a, int b, int c)
	{
		int min=a;
		if(b<min)min=b;
		if(c<min)min=c;
		return min;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LevenshtienDistance ldist=new LevenshtienDistance();
		String str1="Hello World";
		String str2="Hel84lo wworkd2";
		System.out.println("Leveneshtien distance:"+ldist.getLevenshteinDistance(str1, str2));
	}

}
