package validation;

import java.io.File;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

public class FiltermyData {

	/**
	 * @param args
	 */
	
	
	protected void filter_my_data()
	{
		//code for filtering my data
		try
		{
			String sel_folder=StaticData.Dataset_Base+"/Selected";
			String my_folder=StaticData.Dataset_Base+"/MasudData";
			File f=new File(my_folder);
			int deleted=0;
			if(f.isDirectory())
			{
				File[] files=f.listFiles();
				for(File myf:files)
				{
					String fileName=myf.getName();
					String f2Name=sel_folder+"/"+fileName;
					File f2=new File(f2Name);
					if(!f2.exists())
					{
						myf.delete();
						deleted++;
					}
					
				}
			}
			System.out.println("Files deleted:"+deleted);
		}catch(Exception exc){
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FiltermyData().filter_my_data();
		
	}

}
