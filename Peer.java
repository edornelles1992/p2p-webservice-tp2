import java.util.ArrayList;

public class Peer {
	private String ip;
	private String porta;
	private ArrayList<Arquivo> arquivos = new ArrayList<>();

	public Peer(String ip, String porta) {
		super();
		this.ip = ip;
		this.porta = porta;
	}

	public Peer() {
		super();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPorta() {
		return porta;
	}

	public void setPorta(String porta) {
		this.porta = porta;
	}

	public ArrayList<Arquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(ArrayList<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}

}
