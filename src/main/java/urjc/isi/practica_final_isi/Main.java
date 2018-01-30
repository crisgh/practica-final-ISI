package urjc.isi.practica_final_isi;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import javax.servlet.MultipartConfigElement;
import spark.Request;
import spark.Response;

// This code is quite dirty. Use it just as a hello world example 
// to learn how to use JDBC and SparkJava to upload a file, store 
// it in a DB, and do a SQL SELECT query
public class Main {

	// Connection to the SQLite database. Used by insert and select methods.
	// Initialized in main
	private static Connection connection;

	public static String distanceElements(Graph graph,String element1,String element2) {
		if (element1 == null || element2 == null || graph.V() == 0) {
			System.out.println("aqui");
			throw new NullPointerException("Element null");
		}
		String result = new String("");
		try {
			PathFinder pf = new PathFinder(graph, element1);
			graph.validateVertex(element2);
			if (pf.distanceTo(element2) != Integer.MAX_VALUE) {			
				String ruta = new String("");
				for (String v : pf.pathTo(element2)) {
					ruta += v + " -> ";
				}        
				char[] ruta1 = ruta.toCharArray();
				result = new String(ruta1, 0, ruta1.length-4);
				result += "<br>Distancia: " + pf.distanceTo(element2);
			} else {
				result += "Distancia: 0";
			}
		} catch (IllegalArgumentException e) {
			result = "No se han encontrado resultados para su búsqueda.</br>"
					+ "Puede que haya introducido mal alguno de los elementos.";
		}
		return result;
	}

	public static String peliculas(String categoria) {
		if (categoria == null) {
			throw new NullPointerException("categoria null");
		}
		In in;
		String result = "";
		if ("Cat_Nula".equals(categoria)) {
			result = "Necesita seleccinar una categoria.";
		} else {
			try {
				String path = "resources/data/imdb-data/cast." + categoria + ".txt";
				in = new In(path);
				while (!in.isEmpty()) {
					String line = in.readLine();				    	//Leo linea a linea (cada linea es una película)
					String[] parts = line.split("/");					//Hago un split hasta la primera /
					result += (parts[0]) + "<br>";					//Concateno todas las películas
				}
			}
			catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Error al abrir el archivo");
			}
		}
		return result;
	}

	//Para saber si esta un elemento en otro
	public static String Vecinos(Graph graph, String element) {
		if (element == null) {
			throw new NullPointerException("Elemento nulo, hay que pasar un paámetro de búsqueda");
		}

		String result = new String("");
		try {
			for (String v:graph.adjacentTo(element)) {

				if(graph.st.contains(v)) {
					result += v + "</br>";
				}
			}
		}catch (IllegalArgumentException e) {
			result += "No se han encontrado resultados para '" + element + "'";
		}
		return result;
	}

	public static String pelicula(String pelicula) {
		if (pelicula == null) {
			throw new NullPointerException("Movie null");
		}

		String[] docs = {"cast.00-06.txt", "cast.06.txt", "cast.action.txt","cast.G.txt", "cast.mpaa.txt", "cast.PG.txt","cast.PG13.txt", "cast.rated.txt"};
		String categorias = new String();
		String categoria = new String();
		In in;
		try {
			for (int i = 0; i < docs.length; i++) {
				in = new In("resources/data/imdb-data/" + docs[i]);
				String Documento = in.readAll(); // Pasa por todos los ficheros que haya en la carpeta origen
				if(Documento.contains(pelicula)) { // busqueda en los archivos que contenga
					switch (docs[i]) {
					case "cast.00-06.txt": 
						categoria = "Movies release since 2000";
						break;
					case "cast.06.txt": 
						categoria = "Movies release in 2006";
						break;
					case "cast.action.txt": 
						categoria = "Action Movies";
						break;
					case "cast.all.txt": 
						categoria = "All Movies";
						break;
					case "cast.G.txt": 
						categoria = "Movies rated G by MPAA";
						break;
					case "cast.mpaa.txt": 
						categoria = "Movies rated by MPAA";
						break;
					case "cast.PG13.txt": 
						categoria = "Movies rated PG13 by MPAA";
						break;
					case "cast.PG.txt": 
						categoria = "Movies rated PG by MPAA";
						break;
					case "cast.rated.txt": 
						categoria = "Popular Movies";
						break;
					default: 
						categoria = "NOT FOUND";
						break;
					}
					categorias += categoria + "<br>";
				}
				in.close();
			}

			if (categorias.isEmpty()) {
				categorias = "No se han encontrado resultados para '" + pelicula + "'";
			}
		}catch(IllegalArgumentException e) {
			throw new IllegalArgumentException();
		}
		return categorias;
	}


	// Used to illustrate how to route requests to methods instead of
	// using lambda expressions
	public static String doSelect(Request request, Response response) {
		return select (connection, request.params(":table"), 
				request.params(":film"));
	}


	public static String doDistance(Request request, Response response) throws ClassNotFoundException, URISyntaxException, SQLException {
		String filePath = "resources/data/other-data/moviesG.txt";
		String delimiter = "/";
		Graph graph = new Graph(filePath, delimiter);
		String element1 = request.queryParams("Element1");
		String element2 = request.queryParams("Element2");
		String distance = distanceElements(graph, element1, element2);
		insert_distancia(connection, element1,  element2, distance);
		return distance;
	}

	public static String doVecinos(Request request, Response response) throws ClassNotFoundException, URISyntaxException, SQLException {
		String filePath = "resources/data/other-data/moviesG.txt";
		String delimiter = "/";
		Graph graph = new Graph(filePath, delimiter);
		String element1 = request.queryParams("Element1");
		String vecinos = Vecinos(graph, element1);
		insert_vecinos(connection, element1,  vecinos );
		return vecinos;
	}

	public static String doCategorias(Request request, Response response) throws ClassNotFoundException, URISyntaxException, SQLException {
		String categoria = request.queryParams("Categoria");
		System.out.println("categoria " + categoria);
		String peliculas = peliculas(categoria);
		System.out.println("peliculas  " + peliculas);
		insert_categoria(connection,peliculas,categoria);
		return peliculas;
	}
	public static String doCategoriaPeli(Request request, Response response) throws ClassNotFoundException, URISyntaxException, SQLException {
		String pelicula = request.queryParams("movie");
		System.out.println("pelicula:  " + pelicula);
		String categoria = pelicula(pelicula);
		System.out.println("categoria " + categoria);
		insert_categoria(connection,pelicula,categoria);
		return categoria;
	}


	public static String select(Connection conn, String film,  String table) {
		String sql = "SELECT * FROM " + table + " WHERE film=?";
		System.out.println(sql);
		String result = new String();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, film);
			ResultSet rs = pstmt.executeQuery();
			// Commit after query is executed
			connection.commit();

			while (rs.next()) {
				// read the result set
				result += "film = " + rs.getString(film) + "\n";
				System.out.println("film = "+rs.getString(film) + "\n");

				result += "actor = " + rs.getString("actor") + "\n";
				System.out.println("actor = "+rs.getString("actor")+"\n");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("result: " + result);

		return result;
	}

	public static void insert(Connection conn, String film, String actor) {
		String sql = "INSERT INTO films(film, actor) VALUES(?,?)";

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, film);
			pstmt.setString(2, actor);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}


	public static Integer insert_distancia(Connection conn, String elem1, String elem2, String result) throws SQLException {
		// Prepare SQL to create table
		//Statement statement = connection.createStatement();
		//statement.setQueryTimeout(30); // set timeout to 30 sec.
		//statement.executeUpdate("drop table if exists Tabla_distancia");
		//statement.executeUpdate("create table Tabla_distancia (elem1 string, elem2 string ,result string)");

		String sql = "INSERT INTO Tabla_distancia (elem1 , elem2  ,result ) VALUES(?,?,?)";
		if (elem1 == null || elem2 == null){
			return 1;
		}else{
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, elem1);
				pstmt.setString(2, elem2);
				pstmt.setString(3, result);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return 0;
		}	
	}
	public static Integer insert_vecinos(Connection conn, String peticion, String vecino) throws SQLException {
		// Prepare SQL to create table
		//		Statement statement = connection.createStatement();
		//		statement.setQueryTimeout(30); // set timeout to 30 sec.
		//		statement.executeUpdate("drop table if exists Tabla_vecinos");
		//		statement.executeUpdate("create table Tabla_vecinos (peticion string, vecino string)");

		String sql = "INSERT INTO Tabla_vecinos (peticion , vecino ) VALUES(?,?)";
		if (peticion == null || vecino == null){
			return 1 ;
		}else{
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, peticion);
				pstmt.setString(2, vecino);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return 0;
		}
	}

	public static boolean insert_categoria(Connection conn, String film, String categorias) throws SQLException {
		// Prepare SQL to create table
		Statement statement = connection.createStatement();
		statement.setQueryTimeout(30); // set timeout to 30 sec.
		//		statement.executeUpdate("drop table if exists Tabla_categorias");
		//		statement.executeUpdate("create table Tabla_categorias (film string, categorias string)");
		//String sql = "CREATE table Tabla_categorias (film string, categorias string)";
		String OK = select(conn, film,"Tabla_categorias");
	
			String sql = "INSERT INTO Tabla_categorias(film, categorias) VALUES(?,?)";
	
			try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
				pstmt.setString(1, film);
				pstmt.setString(2, categorias);
				pstmt.executeUpdate();
				return true;
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			return false;
		
		
	}

	public static String FormuDistancia(Request request, Response response)  throws ClassNotFoundException, URISyntaxException {
		String body = "<form action='/Distance' method='post'>" +
				"<div>" + 
				"<label for='name'>Actor/película: </label>" +
				"<input type='text' id='name' name='Element1'/>" +
				"<label for='name'>Actor/película: </label>" +
				"<input type='text' id='name' name='Element2'/>" +
				"<button type='submit'>¡DISTANCIA!</button>" +
				"</div>" +
				"</form>";
		return body;
	}


	public static String FormuVecinos(Request request, Response response) throws ClassNotFoundException, URISyntaxException {
		String body = "<form action='/vecinos' method='post'>" +
				"<div>" + 
				"<label for='name'>Actor/película: </label>" +
				"<input type='text' id='name' name='Element1'/>" +
				"<button type='submit'>¿Qué vecinos tiene...?</button>"+
				"</div>" +
				"</form>";
		return body;
	}

	public static String FormuCategorias(Request request, Response response) throws ClassNotFoundException, URISyntaxException {
		String body = "<form action='/Categorias' method='post'>" +
				"<div>" + 
				"<select name='Categoria'>\n\t<option selected value=Cat_Nula>Selecciona la categoria</option>" +
				"<option value=00-06>Movies released since 2000</option>" +
				"<option value=06>Movies release in 2006</option>" +
				"<option value=action>Action Movies</option>" +
				"<option value=all>all the movies</option>" +
				"<option value=G>Movies G by MPAA</option>" +
				"<option value=mpaa>Movies by MPAA</option>" +
				"<option value=PG13>Movies PG13 by MPAA</option>" +
				"<option value=PG>Movies PG by MPAA</option>" +
				"<option value=rated>Popular Movies</option>" +
				"</select><input class='button' type='submit' value='Buscar'>"+
				"</div>" +
				"</form>";
		return body;
	}

	public static String FormuCategoriaPeli(Request request, Response response) throws ClassNotFoundException, URISyntaxException {
		String body = "<form action='/pelicula' method='post'>" +
				"<div>" + 
				"<label for='name'>Película: </label>" +
				"<input type='text' id='name' name='movie'/>" +
				"<button type='submit'>¿Qué categoría es...?</button>" +
				"</div>" +
				"</form>";
		return body;
	}
	public static String select(String film) {
		String sql = "SELECT * FROM films WHERE films.film=?";
		String result = new String();
		try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
			pstmt.setString(1, film);
			ResultSet rs = pstmt.executeQuery();
			rs.next();
			result += rs.getString("categorias") + "<br/>";
			if (rs.getString("categorias").isEmpty()) {
				result = "No se han encontrado categorías para la película '" + film + "'";
			}
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		if (result.isEmpty()) {
			result = "La película '" + film + "' no se encuentra en nuestra base de datos";
		}
		return result;
	}

	public static void main(String[] args) throws 
	ClassNotFoundException, SQLException {
		port(getHerokuAssignedPort());


		// Connect to SQLite sample.db database
		// connection will be reused by every query in this simplistic example
		connection = DriverManager.getConnection("jdbc:sqlite:sample.db");

		// SQLite default is to auto-commit (1 transaction / statement execution)
		// Set it to false to improve performance
		connection.setAutoCommit(true);


		// In this case we use a Java 8 method reference to specify
		// the method to be called when a GET /:table/:film HTTP request
		// Main::doWork will return the result of the SQL select
		// query. It could've been programmed using a lambda
		// expression instead, as illustrated in the next sentence.
		get("/upload_films", (req, res) -> 
		"<form action='/upload' method='post' enctype='multipart/form-data'>" 
		+ "    <input type='file' name='uploaded_films_file' accept='.txt'>"
		+ "    <button>Upload file</button>" + "</form>");

		get("/:table/:film", Main::doSelect);

		get("/FormuDistancia", Main::FormuDistancia);
		get("/FormuVecinos", Main::FormuVecinos);
		get("/FormuCategorias",Main::FormuCategorias);
		get("/FormuCPelicula", Main::FormuCategoriaPeli);


		post("/FormuDistancia", Main::FormuDistancia);
		post("/Distance", Main::doDistance);

		post("/FormuVecinos", Main::FormuVecinos);
		post("/vecinos", Main::doVecinos);

		post("/FormuCategorias",Main::FormuCategorias);
		post("/Categorias",Main::doCategorias);

		post("/FormuCPelicula", Main::FormuCategoriaPeli);
		post("/pelicula",Main::doCategoriaPeli);

		// In this case we use a Java 8 Lambda function to process the
		// GET /upload_films HTTP request, and we return a form

		// You must use the name "uploaded_films_file" in the call to
		// getPart to retrieve the uploaded file. See next call:
		get("/", (req, res) -> {
			String pprin = "<body style=\"background-color:rgba(0, 255, 228, 0.26);\">"
					+ " <center><h1 style=\"color:#ff006c;\">PRACTICA FINAL ISI</h1> "
					+ "<h2>Primero se cargan los archivos y luego podemos realizar las siguientes acciones: </h2>"
					+ "<li>Para cargar archivos:--------------------------------->/upload_films/</li>"
					+ "<li>Buscar los actores de una película------------>/FormuVecinos</li>"
					+ "<li>Buscar las películas que tiene un actor ---->/FormuVecinos</li>"
					+ "<li>Buscar la distancia entre dos elementos--->/FormuDistancia</li>"
					+ "<li>Peliculas de una categoria------->/FormuCategorias</li>"
					+ "<li>Categoria del elemento----------->/FormuCPelicula</li>"
					+ "</center></body>";
			System.out.println("Pag principal");
			return pprin;
		}); 

		// Retrieves the file uploaded through the /upload_films HTML form
		// Creates table and stores uploaded file in a two-columns table
		post("/upload", (req, res) -> {
			req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/tmp"));
			String result = "File uploaded!";
			try (InputStream input = req.raw().getPart("uploaded_films_file").getInputStream()) { 
				// getPart needs to use the same name "uploaded_films_file" used in the form

				// Prepare SQL to create table
				Statement statement = connection.createStatement();
				statement.setQueryTimeout(30); // set timeout to 30 sec.
				statement.executeUpdate("drop table if exists films");
				statement.executeUpdate("create table films (film string, actor string)");



				// Read contents of input stream that holds the uploaded file
				InputStreamReader isr = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(isr);
				String s;
				while ((s = br.readLine()) != null) {
					System.out.println(s);

					// Tokenize the film name and then the actors, separated by "/"
					StringTokenizer tokenizer = new StringTokenizer(s, "/");

					// First token is the film name(year)
					String film = tokenizer.nextToken();


					// Now get actors and insert them
					while (tokenizer.hasMoreTokens()) {
						insert(connection, film, tokenizer.nextToken());
					}
					// Commit only once, after all the inserts are done
					// If done after each statement performance degrades
					connection.commit();


				}
				input.close();
			}
			return result;
		});

	}

	static int getHerokuAssignedPort() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		if (processBuilder.environment().get("PORT") != null) {
			return Integer.parseInt(processBuilder.environment().get("PORT"));
		}
		return 4567; // return default port if heroku-port isn't set (i.e. on localhost)
	}
}
