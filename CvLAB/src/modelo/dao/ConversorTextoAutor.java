package modelo.dao;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modelo.entidad.Articulo;
import modelo.entidad.Autor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
	
	private Autor autor;
	private String texto;
	private Document documento;
	private static final String INICIO_CONTENIDO = "<!--Bibliografica-->";
	private static final String FIN_CONTENIDO = "<td width=\"100%\"><a name=\"libros\"></a></td>";
	private static final String EXP_REG_NO_HTML = "<[^>]*>";
	
	public ConversorTextoAutor(String archivo) {
		try {
			texto = LectorWeb.leerArticulo(archivo);
			autor = new Autor(obtenerDato("<td>Nombre</td>(.*?)</td>", texto).replace("Nombre", "")
					.replaceAll("(\\s{2,})", "").replaceAll(EXP_REG_NO_HTML, ""), obtenerDato(
					"<td>Categoría</td>(.*?)</td>", texto).replace("Categoría", "")
					.replaceAll("(\\s{2,})", "").replaceAll(EXP_REG_NO_HTML, ""));
			System.out.println(autor.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void extraerTextoArticulo(String textoInicio, String textoFin) {
		int indexInicioMetadatos = texto.indexOf(textoInicio);
		int indexFinMetadatos = texto.indexOf(textoFin);
		documento = Jsoup.parse(texto.substring(indexInicioMetadatos, indexFinMetadatos));
	}
	
	public void extraerArticulos() {
		extraerTextoArticulo(INICIO_CONTENIDO, FIN_CONTENIDO);
		Elements listaArticulos =
				documento.select("blockquote");
		for (Element articuloWeb : listaArticulos) {
			String texto = articuloWeb.html();
			Articulo articulo = new Articulo();
			articulo.setTitulo(obtenerDato("\".+\"", texto));
			articulo.setPais(obtenerDato("(En: )(.)+", texto).substring(4));
			articulo.setRevista(obtenerDato("(<br>)(.)+", texto).substring(5));
			System.out.println(articulo.toString());
			autor.agregarArticulo(articulo);
			System.out.println("-------------------------------------");
		}
	}

	private String obtenerDato(String expresion, String texto, int respuesta) {
		Pattern patron = Pattern.compile(expresion);
		Matcher interpretador = patron.matcher(texto);
		String resultado = null;
		if (interpretador.find()) {
			resultado = interpretador.group(respuesta);
		}
		return resultado;
	}
	
	private String obtenerDato(String expresion, String texto) {
		return obtenerDato(expresion, texto, 0);
	}
}