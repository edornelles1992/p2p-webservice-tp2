import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class RestApiClient extends Utils {



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
				System.out.println("registrar - registra o peer no servidor");
				System.out.println("buscar - busca lista de arquivos disponiveis para baixar");
				String command = scanner.nextLine();

				if ("registrar".equalsIgnoreCase(command)) {
					arquivosMap = new HashMap<String, Arquivo>();
					final File folder = new File("arquivos");
					Peer peer = new Peer();
					carregarArquivos(folder, arquivosMap, peer.getArquivos());
					registrar(server, peer);
					iniciarUploadSocket(); //inicializa socket de upload de arquivos
					iniciaThreadUpload();
				} else if ("buscar".equalsIgnoreCase(command)) {
					Map<String, Peer> peersDisponiveis = listar(server);
					while (true) {
						System.out.println("Informe o nome do arquivo que deseja baixar:");
						String arqEscolhido = scanner.nextLine();
						if (arqEscolhido != null && !arqEscolhido.isEmpty()) {
							Peer peerDoArquivo = getPeerByArquivoNome(peersDisponiveis, arqEscolhido);
							if (peerDoArquivo != null) {
								downloadArquivo(peerDoArquivo, arqEscolhido);
							} else {
								System.out.println("Arquivo inválido ou não disponivel para download.");
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private static void downloadArquivo(Peer peerDoArquivo, String arqEscolhido) throws Exception {
		Arquivo arquivoSelecionado = null;
		for (Arquivo arquivo : peerDoArquivo.getArquivos()) {
			if (arquivo.getNome().equalsIgnoreCase(arqEscolhido))
				arquivoSelecionado = arquivo;
		}

		try {
			//conexao com o socket e realiza o download do arquivo.
			iniciarDownloadSocket(peerDoArquivo.getIp());
			solitarArquivoDownload(arquivoSelecionado);
			byte[] arqBytes = receberArquivo();
			desconectarPeerToDownload();
			salvarArquivo(arqBytes, arqEscolhido);
			System.out.println("Arquivo baixado com sucesso na pasta arquivos!");
		} catch (Exception e) {
			throw new Exception("Erro na comunicação com o peer para baixar o arquivo!");
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

	public static Map<String, Peer> listar(String server) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL("http://" + server + ":8080/Servlet/p2p/listar/")
				.openConnection();
		connection.setRequestProperty("Content-Type", "application/json; utf-8");
		connection.setRequestProperty("Accept", "application/json");
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();
		String jsonResponse = convertStreamToString(connection.getInputStream());
		Type type = new TypeToken<ResponseDTO<Map<String, Peer>>>() {
		}.getType();
		ResponseDTO<Map<String, Peer>> dto = new Gson().fromJson(jsonResponse, type);
		if (dto.getSuccess()) {
			Map<String, Peer> peersDisponiveis = dto.getConteudo();
			for (Map.Entry<String, Peer> entry : peersDisponiveis.entrySet()) {
				Peer Peer = entry.getValue();
				System.out.println("Peer " + Peer.getIp() + " Arquivos: ");
				for (Arquivo arq : Peer.getArquivos()) {
					System.out.println(arq.getNome());
				}
			}
			return peersDisponiveis;
		} else {
			throw new Exception(dto.getMensagem());
		}
	}
}
