package controlador;

import modelo.dao.ConversorTextoAutor;

public class Controlador {
	
	public static void main(String[] args) {
		ConversorTextoAutor c = new ConversorTextoAutor(
				"http://scienti1.colciencias.gov.co:8081/cvlac/visualizador/generarCurriculoCv.do?cod_rh=0000491268");
		c.extraerArticulos();
	}
	
}
