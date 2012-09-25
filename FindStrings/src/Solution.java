import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 * https://www.interviewstreet.com/challenges/dashboard/#problem/4efa210eb70ac
 */
//TODO: Use a suffix tree
public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	private SuffixTree suffixTree;
	
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
		this.suffixTree = new SuffixTree();
	}
	
	public void start() throws Exception {

		int numberOfStrings = readInt();
		for (int i=0; i<numberOfStrings; i++) {
			suffixTree.add(inputStream.readLine());
		}
		
		ArrayList<Integer> queries = readQueries();
		
		for (Integer query : queries) {
			int queryIndex = query.intValue() - 1;
			try {
				outputStream.println(suffixTree.suffixAtPosition(queryIndex));
			}
			catch (IndexOutOfBoundsException e) {
				outputStream.println("INVALID");
			}
		}
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
	
	private ArrayList<Integer> readQueries() throws IOException {
		int numberOfQueries = readInt();
		ArrayList<Integer> queries = new ArrayList<Integer>(numberOfQueries);
		for (int i=0; i<numberOfQueries; i++) {
			queries.add(readInt());
		}
		return queries;
	}
}
