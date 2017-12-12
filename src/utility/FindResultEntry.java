package utility;
import java.util.ArrayList;
import core.Result;

public class FindResultEntry {

	/**
	 * @param args
	 */
	public boolean result_found_in_collection(Result result, ArrayList<Result> resultCollection)
	{
		boolean found=false;
		try
		{
			for(int i=0;i<resultCollection.size();i++)
			{
				Result myentry=resultCollection.get(i);
				if(result.resultURL==myentry.resultURL)
				{
					found=true;
					break;
				}
			}
		}catch(Exception exc){
			System.err.println(exc.getMessage());
		}
		return found;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
