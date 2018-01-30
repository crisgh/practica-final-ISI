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
	String pelicula = "";
	String categoria = "";
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
		String result = "No se han encontrado resultados para 'Gallego, Cristina'";
		graph = new Graph();
		assertEquals(result, Main.Vecinos(graph, "Gallego, Cristina"));
	}



	//El nombre de la película es null
	@Test(expected=NullPointerException.class)
	public void testCategoria_peliculas() {
		pelicula = null;
		Main.pelicula(pelicula);
	}

	//No existe la pelicula
	@Test() 
	public void test_Categorias_peliculas_noFound() {
		String pelicula = "Mi Gran peli";
		String answer = "No se han encontrado resultados para '" + pelicula + "'";
		assertEquals(answer,Main.pelicula(pelicula));
	}

	//La categoría es null
	@Test(expected=NullPointerException.class)
	public void test_CategoriaPeli() {
		categoria = null;
		Main.peliculas(categoria);
	}

	//La categoría no existe 
	@Test(expected=IllegalArgumentException.class)
	public void test_Categoria_NOFOUND() {
		categoria = " Categoria Cristina";
		Main.peliculas(categoria);
	}


	//Se selecciona la opción elige una categoría, FLUJO--> Entra en el segundo if
	@Test()
	public void Test_NO_Categoria() {
		String categoria = "Cat_Nula";
		String answer = "Necesita seleccinar una categoria.";
		assertEquals(answer, Main.peliculas(categoria));
	}


	//Vecinos null
	@Test(expected= NullPointerException.class)
	public void Test_doVecinos() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doVecinos(request, response);
	}
	//Request, response son null
	@Test (expected=NullPointerException.class)
	public void Test_Vecinos_RequestResponse() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doDistance(request, response);
	}


	//Categorias null
	@Test(expected= NullPointerException.class)
	public void doCategorias() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doCategorias(request, response);
	}

	//Request and Response null
	@Test(expected= NullPointerException.class)
	public void doCategoriaPeli() throws ClassNotFoundException, URISyntaxException, SQLException {
		Main.doCategoriaPeli(request, response);
	}
	@Test 
	public void Test_Insert_Distancia() throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		String elem1 = "Wilson, Debra (I)";
		String elem2 = null;
		String distancia = "2";
		Integer result = 1;
		assertEquals(result,Main.insert_distancia(connection, elem1, elem2, distancia));
	} 

	//No tiene vecinos
	@Test 
	public void Test_Insert_Vecinos() throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		String peticion = "Olson, Sandra";
		String element2 = "Cristina";
		Integer resultado = 0;
		assertEquals(resultado, Main.insert_vecinos(connection, peticion,element2));
	} 


	public void Test_Insert_Categorias() throws SQLException {
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		String categoria = "Action Movies";
		String film = "Mi gran Peli";
		assertFalse( Main.insert_categoria(connection,film,categoria));
	} 

	public void testApp()
	{
		assertTrue( true );
	}
}
