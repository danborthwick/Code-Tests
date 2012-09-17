import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;



public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	public static void main(String[] args) {
		try {
			BufferedReader sysIn = 
		          new BufferedReader(new InputStreamReader(System.in));
			
			Solution solution = new Solution(sysIn, System.out);
			solution.start();
			
		} catch (Exception e) {
	        System.err.println("Error:" + e.getMessage());
		}
	}
	
	public Solution(BufferedReader inputStream, PrintStream outputStream) throws IOException {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	public void start() throws Exception {
		int[] configuration = readIntArray();
		int difference = configuration[1];
		
		int[] numbers = readIntArray();
		Arrays.sort(numbers);
		
		outputStream.println(numberOfPairs(numbers, difference));
		
	}

	private int numberOfPairs(int[] numbers, int expectedDifference) {
		int numberCount = numbers.length;
		int matchesFound = 0;
		
		for (int firstIndex = 0; firstIndex < numberCount; firstIndex++) {
			for (int secondIndex = firstIndex+1; secondIndex < numberCount; secondIndex++) {
				int actualDifference = numbers[secondIndex] - numbers[firstIndex];
				
				if (actualDifference > expectedDifference)
					break;
				
				if (actualDifference == expectedDifference)
					matchesFound++;
			}
		}
		return matchesFound;
	}

	private int readInt() throws IOException {
		String lineAsString = inputStream.readLine();
		return Integer.parseInt(lineAsString);
	}
	
	private int[] readIntArray() throws IOException {
		String[] strings = inputStream.readLine().split("\\s");
		int[] ints = new int[strings.length];
		for (int i=0; i<strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}
		return ints;
	}
}
