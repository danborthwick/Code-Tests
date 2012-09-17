import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * https://www.interviewstreet.com/challenges/dashboard/#problem/4efa210eb70ac
 */
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

		int numberOfStrings = readInt();
		ArrayList<String> strings = new ArrayList<String>(numberOfStrings);
		for (int i=0; i<numberOfStrings; i++) {
			strings.add(inputStream.readLine());
		}
		
		ArrayList<Integer> queries = readQueries();
		
		String[] allSubStrings = getAllSubStringsOf(strings);
		Arrays.sort(allSubStrings);
		
		for (Integer query : queries) {
			int queryInt = query.intValue();
			if (queryInt < allSubStrings.length) {
				outputStream.println(allSubStrings[queryInt]);
			}
			else {
				outputStream.println("INVALID");				
			}
		}
	}

	private String[] getAllSubStringsOf(ArrayList<String> strings) {
		HashSet<String> hashSet = new HashSet<String>(10000000);
		
		for (String sourceString : strings) {
			addSubStrings(sourceString, hashSet);
		}
		
		String[] subStrings = new String[hashSet.size()];
		subStrings = hashSet.toArray(subStrings);
		return subStrings;
	}

	private void addSubStrings(String string, HashSet<String> hashSet) {
		int stringLength = string.length();
		for (int startIndex = 0; startIndex < stringLength; startIndex++) {
			for (int endIndex = startIndex; endIndex <= stringLength; endIndex++) {;
				hashSet.add(string.substring(startIndex, endIndex));
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
