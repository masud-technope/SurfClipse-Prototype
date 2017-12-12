package scoring;

import java.util.ArrayList;

import core.Result;

public class SEConfidenceScore {
	
	public ArrayList<Result> EntryList;
	public SEConfidenceScore(ArrayList<Result> EntryList)
	{
		this.EntryList=EntryList;
	}
	
	
	public ArrayList<Result> get_normalized_confidence()
	{
		//code for getting normalized confidence
		double max_confidence=0;
		for(Result result:this.EntryList)
		{
			if(result.search_result_confidence>max_confidence)
				max_confidence=result.search_result_confidence;
		}
		//now do the normalization
		for(Result result:EntryList)
		{
			double confidence=result.search_result_confidence;
			result.search_result_confidence=confidence/max_confidence;
		}
		return this.EntryList;
	}
	
	

}
