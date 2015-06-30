package modelo.dao;

import java.io.IOException;
import java.text.Normalizer;
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
	
	private static final String FIN_PROYECTOS = "</body>";
	private static final String TAG_PROYECTOS = "<td><h3>Proyectos</h3></td>";
	private static final String TAG_LIBRO = "blockquote";
	private static final String SOLO_ASCII = "[^\\p{ASCII}]";
	private static final String SEPARADOR = "|";
	private static final String TAG_REVISTA = "(<br>)(.)+";
	private static final String TAG_PAIS = "(En: )(.)+";
	private static final String TAG_TITULO = "\".+\"";
	private static final String TAG_ANIO = ",\\d{4}";
	private static final String TAG_ISSN = "(ISSN:</i>)(.)+";
	private static final String TAG_ARTICULO = "blockquote";
	private static final String REG_NO_ESPACIOS = "(\\s{3,})";
	private static final String REG_NO_DOBLE_ESPACIO = "(\\s{2})";
	private static final String TAG_NOMBRE = "<td>Nombre</td>(.*?)</td>";
	private static final String INICIO_CONTENIDO = "<!--Bibliografica-->";
	private static final String FIN_CONTENIDO = "<td width=\"100%\"><a name=\"libros\"></a>";
	private static final String REG_NO_HTML = "<[^>]*>";
	private static final String FIN_LIBROS = "<td width=\"100%\"><a name=\"capitulos\"></a></td>";
	private static final String TAG_CATEGORIA = "<td>Categoría</td>(.*?)</td>";
	private static String texto;
	private static Document documento;
	private static String textoWeb;
	private static StringBuilder articulo;
	
	
	private static void extraerTextoArticulo(String textoInicio, String textoFin) {
		int indexInicioMetadatos = texto.indexOf(textoInicio);
		int indexFinMetadatos = texto.indexOf(textoFin);
		documento = Jsoup.parse(texto.substring(indexInicioMetadatos, indexFinMetadatos));
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
	
	private static String agregarNombre() {
		String nombre = quitarAcentos(normalizarNombre(obtenerDato(TAG_NOMBRE, texto).replace("Nombre", "")
				.replaceAll(REG_NO_DOBLE_ESPACIO, " ").replaceAll(REG_NO_ESPACIOS, "").replaceAll(REG_NO_HTML, "")));
		articulo.append(nombre);
		articulo.append(SEPARADOR);
		return nombre;
	}
	
	private static void agregarCategoria() {
		String categoria = obtenerDato(TAG_CATEGORIA, texto);
		if (categoria != null) {
			categoria = categoria.replaceAll(REG_NO_ESPACIOS, "");
			articulo.append(categoria.substring(93, categoria.indexOf("<b>")));
		} else {
			articulo.append("Sin categoria");
		}
		articulo.append(SEPARADOR);
	}

	private static void agregarCoautores(String nombre) {
		String[] listaAutores = textoWeb.substring(0, textoWeb.indexOf("\"") - 2).split(",");
		int numeroAutores = 0;
		for (int i = 0; i < 6; i++) {
			if (i < listaAutores.length) {
				String coAutor = listaAutores[i];
				if (coAutor.startsWith(" ")) {
					coAutor = listaAutores[i].substring(1);
				}
				if (!nombre.equalsIgnoreCase(coAutor)) {
					articulo.append(coAutor);
					numeroAutores++;
				}
			}
			articulo.append(SEPARADOR);
		}
		articulo.append(numeroAutores);
		articulo.append(SEPARADOR);
	}

	private static void agregarTitulo() {
		articulo.append(obtenerDato(TAG_TITULO, textoWeb).toUpperCase());
		articulo.append(SEPARADOR);
	}
	
	private static void agregraPais() {
		String pais = obtenerDato(TAG_PAIS, textoWeb).substring(4);
		articulo.append(pais.substring(0, pais.length() - 1));
		articulo.append(SEPARADOR);
	}
	
	private static void agregarRevista() {
		articulo.append(obtenerDato(TAG_REVISTA, textoWeb).substring(5));
		articulo.append(SEPARADOR);
	}
	
	private static void agregarISSN() {
		articulo.append(obtenerDato(TAG_ISSN, textoWeb).substring(10));
		articulo.append(SEPARADOR);
	}
	
	private static void agregarAnio() {
		articulo.append(obtenerDato(TAG_ANIO, textoWeb).substring(1));
		articulo.append(SEPARADOR);
	}

	public static void extraerArticulosAutor(String enlace) throws IOException {
		texto = LectorWeb.leerArticulo(enlace);
		articulo = new StringBuilder();
		String nombre = agregarNombre();
		agregarCategoria();
		extraerTextoArticulo(INICIO_CONTENIDO, FIN_CONTENIDO);
		Elements listaArticulos = documento.select(TAG_ARTICULO);
		int indexDatosAutor = articulo.length();
		for (Element articuloWeb : listaArticulos) {
			articulo.delete(indexDatosAutor, articulo.length());
			textoWeb = articuloWeb.html();
			agregarCoautores(nombre);
			agregarTitulo();
			agregraPais();
			agregarRevista();
			agregarISSN();
			agregarAnio();
			contarLibros();
			contarProyectos();
			LectorArchivosTextos.escribirLineaArchivo(articulo.toString());
		}
	}
	
	public static void contarLibros() {
		extraerTextoArticulo(FIN_CONTENIDO, FIN_LIBROS);
		articulo.append(documento.getElementsByTag(TAG_LIBRO).size());
		articulo.append(SEPARADOR);
	}
	
	public static void contarProyectos() {
		if (texto.contains(TAG_PROYECTOS)) {
			extraerTextoArticulo(TAG_PROYECTOS, FIN_PROYECTOS);
			articulo.append(documento.getElementsByTag(TAG_LIBRO).size());
		} else {
			articulo.append("0");
		}
		articulo.append(System.lineSeparator());
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

	private static final String quitarAcentos(String nombre) {
		return Normalizer.normalize(nombre, Normalizer.Form.NFD).replaceAll(SOLO_ASCII, "");
	}
	public static void analizarAutores() throws IOException {
		List<String> lista = LectorArchivosTextos.leerEnlacesAutores();
		for (String enlaceAutor : lista) {
			extraerArticulosAutor(enlaceAutor);
		}
	}
}