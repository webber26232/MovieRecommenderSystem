package OldMovieRcommenderSystem;
import java.io.IOException;


public class Run {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		DataCenter.initializeDataCenter();
		User.initializeUser();
		Windows.initializeWindows();
	}

}
