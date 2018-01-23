package urjc.isi.practica_final_isi;

/******************************************************************************
 *  Compilation:  javac IndexGraph.java
 *  Dependencies: Graph.java In.java
 *  Execution:    java IndexGraph movies.txt "/"
 *  Data files:   https://introcs.cs.princeton.edu/45graph/tinyGraph.txt
 *                https://introcs.cs.princeton.edu/45graph/movies.txt
 *                https://introcs.cs.princeton.edu/45graph/amino.csv
 *  
 *  Builds a graph, then accepts vertex names from standard input
 *  and prints its neighbors.
 *
 *  % java IndexGraph tinyGraph.txt " "
 *  C
 *    A
 *    B
 *    G
 *  A
 *    B
 *    C
 *    G
 *    H
 *
 ******************************************************************************/

public class IndexGraph {

    public static String main(String[] args) {
    	String filename = args[0];
        String delimiter = args[1];
        String peticion = args[2];
        String respuesta = "";
        Graph G = new Graph(filename, delimiter);

        // read a vertex and print its neighbors
        while (!StdIn.isEmpty()) {
            String v = StdIn.readLine();
            if (G.hasVertex(peticion)) {
                for (String w : G.adjacentTo(peticion)) {
                	respuesta += "  " + w + "\n";
                }
            }
        }
		return respuesta;

    }

	public static String index(String filename, String delimiter, String peticion) {
		String respuesta = "";
        Graph G = new Graph(filename, delimiter);

        // read a vertex and print its neighbors
        while (!StdIn.isEmpty()) {
            String v = StdIn.readLine();
            if (G.hasVertex(peticion)) {
                for (String w : G.adjacentTo(peticion)) {
                	respuesta += "  " + w + "\n";
                }
            }
        }
		return respuesta;
		
	}
}
