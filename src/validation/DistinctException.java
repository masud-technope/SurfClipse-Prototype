package validation;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import utility.StackTraceUtils;

public class DistinctException {

	/**
	 * @param args
	 */
	
	String exceptionFile;
	HashSet<String> myset=new HashSet<String>();
	HashSet<String> mydistinctExp;
	HashMap<String, HashSet<String>> collector;
	
	public DistinctException(String fileName)
	{
		exceptionFile=fileName;
		mydistinctExp=new HashSet<>();
		collector=new HashMap<>();
	}
	
	protected void getDistinctException()
	{
		//code for getting distinct exceptions
		try
		{
			for(String excp:this.myset)
			{
				StackTraceUtils utils=new StackTraceUtils(excp);
				String expName=utils.extract_exception_name();
				String errorName=utils.get_error_without_exception_name();
				if(!collector.containsKey(expName))
				{
					HashSet<String> hs=new HashSet<>();
					if(!errorName.isEmpty())
					{
						hs.add(errorName.trim());
						collector.put(expName, hs);
					}	
				}else
				{
					HashSet<String> hs=collector.get(expName);
					if(!errorName.isEmpty())
					{
						hs.add(errorName.trim());
						collector.put(expName, hs);
					}
				}
			}
			
			System.out.println("Distinct exception name:"+this.collector.keySet().size());
		}catch(Exception exc){
		}	
	}
	
	protected void show_the_exceptions_with_errors()
	{
		//code for showing the exceptions with error message
		try
		{
			for(String key:this.collector.keySet())
			{
				System.out.println(key);
				System.out.println("------------------------");
				HashSet<String> hs=collector.get(key);
				for(String error:hs)
				{
					System.out.println(error);
				}
				System.out.println("============================");
			}
		}catch(Exception exc){
		}
	}
	
	protected void getDistinctExceptionMessage()
	{
		//code for getting distinct Exception
		try
		{
			Scanner scanner=new Scanner(new File(exceptionFile));
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				line=line.trim();
				this.myset.add(line);
			}
			System.out.println("Distinct exception:"+this.myset.size());
			/*for(String exp:this.myset)
			{
				System.out.println(exp);
			}*/
		}catch(Exception exc){
			
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String exceptionFile=StaticData.Dataset_Base+"/Exceptions/Exceptions_all.txt";
		DistinctException dist=new DistinctException(exceptionFile);
		dist.getDistinctExceptionMessage();
		dist.getDistinctException();
		dist.show_the_exceptions_with_errors();
	}
}
