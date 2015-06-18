package modelo.entidad;

public class Articulo {
	
	private String titulo;
	private String revista;
	private String pais;
	

	public String getTitulo() {
		return titulo;
	}
	
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	public String getRevista() {
		return revista;
	}
	
	public void setRevista(String revista) {
		this.revista = revista;
	}
	
	public String getPais() {
		return pais;
	}
	
	public void setPais(String pais) {
		this.pais = pais;
	}
	
	@Override
	public String toString() {
		return titulo + "\n" + pais + "\n" + revista;
	}
}