import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Utils {

	private static DatagramSocket downloadSocket;
	private static DatagramSocket uploadSocket;
	private final static Integer portaDownload = 50000;
	private final static Integer portaUpload = 40000;
	private static Integer timeout = 1500;
	protected static HashMap<String, Arquivo> arquivosMap;
	private static InetAddress enderecoParaEnvio;

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

	protected static void writeJsonData(HttpURLConnection connection, Object e) throws IOException {
		try (OutputStream os = connection.getOutputStream()) {
			byte[] input = new Gson().toJson(e).getBytes("utf-8");
			os.write(input, 0, input.length);
		}
	}

	/**
	 * Pega os dados do peer no qual esta com o arquivo passado por parametro
	 */
	protected static Peer getPeerByArquivoNome(Map<String, Peer> peersDisponiveis, String arqEscolhido) {
		for (Map.Entry<String, Peer> entry : peersDisponiveis.entrySet()) {
			Peer peer = entry.getValue();
			System.out.println("Peer " + peer.getIp() + "Arquivos: ");
			for (Arquivo arq : peer.getArquivos()) {
				if (arq.getNome().equalsIgnoreCase(arqEscolhido)) {
					return peer;
				}
			}
		}
		return null;
	}

	/**
	 * Cria a conexao do socket destino com base no endereco e porta configurados.
	 */
	protected static void iniciarDownloadSocket(String endereco) {
		try {
			iniciarSocketDeDownload();
			downloadSocket.connect(InetAddress.getByName(endereco), portaUpload);
		} catch (UnknownHostException e) {
			System.out.println("Erro ao conectar no servidor!");
			System.out.println("Tentando conectar novamente...");
			iniciarDownloadSocket(endereco);
		}
	}

	/**
	 * Inicia o socket que sera disponibilizado para baixar os arquivos do client
	 */
	protected static void iniciarUploadSocket() {
		try {
			uploadSocket = new DatagramSocket(portaUpload);
			uploadSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			System.out.println("Erro ao iniciar socket servidor!");
			e.printStackTrace();
		}
	}

	/**
	 * Fecha a conexao com o socket.
	 */
	protected static void desconectarPeerToDownload() {
		System.out.println("Desconectando do peer...");
		downloadSocket.close();
		downloadSocket.disconnect();
		System.out.println("Desconectado com sucesso!");
	}

	/**
	 * Inicia o socket atribuindo um limite de tempo (timeout para receber dados.
	 */
	private static void iniciarSocketDeDownload() {
		try {
			downloadSocket = new DatagramSocket(portaDownload);
			downloadSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			System.out.println("Erro ao iniciar socket");
			e.printStackTrace();
		}
	}

	/**
	 * Envia os dados hash/nome para baixar o arquivo do peer que contem ele.
	 */
	protected static void solitarArquivoDownload(Arquivo dadosArquivo) {
		try {
			byte[] serialized = arquivoToByteArray(dadosArquivo);
			DatagramPacket sendPacket = new DatagramPacket(serialized, serialized.length);
			downloadSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("Houve um problema na comunicacao com o servidor...");
			System.out.println("Tentando restabelecer a conexao...");
			solitarArquivoDownload(dadosArquivo);
		}
	}

	protected static Arquivo receberSolicitacaoDownload() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			uploadSocket.receive(receiveDatagram);
			enderecoParaEnvio = receiveDatagram.getAddress();
			byte[] arquivoBytes = receiveDatagram.getData();
			return byteArrayToArquivo(arquivoBytes);
		} catch (IOException e) {
			return receberSolicitacaoDownload();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Receber dados via download socket;
	 * 
	 * @return
	 */
	protected static byte[] receberArquivo() {
		try {
			byte[] receiveData = new byte[1024];
			DatagramPacket receiveDatagram = new DatagramPacket(receiveData, receiveData.length);
			downloadSocket.receive(receiveDatagram);
			byte[] arquivoBytes = receiveDatagram.getData();
			return arquivoBytes;
		} catch (IOException e) {
			return receberArquivo();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static void enviarArquivo(byte[] arquivo, InetAddress address) {
		try {
			DatagramPacket sendPacket = new DatagramPacket(arquivo, arquivo.length, address, portaDownload);
			uploadSocket.send(sendPacket);
		} catch (IOException e) {
			System.out.println("Houve um problema na comunicacao com o peer que deseja o arquivo...");
			System.out.println("Tentando restabelecer a conexao...");
			enviarArquivo(arquivo, address);
		}
	}

	/**
	 * Converte o objeto pacote para um byteArray
	 */
	private static byte[] arquivoToByteArray(Arquivo dadosArquivo) {
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(bStream);
			oo.writeObject(dadosArquivo);
			oo.close();
			return bStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Converte de byteArray para o objeto arquivo.
	 */
	private static Arquivo byteArrayToArquivo(byte[] arquivo) {
		try {
			ObjectInputStream iStream = new ObjectInputStream(new ByteArrayInputStream(arquivo));
			Arquivo pacoteObj = (Arquivo) iStream.readObject();
			iStream.close();
			return pacoteObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Salva arquivo baixado na pasta arquivos.
	 */
	protected static void salvarArquivo(byte[] arqBytes, String nomeArquivo) {
		try {
			Path path = Paths.get("./arquivos/" + nomeArquivo);
			Files.write(path, arqBytes);
			System.out.println("Arquivo Recebido e salvo com sucesso na pasta de arquivos!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erro ao salvar arquivo!");
		}
	}

	/**
	 * Thread que fica rodando disponivel para outros peers baixarem os arquivos
	 * deste peer enquanto ele estiver registrado.
	 */
	public static void iniciaThreadUpload() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					Arquivo dadosArquivo = receberSolicitacaoDownload();
					byte[] arquivo = buscarArquivo(new File("arquivos"), arquivosMap, dadosArquivo.getNome());
					enviarArquivo(arquivo, enderecoParaEnvio);
				}
			}
		}).start();
	}

	/**
	 * Thread que fica disparando para o servidor a chamada rest avisando que o peer
	 * ainda esta ativo de 5 em 5 segundos
	 */
	protected static void iniciarThreadConexao(String server, Peer peer) {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(5000l);
						HttpURLConnection connection = (HttpURLConnection) new URL(
								"http://" + server + ":8080/Servlet/p2p/conexao/").openConnection();
						connection.setRequestProperty("Content-Type", "application/json; utf-8");
						connection.setRequestProperty("Accept", "application/json");
						connection.setRequestMethod("GET");
						int responseCode = connection.getResponseCode();
						String jsonResponse = convertStreamToString(connection.getInputStream());
						Type type = new TypeToken<ResponseDTO<Map<String, Peer>>>() {
						}.getType();
						ResponseDTO<Map<String, Peer>> dto = new Gson().fromJson(jsonResponse, type);
						if (dto.getSuccess()) {
							//comunicao ok, n faz nada...
						} else {
							throw new Exception(dto.getMensagem());
						}
					} catch (Exception e) {
						System.out
								.println(e.getMessage() != null ? e.getMessage() : "Erro ao comunicar com o servidor");
					}
				}
			}
		}).start();
	}

	protected static void carregarArquivos(final File folder, HashMap<String, Arquivo> arquivosMap,
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
				Arquivo arq = new Arquivo(hash, fileEntry.getName());
				arq.setSize(arquivoToByteArray(arq).length);
				arquivosMap.put(hash, arq); // map dos arquivos do client
				arquivosHash.add(arq); // lista dos arquivos hash/nome para enviar
										// pro server
			}
		}
	}

	protected static byte[] buscarArquivo(final File folder, HashMap<String, Arquivo> arquivosMap, String arquivoNome) {

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				buscarArquivo(fileEntry, arquivosMap, arquivoNome);
			} else {
				byte[] arquivo = null;
				try {
					arquivo = Files.readAllBytes(fileEntry.toPath());
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (fileEntry.getName().equalsIgnoreCase(arquivoNome)) {
					return arquivo;
				}
			}
		}
		return null;
	}

	protected static String md5sum(String hash) {
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
