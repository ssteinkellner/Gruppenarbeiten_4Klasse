import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.*;


public class Test_GummiEnte {
	GummiEnte ente;
	private ByteArrayOutputStream outContent;
	private PrintStream original;
	
	@Before
	public void setup(){
		ente = new GummiEnte();
		outContent = new ByteArrayOutputStream();
		original = System.out;
		System.setOut(new PrintStream(outContent));
	}
	
	@Test
	public void test_quaken(){
		ente.quaken();
		assertEquals("Quietsch\r\n",outContent.toString());
	}
	
	@After
	public void tearDown() throws Exception {
		System.setOut(original);
	}
}
