import java.io.IOException;
import java.io.Reader;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class Servlet extends HttpServlet {
	
	public void handlerResources(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setHeader("Content-type", "application/json");
		String requestUrl = request.getRequestURI();
		String resouce = requestUrl.split("/")[3];
		String last = requestUrl.substring(requestUrl.lastIndexOf('/')).substring(1);
		System.out.println("requesting " + last + "\n");

		switch (resouce) {
		case "registrar":
			this.registrar(request, response);
		break;
		default:
			this.invalidRequest(response, 404);
		}
	}

	public void invalidRequest(HttpServletResponse response, int statusCode) {
		response.setStatus(statusCode);
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		String requestUrl = request.getRequestURI();
		String name = requestUrl.substring("/people/".length());
		String last = requestUrl.substring(requestUrl.lastIndexOf('/')).substring(1);
		System.out.println("requesting " + last + "\n");

		Peer peer = DataStore.getInstance().getPeers(last);

		if (peer != null) {
			Gson gson = new Gson();
			String json = gson.toJson(peer);
			response.getOutputStream().println(json);
		} else {
			response.getOutputStream().println("{}");
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		handlerResources(request, response);
	}



	public void registrar(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestData = request.getReader().lines().collect(Collectors.joining());
		Gson gson = new Gson();
		Peer peer = gson.fromJson(requestData, Peer.class);
		peer.setIp(request.getRemoteAddr());
		peer.setPorta(request.getRemotePort());
		response.setStatus(200);
		
		if (DataStore.getInstance().jaRegistrado(peer.getIp())) {
			response.getOutputStream().println(gson.toJson(new ResponseDTO(false, "Você ja está registrado!")));
		} else { //registra
			DataStore.getInstance().putPeers(peer);
			System.out.println("Peer "+ peer.getIp() + " registrado!");
			response.setStatus(200);
			response.getOutputStream().println(gson.toJson(new ResponseDTO(true, "Registrado com sucesso!")));
		}
	}
}
