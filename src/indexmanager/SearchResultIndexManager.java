package indexmanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import core.Result;
import core.SurfClipseSearch;
import utility.ContentLoader;
import utility.ResultEntryMerger;

public class SearchResultIndexManager {

	/**
	 * @param args
	 */
	HashMap<Integer, String> queryMap;
	HashMap<Integer, ArrayList<String>> solutionMap;

	public SearchResultIndexManager() {
		queryMap = new HashMap<>();
		solutionMap = new HashMap<>();
		this.load_search_queries();
	}

	@Deprecated
	protected void build_sresult_index() {
		int count = 0;
		for (Integer key : this.queryMap.keySet()) {
			int mykey = key.intValue();
			String searchQuery = this.queryMap.get(key);
			// now perform the search and collect results
			try {
				SurfClipseSearch search = new SurfClipseSearch(searchQuery);
				ResultEntryMerger merger = new ResultEntryMerger(
						search.Google_Results, search.Bing_Results,
						search.Yahoo_Results, search.SO_Results);
				ArrayList<Result> merged = merger.merge();
				// now save the result index
				int succeed = save_search_results(mykey, merged);
				if (succeed > 0)
					count++;
				Thread.sleep(3000);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			System.out.println("Results saved for:" + count
					+ " items: Completed " + mykey);
		}
	}

	@Deprecated
	protected void build_single_sresult_index(int key) {
		int count = 0;
		int mykey = key;
		String searchQuery = this.queryMap.get(key);
		// now perform the search and collect results
		try {
			SurfClipseSearch search = new SurfClipseSearch(searchQuery);
			ResultEntryMerger merger = new ResultEntryMerger(
					search.Google_Results, search.Bing_Results,
					search.Yahoo_Results, search.SO_Results);
			ArrayList<Result> merged = merger.merge();
			// now save the result index
			int succeed = save_search_results(mykey, merged);
			if (succeed > 0)
				count++;
			System.out.println("Results pulled successfully.");
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	@Deprecated
	protected int save_search_results(int mykey, ArrayList<Result> list) {
		int succeed = 0;
		String outFile = StaticData.Lucene_Data_Base + "/sclipseIndex/" + mykey
				+ ".ser";
		try {
			FileOutputStream fstream = new FileOutputStream(new File(outFile));
			ObjectOutputStream ostream = new ObjectOutputStream(fstream);
			ostream.writeInt(list.size());
			for (Result result : list) {
				ostream.writeObject(result);
			}
			ostream.close();
			succeed = 1;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return succeed;
	}

	public static ArrayList<Result> load_sresult_index(int key) {
		// code for loading the search result index
		ArrayList<Result> merged = new ArrayList<>();
		String file = StaticData.Lucene_Data_Base + "/sclipseIndex/" + key
				+ ".ser";
		try {
			FileInputStream fstream = new FileInputStream(new File(file));
			ObjectInputStream oistream = new ObjectInputStream(fstream);
			int size = oistream.readInt();
			for (int i = 0; i < size; i++) {
				Result result = (Result) oistream.readObject();
				merged.add(result);
			}
			oistream.close();

			String docsource = StaticData.Lucene_Data_Base + "/docsource/"
					+ key;
			for (int i = 0; i < merged.size(); i++) {
				String htmlFile = docsource + "/" + i + ".html";
				File myFile = new File(htmlFile);
				long flength = myFile.length() / 1024;
				if (flength <= 100) {
					String html = ContentLoader.loadFileContent(htmlFile);
					merged.get(i).resultContent = html;
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return merged;
	}

	protected void load_search_solutions() {
		// code for loading the solutions
		String query = StaticData.Lucene_Data_Base + "/solution";
		File dir = new File(query);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				try {
					Scanner scanner = new Scanner(f);
					String line = new String();
					ArrayList<String> tempList = new ArrayList<>();
					scanner.nextLine();
					while (scanner.hasNext()) {
						line = scanner.nextLine();
						if (!line.isEmpty()) {
							tempList.add(line.trim());
						}
					}
					scanner.close();
					int key = Integer.parseInt(f.getName().split("\\.")[0]);
					this.solutionMap.put(new Integer(key), tempList);

				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	protected void load_search_queries() {
		// code for loading the queries
		String query = StaticData.Lucene_Data_Base + "/query";
		File dir = new File(query);
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				try {
					Scanner scanner = new Scanner(f);
					String line = new String();
					while (scanner.hasNext()) {
						line = scanner.nextLine();
						if (!line.isEmpty()) {
							break;
						}
					}
					scanner.close();
					int key = Integer.parseInt(f.getName().split("\\.")[0]);
					this.queryMap.put(new Integer(key), line);

				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}
	}

	protected void get_optimal_performance() {
		// code for getting optimal performance
		load_search_solutions();
		int solfound = 0;
		ArrayList<Integer> culprits = new ArrayList<>();
		for (Integer key : this.solutionMap.keySet()) {
			int _key = key.intValue();
			// if(_key>84)continue;

			ArrayList<String> solList = this.solutionMap.get(key);
			ArrayList<Result> entries = load_sresult_index(key.intValue());
			// now check
			int found = 0;
			for (Result result : entries) {
				String resultURL = result.resultURL;
				if (solList.contains(resultURL.trim())) {
					solfound++;
					found = 1;
					break;
				}
			}
			if (found == 0)
				culprits.add(key);
		}
		// System.out.println("Optimally solution can be found:" + solfound);
		// the culprits whose solution cant be found
		for (Integer key : culprits) {
			System.out.println(key.intValue());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		SearchResultIndexManager builder = new SearchResultIndexManager();
		int key = 60;
		ArrayList<Result> loaded = builder.load_sresult_index(key);
		System.out.println("Loaded:" + loaded.size());
		long end = System.currentTimeMillis();
		System.out.println("Time needed:" + (end - start) / 1000 + " s");
	}
}
