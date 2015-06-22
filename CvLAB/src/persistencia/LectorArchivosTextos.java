package persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class LectorArchivosTextos {
	
	private static final String ENLACE_INFORME = "src/data/informeCvLAC.csv";
	private static final String ENLACE_AUTORES = "src/data/enlacesAutores.txt";

	public static List<String> leerEnlacesAutores() throws IOException {
		List<String> lista = Files.readAllLines(Paths.get(ENLACE_AUTORES));
		return lista;
	}
	
	public static void escribirLineaArchivo(String articulo) throws IOException {
		Files.write(Paths.get(ENLACE_INFORME), articulo.getBytes(), StandardOpenOption.APPEND);
	}
}