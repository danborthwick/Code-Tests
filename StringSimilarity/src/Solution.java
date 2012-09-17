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
		int numberOfCases = readInt();
		
		for (int caseId = 0; caseId < numberOfCases; caseId++) {
			String caseString = inputStream.readLine();
			outputStream.println(similarityOf(caseString));
		}
	}

	private int similarityOf(String string) {
		
		int similarity = 0;
		int stringLength = string.length();
		char[] characters = string.toCharArray();
		
		for (int suffixStart=0; suffixStart < stringLength; suffixStart++) {
			int subSimilarity = 0;
			while (   ((suffixStart + subSimilarity) < stringLength)
				   && (characters[subSimilarity] == characters[suffixStart + subSimilarity])) {
				subSimilarity++;
			}
			similarity += subSimilarity;
		}
		
		return similarity;
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
