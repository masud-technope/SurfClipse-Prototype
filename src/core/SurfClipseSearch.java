package core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ranking.ResultVoteWeightManager;

public class SurfClipseSearch {

	// search engine results
	public ArrayList<Result> SO_Results;
	public ArrayList<Result> Google_Results;
	public ArrayList<Result> Bing_Results;
	public ArrayList<Result> Yahoo_Results;

	// vote score
	HashMap<String, Double> voteScores = new HashMap<String, Double>();
	public ArrayList<Result> my_big_array = null;

	String stacktrace;
	String codecontext;
	public String searchQuery;

	public SurfClipseSearch() {
		// initialization
		my_big_array = new ArrayList<Result>();
	}

	public SurfClipseSearch(String searchQuery) {
		this.searchQuery = searchQuery;
		my_big_array = new ArrayList<>();
	}

	@Deprecated
	protected void get_result_vote_score() {
		// code for getting result vote score
		try {
			ResultVoteWeightManager voteManager = new ResultVoteWeightManager(
					this.SO_Results, this.Google_Results, this.Bing_Results,
					this.Yahoo_Results);
			voteManager.create_vote_score();
			this.voteScores = voteManager.AllResults;
			// showing the scores
			voteManager.show_freq_score();
		} catch (Exception exc) {

		}
	}

	protected ArrayList<Map.Entry<String, Integer>> sort_search_results(
			HashMap<String, Integer> searchMap) {
		// code for sorting the search results
		ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(
				searchMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
					Map.Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list;
	}

	@Deprecated
	public void show_the_search_results(ArrayList<Result> my_big_array) {
		try {
			HashMap<String, Integer> myurls = new HashMap<String, Integer>();
			for (int i = 0; i < my_big_array.size(); i++) {
				Result resultEntry = (Result) my_big_array.toArray()[i];
				String postUrl = resultEntry.resultURL;
				if (myurls.containsKey(postUrl)) {
					// updating existing weights
					int curr_score = myurls.get(postUrl).intValue();
					curr_score++;
					myurls.put(postUrl, new Integer(curr_score));
				} else {
					// adding the first weight
					myurls.put(postUrl, new Integer(1));
				}
			}
			// sorting the results
			ArrayList<Map.Entry<String, Integer>> sorted = sort_search_results(myurls);
			Iterator<Map.Entry<String, Integer>> iter1 = sorted.iterator();
			while (iter1.hasNext()) {
				Map.Entry<String, Integer> mapEntry = iter1.next();
				System.out.println(mapEntry.getKey());
				System.out.println(mapEntry.getValue());
				System.out.println();
			}
		} catch (Exception exc) {
		}
	}

}
