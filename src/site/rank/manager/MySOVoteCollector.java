package site.rank.manager;

import java.util.ArrayList;
import java.util.HashMap;
import scoring.SOVoteScore;
import utility.ContentLoader;
import utility.ContentWriter;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class MySOVoteCollector {

	int caseID = 0;
	HashMap<Integer, String> urlMap;

	public MySOVoteCollector(int caseID) {
		this.caseID = caseID;
	}

	protected HashMap<Integer, String> extractURLs(int caseID) {
		// extracting URLs
		String urlFile = StaticData.Lucene_Data_Base + "/sclipse/" + caseID
				+ ".txt";
		String[] lines = ContentLoader.getAllLines(urlFile);
		HashMap<Integer, String> temp = new HashMap<>();
		for (int index = 0; index < lines.length; index++) {
			temp.put(index, lines[index]);
		}
		return temp;
	}

	protected void collectSOVotes() {
		this.urlMap = extractURLs(caseID);
		ArrayList<String> lines = new ArrayList<>();
		for (int key : this.urlMap.keySet()) {
			String myURL = this.urlMap.get(key);
			SOVoteScore svColl = new SOVoteScore();
			long votes = svColl.collect_SO_votes(myURL);
			lines.add(key + ":" + votes);
		}
		String outFile = StaticData.Lucene_Data_Base + "/so-vote/" + caseID
				+ ".txt";
		ContentWriter.writeContent(outFile, lines);
		System.out.println("Done:" + caseID);
	}

	public HashMap<Integer, Integer> loadSOVotes() {
		// loading the site ranks
		String alexaRankFile = StaticData.Lucene_Data_Base + "/so-vote/"
				+ caseID + ".txt";
		String[] lines = ContentLoader.getAllLines(alexaRankFile);
		HashMap<Integer, Integer> temp = new HashMap<>();
		for (String line : lines) {
			String[] part = line.split(":");
			if (part.length == 2) {
				int index = Integer.parseInt(part[0].trim());
				int rank = Integer.parseInt(part[1].trim());
				temp.put(index, rank);
			}
		}
		return temp;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		for(int caseID=7;caseID<=15;caseID++){
			new MySOVoteCollector(caseID).collectSOVotes();
		}
	}
}
