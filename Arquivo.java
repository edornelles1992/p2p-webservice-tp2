
public class Arquivo {

	private String hash;
	private String nome;

	public Arquivo(String hash, String nome) {
		super();
		this.hash = hash;
		this.nome = nome;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
