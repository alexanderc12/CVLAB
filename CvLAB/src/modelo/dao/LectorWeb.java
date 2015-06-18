package modelo.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;


/**
 * Permite leer una pagina Web.
 *
 * @author Alexander Castro
 */
public class LectorWeb {

	/**
	 * A partir de un URL retorna el contenido HTML de un pagina
	 *
	 * @param urlArticulo
	 * @return String con el HTML de la pagina
	 * @throws IOException
	 */
	public static String leerArticulo(String urlArticulo) throws IOException {
		URL url = new URL(urlArticulo);
		BufferedReader entrada = new BufferedReader(new InputStreamReader(url.openStream(),
				Charset.forName("ISO-8859-1")));
		String linea;
		StringBuilder texto = new StringBuilder();
		while ((linea = entrada.readLine()) != null)
			texto.append(linea);
		entrada.close();
		return remplazarCaracteresHTML(texto.toString());
	}

	/**
	 * Resuelve problemas de internazionalización cambiando los caracteres
	 * especiales de HTML a sus correspondientes valor para porder analizarlos
	 * en JAVA.
	 *
	 * @param texto
	 *            con caracteres especiales de HTML
	 * @return texto libre de caracteres especiales de HTML
	 */
	private static String remplazarCaracteresHTML(String texto) {
		return texto = texto.replaceAll("&aacute;", "á").replace("&eacute;", "é").replace("&iacute;", "í")
				.replace("&oacute;", "ó").replace("&uacute;", "ú").replace("&Aacute;", "Á").replace("&Eacute;", "É")
				.replace("&Iacute;", "Í").replace("&Oacute;", "Ó").replace("&Uacute;", "Ú").replace("&ntilde;", "ñ")
				.replace("&Ntilde;", "Ñ").replace("&amp;", "&").replace("&nbsp;", " ");
	}
}