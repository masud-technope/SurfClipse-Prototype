package validation;

import java.io.File;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class ExceptionFiltration {

	/**
	 * @param args
	 */
	String techMessageList;
	
	public ExceptionFiltration()
	{	
		techMessageList=new String();
	}
	
	
	protected void load_excep_technical_error()
	{
		//code for loading technical error message
		try
		{
			String folder=StaticData.Dataset_Base+"/Selected";
			File file=new File(folder);
			if(file.isDirectory())
			{
				File[] files=file.listFiles();
				for(File f:files)
				{
					Scanner scanner=new Scanner(f);
					scanner.nextLine();
					scanner.nextLine();
					String techMessage=scanner.nextLine();
					techMessageList+=f.getName()+"\t"+techMessage+"\n";
					scanner.close();
				}
				System.out.println(techMessageList);
			}	
		}catch(Exception exc){
			
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new ExceptionFiltration().load_excep_technical_error();
	}

}
