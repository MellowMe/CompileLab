package lab1;

public class Token {
	private String token;
	private String cate;
	private int code=-1;

	public void setCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public Token(String cate, String token) {
		this.cate = cate;
		this.token = token;
	}

	@Override
	public String toString() {
		return cate + " : " + token;
	}

	public String getCate() {
		return cate;
	}

	public String getToken(){
		return token;
	}

	public void setCate(String s) {
		this.cate = s;
	}
}
