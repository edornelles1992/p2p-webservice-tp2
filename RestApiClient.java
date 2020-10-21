import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;

public class RestApiClient {
	
	public static void main(String[] args) throws IOException{
		
		if (args.length != 1) {
		//	System.out.println("Usage: java RestApiClient <server_ip>\n");
			args = new String[]{"localhost"};
		//	return;
		}
		
		String server = args[0];
		Scanner scanner = new Scanner(System.in);
		
		while (true) {
			System.out.println("(Digite 'registrar' para se conectar ao servidor)");
			String getOrSet = scanner.nextLine();
			
			if("registrar".equalsIgnoreCase(getOrSet)){
				HashMap<String, Arquivo> arquivosMap = new HashMap<String, Arquivo>();
				final File folder = new File("arquivos");
				carregarArquivos(folder, arquivosMap);
				String jsonString = registrar(server, new Peer());
//				System.out.println(jsonString);
				
			}
			else if("set".equalsIgnoreCase(getOrSet)){
				System.out.println("Whose info do you want to set?");
				System.out.println("(Type a person's name now.)");
				String name = scanner.nextLine();
				
				System.out.println("When was " + name + " born?");
				System.out.println("(Type a year now.)");
				String birthYear = scanner.nextLine();
				
				System.out.println("Can you tell me about " + name + "?");
				System.out.println("(Type a sentence now.)");
				String about = scanner.nextLine();
				
				setPersonData(server, name, birthYear, about);
			} else break;
		}
		scanner.close();	
	}
	
	public static String registrar(String server, Peer peer) throws IOException{
		HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server + ":8080/Servlet/p2p/registrar/").openConnection();
		connection.setRequestProperty("Content-Type","application/json");  
		connection.setRequestMethod("POST");
		
		Gson gson = new Gson();
		String object =  gson.toJson(peer);
		byte[] outputInBytes = object.getBytes("UTF-8");
		OutputStream os = connection.getOutputStream();
		os.write( outputInBytes );    
		os.close();		
		connection.setDoOutput(true);
		
		int responseCode = connection.getResponseCode();
		if(responseCode == 200){
			System.out.println("POST was successful.");
			return "sucesso";
		} else {
			return "erro";
		}
	}
	
//	public static String registrar(String server, HashMap<String, Arquivo> arquivosMap) throws IOException{ EXEMPLO GET
//
//		HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server + ":8080/PersonServlet/people/").openConnection();
//		
//		connection.setRequestMethod("GET");
//
//		int responseCode = connection.getResponseCode();
//		if(responseCode == 200){
//			String response = "";
//			Scanner scanner = new Scanner(connection.getInputStream());
//			while(scanner.hasNextLine()){
//				response += scanner.nextLine();
//				response += "\n";
//			}
//			scanner.close();
//
//			return response;
//		}else{
//			System.out.println("?");
//		}
//		
//		// an error happened
//		return null;
//	}


	public static void setPersonData(String server, String name, String birthYear, String about) throws IOException{
		HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server + ":8080/PersonServlet/people/" + name).openConnection();

		connection.setRequestMethod("POST");
		
		String postData = "name=" + URLEncoder.encode(name);
		postData += "&about=" + URLEncoder.encode(about);
		postData += "&birthYear=" + birthYear;
		
		connection.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(postData);
		wr.flush();
		
		int responseCode = connection.getResponseCode();
		if(responseCode == 200){
			System.out.println("POST was successful.");
		}
	}
	
	public static void carregarArquivos(final File folder, HashMap<String, Arquivo> arquivosMap) {
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	        	carregarArquivos(fileEntry, arquivosMap);
	        } else {
	        	byte[] arquivo = null;
				try {
					arquivo = Files.readAllBytes(fileEntry.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
	        	String hash = md5sum(Arrays.toString(arquivo));
	        	arquivosMap.put(hash, new Arquivo(hash, fileEntry.getName()));
	        }
	    }
	}
	
	public static String md5sum(String hash) {
		String s = hash;
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.update(s.getBytes(), 0, s.length());
			return new BigInteger(1, m.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
}
