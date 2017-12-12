package indexmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import core.Result;

public class SResultScoreManager {

	public static void saveResultScores(int key, ArrayList<Result> finalResults) {
		// saving individual result scores
		String serFile = StaticData.Lucene_Data_Base
				+ "/completeds/sclipseScore/" + key + ".ser";
		try {
			FileOutputStream fos = new FileOutputStream(new File(serFile));
			ObjectOutputStream ostream = new ObjectOutputStream(fos);
			ostream.writeInt(finalResults.size());
			for (Result result : finalResults) {
				ostream.writeObject(result);
			}
			ostream.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected static ArrayList<Result> loadResultScores(int key) {
		// loading result scores
		String serFile = StaticData.Lucene_Data_Base
				+ "/completeds/sclipseScore/" + key + ".ser";
		ArrayList<Result> finalResults = new ArrayList<>();
		try {
			FileInputStream fstream = new FileInputStream(new File(serFile));
			ObjectInputStream oistream = new ObjectInputStream(fstream);
			int size = oistream.readInt();
			for (int i = 0; i < size; i++) {
				Result result = (Result) oistream.readObject();
				System.out.println(result.title_title_MatchScore + "\t"
						+ result.stackTraceMatchScore);
				finalResults.add(result);
			}
			oistream.close();
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		// returning the retrieved results
		return finalResults;
	}

}
