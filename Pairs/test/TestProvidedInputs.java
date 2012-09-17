import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.Test;


public class TestProvidedInputs {

	@Test
	public void testProvidedInputs00() throws Exception {
		testProvidedInputs("00");
	}

	@Test
	public void testProvidedInputs01() throws Exception {
		testProvidedInputs("01");
	}

	@Test
	public void testProvidedInputs02() throws Exception {
		testProvidedInputs("02");
	}

	@Test
	public void testProvidedInputsFromQuestion() throws Exception {
		testProvidedInputs("FromQuestion");
	}


	private void testProvidedInputs(String caseSuffix) throws Exception {
		
		ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
		PrintStream output = new PrintStream(outputBytes);
		
		Solution solution = new Solution(inputStreamForCase(caseSuffix), output);
		solution.start();
		
		String actualOutput = new String(outputBytes.toByteArray());

		assertEquals(readExpectedOutput(caseSuffix).trim(), actualOutput.trim());
	}

	private BufferedReader inputStreamForCase(String caseSuffix)
			throws FileNotFoundException {
		String filename = "Test Cases/input" + caseSuffix + ".txt";
		BufferedReader input = 
		          new BufferedReader(new FileReader(filename));
		return input;
	}

	private String readExpectedOutput(String caseSuffix)
			throws FileNotFoundException {
		String filename = "Test Cases/output" + caseSuffix + ".txt";
		String expectedOutput = new Scanner(new File(filename)).useDelimiter("\\A").next();
		return expectedOutput;
	}

}
