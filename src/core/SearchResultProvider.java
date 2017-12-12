package core;

import indexmanager.SearchResultIndexManager;
import java.util.ArrayList;
import java.util.HashMap;
import scoring.ResultScoreManager;
import scoring.ScoreCalculator;
import site.rank.manager.MySiteRankManager;

public class SearchResultProvider {

	SurfClipseSearch search;
	String searchQuery;
	String stackTrace;
	String sourceCodeContext;
	String recentPageData;
	public int currentException = 0;

	public SearchResultProvider() {
		// default constructor
	}

	public SearchResultProvider(int caseID, String searchQuery,
			String stackTrace, String sourceCodeContext) {
		this.currentException = caseID;
		this.searchQuery = searchQuery;
		this.stackTrace = stackTrace;
		this.sourceCodeContext = sourceCodeContext;
		this.search = new SurfClipseSearch();
	}

	public SearchResultProvider(String searchQuery, String stackTrace,
			String sourceCodeContext, String recentPageData) {
		// code for providing the search results
		this.searchQuery = searchQuery;
		this.stackTrace = stackTrace;
		this.sourceCodeContext = sourceCodeContext;
		this.recentPageData = recentPageData;
		this.search = new SurfClipseSearch();
	}

	public ArrayList<Result> provide_the_final_results() {
		ArrayList<Result> sorted = new ArrayList<>();
		search.my_big_array = SearchResultIndexManager
				.load_sresult_index(currentException);
		this.attachAlexaRanks(currentException);
		search.my_big_array = perform_parallel_score_computation(search);
		try {
			ResultScoreManager manager = new ResultScoreManager(
					search.my_big_array);
			ArrayList<Result> finalRes = manager.calculate_relative_scores();
			finalRes = manager.prerpare_final_score(finalRes);
			sorted = manager.sort_the_result_ctxp(finalRes);
			
		} catch (Exception exc) {
			System.out.println("Final score calculation failed."
					+ exc.getStackTrace());
		}
		return sorted;
	}

	protected void attachAlexaRanks(int caseID) {
		HashMap<Integer, Integer> alexaMap = new MySiteRankManager(caseID)
				.loadSiteRanks();
		for (int i = 0; i < search.my_big_array.size(); i++) {
			if (alexaMap.containsKey(i)) {
				int rank = alexaMap.get(i);
				Result result = search.my_big_array.get(i);
				result.alexaRank = rank;
			}
		}
	}

	protected ArrayList<Result> provide_segmented_links(
			ArrayList<Result> totalLinks, int index, int stepSize) {
		ArrayList<Result> tempList = new ArrayList<Result>();
		int endIndex = index + stepSize;

		if (endIndex > totalLinks.size())
			endIndex = totalLinks.size();
		for (int i = index; i < endIndex; i++) {
			Result result = totalLinks.get(i);
			tempList.add(result);
		}
		return tempList;
	}

	protected ArrayList<Result> perform_parallel_score_computation(
			SurfClipseSearch search) {
		// master result list
		ArrayList<Result> masterList = new ArrayList<Result>();

		// now perform the parallel task on the search operations
		int number_of_processors = 10;// Runtime.getRuntime().availableProcessors();

		int stepSize = 0;

		double _stepsize = (double) search.my_big_array.size()
				/ number_of_processors;

		stepSize = (int) Math.ceil(_stepsize);
		if (stepSize <= 1) {
			stepSize = search.my_big_array.size() % number_of_processors;
			ScoreCalculator scal = new ScoreCalculator(search.my_big_array,
					this.searchQuery, this.stackTrace, this.sourceCodeContext);
			masterList.addAll(scal.get_computed_results());
			return masterList;
		}

		ArrayList<Thread> myThreads = new ArrayList<Thread>();
		ArrayList<ScoreCalculator> scals = new ArrayList<ScoreCalculator>();

		// parallelize the score computation
		for (int i = 0; i < number_of_processors; i++) {
			ArrayList<Result> tempList = provide_segmented_links(
					search.my_big_array, i * stepSize, stepSize);
			ScoreCalculator scal = new ScoreCalculator(tempList,
					this.searchQuery, this.stackTrace, this.sourceCodeContext);
			Runnable runnable = scal;
			Thread t = new Thread(runnable);

			t.setName("Thread: #" + i);
			myThreads.add(t);
			scals.add(scal);
			t.setPriority(Thread.MAX_PRIORITY);
			t.start();
		}

		// checking the thread status and collecting results
		int running = number_of_processors;
		while (running > 0) {
			for (int k = 0; k < myThreads.size(); k++) {
				Thread t1 = myThreads.get(k);
				if (!t1.isAlive()) {
					ScoreCalculator scal1 = (ScoreCalculator) scals.get(k);
					masterList.addAll(scal1.get_computed_results());
					myThreads.remove(k);
					scals.remove(k);
					running--;
				}
			}
		}
		return masterList;
	}
}
