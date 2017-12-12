package validation;

import java.io.File;
import java.io.FileWriter;
import java.nio.channels.WritableByteChannel;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class StraceCleaner {

	/**
	 * @param args
	 */
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String strace=StaticData.Lucene_Data_Base+"/completeds/strace";
		File file=new File(strace);
		if(file.isDirectory())
		{
			File[] files=file.listFiles();
			for(File f:files)
			{
				String content=new String();
				try
				{
				Scanner scanner=new Scanner(f);
				scanner.nextLine();
				while(scanner.hasNext())
				{
					String line=scanner.nextLine();
					if(line.startsWith("!"))continue;
					content+=line.trim()+"\n";
				}
				scanner.close();
				f.delete();
				f.createNewFile();
				FileWriter fwriter=new FileWriter(f);
				fwriter.write(content);
				fwriter.close();
				
				}catch(Exception exc){
					exc.printStackTrace();
				}
				
			}
		}
	}

}
