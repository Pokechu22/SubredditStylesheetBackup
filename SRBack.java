import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Backs up the stylesheet and images for subreddits.
 * 
 * @author Pokechu22
 */
public class SRBack {
	public static final String[] SUBREDDITS = {
		"subreddit"
	};
	
	private static final String USER_AGENT = 
			"PC:backup-subreddit-images:v0.0.0 (by /u/pokechu22) "; 
	
	private static final String PATH = 
			"http://www.reddit.com/r/SUBREDDIT/about/stylesheet.json"; 
	
	private static final Pattern IMAGE_RE = Pattern.compile(
			"\"url\": \"(.+?\\.(png|jpg|bmp|tiff|jpeg))\", \"link\": \"(.+?)\", \"name\": \"(.+?)\"");
	
	public static void main(String[] args) {
		File baseDir = new File("C:\\Users\\Pokechu22\\Pictures\\SS");
			
		baseDir.mkdirs();
		
		for (String subreddit : SUBREDDITS) {
			try {
				System.out.println("Doing " + subreddit);
				
				File folder = new File(baseDir, subreddit);
				folder.mkdir();
				
				String json = query(PATH.replace("SUBREDDIT", subreddit));

				try (PrintWriter fileOut = new PrintWriter(new File(folder,
						"stylesheet.json"))) {
					fileOut.print(json);
				}
				
				System.out.println("Saving " + subreddit + " images");
				
				Matcher matcher = IMAGE_RE.matcher(json);
				while (matcher.find()) {
					String url = json.substring(matcher.start(1), matcher.end(1));
					String ext = json.substring(matcher.start(2), matcher.end(2));
					String name = json.substring(matcher.start(4), matcher.end(4));
					
					System.out.println("Saving " + name + " for " + subreddit);
					
					File file = new File(folder, name + "." + ext);
					
					saveFile(url, file);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Quereys a reddit url and returns the API result.
	 * 
	 * Delays to not get in trouble.
	 * 
	 * @param url
	 * @return
	 */
	public static String query(String url) throws Exception {
		URLConnection connection = (new URL(url)).openConnection();
		
		Thread.sleep(3000);
		
		connection.setRequestProperty("User-Agent", USER_AGENT);
		
		try (InputStream stream = connection.getInputStream()) {
			try (Scanner scanner = new Scanner(stream)) {
				String result = scanner.useDelimiter("\\Z").next();
				
				return result;
			}
		}
	}
	
	/**
	 * Saves a file to the specified path.
	 */
	public static void saveFile(String url, File path) throws Exception {
		URLConnection connection = (new URL(url)).openConnection();
		
		Thread.sleep(3000);
		
		connection.setRequestProperty("User-Agent", USER_AGENT);
		
		try (InputStream in = connection.getInputStream()) {
			try (FileOutputStream out = new FileOutputStream(path)) {
				final byte data[] = new byte[1024];
		        int count;
		        while ((count = in.read(data, 0, 1024)) != -1) {
		        	out.write(data, 0, count);
		        }
			}
		}
	}
}

