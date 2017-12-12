package validation;

import java.io.File;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class ExceptionCounter {

	String exceptionFile;
	public ExceptionCounter(String exceptionFile)
	{
		this.exceptionFile=exceptionFile;
	}
	
	protected void count_exception_message()
	{
		//code for counting exception message
		try
		{
			Scanner scanner=new Scanner(new File(exceptionFile));
			int count=0;
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				if(line.startsWith("="))continue;
				if(line.startsWith("-"))continue;
				if(line.endsWith("Exception"))continue;
				count++;
			}
			System.out.println("Distinct exceptions:"+count);
		}catch(Exception exc){
			
		}
	}

	public static void main(String args[])
	{
		String exceptionFile=StaticData.Dataset_Base+"/Exceptions/exceptions.txt";
		ExceptionCounter ecounter=new ExceptionCounter(exceptionFile);
		ecounter.count_exception_message();
		
	}
	
}
