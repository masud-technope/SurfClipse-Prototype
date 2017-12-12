package surfclipse.app;

import java.io.File;
import java.util.ArrayList;
import core.Result;
import core.SearchResultProvider;
import utility.ContentLoader;
import utility.ContentWriter;
import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class DemoSCRunner {

	ArrayList<Integer> casesIDList;
	String queryFolder;
	String straceFolder;
	String ccontextFolder;
	String outputFolder;
	int TOPK;

	public DemoSCRunner(int caseID, String queryFolder, String straceFolder,
			String ccontextFolder, String outputFolder, int TOPK) {
		this.casesIDList = new ArrayList<>();
		casesIDList.add(caseID);
		this.queryFolder = queryFolder;
		this.straceFolder = straceFolder;
		this.ccontextFolder = ccontextFolder;
		this.outputFolder = outputFolder;
		this.TOPK = TOPK;
	}

	public DemoSCRunner(String queryFolder, String straceFolder,
			String ccontextFolder, String outputFolder, int TOPK) {
		this.casesIDList = new ArrayList<>();
		this.queryFolder = queryFolder;
		this.straceFolder = straceFolder;
		this.ccontextFolder = ccontextFolder;
		this.outputFolder = outputFolder;
		this.TOPK = TOPK;
		this.casesIDList = getAllCases();
	}

	protected ArrayList<Integer> getAllCases() {
		// collecting all cases
		String queryFolderPath = StaticData.Lucene_Data_Base + "/"
				+ this.queryFolder;
		int total = new File(queryFolderPath).list().length;
		ArrayList<Integer> temp = new ArrayList<>();
		for (int i = 1; i <= total; i++) {
			String queryFile = queryFolderPath + "/" + i + ".txt";
			if (new File(queryFile).exists()) {
				temp.add(i);
			}
		}
		return temp;
	}

	protected String getCaseQuery(int caseID) {
		String queryFilePath = StaticData.Lucene_Data_Base + "/"
				+ this.queryFolder + "/" + caseID + ".txt";
		return ContentLoader.loadFileContent(queryFilePath);
	}

	protected String getSTQuery(int caseID) {
		String stackFilePath = StaticData.Lucene_Data_Base + "/"
				+ this.straceFolder + "/" + caseID + ".txt";
		if (!new File(stackFilePath).exists())
			return "";
		return ContentLoader.loadFileContentSC(stackFilePath);
	}

	protected String getCodeContext(int caseID) {
		String codeFilePath = StaticData.Lucene_Data_Base + "/"
				+ this.ccontextFolder + "/" + caseID + ".txt";
		if (!new File(codeFilePath).exists())
			return "";
		return ContentLoader.loadFileContentSC(codeFilePath);
	}

	protected ArrayList<String> executeQuery(int caseID, String searchQuery,
			String stackTrace, String codeContext) {
		SearchResultProvider srProvider = new SearchResultProvider(caseID,
				searchQuery, stackTrace, codeContext);
		ArrayList<Result> results = srProvider.provide_the_final_results();
		ArrayList<String> temp = new ArrayList<>();
		for (Result result : results) {
			temp.add(result.resultURL);
		}
		return temp;
	}

	protected ArrayList<String> getTopKOnly(ArrayList<String> results) {
		ArrayList<String> shortList = new ArrayList<>();
		for (String resultURL : results) {
			shortList.add(resultURL);
			if (shortList.size() == TOPK)
				break;
		}
		return shortList;
	}

	public void executeAllQueries() {
		for (int caseID : this.casesIDList) {
			String query = getCaseQuery(caseID);
			String strace = getSTQuery(caseID);
			String ccontext = getCodeContext(caseID);
			ArrayList<String> ranked = executeQuery(caseID, query, strace,
					ccontext);
			ranked = getTopKOnly(ranked);
			String outputFile = StaticData.Lucene_Data_Base + "/"
					+ outputFolder + "/" + caseID + ".txt";
			ContentWriter.writeContent(outputFile, ranked);
			System.out.println("Done: " + caseID);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		int caseID = 20;
		String queryFolder = "query";
		String straceFolder = "strace";
		String codeFolder = "ccontext";
		int topk = 10;
		String outputFolder = "output";
		new DemoSCRunner(caseID, queryFolder, straceFolder, codeFolder,
				outputFolder, topk).executeAllQueries();
		long end = System.currentTimeMillis();
		System.out.println("Time needed:" + (end - start) / 1000 + "s ");
	}
}
