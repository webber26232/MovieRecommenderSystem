package OldMovieRcommenderSystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeMap;

public class DataProcess {
	public static void movieDataProcess() throws IOException{
		
		BufferedReader brRate = new BufferedReader(new FileReader("originalData/u.data"));

		BufferedReader brItem = new BufferedReader(new FileReader("originalData/u.item"));//Read the information of movies
		
		BufferedWriter bwItem = new BufferedWriter(new FileWriter("integratedData/IDSequenceMovies.csv"));
		
		String ratingRecord;//A record of rating
		
		TreeMap<String, double[]> movieTimesRatings = new TreeMap<String, double[]>();//Be used to store rated movies and their rating times and rating scores, String is the ID of rated movies, double array contains rating times and rating scores of rated movies
		
		while((ratingRecord = brRate.readLine())!=null){
			
			String[] ratingElements = ratingRecord.split("	");//Split each record of rating into 4 elements. [0]:user ID [1]:movie ID [2]:rating score [3]:rating data
			
			ratingElements[1] = ratingElements[1].replaceAll("(\\d+)","000$1").replaceAll("0*(\\d{4})","$1");
		
			if(movieTimesRatings.containsKey(ratingElements[1])){//Judge whether the TreeMap has already contained a record of specific movie rating
				
				double[] numberRating = movieTimesRatings.get(ratingElements[1]);//If containing, get its current rating times and rating score
				
				numberRating[0]++;//rating time +1
				
				numberRating[1] += Integer.parseInt(ratingElements[2]);//Change the sum of rating score
				
				numberRating[2] = numberRating[1]/numberRating[0];//Calculate average rating score
			
			}else{
			
				double[] numberRating = new double[3];//If not containing any record of a specific movie, create the rating information 
				
				numberRating[0]=1;//Initializing rating times
				
				numberRating[1] = Integer.parseInt(ratingElements[2]);//Append the first rating score
				
				numberRating[2] = numberRating[1]/numberRating[0];//Calculate average rating score
				
				movieTimesRatings.put(ratingElements[1], numberRating);//Add the information into the TreeMap
				
			}
		}
		brRate.close();
		
		Set<String> mtrs = movieTimesRatings.keySet();
		
		for(String movieID: mtrs){
			
			double[] d = movieTimesRatings.get(movieID);
			
			bwItem.write(brItem.readLine().replaceAll("\\,"," ").replaceAll("\\|{2}", "\\|").replaceAll("\\|",",")+","+(int)d[0]+","+Double.toString(d[2]).replaceAll("(\\d+\\.\\d{1})\\d*", "$1")+"\r\n");
			
			bwItem.flush();
			
		}
				
		brItem.close();
		
		bwItem.close();

	}

	public static void ratingsToUser() throws IOException{
		
		BufferedReader brRate = new BufferedReader(new FileReader("originalData/u.data"));
		
		BufferedWriter bwItem = new BufferedWriter(new FileWriter("integratedData/UserRatings.csv"));
		
		TreeMap<Integer, TreeMap<Integer,String>> row = new TreeMap<Integer,TreeMap<Integer,String>>();
		
		String rating;//A rating record
		
		int maxMovieID = 0;//Be used to find the number of movies
		
		while((rating=brRate.readLine())!=null){
			
			String[] ratingElements = rating.split("	");//Get rating elements, [0]:userID    [1]:movieID    [2]:rating score    [3]:rating time
			
			int userID = Integer.parseInt(ratingElements[0]);
			
			int movieID = Integer.parseInt(ratingElements[1]);
			
			if(movieID>maxMovieID){//Find the largest movie ID
				
				maxMovieID = movieID;
			}
			
			if(row.containsKey(userID)){
				
				row.get(userID).put(movieID, ratingElements[2]);//If the matrix has already contained a row of this user, put this rating record inside
				
			}else{
				
				TreeMap<Integer,String> tm = new TreeMap<Integer,String>();//If the matrix doesn't contain a row of this user, create a TreeMap of movie list for this user
				
				tm.put(movieID, ratingElements[2]);//Put the rating record in
				
				row.put(userID, tm);//Put the movie list in
				
			}
			
		}
		
		brRate.close();//Close reader stream
		
		bwItem.write("UserID,");//Write the name for the first column
		
		for(int i = 1; i<=maxMovieID;i++){
			
			bwItem.write(i+",");//Write the movieIDs
			
		}
		
		bwItem.write("mean,Sd\r\n");//Write a line separator after the mean and standard deviation
		
		for(int userID:row.keySet()){
			
			bwItem.write(userID+",");//Write userIDs in the first column
			
			TreeMap<Integer,String> movies= row.get(userID);//Get the movie list of this user
			
			int count = 0;//Count the number of a user's rating times
			
			int sum = 0;//Count the total rating scores to calculate the mean score
			
			for(int i =1; i <= maxMovieID;i++){
				
				if(movies.containsKey(i)){//If this user has rated a specific movie?
					
					bwItem.write(movies.get(i)+",");//If yes, write down the rating score
					
					count++;
					
					sum += Integer.parseInt(movies.get(i));
					
				}else{
					
					bwItem.write(",");//If not, just write a comma to keep the area empty
					
				}
				
			}
			
			double mean=(double)sum/count;
			
			double sumSquareError = 0;
			
			for(int i =1; i <= maxMovieID;i++){
				
				if(movies.containsKey(i)){//If this user has rated a specific movie?
					
					double error = Integer.parseInt(movies.get(i))-mean;//If yes, calculate the deviation from the mean
					 
					sumSquareError += error*error;
				
				}
				
			double sd = Math.sqrt(sumSquareError/count);//Calculate the standard deviation
			
			bwItem.write(mean+","+sd+"\r\n");//Write the average rating score and standard deviation of the user and a line separator
			
			bwItem.flush();
			
			}
		
			bwItem.close();
		}
	}
}
