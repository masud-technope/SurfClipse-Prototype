package codestack;

import utility.StackTraceUtils;

public class MyExceptionManager {

	public static String getCurrentExceptionName(String stackTrace)
	{
		//code for getting current exception name
		String currentExceptionName=new String();
		try{
			String[] lines=stackTrace.split("\n");
			String firstLine=lines[0];
			StackTraceUtils utils=new StackTraceUtils(firstLine);
			currentExceptionName=utils.extract_exception_name();
		}catch(Exception exc){}
		return currentExceptionName;
	}
}
