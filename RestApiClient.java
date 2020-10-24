import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.google.gson.Gson;

public class RestApiClient extends Utils {

	static HashMap<String, Arquivo> arquivosMap;

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		if (args.length != 1) {
			// System.out.println("Usage: java RestApiClient <server_ip>\n");
			args = new String[] { "localhost" };
			// return;
		}

		String server = args[0];
		Scanner scanner = new Scanner(System.in);

		while (true) {
			try {
				System.out.println("(Digite 'registrar' para se conectar ao servidor)");
				String command = scanner.nextLine();

				if ("registrar".equalsIgnoreCase(command)) {
					arquivosMap = new HashMap<String, Arquivo>();
					final File folder = new File("arquivos");
					Peer peer = new Peer();
					carregarArquivos(folder, arquivosMap, peer.getArquivos());
					registrar(server, peer);
				} else if ("set".equalsIgnoreCase(command)) {
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
				} 
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static void registrar(String server, Peer peer) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server + ":8080/Servlet/p2p/registrar/")
				.openConnection();
		connection.setRequestProperty("Content-Type", "application/json; utf-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestMethod("POST");
		connection.setDoOutput(true);
		connection.setDoInput(true);

		writeJsonData(connection, peer);

		int responseCode = connection.getResponseCode();
		String jsonResponse = convertStreamToString(connection.getInputStream());
		ResponseDTO dto = new Gson().fromJson(jsonResponse, ResponseDTO.class);
		if (dto.getSuccess()) {
			System.out.println(dto.getMensagem());
		} else {
			throw new Exception(dto.getMensagem());
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

	public static void setPersonData(String server, String name, String birthYear, String about) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) new URL(
				"http://" + server + ":8080/PersonServlet/people/" + name).openConnection();

		connection.setRequestMethod("POST");

		String postData = "name=" + URLEncoder.encode(name);
		postData += "&about=" + URLEncoder.encode(about);
		postData += "&birthYear=" + birthYear;

		connection.setDoOutput(true);
		OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
		wr.write(postData);
		wr.flush();

		int responseCode = connection.getResponseCode();
		if (responseCode == 200) {
			System.out.println("POST was successful.");
		}
	}

	public static void carregarArquivos(final File folder, HashMap<String, Arquivo> arquivosMap,
			ArrayList<Arquivo> arquivosHash) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				carregarArquivos(fileEntry, arquivosMap, arquivosHash);
			} else {
				byte[] arquivo = null;
				try {
					arquivo = Files.readAllBytes(fileEntry.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				String hash = md5sum(Arrays.toString(arquivo));
				arquivosMap.put(hash, new Arquivo(hash, fileEntry.getName())); // map dos arquivos do client
				arquivosHash.add(new Arquivo(hash, fileEntry.getName())); // lista dos arquivos hash/nome para enviar
																			// pro server
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
