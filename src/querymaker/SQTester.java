package querymaker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class SQTester {

	/**
	 * @param args
	 */
	
	static String getQuery(String filePath) {
		// code for getting query
		String content = new String();
		try {
			Scanner scanner = new Scanner(new File(filePath));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (!line.isEmpty()) {
					content = line;
					break;
				}
			}
			scanner.close();
		} catch (Exception exc) {
		}
		
		return content;
	}
	
	static String getStackTrace(String filePath) {
		// code for getting stack trace
		String content = new String();
		try {
			Scanner scanner = new Scanner(new File(filePath));
			//scanner.nextLine(); // skips the exception name
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (line.startsWith("!MESSAGE"))
					continue;
				if (!line.isEmpty()) {
					content += line + "\n";
				}
			}
			scanner.close();
		} catch (Exception exc) {
		}
		return content;
	}
	
	static String getCodeContext(String filePath) {
		// code for getting code context
		String content = new String();
		try {
			Scanner scanner = new Scanner(new File(filePath));
			while (scanner.hasNext()) {
				String line = scanner.nextLine().trim();
				if (!line.isEmpty()) {
					content += line + "\n";
				}
			}
			scanner.close();
		} catch (Exception exc) {
		}
		return content;
	}
	
	protected static void save_new_query(String query, String key) 
	{
		//code for saving new query
		String fileName=StaticData.Lucene_Data_Base+"/completeds/newquery/"+key;
		try {
			PrintWriter writer=new PrintWriter(new File(fileName));
			writer.write(query);
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String query = StaticData.Lucene_Data_Base + "/completeds/query";
		String strace = StaticData.Lucene_Data_Base + "/completeds/strace";
		String ccontext = StaticData.Lucene_Data_Base + "/completeds/ccontext";
		File f = new File(query);
		if (f.isDirectory()) {
			// String[]
			// problemList={"7.txt","15.txt","43.txt","66.txt","71.txt","72.txt","75.txt"};
			// ArrayList<String> blist=new
			// ArrayList<>(Arrays.asList(problemList));
			File[] qfiles = f.listFiles();
			for (File f1 : qfiles) {
				try {
					String key = f1.getName();
					// if(blist.contains(key))continue;
					String queryContent = new String();
					String traceContent = new String();
					String codeContent = new String();
					// query
					queryContent = getQuery(f1.getAbsolutePath());
					// System.out.println("Started:"+counter+" "+queryContent);
					// trace
					try {
						traceContent = getStackTrace(strace + "/" + key);
					} catch (Exception exc) {
					}
					// code context
					try {
						codeContent = getCodeContext(ccontext + "/" + key);
					} catch (Exception exc) {
					}
					// now conduct the search and collect results
					
					String newSearchQuery=null;
					try
					{
					System.out.println(key);
					SCQueryMaker qmaker = new SCQueryMaker(queryContent,
							traceContent, codeContent);
					newSearchQuery=qmaker.getSearchQuery();
					System.out.println(" Old: " + queryContent);
					System.out.println("Formulated: " +newSearchQuery );}catch(Exception exc){	
					newSearchQuery=queryContent;
					}
					
					String set=new String();
					if(codeContent.isEmpty())set="SetA";
					else set="SetB";
					
					save_new_query(newSearchQuery, set+"/"+key);
					System.out.println("========================");
					

				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}

	}

}
