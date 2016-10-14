package OldMovieRcommenderSystem;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;


public class User {
	private static HashMap<String[],String> ratedMovies = new HashMap<String[],String>();
	private static LinkedHashSet<String[]> userBasedRecommends = new LinkedHashSet<String[]>();
	private static LinkedHashSet<String[]> itemBasedRecommends = new LinkedHashSet<String[]>();
	private static LinkedHashSet<String[]> genreBasedRecommends = new LinkedHashSet<String[]>();
	private static LinkedHashSet<String[]> recommends = new LinkedHashSet<String[]>();
	private static double averageRatingScore;
	private static HashMap<Integer,Integer> userPreference = new HashMap<Integer,Integer>();
		
	protected static void initializeUser(){
		for(int i=4;i<23;i++){
			userPreference.put(i, 0);
		}
	}
	protected static void addRatedMovies(String movieID,String score){
		String[] movieElements = DataCenter.getIDSequencemovies().get(Integer.parseInt(movieID)-1);
		if(!ratedMovies.containsKey(movieElements)){
			for(int i=4;i<movieElements.length-2;i++){
				if(movieElements[i].equals("1")){				
					userPreference.put(i, userPreference.get(i)+1);
				}
			}
		}
		ratedMovies.put(movieElements, score);
		int sum=0;
		for(String[] movies:ratedMovies.keySet()){
			sum+=Integer.parseInt(ratedMovies.get(movies));
		}
		averageRatingScore=(double)sum/ratedMovies.size();
	}

	protected static double getAverageRtingScore() {
		return averageRatingScore;
	}
	protected static HashMap<String[],String> getRatedMovies() {
		return ratedMovies;
	}
	protected static LinkedHashSet<String[]> getRecommends() {
		return recommends;
	}
	protected static void recommends(){
		userBasedRecommend();
		itemBasedRecommend();
		genreBasedRecommend();
		Iterator<String[]> userBased = userBasedRecommends.iterator();
		Iterator<String[]> itemBased = itemBasedRecommends.iterator();
		Iterator<String[]> genreBased = genreBasedRecommends.iterator();
		while(recommends.size()<100){
			for(int i=0;i<2&&userBased.hasNext();){
				if(recommends.add(userBased.next())){
					i++;
				}
			}
			for(int i=0;i<2&&itemBased.hasNext();){
				if(recommends.add(itemBased.next())){
					i++;
				}
			}
			for(int i=0;i<1&&genreBased.hasNext();){
				if(recommends.add(genreBased.next())){
					i++;
				}
			}
		}
	}
	private static void userBasedRecommend() {
		// TODO Auto-generated method stub
		int userNumber=DataCenter.getUserNumber();
		int movieNumber=DataCenter.getMovieNumber()-2;
		double[] correlations = new double[userNumber];
		String[][] userScoresByCorrelation = new String[userNumber][];
		double[] averageDeviation = new double[movieNumber];
		String[][] movieElements = new String[movieNumber][];
		for(int userID=1;userID<userNumber;userID++){
			userScoresByCorrelation[userID]=DataCenter.getMetrix().get(userID);
			double numerator = 0.0;
			double denominatorU = 0.0;	
			double denominatorNU = 0.0;
			int count=0;
			for(int movieID=1;movieID<movieNumber;movieID++){				
				if(ratedMovies.containsKey(DataCenter.getIDSequencemovies().get(movieID-1))&&!userScoresByCorrelation[userID][movieID].equals("")){
					double residualU = Double.parseDouble(userScoresByCorrelation[userID][movieID]) - Double.parseDouble(userScoresByCorrelation[userID][movieNumber]);	
					double residualNU = Double.parseDouble(ratedMovies.get(DataCenter.getIDSequencemovies().get(movieID-1))) - averageRatingScore;	
					numerator += residualU*residualNU;					
					denominatorU += residualU*residualU;					
					denominatorNU += residualNU*residualNU;
					count++;
				}
			}
			correlations[userID] = denominatorU*denominatorNU==0?0:numerator*Math.log10(count)/Math.sqrt(denominatorU*denominatorNU);
		}
		for(int i=1;i<correlations.length/10;i++){
			for(int j=i+1;j<correlations.length;j++){
				if(correlations[i]<correlations[j]){
					double temd=correlations[i];
					correlations[i]=correlations[j];
					correlations[j]=temd;;
					String[] temstr = userScoresByCorrelation[i];
					userScoresByCorrelation[i]=userScoresByCorrelation[j];
					userScoresByCorrelation[j]=temstr;
				}
			}
		}
		int sdindex=movieNumber+1;
		for(int movieID=1;movieID<movieNumber;movieID++){
			double[] d ={0,0};
			int k=5;
			int count=0;
			for(int userID=1;userID<userNumber/10;userID++){
				if(!userScoresByCorrelation[userID][movieID].equals("")&&correlations[userID]>0){
					d[0]+=(Double.parseDouble(userScoresByCorrelation[userID][movieID])-Double.parseDouble(userScoresByCorrelation[userID][movieNumber]))*correlations[userID]/Double.parseDouble(userScoresByCorrelation[userID][sdindex]);
					d[1]+=correlations[userID];
					if(++count==k){
						break;
					}
				}
			}
			movieElements[movieID]=DataCenter.getIDSequencemovies().get(movieID-1);
			if(count>=k){
				averageDeviation[movieID]=d[0]/d[1];
				
			}else{
				averageDeviation[movieID]=0;
			}
		}
		for(int i=1;i<averageDeviation.length/10;i++){
			for(int j=i+1;j<averageDeviation.length;j++){
				if(averageDeviation[i]<averageDeviation[j]){
					double temd=averageDeviation[i];
					averageDeviation[i]=averageDeviation[j];
					averageDeviation[j]=temd;;
					String[] temstr = movieElements[i];
					movieElements[i]=movieElements[j];
					movieElements[j]=temstr;
				}
			}
		}
		for(int i=1;i<averageDeviation.length/10;i++){
			userBasedRecommends.add(movieElements[i]);
		}
	}
	private static void itemBasedRecommend() {
		// TODO Auto-generated method stub
		for(String[] movieElem:ratedMovies.keySet()){
			if(ratedMovies.get(movieElem).equals("5")){
				int userNumber=DataCenter.getUserNumber();
				int movieNumber=DataCenter.getMovieNumber()-2;
				int highRatingMovieID = Integer.parseInt(movieElem[0]);
				double[] cosineCorrelation = new double[movieNumber];
				String[][] movieElements = new String[movieNumber][];
				for(int movieID=1;movieID<movieNumber;movieID++){
					movieElements[movieID]=DataCenter.getIDSequencemovies().get(movieID-1);
					double numerator = 0.0;
					double denominatorM = 0.0;	
					double denominatorHM = 0.0;
					int count=0;
					for(int userID=1;userID<userNumber;userID++){
						if(!DataCenter.getMetrix().get(userID)[highRatingMovieID].equals("")&&!DataCenter.getMetrix().get(userID)[movieID].equals("")){
							double otherMovieRating = Double.parseDouble(DataCenter.getMetrix().get(userID)[movieID]);
							double myMovieRating = Double.parseDouble(DataCenter.getMetrix().get(userID)[highRatingMovieID]);
							numerator+=otherMovieRating*myMovieRating;
							denominatorM+=otherMovieRating*otherMovieRating;
							denominatorHM+=myMovieRating*myMovieRating;
							count++;
						}						
					}
					double weight=count>100?1.001:count>50?1:count>10?0.999:0.5;;
					cosineCorrelation[movieID]=numerator*weight/Math.sqrt(denominatorHM*denominatorM);
				}
				for(int i=1;i<cosineCorrelation.length/20;i++){
					for(int j=i+1;j<cosineCorrelation.length;j++){
						if(cosineCorrelation[i]<cosineCorrelation[j]){
							double d=cosineCorrelation[i];
							cosineCorrelation[i]=cosineCorrelation[j];
							cosineCorrelation[j]=d;
							String[] str=movieElements[i];
							movieElements[i]=movieElements[j];
							movieElements[j]=str;
						}
					}
				}
				for(int i=1;i<cosineCorrelation.length/20;i++){
					itemBasedRecommends.add(movieElements[i]);
				}
			}		
		}
	}
	private static void genreBasedRecommend() {
		// TODO Auto-generated method stub
		int[][] genreTimes = new int[19][2];
		for(int genreIndex=4;genreIndex<23;genreIndex++){
			genreTimes[genreIndex-4][0]=genreIndex;
			genreTimes[genreIndex-4][1]=userPreference.get(genreIndex);
		}
		for(int i=0;i<3;i++){
			for(int j=i+1;j<genreTimes.length;j++){
				if(genreTimes[i][1]<genreTimes[j][1]){
					int[] tem=genreTimes[i];
					genreTimes[i]=genreTimes[j];
					genreTimes[j]=tem;
				}
			}
		}
		int ratedTimesIndex = DataCenter.getHighScoreMovies().get(1).length-2;
		for(String[] movieElements:DataCenter.getHighScoreMovies()){
			if(movieElements[genreTimes[0][0]].equals("1")&&Integer.parseInt(movieElements[ratedTimesIndex])>20){
				genreBasedRecommends.add(movieElements);
			}
			if(genreBasedRecommends.size()>25){
				break;
			}
		}
		for(String[] movieElements:DataCenter.getHighScoreMovies()){
			if(movieElements[genreTimes[1][0]].equals("1")&&Integer.parseInt(movieElements[ratedTimesIndex])>20){
				genreBasedRecommends.add(movieElements);
			}
			if(genreBasedRecommends.size()>40){
				break;
			}
		}
		for(String[] movieElements:DataCenter.getHighScoreMovies()){
			if(movieElements[genreTimes[2][0]].equals("1")&&Integer.parseInt(movieElements[ratedTimesIndex])>20){
				genreBasedRecommends.add(movieElements);
			}
			if(genreBasedRecommends.size()>50){
				break;
			}
		}
	}
}