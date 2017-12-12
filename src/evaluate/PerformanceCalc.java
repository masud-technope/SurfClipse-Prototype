package evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import utility.ContentLoader;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class PerformanceCalc {

	int caseSize = 0;
	String goldsetFolder;
	String resultFolder;
	int TOPK = 10;
	HashMap<Integer, ArrayList<String>> resultMap;

	public PerformanceCalc(int caseSize, String goldsetFolder,
			String resultFolder, int TOPK) {
		this.caseSize = caseSize;
		this.goldsetFolder = StaticData.Lucene_Data_Base + "/" + goldsetFolder;
		this.resultFolder = StaticData.Lucene_Data_Base + "/" + resultFolder;
		this.resultMap = new HashMap<>();
		this.loadResultMap();
	}

	protected void loadResultMap() {
		for (int caseID = 1; caseID <= caseSize; caseID++) {
			ArrayList<String> result = getResults(caseID);
			this.resultMap.put(caseID, result);
		}
	}

	protected ArrayList<String> getGoldset(int caseID) {
		String goldFile = this.goldsetFolder + "/" + caseID + ".txt";
		String[] lines = ContentLoader.getAllLines(goldFile);
		ArrayList<String> temp = new ArrayList<>();
		for (int i = 2; i < lines.length; i++) {
			temp.add(lines[i]);
		}
		return temp;
	}

	protected ArrayList<String> getResults(int caseID) {
		String resultFile = this.resultFolder + "/" + caseID + ".txt";
		ArrayList<String> temp = new ArrayList<>();
		String[] lines = ContentLoader.getAllLines(resultFile);
		for (int i = 0; i < lines.length; i++) {
			String resultURL = lines[i].split("\\s+")[0].trim();
			temp.add(resultURL);
			if (temp.size() == TOPK)
				break;
		}
		return temp;
	}

	public void calculatePerformance() {

		int correct = 0;
		double sumPrec = 0;
		double sumRec = 0;
		double sumRR = 0;

		for (int caseID : this.resultMap.keySet()) {
			ArrayList<String> recommended = this.resultMap.get(caseID);
			ArrayList<String> goldset = getGoldset(caseID);
			double precision = getPrecision(recommended, goldset, TOPK);
			sumPrec += precision;
			if (precision > 0) {
				correct++;
			}
			double recall = getRecall(recommended, goldset, TOPK);
			sumRec += recall;
			double recrank = getReciprocalRank(recommended, goldset, TOPK);
			sumRR += recrank;
		}

		System.out.println("Top-" + TOPK + " Accuracy:" + (double) correct
				/ this.resultMap.size());
		System.out.println("Mean Reciprocal Rank@" + TOPK + ": " + sumRR
				/ this.resultMap.size());
		System.out.println("Mean Precision@" + TOPK + ": " + sumPrec
				/ this.resultMap.size());
		System.out.println("Mean Recall@" + TOPK + ": " + sumRec
				/ this.resultMap.size());
	}

	protected boolean doesContain(ArrayList<String> goldset, String target) {
		boolean found = false;
		for (String goldFile : goldset) {
			if (goldFile.endsWith(target) || target.endsWith(goldFile)) {
				return true;
			}
		}
		return found;
	}

	protected double getPrecision(ArrayList<String> recommended,
			ArrayList<String> gold, int topk) {
		double found = 0;
		int tcount = 0;
		for (String rev : recommended) {
			if (doesContain(gold, rev)) {
				found++;
			}
			tcount++;
			if (tcount == topk)
				break;
		}
		// returning precision
		return (double) found / tcount;
	}

	protected double getRecall(ArrayList<String> recommended,
			ArrayList<String> gold, int topk) {
		double found = 0;
		int tcount = 0;
		for (String rev : recommended) {
			if (doesContain(gold, rev)) {
				found++;
			}
			tcount++;
			if (tcount == topk)
				break;
		}
		// returning precision
		return (double) found / gold.size();
	}

	protected double getReciprocalRank(ArrayList<String> recommended,
			ArrayList<String> gold, int topk) {
		double found = 0;
		for (String rev : recommended) {
			found++;
			if (doesContain(gold, rev)) {
				break;
			}
		}
		return found > 0 ? (1 / found) : 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int caseSize = 83;
		String resultFolder = "search-output";
		String goldsetFolder = "solution";
		int topk = 30;
		new PerformanceCalc(caseSize, goldsetFolder, resultFolder, topk)
				.calculatePerformance();
	}
}
