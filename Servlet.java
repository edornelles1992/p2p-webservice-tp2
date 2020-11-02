import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class Servlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		handlerResources(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		handlerResources(request, response);
	}

	
	public void handlerResources(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-type", "application/json");
		String requestUrl = request.getRequestURI();
		String resouce = requestUrl.split("/")[3];
		String last = requestUrl.substring(requestUrl.lastIndexOf('/')).substring(1);

		switch (resouce) {
		case "registrar":
			this.registrar(request, response);
		break;
		case "listar":
			this.listar(request, response);
		break;
		case "conexao":
			this.conexao(request, response);
		break;
		default:
			this.invalidResourceRequest(response, 404);
		}
	}

	public void invalidResourceRequest(HttpServletResponse response, int statusCode) {
		response.setStatus(statusCode);
	}

	public void registrar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("Registrando o peer " + request.getRemoteAddr() + "...");
		String requestData = request.getReader().lines().collect(Collectors.joining());
		Gson gson = new Gson();
		Peer peer = gson.fromJson(requestData, Peer.class);
		peer.setPorta(request.getRemotePort());
		peer.setIp(request.getRemoteAddr());
		response.setStatus(200);
		
		if (DataStore.getInstance().jaRegistrado(peer.getIp())) {
			response.getOutputStream().println(gson.toJson(new ResponseDTO(false, "Voce ja esta registrado!")));
		} else { //registra
			DataStore.getInstance().putPeers(peer);
			DataStore.getInstance().putConexao(peer.getIp());
			System.out.println("Peer "+ peer.getIp() + " registrado!");
			response.setStatus(200);
			iniciaThreadValidacaoConexao(peer.getIp());
			response.getOutputStream().println(gson.toJson(new ResponseDTO(true, "Registrado com sucesso!")));
		}
	}
	
	public void listar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("listando arquivos para o peer " + request.getRemoteAddr());
		response.getOutputStream().println(new Gson().toJson(new ResponseDTO<Map<String, Peer>>(true, DataStore.getPeers())));
	}
	
	public void conexao(HttpServletRequest request, HttpServletResponse response) throws IOException {
		System.out.println("recebendo validacao conexao "+ request.getRemoteAddr());
		DataStore.getInstance().putConexao(request.getRemoteAddr()); //atualiza com data/hora q foi feita a chamada
		response.getOutputStream().println(new Gson().toJson(new ResponseDTO(true)));
	}
	
	/**
	 * Thread que fica rodando validando a conexao de um peer a cada segundos.
	 * compara o horario da ultima vez q foi validado, se ultrapassou 10 segundos
	 * apaga o peer registrado.
	 */
	public static void iniciaThreadValidacaoConexao(String ip) {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000);
						LocalDateTime ultimoStatus = DataStore.getConexoes().get(ip);
						LocalDateTime now = LocalDateTime.now();
						Duration d1 = Duration.between(ultimoStatus, now);
						Duration d2 = Duration.ofSeconds(10);
						if (d1.compareTo(d2) > 0) { //passou 10 segundos sem validar conexao
							DataStore.getConexoes().remove(ip);
							DataStore.getPeers().remove(ip);
							System.out.println("Peer " +ip+ " Desconectado, Motivo: nao enviou duas validacoes de conexao em sequencia");
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
			}
		}).start();
	}
}
