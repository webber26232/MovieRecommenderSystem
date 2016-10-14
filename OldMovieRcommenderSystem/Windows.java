package OldMovieRcommenderSystem;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;

public class Windows {
	private static JFrame recommendFrame = new JFrame();
	private static Button left = new Button("Back");
	private static Button right = new Button("Next");
	private static Panel recommends = new Panel();
	private static Panel bottom = new Panel();
	private static JLabel count = new JLabel("You have rated 0 movies  ");
	private static JLabel leftCount = new JLabel("  20 ratings left for next automatic recommendation");
	private static Button recommend = new Button("Recommend Right Now!");
	private static Panel[] movies = new Panel[5];
	private static JLabel[][] movieLabel = new JLabel[5][3];
	private static ButtonGroup[] ratingGroup = new ButtonGroup[5];
	private static JRadioButton[][] ratingButton = new JRadioButton[5][5];
	private static LinkedList<String[]> movieList;
	private static String[][] showingMovies;
	private static int page = 0;	
	/**
	 * @param args
	 */
	protected static JFrame getRecommendFrame() {
		return recommendFrame;
	}
	
	protected static void initializeWindows(){
		recommendFrame.setVisible(true);
		recommendFrame.setTitle("BACK TO SCREENS OF LAST CENTURY");
		recommendFrame.setSize(800,500);
		recommendFrame.setLocationRelativeTo(null);
		recommendFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		recommendFrame.setLayout(new BorderLayout(10,0));
		recommendFrame.setResizable(false);
		left.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Windows.minusPage();}});
		right.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Windows.addPage();}});
		bottom.setLayout(new FlowLayout());
		bottom.add(count);
		bottom.add(leftCount);
		bottom.add(recommend);
		recommend.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e){Windows.beginRecommend();}});
		for(int i =0;i<movies.length;i++){
			movies[i] = new Panel();
			movies[i].setBackground(Color.LIGHT_GRAY);
			movies[i].setLayout(new GridLayout(2,2));
			movieLabel[i][0] = new JLabel();
			movieLabel[i][1] = new JLabel();
			movieLabel[i][2] = new JLabel();
			movieLabel[i][1].setCursor(new Cursor(Cursor.HAND_CURSOR));
			movieLabel[i][1].addMouseListener(new URLMouseLisenter());
			movies[i].add(movieLabel[i][0]);
			movies[i].add(movieLabel[i][1]);
			movies[i].add(movieLabel[i][2]);
			Panel ratingPanel = new Panel();
			movies[i].add(ratingPanel);
			ratingGroup[i] = new ButtonGroup();
			for(int j=0;j<5;j++){
				ratingButton[i][j] = new JRadioButton((j+1)+"");
				ratingButton[i][j].addActionListener(new RatioButtonListener());
				ratingGroup[i].add(ratingButton[i][j]);
				ratingPanel.add(ratingButton[i][j]);
			}
		}
		GridLayout gl = new GridLayout(5,1,5,10);
		recommends.setLayout(gl);
		for(int i =0;i<movies.length;i++){
			recommends.add(movies[i]);
		}
		recommendFrame.add(left,BorderLayout.WEST);
		recommendFrame.add(recommends,BorderLayout.CENTER);
		recommendFrame.add(right,BorderLayout.EAST);
		recommendFrame.add(bottom,BorderLayout.SOUTH);
		movieList = DataCenter.getHighTimesMovies();
		showingMovies = new String[5][];
		show();
		JOptionPane.showMessageDialog(recommendFrame,"Evertime after rating 20 movies, the system will recommend movies automatically","BACK TO SCREENS OF LAST CENTURY",JOptionPane.PLAIN_MESSAGE);
	}
	
	private static void show(){
		for(int i=0, j=page*5;i<5;i++,j++){
			showingMovies[i] = movieList.get(j);
			movieLabel[i][0].setText(showingMovies[i][1]);
			movieLabel[i][1].setText(showingMovies[i][3]);
			movieLabel[i][2].setText("Average Score: "+showingMovies[i][showingMovies[i].length-1]+"  Rated "+showingMovies[i][showingMovies[i].length-2]+" times");
			if(User.getRatedMovies().containsKey(showingMovies[i])){
				ratingButton[i][Integer.parseInt(User.getRatedMovies().get(showingMovies[i]))-1].setSelected(true);	
			}else{
				ratingGroup[i].clearSelection();
			}
		}
	}
	
	protected static void refreshCount(){
		int left;
		for(int i=1;;i++){
			if((left=(i*20-User.getRatedMovies().size()))>0){
				break;
			}
		}
		count.setText("You have rated "+User.getRatedMovies().size()+" movies");
		leftCount.setText(left+" ratings left for next automatic recommendation");
	}
	
	private static void resetPage(){
		page=-1;
	}
	
	protected static void addPage(){
		if((page+2)*5<movieList.size()){
			page++;
			show();
		}
	}
	
	protected static void minusPage(){
		if(page>0){
			page--;
			show();
		}
	}
	
	protected static JLabel[][] getMovieLabel() {
		return movieLabel;
	}
	
	protected static JRadioButton[][] getRatingButton(){
		return ratingButton;
	}
	
	protected static String[][] getShowingMovies(){
		return showingMovies;
	}
	
	protected static void beginRecommend(){
		JOptionPane.showMessageDialog(recommendFrame,"Recommendation begins!","BACK TO SCREENS OF LAST CENTURY",JOptionPane.PLAIN_MESSAGE);
		User.recommends();
		movieList = new LinkedList<String[]>();
		for(String[] str:User.getRecommends()){
			movieList.add(str);
		}
		resetPage();
	}
}

class URLMouseLisenter extends MouseAdapter{
	public void mouseClicked(MouseEvent e){
		for(int i=0;i<Windows.getMovieLabel().length;i++){
			if(e.getSource()==Windows.getMovieLabel()[i][1]){
				try {
					URI uri = new URI(Windows.getMovieLabel()[i][1].getText());
					Desktop desktop = null;
					desktop=Desktop.isDesktopSupported()?Desktop.getDesktop():null;
					if(desktop!=null){
						desktop.browse(uri);
					}
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
}

class RatioButtonListener implements ActionListener{
	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i=0;i<5;i++){
			for(int j=0;j<5;j++){
				if(e.getSource()==Windows.getRatingButton()[i][j]/*.isSelected()*/){
					User.addRatedMovies(Windows.getShowingMovies()[i][0], (j+1)+"");
					Windows.refreshCount();
					break;
				}
			}
		}
		if(User.getRatedMovies().size()%20==0){			
			Windows.beginRecommend();
		}
	}
}
