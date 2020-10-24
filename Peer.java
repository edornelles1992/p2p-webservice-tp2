import java.io.Serializable;
import java.util.ArrayList;

public class Peer implements Serializable {

	private static final long serialVersionUID = -320728501201666805L;
	private String ip;
	private int porta;
	private ArrayList<Arquivo> arquivos = new ArrayList<>();

	public Peer(String ip, int porta) {
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

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}

	public ArrayList<Arquivo> getArquivos() {
		return arquivos;
	}

	public void setArquivos(ArrayList<Arquivo> arquivos) {
		this.arquivos = arquivos;
	}

}
