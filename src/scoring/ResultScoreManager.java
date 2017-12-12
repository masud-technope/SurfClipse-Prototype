package scoring;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import utility.DownloadResultEntryContent;
import core.Result;
import core.ScoreWeights;

public class ResultScoreManager {
	ResultTitleMatcher titleMatcher;
	ResultStackTraceMatcher stackTraceMatcher;
	SourceCodeContextMatcher sourcecodeContextMatcher;
	AlexaCompeteScore alexaCompeteScorer;
	SOVoteScore so_vote_Score;
	SEConfidenceScore se_confidence_score;
	DownloadResultEntryContent downloader;
	public ArrayList<Result> Final_Results;

	// other variables
	String stackTrace;
	String queryTitle;
	String code_context;
	HashMap<String, Double> recentPageScores;

	// default constructor
	public ResultScoreManager(ArrayList<Result> myResults) {
		this.Final_Results = myResults;
	}

	// custom constructor
	public ResultScoreManager(ArrayList<Result> myResults, String queryTitle,
			String currentExceptionMessage, String stackTrace,
			String code_context) {
		// initiating the results
		this.Final_Results = myResults;
	}

	public ArrayList<Result> calculate_relative_scores() {

		try {
			alexaCompeteScorer = new AlexaCompeteScore(this.Final_Results);
			this.Final_Results = alexaCompeteScorer
					.get_alexa_compete_relative_rank_score();
			// System.out.println("Alexa Compete score finalized.");
		} catch (Exception e) { // TODO: handle exception e.printStackTrace();
		}

		try {
			// SO vote score
			so_vote_Score = new SOVoteScore(this.Final_Results);
			this.Final_Results = so_vote_Score.get_SO_relative_score();
			// System.out.println("SO Vote score finalized.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		try {
			// SE confidence
			se_confidence_score = new SEConfidenceScore(this.Final_Results);
			this.Final_Results = se_confidence_score
					.get_normalized_confidence();
			// System.out.println("SE Confidence score normalized.");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return this.Final_Results;
	}

	public ArrayList<Result> prerpare_final_score(ArrayList<Result> sorted) {
		// code preparing the final score
		for (Result result : sorted) {
			try {
				// content
				result.content_score = result.title_title_MatchScore
						* ScoreWeights.TITLE_WEIGHT
						+ result.title_description_MatchScore
						* ScoreWeights.DESC_WEIGHT
						+ result.title_codestack_MathScore
						* ScoreWeights.CODESTACK_WEIGHT
						+ result.title_content_MatchScore
						* ScoreWeights.CONTENT_WEIGHT;

				// context
				result.context_score = result.stackTraceContentMatchScore
						* ScoreWeights.CONT_CXT_WEIGHT
						+ result.stackTraceStructuralMatchScore
						* ScoreWeights.STRUCT_CXT_WEIGHT
						+ result.sourceContextMatchScore
						* ScoreWeights.STRUCT_CXT_WEIGHT
						+ result.recentHistoryScore
						* ScoreWeights.HISTORY_CXT_WEIGHT;

				// popularity
				result.popularity_score = result.SOVoteScore
						* ScoreWeights.SOVOTE_WEIGHT
						+ result.AlexaCompeteRankScore
						* ScoreWeights.ALEXA_WEIGHT;

				// different total scores
				result.totalScore_content_context = result.content_score
						* ScoreWeights.CONTENT_RELEVANCE_WEIGHT
						+ result.context_score
						* ScoreWeights.CONTEXT_RELEVANCE_WEIGHT;
				result.totalScore_content_popularity = result.content_score
						* ScoreWeights.CONTENT_RELEVANCE_WEIGHT
						+ result.popularity_score
						* ScoreWeights.POPULARITY_WEIGHT;
				result.totalScore_context_popularity = result.context_score
						* ScoreWeights.CONTEXT_RELEVANCE_WEIGHT
						+ result.popularity_score
						* ScoreWeights.POPULARITY_WEIGHT;

				// final scores
				result.totalScore_content_context_popularity = result.content_score
						* ScoreWeights.CONTENT_RELEVANCE_WEIGHT
						+ result.context_score
						* ScoreWeights.CONTEXT_RELEVANCE_WEIGHT
						+ result.popularity_score
						* ScoreWeights.POPULARITY_WEIGHT
						+ result.search_result_confidence
						* ScoreWeights.CONFIDENCE_WEIGHT;

			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

		// normalize the results
		normalizeResultScores();

		// returning the result
		return sorted;
	}

	protected void normalizeResultScores() {
		// code for normalizing the result scores
		double maxScore = 0;
		double maxContentScore = 0;
		double maxContextScore = 0;
		double maxConfidence = 0;

		for (Result result : this.Final_Results) {
			if (result.totalScore_content_context_popularity > maxScore) {
				maxScore = result.totalScore_content_context_popularity;
			}
			if (result.content_score > maxContentScore) {
				maxContentScore = result.content_score;
			}
			if (result.context_score > maxContextScore) {
				maxContextScore = result.context_score;
			}
			if (result.search_result_confidence > maxConfidence) {
				maxConfidence = result.search_result_confidence;
			}
		}

		// now normalize the scores
		for (Result result : this.Final_Results) {
			result.totalScore_content_context_popularity = result.totalScore_content_context_popularity
					/ maxScore;
			result.content_score = result.content_score / maxContentScore;
			result.context_score = result.context_score / maxContextScore;
			result.search_result_confidence = result.search_result_confidence
					/ maxConfidence;
		}
	}

	public ArrayList<Result> sort_the_result_ct() {
		// code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ct());
		return this.Final_Results;
	}

	public ArrayList<Result> sort_the_result_cx() {
		// code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_cx());
		return this.Final_Results;
	}

	public ArrayList<Result> sort_the_result_ctx() {
		// code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ctx());
		return this.Final_Results;
	}

	public ArrayList<Result> sort_the_result_ctp() {
		// code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_ctp());
		return this.Final_Results;
	}

	public ArrayList<Result> sort_the_result_cxp() {
		// code for sorting the result
		Collections.sort(this.Final_Results, new CustomComparator_cxp());
		return this.Final_Results;
	}

	public ArrayList<Result> sort_the_result_ctxp(ArrayList<Result> finalResults) {
		// code for sorting the result
		Collections.sort(finalResults, new CustomComparator_ctxp());
		return finalResults;
	}

	public class CustomComparator_ct implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			if (o1.content_score > o2.content_score)
				return -1;
			else if (o1.content_score < o2.content_score)
				return 1;
			else
				return 0;
		}
	}

	public class CustomComparator_cx implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			if (o1.context_score > o2.context_score)
				return -1;
			else if (o1.context_score < o2.context_score)
				return 1;
			else
				return 0;
		}
	}

	public class CustomComparator_ctx implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			if (o1.totalScore_content_context > o2.totalScore_content_context)
				return -1;
			else if (o1.totalScore_content_context < o2.totalScore_content_context)
				return 1;
			else
				return 0;
		}
	}

	public class CustomComparator_ctp implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			if (o1.totalScore_content_popularity > o2.totalScore_content_popularity)
				return -1;
			else if (o1.totalScore_content_popularity < o2.totalScore_content_popularity)
				return 1;
			else
				return 0;
		}
	}

	public class CustomComparator_cxp implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			if (o1.totalScore_context_popularity > o2.totalScore_context_popularity)
				return -1;
			else if (o1.totalScore_context_popularity < o2.totalScore_context_popularity)
				return 1;
			else
				return 0;
		}
	}

	public class CustomComparator_ctxp implements Comparator<Result> {
		@Override
		public int compare(Result o1, Result o2) {
			Double score1 = new Double(o1.totalScore_content_context_popularity);
			Double score2 = new Double(o2.totalScore_content_context_popularity);
			return score2.compareTo(score1);
		}
	}

	protected double format_the_double(double unformatted) {
		// code for formatting the double number
		double formattedNumber = 0;
		try {
			formattedNumber = Double.parseDouble(new DecimalFormat("#.####")
					.format(unformatted));
		} catch (Exception exc) {
		}
		return formattedNumber;
	}

	public void save_the_result_score_ctxp(String fileName) {
		// code for saving the component scores
		try {
			String baseFolder = StaticData.Base_Directory + "/results/ctxp";
			FileWriter writer = new FileWriter(new File(baseFolder + "/"
					+ fileName));
			String header = "TotalScore\tcontent\tcontext\tpopularity\tURL \n";
			writer.write(header);
			int count = 0;
			for (Result result : this.Final_Results) {
				String line = format_the_double(result.totalScore_content_context_popularity)
						+ "";
				line += "\t" + format_the_double(result.content_score);
				line += "\t" + format_the_double(result.context_score);
				line += "\t" + format_the_double(result.popularity_score);
				line += "\t" + result.resultURL + "\n";
				writer.write(line);
				count++;
				// if(count==20)break;
			}
			writer.close();
			System.out.println("Scores (ctxp) saved successfully");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
}
