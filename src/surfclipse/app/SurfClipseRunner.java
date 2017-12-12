package surfclipse.app;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import evaluate.PerformanceCalc;

public class SurfClipseRunner {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long start=System.currentTimeMillis();

		// point to the current directory
		StaticData.Lucene_Data_Base = System.getProperty("user.dir");

		if (args.length == 0) {
			System.out.println("Please enter appropriate parameters.");
			return;
		}
		// default initialization
		int caseID = 0;
		String queryFolder = "query";
		String straceFolder = "strace";
		String ccontextFolder = "ccontext";
		String outputFolder = "output";
		String goldsetFolder = "solution";
		int TOPK = 10;
		String task = "search";
		int totalCase = 0;

		for (int i = 0; i < args.length; i += 2) {
			String key = args[i];
			switch (key) {
			case "-query":
				queryFolder = args[i + 1];
				break;
			case "-strace":
				straceFolder = args[i + 1];
				break;
			case "-ccontext":
				ccontextFolder = args[i + 1];
				break;
			case "-output":
				outputFolder = args[i + 1];
				break;
			case "-goldset":
				goldsetFolder = args[i + 1];
				break;
			case "-totalcase":
				totalCase = Integer.parseInt(args[i + 1]);
				break;
			case "-topk":
				TOPK = Integer.parseInt(args[i + 1]);
				break;
			case "-caseNo":
				caseID = Integer.parseInt(args[i + 1]);
				break;
			case "-task":
				task = args[i + 1];
				break;
			default:
				break;
			}
		}
		if (caseID > 0) {
			// single query execution
			DemoSCRunner demoRunner = new DemoSCRunner(caseID, queryFolder,
					straceFolder, ccontextFolder, outputFolder, TOPK);
			demoRunner.executeAllQueries();
			System.out.println("Query executed successfully!");
		} else {
			// all query execution
			if (task.equals("search")) {
				System.out
						.println("Query execution started. It will take several minutes.");
				DemoSCRunner demoRunner = new DemoSCRunner(queryFolder,
						straceFolder, ccontextFolder, outputFolder, TOPK);
				demoRunner.executeAllQueries();
				System.out.println("Queries executed successfully!");
			} else if (task.equals("evaluate")) {
				new PerformanceCalc(totalCase, goldsetFolder, outputFolder,
						TOPK).calculatePerformance();
			}
		}
		
		long end=System.currentTimeMillis();
		System.out.println("Time needed:"+ (end-start)/1000 +"s");
	}
}
