package controlador;

import java.io.IOException;

import modelo.dao.ConversorTextoAutor;

public class Controlador {
	
	public static void main(String[] args) {
		try {
			ConversorTextoAutor.analizarAutores();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}