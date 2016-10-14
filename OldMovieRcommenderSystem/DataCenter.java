package OldMovieRcommenderSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;


public class DataCenter {
	private static ArrayList<String[]> IDSequenceMovies = new ArrayList<String[]>();
	private static LinkedList<String[]> highTimesMovies = new LinkedList<String[]>();
	private static LinkedList<String[]> highScoreMovies = new LinkedList<String[]>();
	private static ArrayList<String[]> metrix = new ArrayList<String[]>();
	private static int userNumber;
	private static int movieNumber;
	private DataCenter(){}
	protected static void initializeDataCenter() throws IOException{
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		DataCenter dc = new DataCenter();
		BufferedReader brIDSequenceMovies = new BufferedReader(new InputStreamReader(dc.getClass().getResourceAsStream("/integratedData/ratedMovieIDSequence.csv")));
		BufferedReader brMetrix = new BufferedReader(new InputStreamReader(dc.getClass().getResourceAsStream("/integratedData/UserRatingMatrix.csv")));
		String str;
		while((str=brIDSequenceMovies.readLine())!=null){
			IDSequenceMovies.add(str.split(","));
		}
		brIDSequenceMovies.close();
		highTimesMovies.addAll(IDSequenceMovies);
		Collections.sort(highTimesMovies,new Comparator<String[]>(){public int compare(String[] s1,String[] s2){int i=Integer.parseInt(s1[s1.length-2])>Integer.parseInt(s2[s2.length-2])?-1:1;return i;}});
		highScoreMovies.addAll(IDSequenceMovies);
		Collections.sort(highScoreMovies,new Comparator<String[]>(){public int compare(String[] s1,String[] s2){int i=Double.parseDouble(s1[s1.length-1])>Double.parseDouble(s2[s2.length-1])?-1:1;return i;}});
		while((str=brMetrix.readLine())!=null){
			metrix.add(str.split(","));
		}
		brMetrix.close();
		userNumber=metrix.size();
		movieNumber=metrix.get(1).length;
	}
	protected static int getUserNumber() {
		return userNumber;
	}
	protected static int getMovieNumber() {
		return movieNumber;
	}
	protected static ArrayList<String[]> getIDSequencemovies() {
		return IDSequenceMovies;
	}
	protected static LinkedList<String[]> getHighScoreMovies() {
		return highScoreMovies;
	}
	protected static LinkedList<String[]> getHighTimesMovies() {
		return highTimesMovies;
	}
	protected static ArrayList<String[]> getMetrix() {
		return metrix;
	}
}