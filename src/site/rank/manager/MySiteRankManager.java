package site.rank.manager;

import java.util.ArrayList;
import java.util.HashMap;
import scoring.AlexaCompeteScore;
import utility.ContentLoader;
import utility.ContentWriter;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class MySiteRankManager {

	int caseID = 0;
	HashMap<Integer, String> urlMap;

	public MySiteRankManager(int caseID) {
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

	protected void collectRanks() {
		this.urlMap = extractURLs(caseID);
		ArrayList<String> lines = new ArrayList<>();
		for (int key : this.urlMap.keySet()) {
			String myURL = this.urlMap.get(key);
			AlexaCompeteScore acScore = new AlexaCompeteScore();
			lines.add(key + ":" + acScore.provide_alexa_rank_xml(myURL));
		}
		String outFile = StaticData.Lucene_Data_Base + "/site-rank/" + caseID
				+ ".txt";
		ContentWriter.writeContent(outFile, lines);
		System.out.println("Done:" + caseID);
	}

	public HashMap<Integer, Integer> loadSiteRanks() {
		// loading the site ranks
		String alexaRankFile = StaticData.Lucene_Data_Base + "/site-rank/"
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
		for (int caseID = 1; caseID <= 150; caseID++) {
			// int caseID = 83;
			new MySiteRankManager(caseID).collectRanks();
		}
	}
}
