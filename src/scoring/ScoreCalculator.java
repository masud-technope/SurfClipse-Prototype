package scoring;

import java.util.ArrayList;

import codestack.MyExceptionManager;

import utility.DownloadResultEntryContent;

import core.Result;

public class ScoreCalculator implements Runnable {

	ArrayList<Result> segmentedResults;
	ResultTitleMatcher titleMatcher;
	ResultStackTraceMatcher stackTraceMatcher;
	SourceCodeContextMatcher sourcecodeContextMatcher;
	AlexaCompeteScore alexaCompeteScorer;
	SOVoteScore so_vote_Score;
	HistoryRecencyScore historyRecencyScore;
	DownloadResultEntryContent downloader;
	// public ArrayList<Result> segmentedResults;

	// other variables
	String stackTrace;
	String queryTitle;
	String code_context;
	String recentPageData;

	
	public ScoreCalculator(ArrayList<Result> myResults, String queryTitle,
			String stackTrace, String code_context) {
		// assigning required variables
		this.segmentedResults = myResults;
		this.stackTrace = stackTrace;
		this.queryTitle = queryTitle;
		this.code_context = code_context;
	}
	
	public ScoreCalculator(ArrayList<Result> myResults, String queryTitle,
			String stackTrace, String code_context, String recentPageData) {
		// assigning required variables
		this.segmentedResults = myResults;
		this.stackTrace = stackTrace;
		this.queryTitle = queryTitle;
		this.code_context = code_context;
		this.recentPageData = recentPageData;
	}

	public void run() {
		// code for executing the threads
		calculate_intermediate_scores();
	}

	public ArrayList<Result> get_computed_results() {
		// code for getting computed results
		return this.segmentedResults;
	}

	public ArrayList<Result> calculate_intermediate_scores() {
		// content similarity matching
		try {
			String currentException = MyExceptionManager
					.getCurrentExceptionName(this.stackTrace);
			titleMatcher = new ResultTitleMatcher(this.segmentedResults,
					queryTitle, currentException);
			this.segmentedResults = titleMatcher.calculate_title_match_score();
			// System.out.println("Title matching score done by"+Thread.currentThread().getName());
		} catch (Exception e) {
			System.err.println("Exception thrown by TitleMatcher:"
					+ e.getMessage());
			e.printStackTrace();
		}

		// context similarity matching
		try {
			if (stackTrace != null && !stackTrace.isEmpty()) {
				// stack trace matching
				stackTraceMatcher = new ResultStackTraceMatcher(
						this.segmentedResults, stackTrace);
				this.segmentedResults = stackTraceMatcher
						.calculate_stacktrace_score();
				// System.out.println("Stack Trace matching score done by"+Thread.currentThread().getName());
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Exception thrown by StackTraceMatcher:"
					+ e.getMessage());
			e.printStackTrace();
		}

		try {
			if (code_context != null && !code_context.isEmpty()) {
				// source code context matching
				sourcecodeContextMatcher = new SourceCodeContextMatcher(
						this.segmentedResults, code_context);
				this.segmentedResults = sourcecodeContextMatcher
						.calculate_codecontext_score();
				// System.out.println("Source code context score done by"+Thread.currentThread().getName());
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Exception thrown by CodeContextMatcher:"
					+ e.getMessage());
			e.printStackTrace();
		}

		try {
			//so_vote_Score = new SOVoteScore(this.segmentedResults);
			//this.segmentedResults = so_vote_Score.get_SO_vote_score();
			// System.out.println("SO Vote score done by"+Thread.currentThread().getName());
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Exception thrown by SO Vote scorer:"
					+ e.getMessage());
			e.printStackTrace();
		}
		// returning the array list
		return this.segmentedResults;

	}
}
