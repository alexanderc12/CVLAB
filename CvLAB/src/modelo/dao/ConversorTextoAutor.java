package modelo.dao;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import persistencia.LectorArchivosTextos;

/**
 * Usando la liberia jsoup y algunos metodos de la clase String, analiza el
 * texto para extraer los metadatos y la informacion de un articulo cientifico
 * indexado en Scielo, aplica para paginas en español y el algortimo esta
 * diseñado para la pagina de la revista de Ingenieria de la UPTC pero puede ser
 * adaptado e incluso funcionar con otras revistas.
 *
 * @author Alexander Castro
 *
 */
public class ConversorTextoAutor {
	
	private static final String SEPARADOR = "|";
	private static final String TAG_REVISTA = "(<br>)(.)+";
	private static final String TAG_PAIS = "(En: )(.)+";
	private static final String TAG_TITULO = "\".+\"";
	private static final String TAG_ARTICULO = "blockquote";
	private static final String REG_NO_ESPACIOS = "(\\s{3,})";
	private static final String REG_NO_DOBLE_ESPACIO = "(\\s{2})";
	private static final String TAG_NOMBRE = "<td>Nombre</td>(.*?)</td>";
	private static String texto;
	private static Document documento;
	private static final String INICIO_CONTENIDO = "<!--Bibliografica-->";
	private static final String FIN_CONTENIDO = "<td width=\"100%\"><a name=\"libros\"></a>";
	private static final String REG_NO_HTML = "<[^>]*>";
	
	
	private static void extraerTextoArticulo(String textoInicio, String textoFin) {
		int indexInicioMetadatos = texto.indexOf(textoInicio);
		int indexFinMetadatos = texto.indexOf(textoFin);
		documento = Jsoup.parse(texto.substring(indexInicioMetadatos, indexFinMetadatos));
	}
	
	public static void analizarAutores() throws IOException {
		List<String> lista = LectorArchivosTextos.leerEnlacesAutores();
		for (String enlaceAutor : lista) {
			extraerArticulosAutor(enlaceAutor);
		}
	}
	
	public static void extraerArticulosAutor(String enlace) throws IOException {
		texto = LectorWeb.leerArticulo(enlace);
		StringBuilder articulo = new StringBuilder();
		articulo.append(normalizarNombre(obtenerDato(TAG_NOMBRE, texto).replace("Nombre", "")
				.replaceAll(REG_NO_DOBLE_ESPACIO, " ")
				.replaceAll(REG_NO_ESPACIOS, "").replaceAll(REG_NO_HTML, "")));
		articulo.append(SEPARADOR);
		extraerTextoArticulo(INICIO_CONTENIDO, FIN_CONTENIDO);
		Elements listaArticulos = documento.select(TAG_ARTICULO);
		int indexDatosAutor = articulo.length();
		for (Element articuloWeb : listaArticulos) {
			String texto = articuloWeb.html();
			articulo.delete(indexDatosAutor, articulo.length());
			articulo.append(obtenerDato(TAG_TITULO, texto).toUpperCase());
			articulo.append(SEPARADOR);
			String pais = obtenerDato(TAG_PAIS, texto).substring(4);
			articulo.append(pais.substring(0, pais.length() - 1));
			articulo.append(SEPARADOR);
			articulo.append(obtenerDato(TAG_REVISTA, texto).substring(5));
			articulo.append(System.lineSeparator());
			LectorArchivosTextos.escribirLineaArchivo(articulo.toString());
		}
	}
	
	private static String normalizarNombre(String nombre) {
		String[] palabras = nombre.split(" ");
		StringBuilder nombreNormalizado = new StringBuilder();
		for (String palabra : palabras) {
			nombreNormalizado.append(palabra.substring(0, 1).toUpperCase());
			nombreNormalizado.append(palabra.substring(1).toLowerCase());
			nombreNormalizado.append(" ");
		}
		return nombreNormalizado.toString().substring(0, nombreNormalizado.toString().length() - 1);
	}

	private static String obtenerDato(String expresion, String texto, int respuesta) {
		Pattern patron = Pattern.compile(expresion);
		Matcher interpretador = patron.matcher(texto);
		String resultado = null;
		if (interpretador.find()) {
			resultado = interpretador.group(respuesta);
		}
		return resultado;
	}
	
	private static String obtenerDato(String expresion, String texto) {
		return obtenerDato(expresion, texto, 0);
	}
}