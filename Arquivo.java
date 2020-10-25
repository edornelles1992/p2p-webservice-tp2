import java.io.Serializable;

public class Arquivo implements Serializable {

	private String hash;
	private String nome;
	private Integer size;

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

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

}
