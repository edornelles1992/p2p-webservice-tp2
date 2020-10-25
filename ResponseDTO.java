
public class ResponseDTO<T> {
	private Boolean success;
	private String mensagem;
	private T conteudo;

	public ResponseDTO(Boolean success, String mensagem) {
		super();
		this.success = success;
		this.mensagem = mensagem;
	}

	public ResponseDTO(Boolean success) {
		super();
		this.success = success;
	}

	public ResponseDTO(Boolean success, T conteudo) {
		super();
		this.success = success;
		this.conteudo = conteudo;
	}

	public ResponseDTO(Boolean success, String mensagem, T conteudo) {
		super();
		this.success = success;
		this.mensagem = mensagem;
		this.conteudo = conteudo;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public T getConteudo() {
		return conteudo;
	}

	public void setConteudo(T conteudo) {
		this.conteudo = conteudo;
	}

}
