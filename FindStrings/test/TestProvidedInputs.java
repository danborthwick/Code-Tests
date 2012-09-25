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
	public void testLongDuplicates() throws Exception {
		testProvidedInputs("Long");
	}

	@Test
	public void testRandomLong() throws Exception {
		testProvidedInputs("RandomLong");
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
		Scanner scanner = new Scanner(new File(filename)).useDelimiter("\\A");
		
		if (scanner.hasNext())
			return scanner.next();
		else
			return "";
	}

}
