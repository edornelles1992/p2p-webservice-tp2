
public class ResponseDTO {
	private Boolean success;
	private String mensagem;

	public ResponseDTO(Boolean success, String mensagem) {
		super();
		this.success = success;
		this.mensagem = mensagem;
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

}
