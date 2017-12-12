package ranking;

import java.util.ArrayList;
import java.util.HashMap;

import core.Result;

public class ResultVoteWeightManager {

	/**
	 * @param args
	 */
	
	/**
	 * @param args
	 */
	
	//search engine results
	ArrayList<Result> SOResults;
	ArrayList<Result> GoogleResults;
	ArrayList<Result> BingResults;
	ArrayList<Result> YahooResults;
	public HashMap<String,Double> AllResults=new HashMap<String,Double>();
	
	//search engine weights
	double SO_Weight=.80;
	double Google_Weight=.90;
	double Bing_Weight=.60;
	double Yahoo_Weight=.60;
	
	public ResultVoteWeightManager(ArrayList<Result> SOResults,ArrayList<Result> GoogleResults, 
			ArrayList<Result> BingResults, ArrayList<Result> YahooResults)
	{
		//assigning the results
		this.SOResults=SOResults;
		this.GoogleResults=GoogleResults;
		this.BingResults=BingResults;
		this.YahooResults=YahooResults;
		
		//adding all URLS
		this.add_to_all_results(this.SOResults);
		this.add_to_all_results(this.GoogleResults);
		this.add_to_all_results(this.BingResults);
		this.add_to_all_results(this.YahooResults);
	}
	
	protected void add_to_all_results(ArrayList<Result> tempList)
	{
		for(int i=0;i<tempList.size();i++)
		{
			Result result=tempList.get(i);
			String resultURL=result.resultURL.trim();
			this.AllResults.put(resultURL.toString(), new Double(0));
		}
	}
	
	protected void update_url_count(ArrayList<Result> tempList)
	{
		//code for updating the URL count
		try
		{
			for(int i=0;i<tempList.size();i++)
			{
				Result result=tempList.get(i);
				String resultURL=result.resultURL;
				String key=resultURL.trim();
				if(AllResults.containsKey(key))
				{
					Double resCount=AllResults.get(key);
					resCount=new Double(resCount.doubleValue()+1);
					AllResults.put(key, resCount);
				}
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	
	public void create_vote_score()
	{
		//update URL count
		update_url_count(this.SOResults);
		update_url_count(this.GoogleResults);
		update_url_count(this.BingResults);
		update_url_count(this.YahooResults);
		//now normalizing the results by 4: we have four 
		double normalizing_constant=4;
		
		for(String key:AllResults.keySet())
		{
			double freq_vote=AllResults.get(key).doubleValue();
			double freq_score=freq_vote/normalizing_constant;
			AllResults.put(key, new Double(freq_score));	
		}
	}
	
	
	public void show_freq_score()
	{
		//code for showing the score
		try
		{
			for(String key:AllResults.keySet())
			{
				System.out.println(key+" >> Score:"+AllResults.get(key));
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
