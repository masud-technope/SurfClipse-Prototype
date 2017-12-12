package scoring;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import codestack.CodeStackMiner;
import similarity.CosineSimilarityMeasure;
import utility.DownloadResultEntryContent;
import utility.StackTraceUtils;
import core.Result;
import core.StackTrace;
import core.StackTraceElem;

public class ResultStackTraceMatcher {

	public ArrayList<Result> EntryList;
	public String stacktrace;
	HashMap<String, Double> doiMap;
	HashMap<String, StackTraceElem> doiElems;
	public String queryStackTokens;
	double stack_full_matching_weight = 0;
	StackTrace queryStackTraceObj;
	String charset = "UTF-8";

	public ResultStackTraceMatcher(ArrayList<Result> EntryList,
			String querystacktrace) {
		// initialization
		this.EntryList = EntryList;
		try {
			this.stacktrace = URLDecoder.decode(querystacktrace, charset);
			// getting stackTrace Object
			StackTraceUtils utils = new StackTraceUtils(this.stacktrace);
			this.queryStackTraceObj = utils.analyze_stack_trace();
			this.queryStackTokens = this.queryStackTraceObj.stackTraceTokens;

			// DOI: Degree of interest score
			this.doiMap = new HashMap<String, Double>();
			this.doiElems = new HashMap<>();
			this.develop_doi_score();

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultStackTraceMatcher(ArrayList<Result> EntryList) {
		this.EntryList = EntryList;
	}

	protected void develop_doi_score() {
		// code for developing the DOI score
		try {
			ArrayList<StackTraceElem> elems = this.queryStackTraceObj.TraceElems;
			double N = elems.size();
			// creating DOI score
			int index = 0;
			for (StackTraceElem elem : elems) {
				String key = elem.className + "." + elem.methodName;
				double doi_score = 1 - (index / N);
				doiMap.put(key, new Double(doi_score));
				doiElems.put(key, elem);
				this.stack_full_matching_weight += doi_score;
				index++;
			}
		} catch (Exception exc) {
		}
	}

	protected double get_stack_content_matching_score(String candidate_tokens) {
		// code for content matching score
		double content_matching_score = 0;
		CosineSimilarityMeasure cos_measure = new CosineSimilarityMeasure(
				this.queryStackTokens, candidate_tokens);
		content_matching_score = cos_measure.get_cosine_similarity_score(false);
		return content_matching_score;
	}

	protected double get_stack_structural_matching_score(
			ArrayList<StackTraceElem> candidate_elems) {
		// code for getting structural scores
		double structural_score = 0;
		for (StackTraceElem elem : candidate_elems) {
			String formulated_key = elem.className + "." + elem.methodName;
			if (this.doiMap.containsKey(formulated_key)) {
				double starting_score = 0.5;
				StackTraceElem qeleme = this.doiElems.get(formulated_key);
				if (qeleme.packageName.equals(elem.packageName))
					starting_score += .40; // package is more important
				if (qeleme.methodCallLineNumber == elem.methodCallLineNumber)
					starting_score += .10;
				double doi_score = this.doiMap.get(formulated_key);
				double unit_structural_score = starting_score * doi_score;
				structural_score += unit_structural_score;
			}
		}
		// canonicalization
		structural_score = structural_score / this.stack_full_matching_weight;
		return structural_score;
	}

	protected double get_stack_trace_score(StackTrace candidate_stacktrace) {
		// code for getting stack trace score
		double stack_content_score = 0;
		try {
			stack_content_score = get_stack_content_matching_score(candidate_stacktrace.stackTraceTokens);
		} catch (Exception exc) {
		}
		double stack_structural_matching_score = 0;
		try {
			stack_structural_matching_score = get_stack_structural_matching_score(candidate_stacktrace.TraceElems);
		} catch (Exception e) {
			// TODO: handle exception
		}
		double matching_score = (stack_content_score + stack_structural_matching_score) / 2;
		return matching_score;
	}

	public ArrayList<Result> calculate_stacktrace_score() {
		// code for calculating stack trace score
		try {
			// calculating the HashMap values
			for (int i = 0; i < this.EntryList.size(); i++) {
				Result result = (Result) this.EntryList.toArray()[i];
				// collecting the stack trace code
				ArrayList<StackTrace> stacksProcessed = new ArrayList<>();
				stacksProcessed.addAll(result.StacksProcessed);
				double max_stack_matching_score = 0;
				// System.err.println("Processed stack "+stacksProcessed.size()+" for "+result.resultURL);

				if (stacksProcessed.size() > 0) {
					for (StackTrace stack : stacksProcessed) {
						// it is a stack trace
						double match_score = 0;
						try {
							double stack_content_score = get_stack_content_matching_score(stack.stackTraceTokens);
							double stack_structural_matching_score = get_stack_structural_matching_score(stack.TraceElems);
							match_score = (stack_content_score + stack_structural_matching_score) / 2;
							if (match_score > max_stack_matching_score) {
								max_stack_matching_score = match_score;
								result.max_matching_score = max_stack_matching_score;
								result.representativeText = stack.primaryContent;

								// storing context : content & structural
								result.stackTraceContentMatchScore = stack_content_score;
								result.stackTraceStructuralMatchScore = stack_structural_matching_score;
							}
						} catch (Exception exc) {
						}
					}
					result.stackTraceMatchScore = max_stack_matching_score;
				} else {
					// in case the stack traces are not found in those tags
					double match_score = 0;
					match_score = get_stack_content_matching_score(result.textContent);
					max_stack_matching_score = match_score;
					result.stackTraceMatchScore = max_stack_matching_score;
					result.stackTraceContentMatchScore = max_stack_matching_score;
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return this.EntryList;
	}

	protected void show_the_score(ArrayList<Result> results) {
		// code for showing the stack trace score
		for (Result result : results) {
			System.out.println(result.stackTraceMatchScore + " "
					+ result.resultURL);
		}
	}

	protected static String getStackTrace(int key) {
		// code for getting the stack trace
		String stacktrace = new String();
		try {
			String path = StaticData.Lucene_Data_Base + "/strace/" + key
					+ ".txt";
			Scanner scanner = new Scanner(new File(path));
			while (scanner.hasNext()) {
				stacktrace += scanner.nextLine() + "\n";
			}
			scanner.close();
		} catch (Exception exc) {

		}
		return stacktrace;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Result> results = new ArrayList<>();
		Result result = new Result();
		result.title = "java.util.concurrent.ExecutionException: java.lang.OutOfMemoryError: PermGen space";
		result.resultURL = "http://stackoverflow.com/questions/20047152/java-util-concurrent-executionexception-java-lang-outofmemoryerror-permgen-spa";
		results.add(result);

		// download page content
		DownloadResultEntryContent downloader = new DownloadResultEntryContent(
				results);
		results = downloader.download_result_entry_content();

		String searchQuery = "java.util.concurrent.ExecutionException: java.lang.OutOfMemoryError: Java heap space";
		String currentException = "java.util.concurrent.ExecutionException";
		String stacktrace = getStackTrace(5);
		String exceptionName = "java.util.concurrent.ExecutionException";

		ResultTitleMatcher tmatcher = new ResultTitleMatcher(results,
				searchQuery, currentException);
		results = tmatcher.calculate_title_match_score();

		ResultStackTraceMatcher smatcher = new ResultStackTraceMatcher(results,
				stacktrace);
		smatcher.calculate_stacktrace_score();

	}
}
