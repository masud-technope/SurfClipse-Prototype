package ranking;

public class PageRankManager {

	/**
	 * @param args
	 */
	int[][] nodes=new int[4][4];
	double dumping_factor=.50;
	double initial_score=0.20;
	double google_score=0; //0
	double bing_score=0;  //1
	double yahoo_score=0;  //2
	double so_score=0;  //3
	
	double threshold_change_rate=.005;
	double[] currscores=new double[4];
	double[] oldscores=new double[4];
	
	public PageRankManager()
	{
		//initialize scores
//		this.google_score=initial_score;
//		this.bing_score=initial_score;
//		this.yahoo_score=initial_score;
//		this.so_score=initial_score;
		
		for(int i=0;i<4;i++)oldscores[i]=initial_score;
		
		//initialize the nodes table
		nodes[0][0]=0;
		nodes[0][1]=192;//201;
		nodes[0][2]=174;//191;
		nodes[0][3]=29;//24;
		
		nodes[1][0]=191;//206;
		nodes[1][1]=0;
		nodes[1][2]=650;//733;
		nodes[1][3]=23;//21;
		
		nodes[2][0]=174;//193;
		nodes[2][1]=650;//733;
		nodes[2][2]=0;
		nodes[2][3]=19;//20;
		
		nodes[3][0]=29;//1222;
		nodes[3][1]=23;//1275;
		nodes[3][2]=19;//20;
		nodes[3][3]=0;	
	}
	
	protected void page_score(int startNodeID) {
		// code for getting page rank score
		double dump_prefix = (1 - dumping_factor);
		for (int i = 0; i < 4; i++) {
			if (i != startNodeID) {
				int total_outbound = 0;
				for (int j = 0; j < 4; j++) {
					total_outbound += nodes[j][i];
				}
				currscores[startNodeID] += dumping_factor
						* (oldscores[i]/total_outbound);
			}
			currscores[startNodeID]+=dump_prefix;
		}
	}

	protected boolean check_convergeness(int[] thresholds)
	{
		int count=0;
		for(int i=0;i<4;i++)
			if(thresholds[i]==1)count++;
		if(count==4)return true;
		else return false;
	}
	
	
	protected void manage_page_rank_score()
	{
		//code for managing page rank score
		int thresholds[]=new int[4];
		int iteration=0;
		while(!check_convergeness(thresholds))
		{
		for(int i=0;i<4;i++)
		{
			double diff=calculate_change_rate(oldscores[i], currscores[i]);
			if(diff>=threshold_change_rate)
			{
				oldscores[i]=currscores[i];
				page_score(i);
			}else{ 
				thresholds[i]=1;
			}
		}
		iteration++;
		if(iteration>500)break;
		}
		System.out.println("Iteration passed:"+iteration);
	}
	
	protected double calculate_change_rate(double prev,double current)
	{
		//code for getting the change rate of score
		double difference=0;
		if(current>prev)difference=current-prev;
		else difference=prev-current;
		return difference/prev;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method student
		//code for page rank score
		try
		{
			PageRankManager manager=new PageRankManager();
			manager.manage_page_rank_score();
			//show the score
			for(int i=0;i<4;i++)
			{
				System.out.println(manager.oldscores[i]+" "+ manager.currscores[i]);
			}
		}catch(Exception exc){
		}
	}
}
