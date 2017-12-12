package scoring;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import UnZip.UnZipper;
import core.Result;

public class SOVoteScore {

	public ArrayList<Result> EntryList;

	public SOVoteScore(ArrayList<Result> EntryList) {
		// assigning the entry list
		this.EntryList = EntryList;
	}

	public SOVoteScore() {
		// default constructor
	}

	public ArrayList<Result> get_SO_vote_score() {
		// code for getting SO vote score
		for (Result result : this.EntryList) {
			// check if the vote collected or not.
			result.SOVoteScore = collect_SO_votes(result.resultURL);
		}
		return this.EntryList;
	}

	public ArrayList<Result> get_SO_relative_score() {
		// code for getting relative SO scores
		double max_vote = 0;
		for (Result result : this.EntryList) {
			if (result.SOVoteScore > max_vote)
				max_vote = result.SOVoteScore;
		}
		// System.out.println("Max SO Vote score:" + max_vote);
		if (max_vote > 0) {
			for (Result result : this.EntryList) {
				double vote_score = result.SOVoteScore / max_vote;
				result.SOVoteScore = vote_score;
			}
		}
		return this.EntryList;
	}

	protected long get_question_ID(String resultURL) {
		int number = 0;
		if (!resultURL.contains("stackoverflow.com"))
			return 0;
		// code for getting question id
		// resultURL="http://stackoverflow.com/questions/17101138/how-would-i-fix-this-error-java-lang-object-cannot-be-cast-to-java-lang-string";
		int end_limit = resultURL.lastIndexOf('/');
		String tempStr = resultURL.substring(0, end_limit);
		int end_limit2 = tempStr.lastIndexOf('/');
		// String questIDStr = tempStr.substring(end_limit2 + 1, end_limit);
		try {
			number = Integer.parseInt(tempStr.substring(end_limit2 + 1,
					end_limit));
		} catch (Exception exc) {
		}
		// System.out.println(questIDStr);
		return number;
	}

	protected long collect_question_score(long questionID) {
		// code for collecting question score
		long questionScore = 0;
		try {
			String targetURL = "https://api.stackexchange.com/2.1/questions/"
					+ questionID
					+ "?order=desc&sort=activity&site=stackoverflow";
			URL url = new URL(targetURL);
			InputStream response = url.openStream();

			UnZipper unzipper = new UnZipper();
			String responseStr = unzipper.unzip_the_response(response);
			// System.out.println(responseStr);
			JSONParser parser = new JSONParser();
			Object wholeObj = parser.parse(responseStr);

			Object items = ((JSONObject) wholeObj).get("items");
			Object item = ((JSONArray) items).get(0);
			String rankStr = ((JSONObject) item).get("score").toString();
			questionScore = Long.parseLong(rankStr);

		} catch (Exception exc) {
		}
		return questionScore;
	}

	protected long collect_answer_score(long questionID) {
		// code for collecting answer score
		long answerScore = 0;
		try {
			String targetURL = "https://api.stackexchange.com/2.1/questions/"
					+ questionID + "/answers"
					+ "?order=desc&sort=activity&site=stackoverflow";
			URL url = new URL(targetURL);
			InputStream response = url.openStream();

			UnZipper unzipper = new UnZipper();
			String responseStr = unzipper.unzip_the_response(response);
			// System.out.println(responseStr);
			JSONParser parser = new JSONParser();
			Object wholeObj = parser.parse(responseStr);

			Object items = ((JSONObject) wholeObj).get("items");
			JSONArray jitems = (JSONArray) items;
			for (Object obj : jitems) {
				String ansScore = ((JSONObject) obj).get("score").toString();
				try {
					long _ansScore = Long.parseLong(ansScore);
					answerScore += _ansScore;
				} catch (Exception exc) {
				}
			}

		} catch (Exception exc) {
		}
		return answerScore;
	}

	public long collect_SO_votes(String resultURL) {
		// code for collecting SO information
		long total_votes = 0;
		long questionID = get_question_ID(resultURL);
		if (questionID == 0)
			return 0; // non-StackOverflow site
		try {
			long questionVotes = collect_question_score(questionID);
			long answerVotes = collect_answer_score(questionID);
			total_votes = questionVotes + answerVotes;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return total_votes;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SOVoteScore svscore = new SOVoteScore(new ArrayList<Result>());
		// svscore.get_question_ID("some url");
		String resultURL = "http://stackoverflow.com/questions/20047152/java-util-concurrent-executionexception-java-lang-outofmemoryerror-permgen-spa";
		long votes = svscore.collect_SO_votes(resultURL);
		System.out.println(votes);

	}

}
