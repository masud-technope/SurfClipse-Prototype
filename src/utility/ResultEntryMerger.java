package utility;
import java.util.ArrayList;
import java.util.HashMap;
import core.Result;
import core.SurfClipseSearch;


public class ResultEntryMerger {

	/**
	 * @param args
	 */
	ArrayList<Result> Google_Results;
	ArrayList<Result> Bing_Results;
	ArrayList<Result> Yahoo_Results;
	ArrayList<Result> SO_Results;
	ArrayList<Result> Results_Collection;
	
	//search engine weights
	double google_sew_score= 0.29; //0.41
	double bing_sew_score=0.34;// 0.30;
	double yahoo_sew_score=0.36;// 0.29;
	double stackoverflow_sew_score=1.00;
	
	//result to incorporate
	int max_result_limit;
	
	
	public ResultEntryMerger(ArrayList<Result> Google_Results,ArrayList<Result> Bing_Results,
	ArrayList<Result> Yahoo_Results,ArrayList<Result> SO_Results)
	{
		//code for merging the result entries
		this.Google_Results=Google_Results;
		this.Bing_Results=Bing_Results;
		this.Yahoo_Results=Yahoo_Results;
		this.SO_Results=SO_Results;
		this.Results_Collection=new ArrayList<Result>();
		max_result_limit=30;
	}
	
	public ArrayList<Result> merge() {
		
		// code for merging the results
		HashMap<String, Result> urlmaps = new HashMap<String,Result>();
		
		// adding google results
		int desired_size=0;
		try
		{
		desired_size=Google_Results.size()<max_result_limit?Google_Results.size():max_result_limit;
		for (int i = 0; i <desired_size /*Google_Results.size()*/; i++) {
			Result result = (Result)Google_Results.get(i);
			String resultURL = result.resultURL;
			if(!urlmaps.containsKey(resultURL))
			{
				//adding confidence score
				result.search_result_confidence+=google_sew_score;
				//adding the position
				result.googleRank=(i+1);
				result.frequency++;
				//adding to Hash Map
				urlmaps.put(resultURL.trim(), result);
			}
		}
		}catch(Exception exc){}
		
		// adding bing results
		try
		{
		desired_size=Bing_Results.size()<max_result_limit?Bing_Results.size():max_result_limit;
		for (int i = 0; i <desired_size /*Bing_Results.size()*/; i++) {
			Result result = Bing_Results.get(i);
			String resultURL = result.resultURL;
			
			if(!urlmaps.containsKey(resultURL))
			{
				//adding confidence
				result.search_result_confidence+=bing_sew_score;
				//adding position
				result.BingRank=(i+1);
				result.frequency++;
				//add to hash map
				urlmaps.put(resultURL.trim(), result);
			}else{
				Result tempResult=urlmaps.get(resultURL);
				tempResult.search_result_confidence+=bing_sew_score;
				result.BingRank=(i+1);
				result.frequency++;
				urlmaps.put(resultURL,tempResult);
			}
		}
		}catch(Exception exc){}
		// adding Yahoo results
		try
		{
		desired_size=Yahoo_Results.size()<max_result_limit?Yahoo_Results.size():max_result_limit;
		for (int i = 0; i <desired_size /*Yahoo_Results.size()*/; i++) {
			Result result = Yahoo_Results.get(i);
			String resultURL = result.resultURL;
			if(!urlmaps.containsKey(resultURL))
			{
				//adding confidence
				result.search_result_confidence+=yahoo_sew_score;
				//adding yahoo rank
				result.YahooRank=(i+1);
				result.frequency++;
				//add to hash map
				urlmaps.put(resultURL.trim(), result);
			}else
			{
				Result tempResult=urlmaps.get(resultURL);
				tempResult.search_result_confidence+=yahoo_sew_score;
				result.YahooRank=(i+1);
				result.frequency++;
				urlmaps.put(resultURL,tempResult);
			}
		}
		}catch(Exception exc){}
		// adding SO results
		try{
		desired_size=SO_Results.size()<max_result_limit?SO_Results.size():max_result_limit;
		for (int i = 0; i < desired_size; i++) {
			Result result = SO_Results.get(i);
			String resultURL = result.resultURL;
			if(!urlmaps.containsKey(resultURL))
			{
				//adding confidence
				result.search_result_confidence+=stackoverflow_sew_score;
				//adding position
				result.SORank=(i+1);
				result.frequency++;
				//add to hash map
				urlmaps.put(resultURL.trim(), result);
			}else
			{
				Result tempResult=urlmaps.get(resultURL);
				tempResult.search_result_confidence+=stackoverflow_sew_score;
				result.SORank=(i+1);
				result.frequency++;
				urlmaps.put(resultURL,tempResult);
			}
		}
		}catch(Exception exc){}
		//now we have got a unique set of URLS and corresponding results
		for(String key:urlmaps.keySet())
		{
			Result result=urlmaps.get(key);
			//calculate the average position
			result.avgSearchRank=((double)(result.googleRank+result.BingRank+result.YahooRank+result.SORank))/result.frequency;
			this.Results_Collection.add(result);
			System.out.println(result.resultURL);
		}
		System.out.println("Total unique results:"+this.Results_Collection.size());
		return this.Results_Collection;
	}
	
	
	protected void show_distinct_results()
	{
		//code for showing distinct items
		for(int i=0;i<this.Results_Collection.size();i++)
		{
			Result result=(Result)this.Results_Collection.toArray()[i];
			//System.out.println(result.resultURL+" "+result.topTenScore);
		}
		System.out.println("Total results: "+this.Results_Collection.size());
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try
		{
			String query="Invalid preference page path: XML Syntax";
			SurfClipseSearch search=new SurfClipseSearch(query);
			ResultEntryMerger entryManager=new ResultEntryMerger(search.Google_Results, 
					search.Bing_Results, search.Yahoo_Results, search.SO_Results);
			entryManager.merge();
			entryManager.show_distinct_results();
			
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
}
