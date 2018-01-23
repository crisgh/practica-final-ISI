package urjc.isi.practica_final_isi;


import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.*;
import spark.Request;
import spark.Response;

public class AppTest {
	/**
	 * Unit test for simple App.
	 */

	/**
	 *  Inicializamos los datos que se utilizan en los test
	 */

	String filePath = "resources/data/other-data/tinyMovies.txt";
	String delimiter = "/";
	Graph graph = new Graph(filePath, delimiter);	
	String actor1 = "";
	String actor2 = "";
	String movie = "";
	Request request = null;
	Response response = null;


	// Esperamos que se eleve la exception, tenemos algun valor nulo
	@Test(expected=NullPointerException.class)
	public void TestDistance_NULO1() {
		actor1 = null;
		Main.distanceElements(graph, actor1, actor2);
	}

	@Test(expected=NullPointerException.class)
	public void TestDistance_NULO2() {
		actor1 = null;
		actor2 = null;
		Main.distanceElements(graph, actor1, actor2);
	}

	// Grafo nulo
	@Test(expected=NullPointerException.class)
	public void TestDistance_NULOG() {
		Graph graph = new Graph();
		Main.distanceElements(graph, actor1, actor2);
	}

	public void testApp()
	{
		assertTrue( true );
	}
}
