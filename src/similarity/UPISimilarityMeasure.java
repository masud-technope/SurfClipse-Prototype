package similarity;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import net.barenca.jastyle.ASFormatter;
import net.barenca.jastyle.FormatterHelper;

public class UPISimilarityMeasure {

	/**
	 * @param args
	 */
	
	//member variables
	String code1;
	String code2;
	ArrayList<String> token1List;
	ArrayList<String> token2List;
	ArrayList<String> lcsList;
	
	
	public UPISimilarityMeasure(String query_code1, String candidate_code2)
	{
		//assigning codes
		this.code1=query_code1; //query code
		this.code2=candidate_code2; //code sample from page
		
		//creating array lists
		token1List=new ArrayList<String>();
		token1List=getTokenized(this.code1);
		token2List=new ArrayList<String>();
		token2List=getTokenized(this.code2);
		
		//longest common subsequence
		lcsList=new ArrayList<String>();
		lcsList=get_longest_common_subseq_list();
	}
	
	protected ArrayList<String> get_longest_common_subseq_list()
	{
		//code for getting longest common subsequence list
		LCS lcs=new LCS(this.token1List, this.token2List);
		this.lcsList=lcs.getLCS_Dynamic(this.token1List.size(), this.token2List.size());
		return this.lcsList;
	}
	
	protected static String remove_code_comment(String codeFragment)
	{
		//code for removing code fragment
		String pattern="//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/";
		return codeFragment.replaceAll(pattern, "");
	}
	
	protected static String format_the_code(String codeFragment)
	{
		//code for formatting code fragment
		ASFormatter formatter=new ASFormatter();
		Reader in=new BufferedReader(new StringReader(codeFragment));
		formatter.setJavaStyle();
		String formattedCode=FormatterHelper.format(in, formatter);
		return formattedCode;
	}
	
	protected static ArrayList<String> getTokenized(String code)
	{
		// code for getting tokens of a code fragment
		String tcode=format_the_code(code);
		String fcode=remove_code_comment(tcode);
		StringTokenizer tokenizer = new StringTokenizer(fcode);
		ArrayList<String> tokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			token.trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens;
	}
	
	protected double get_similarity_measure()
	{
		//code for getting similarity score
		double similarity=0;
		double lcs_size=this.lcsList.size();
		int len1=this.token1List.size();
		double unique1=len1-lcs_size;
		double upi1=unique1/len1;
		similarity=1-upi1;
		return similarity;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
