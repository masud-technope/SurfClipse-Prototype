package validation;

import java.io.File;
import java.util.HashSet;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;
import utility.StackTraceUtils;

public class DistinctCounter2 {

	/**
	 * @param args
	 */
	
	HashSet<String> items;
	public DistinctCounter2()
	{
		items=new HashSet<>();
	}
	
	
	protected void show_items()
	{
		for(String item:items)
		{
			System.out.println(item);
		}
	}
	
	protected void count_distinct_exception()
	{
		//code for calculating the distinct exception
		String folder= StaticData.Dataset_Base+"/Selected";
		File file=new File(folder);
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			for(File f:files)
			{
				try
				{
					Scanner scanner=new Scanner(f);
					if(scanner.hasNext()){
						String line=scanner.nextLine();
						StackTraceUtils utils=new StackTraceUtils(line);
						String exceptionName=utils.extract_exception_name();
						if(!exceptionName.trim().isEmpty())
						this.items.add(exceptionName);
						System.out.println(exceptionName);
					}
					scanner.close();
				}catch(Exception exc){
					exc.printStackTrace();
				}
			}
		}
		System.out.println("Items:"+items.size());
		show_items();
	}
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new DistinctCounter2().count_distinct_exception();

	}

}
