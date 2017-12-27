package urjc.isi.pruebasMockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


import static org.mockito.Mockito.*;


import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class MockitoTests {
// Mock : objecto simulado -- Necesidad de pruebas unitarias aisladas sin necesidad de realizar mas pruebas sobre otros metodos anidados.
	@Test
	public void MockListSize() {
		List list = mock(List.class); // mock(recubrimiento) : esta dentro de Mockito- mock es un sustituto de la calse que pasamos
		when(list.size()).thenReturn(10); // identificamos como se va a comportar. Vamos añadiendo funcionalidad del opbjecto mock creado.
		assertEquals(10, list.size()); // nos compara con el list.size() del objecto mock sobre el cual se "suplantado" la clase list
	}

	@Test
	public void MockListSizeWithMultipleReturnValues() {
		List list = mock(List.class);
		when(list.size()).thenReturn(10).thenReturn(20); // si llaman a size una vez es 10 y las siguientes veces es 20 (ultimo size que hayamos indicado)
		assertEquals(10, list.size()); // Primera llamada
		assertEquals(20, list.size()); // Segunda llamada
		assertEquals(20, list.size()); // Tercera llamada y 
		      						   // subsiguientes devuelven el mismo valor
	  //assertEquals(20, list.size()); // si añadimos otra llamada fallaria

		
		verify(list, times(3)).size(); // verifica cuantas iteracciones se han realizado sobre el mock object -- verify es semejante a un assert
	}

	@Test
	public void MockListGet() {
		List<String> list = mock(List.class);
		when(list.get(0)).thenReturn("Hello World"); //solo si la llamada al metodo get con un 0 de argumento pondra "Hello", en el resto null
		assertEquals("Hello World", list.get(0));
		assertNull(list.get(1)); // en el resto de los casos de la llamada a get -- devuelve null 
	}

	@Test
	public void MockListGetWithAny() {
		List<String> list = mock(List.class);
		when(list.get(anyInt())).thenReturn("Hello World"); // con anyInt() no especifica el valor que se le psa a get.
		when(list.get(3)).thenReturn("Bye World"); // indicamos el int explicitamente para que tenga ese valor
		
		assertEquals("Hello World", list.get(0));
		assertEquals("Hello World", list.get(1));
		assertEquals("Hello World", list.get(2));
		assertEquals("Bye World", list.get(3));
		
		
	}
	
	@Test
	public void MockIterator_will_return_hello_world(){
		Iterator i = mock(Iterator.class);
		when(i.next()).thenReturn("Hello").thenReturn("World");

		String result=i.next()+" "+i.next();

		assertEquals("Hello World", result);
	}


	@Test
	public void MockWithArguments(){
		Comparable c=mock(Comparable.class);
		when(c.compareTo("Test")).thenReturn(1);
		assertEquals(1,c.compareTo("Test"));
		assertEquals(0,c.compareTo("Foo"));
	}
	

	@Test
	public void MockWithUnspecifiedArguments(){
		Comparable c=mock(Comparable.class);
		when(c.compareTo(anyInt())).thenReturn(-1);
		when(c.compareTo(3)).thenReturn(0);
		assertEquals(-1, c.compareTo(5));
		assertEquals(0, c.compareTo(3));


		// preguntamos al objeto mock 
		verify(c).compareTo(5); // se ha llamado a mock con el obejto 5
		verify(c).compareTo(3); // se ha llamado a mock con el objeto 3
		verify(c, never()).compareTo(25); // never( ) -- nunca se llama a mock con el arg 25 ...
		verify(c, times(1)).compareTo(5);
		verify(c, atLeastOnce()).compareTo(5);// atLeastOnce() -- al menos se ha llamado a compareTo de mock una vez
		verify(c, atLeast(1)).compareTo(5); // alLeast(N) -- al menos se ha llamado esas N veces
	}


	@Test(expected=IOException.class)
	public void MockOutputStreamWriterRethrowsAnExceptionFromOutputStream() 
			throws IOException{
		OutputStream mock=mock(OutputStream.class);
		OutputStreamWriter osw=new OutputStreamWriter(mock);
		doThrow(new IOException()).when(mock).close();
		osw.close();
		
		verify(mock).close();

	}
	

	@Test
	public void MockOutputStreamWriterClosesOutputStreamOnClose()
			throws IOException{
		OutputStream mock=mock(OutputStream.class);
		OutputStreamWriter osw=new OutputStreamWriter(mock);
		osw.close();
		verify(mock).close();
	}
// Help -> Eclipse Markplace -> Pitclipse
	// pit: va a realizar "putadas" y comprobando de nuevo los test --> pruebas de mutacion, los test si tenemos bien programado deberian fallar, si los pasan estan mal !!!
	// pit ok .. si fallan los test , pit KO , si los test fallan
}