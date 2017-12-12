package core;

public interface ScoreWeights {
	
	//content weights
		public double TITLE_WEIGHT=1.0000;// .5;// 0.2568;// 0.4516;
		public double DESC_WEIGHT=.3866;//.6;// 0.2750;// 0.3257;
		public double CODESTACK_WEIGHT=.6881;//.3;// 1.0000;
		public double CONTENT_WEIGHT=.2808;//.2;//0.2295;// 0.1885;
		//context weights
		public double CONT_CXT_WEIGHT= 1.0000;//.5;//1.000;
		public double STRUCT_CXT_WEIGHT=.1102;//.5;//0.0011;//0.005;
		public double CODE_CXT_WEIGHT=.4768;//.4;// .0771;// 0.0845;
		public double HISTORY_CXT_WEIGHT=0;
		
		//pop and confidence
		public double ALEXA_WEIGHT=0.0174;// 0.0024;//0.0174;
		public double SOVOTE_WEIGHT=1.0000;
		
		//macro weights
		public double CONTENT_RELEVANCE_WEIGHT=0.35;// .5105;// 0.35;// 0.3737;
		public double CONTEXT_RELEVANCE_WEIGHT=0.85;// 1.000;// 0.85;// 1.0000;
		public double POPULARITY_WEIGHT=0.3820;// 0.20;// 0.0542;
		public double CONFIDENCE_WEIGHT=0.10;// 0.1152;// 0.1958;// 0.0103;
}
