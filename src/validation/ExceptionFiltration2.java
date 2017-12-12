package validation;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

import ca.usask.cs.srlab.surfclipse.prototype.config.StaticData;

import com.google.common.io.Files;

import similarity.CosineSimilarityMeasure;

public class ExceptionFiltration2 {

	/**
	 * @param args
	 */

	HashMap<String,String> mymap;
	public ExceptionFiltration2()
	{
		this.mymap=new HashMap<>();
	}
	
	protected void load_the_exception_technical_messages()
	{
		//code for loading the exception message
		try
		{
			String fileName=StaticData.Dataset_Base+"/Exceptions/techlist.txt";
			Scanner scanner=new Scanner(new File(fileName));
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				String fileID=parts[0].trim();
				String techMessage=line.replaceFirst(fileID, "").trim();
				this.mymap.put(fileID, techMessage);
				//System.out.println(techMessage);
			}
			scanner.close();
		}catch(Exception exc){
		}
	}
	
	protected int move_to_selected(String key2)
	{
		int moved = 0;
		// code for moving to selected
		String src = StaticData.Dataset_Base + "/ExcepData/" + key2;
		String dest = StaticData.Dataset_Base + "/Selected/" + key2;
		try {
			Files.move(new File(src), new File(dest));
			moved = 1;
		} catch (Exception exc) {
		}
		return moved;
	}
	
	
	protected void measure_cosine_similarity()
	{
		//code for measuring cosine similarities
		for(String key1:this.mymap.keySet())
		{
			String msg1=this.mymap.get(key1);
			//System.out.println(key1+" "+msg1);
			System.out.print(key1);
			int sim_count=0;
			for(String key2:this.mymap.keySet())
			{
				String msg2=this.mymap.get(key2);
				//now perform cosine similarities
				CosineSimilarityMeasure cos_measure=new CosineSimilarityMeasure(msg1, msg2);
				double cos_score=cos_measure.get_cosine_similarity_score(false);
				if(cos_score>.80)
				{	
					if(!key2.equals(key1))
					{
					System.out.print(" "+key2);
					sim_count++;
					}
				}
			}
			if(sim_count==0)
			{
				move_to_selected(key1);
			}
			
			System.out.println();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ExceptionFiltration2 filter2= new ExceptionFiltration2();
		filter2.load_the_exception_technical_messages();
		filter2.measure_cosine_similarity();
	}
}
