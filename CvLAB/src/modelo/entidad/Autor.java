package modelo.entidad;

import java.util.ArrayList;

public class Autor {
	
	private int id;
	private static int ID;
	private String nombre;
	private String categoria;
	private ArrayList<Articulo> listaArticulos;
	
	public Autor(String nombre, String categoria) {
		this.id = id++;
		this.nombre = nombre;
		this.categoria = categoria;
		listaArticulos = new ArrayList<Articulo>();
	}
	
	public void agregarArticulo(Articulo articulo) {
		listaArticulos.add(articulo);
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public ArrayList<Articulo> getListaArticulos() {
		return listaArticulos;
	}
	
	public void setListaArticulos(ArrayList<Articulo> listaArticulos) {
		this.listaArticulos = listaArticulos;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	@Override
	public String toString() {
		return id + nombre + "-" + categoria;
	}
}