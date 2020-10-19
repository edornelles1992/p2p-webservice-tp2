import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class Servlet extends HttpServlet {
	
	public void handlerResources(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Content-type", "application/json");
		String requestUrl = request.getRequestURI();
		String resouce = requestUrl.split("/")[3];
		String last = requestUrl.substring(requestUrl.lastIndexOf('/')).substring(1);
		System.out.println("requesting " + last + "\n");

		switch (resouce) {
		case "registrar":
			this.registrar(request, response);
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



	public void registrar(HttpServletRequest request, HttpServletResponse response) {
		String ip = request.getParameter("ip");
		String porta = request.getParameter("porta");
		DataStore.getInstance().putPeers(new Peer(ip, porta));
		System.out.println("Peer"+ ip + " registrado!");
	}
}
