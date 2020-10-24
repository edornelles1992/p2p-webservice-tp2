import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import com.google.gson.Gson;

public class Utils {

	protected static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}		
	
	protected static void writeJsonData(HttpURLConnection connection , Object e) throws IOException {
	    try(OutputStream os = connection.getOutputStream()) {
	        byte[] input = new Gson().toJson(e).getBytes("utf-8");
	        os.write(input, 0, input.length);			
	    }
	}
}
