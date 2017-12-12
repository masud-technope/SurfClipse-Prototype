package scoring;

import java.util.ArrayList;
import java.util.HashMap;

import core.Result;

public class HistoryRecencyScore {

	public ArrayList<Result> EntryList;
	HashMap<String, Double> historyScore;
	
	public HistoryRecencyScore(ArrayList<Result> EntryList, String recentPageData)
	{
		this.EntryList=EntryList;
		this.historyScore=this.develop_history_score_hmap(recentPageData);
	}
	
	protected HashMap<String,Double> develop_history_score_hmap(String recentPageData)
	{
		// code for developing the Hash Map
		HashMap<String, Double> myhmap = new HashMap<String, Double>();
		String[] entries = recentPageData.split(",");
		for (String entry : entries) {
			try
			{
			String parts[] = entry.trim().split("=");
			String key = parts[0];
			Double scoreVal = new Double(parts[1]);
			myhmap.put(key, scoreVal);
			}catch(Exception exc){
			}
		}
		return myhmap;
	}
	
	public ArrayList<Result> load_history_scores()
	{
		//code for loading the history scores
		for(Result result:this.EntryList)
		{
			String resultURL=result.resultURL;
			if(this.historyScore.containsKey(resultURL))
			{
				result.recentHistoryScore=this.historyScore.get(resultURL).doubleValue();
			}
		}
		return this.EntryList;
	}
	
	
	
}
