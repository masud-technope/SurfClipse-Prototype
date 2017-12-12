package validation;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import utility.RegexMatcher;
import utility.StackTraceUtils;

public class ErrorStackExtractor {

	String logFile;
	HashMap<String,CException> myExceptionList;
	int global_file_counter=0;
	
	public ErrorStackExtractor()
	{
		//logFile=fileName;
		myExceptionList=new HashMap<>();
	}
	
	
	protected void read_the_error_log_file(String fileName)
	{
		try
		{
			Scanner scanner=new Scanner(new File(fileName));
			boolean ex_started=false;
			boolean m_started=false;
			boolean st_found=false;
			boolean e_found=false;
			boolean ex_finished=true;

			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				if(line.startsWith("!MESSAGE"))
				{
					ex_started=true;
					if(scanner.hasNext())
					{
						String line2=scanner.nextLine();
						if(line2.startsWith("!STACK"))
						{
							st_found=true;
							if(scanner.hasNext())
							{
								String line3=scanner.nextLine();
								if(RegexMatcher.matches_exception_name(line3))
								{
									e_found=true;
									
									CException exc=new CException();
									exc.errorMessage=line;
									exc.technicalErrorMessage=line3;
									StackTraceUtils utils=new StackTraceUtils(exc.technicalErrorMessage);
									exc.exceptionName=utils.extract_exception_name();
									String at_lines=new String();
									//System.out.println(exc.errorMessage);
									//System.out.println(exc.technicalErrorMessage);
									while(scanner.hasNext())
									{
										String line4=scanner.nextLine();
										if(line4.trim().startsWith("at"))
										{
											at_lines+=line4.trim()+"\n";
										}else break;
									}
									exc.stackTrace=at_lines;
									
									//now add to the HashMap
									if(!myExceptionList.containsKey(exc.technicalErrorMessage))
									{
										myExceptionList.put(exc.technicalErrorMessage, exc);
									}
									
									//System.out.println(exc.stackTrace);
									//System.out.println("-------------");
								}
							}
						}
					}
				}	
			}
			
			/*for(String key:myExceptionList.keySet())
			{
				System.out.println(key);
			}*/
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	
	protected boolean save_individual_exception(CException exp)
	{
		//code for saving single exception
		boolean saved=false;
		try
		{
		Class.forName(DbInfo.Driver_name).newInstance();
		Connection conn=DriverManager.getConnection(DbInfo.connectionString);
		String exceptionName=exp.exceptionName;
		String errorMessage=exp.errorMessage;
		String technicalMessage=exp.technicalErrorMessage;
		String stack_trace=exp.stackTrace;
		String codeContext=exp.codeContext;
		String insert_query="INSERT INTO Exception(ExceptionName, ErrorMessage, TechnicalMessage, StackTrace) "+
		"VALUES('"+exceptionName+"','"+errorMessage+"','"+technicalMessage+"','"+stack_trace+"')";
		Statement stmt=conn.createStatement();
		stmt.executeUpdate(insert_query);
		saved=true;
		}catch(Exception exc){}
		return saved;
	}
	
	protected boolean save_each_exception(CException exp)
	{
		// code for saving the exception
		boolean saved = false;
		String fileName = StaticData.Dataset_Base + "/ExcepData/"
				+ (global_file_counter + 1) + ".txt";
		try {
			File file = new File(fileName);
			FileWriter writer = new FileWriter(file);
			writer.write(exp.exceptionName + "\n");
			writer.write(exp.errorMessage + "\n");
			writer.write(exp.technicalErrorMessage + "\n");
			writer.write(exp.stackTrace + "\n");
			writer.close();
			saved = true;
			global_file_counter++;
		} catch (Exception exc) {
			System.err.println("Failed to save exception to the file:"
					+ exc.getMessage());
		}
		return saved;
	}
	
	
	protected void save_all_exceptions()
	{
		//code for saving the exceptions
		int count=0;
		for(String key:this.myExceptionList.keySet())
		{
			CException exp=myExceptionList.get(key);
			//boolean saved=save_individual_exception(exp);
			boolean saved=save_each_exception(exp);
			if(saved)count++;
		}
		System.out.println("Items saved:"+count);
	}
	
	
	
	protected void load_folder(String dir)
	{
		
		File fileDir=new File(dir);
		if(fileDir.isDirectory())
		{
			File[] files=fileDir.listFiles();
			for(File f:files)
			{
				if(f.isDirectory())
				{
					load_folder(f.getAbsolutePath());
				}else
				{
					read_the_error_log_file(f.getAbsolutePath());
				}
			}
		}
		else
		{
			read_the_error_log_file(fileDir.getAbsolutePath());
		}	
	}
	
	
	protected void extract_info_from_files()
	{
		//code for extracting elements
		String folder=StaticData.Dataset_Base +"/Users";
		load_folder(folder);
		System.out.println("Total exceptions extracted:"+myExceptionList.size());
	}
	
	
	
	
	public static void main(String args[])
	{
		//String fileName="D:/My MSc/Thesis Works/IDE_Based_Search_Recommendation/Dataset/Users/Masud/semwork/bak_9.log";
		ErrorStackExtractor errorExtractor=new ErrorStackExtractor();
		errorExtractor.extract_info_from_files();
		errorExtractor.save_all_exceptions();
	}	
}
