package validation;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class DatasetMaker {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String solutionPath=StaticData.Lucene_Data_Base+"/completeds/solution";
		File file=new File(solutionPath);
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			
			int success=0;
			for(File f:files)
			{
				String qfileName=StaticData.Lucene_Data_Base+"/completeds/query/"+f.getName();
				try
				{
				Scanner scanner=new Scanner(f);
				String query=new String();
				while(scanner.hasNext())
				{
					String line=scanner.nextLine();
					query=line.trim();
					if(!query.isEmpty())break;
				}
				//now saving the query in query folder
				PrintWriter writer=new PrintWriter(new File(qfileName));
				writer.write(query+"\n");
				writer.close();
				success++;
				
				}catch(Exception exc){
					
				}
			}
			System.out.println("Succeeded for :"+success);
		}
	}
}
