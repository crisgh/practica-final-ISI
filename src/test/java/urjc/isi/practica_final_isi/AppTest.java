package urjc.isi.practica_final_isi;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
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
	private Connection connection;

	@Test 
	public void Test_Distancia() {
		String actor1 = "Actor A";
		String actor2 = "Actor H";
		String resultado = "Actor A -> Movie 1 -> Actor H<br>Distancia: 2";
		assertEquals( resultado, Main.distanceElements(graph,actor1, actor2));
	}
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
	// Distancia null
	@Test (expected=NullPointerException.class)
	public void Test_doDistancia() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doDistance(request, response);
	} 
	//Elemento NO_FOUND
	@Test()
	public void TestDistance_NOFOUND() {
		String actor1 = "Cristina";
		String actor2 = "Actor A";
		String answer = "No se han encontrado resultados para su búsqueda.</br>"
				+ "Puede que haya introducido mal alguno de los elementos.";
		assertEquals(answer,Main.distanceElements(graph, actor1, actor2));

	}

	@Test(expected=NullPointerException.class)
	public void TestVecinos() {
		String result = "";
		actor1 = null;
		assertEquals(result, Main.Vecinos(graph, actor1));
	}

	@Test()
	public void TestVecinos_NOFOUND() {
		String result = "No se han encontrado resultados para 'Hill, Marianna'";
		graph = new Graph();
		assertEquals(result, Main.Vecinos(graph, "Hill, Marianna"));
	}

	//Vecinos null
	@Test(expected= NullPointerException.class)
	public void doAInB1() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doVecinos(request, response);
	}

	public void testApp()
	{
		assertTrue( true );
	}
}
