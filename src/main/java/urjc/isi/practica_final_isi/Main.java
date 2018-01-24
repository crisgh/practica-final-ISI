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

	public static String select(Connection conn, String table, String film) {
		String sql = "SELECT * FROM " + table + " WHERE film=?";

		String result = new String();

		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, film);
			ResultSet rs = pstmt.executeQuery();
			// Commit after query is executed
			connection.commit();

			while (rs.next()) {
				// read the result set
				result += "film = " + rs.getString("film") + "\n";
				System.out.println("film = "+rs.getString("film") + "\n");

				result += "actor = " + rs.getString("actor") + "\n";
				System.out.println("actor = "+rs.getString("actor")+"\n");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

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


	public static void insert_distancia(Connection conn, String elem1, String elem2, String result) throws SQLException {
		// Prepare SQL to create table
		//Statement statement = connection.createStatement();
		// statement.setQueryTimeout(30); // set timeout to 30 sec.
		//statement.executeUpdate("drop table if exists Tabla_distancia");
		//statement.executeUpdate("create table Tabla_distancia (elem1 string, elem2 string ,result string)");

		String sql = "INSERT INTO Tabla_distancia (elem1 , elem2  ,result ) VALUES(?,?,?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, elem1);
			pstmt.setString(2, elem2);
			pstmt.setString(3, result);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public static void insert_vecinos(Connection conn, String peticion, String vecino) throws SQLException {
		// Prepare SQL to create table
		//Statement statement = connection.createStatement();
		// statement.setQueryTimeout(30); // set timeout to 30 sec.
		//statement.executeUpdate("drop table if exists Tabla_vecinos");
		//statement.executeUpdate("create table Tabla_vecinos (peticion string, vecino string)");

		String sql = "INSERT INTO Tabla_vecinos (peticion , vecino ) VALUES(?,?)";
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, peticion);
			pstmt.setString(2, vecino);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
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


		post("/FormuDistancia", Main::FormuDistancia);
		post("/Distance", Main::doDistance);

		post("/FormuVecinos", Main::FormuVecinos);
		post("/vecinos", Main::doVecinos);
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
