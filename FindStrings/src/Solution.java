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
//TODO: Use a suffix tree
public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	class SubString implements Cloneable, Comparable<SubString> {
		
		public char[] sourceChars;
		public int startIndex;
		public int endIndex;
		public int hashCode;
		
		public SubString(char[] sourceString) {
			this.sourceChars = sourceString;
		}

		@Override
		public int hashCode() {
			return hashCode;
		}

		private int length() {
			return (endIndex - startIndex);
		}

		@Override
		public String toString() {
			return String.copyValueOf(sourceChars, startIndex, length());
		}

		@Override
		public int compareTo(SubString other) {
			
			for (int thisPosition = this.startIndex, otherPosition = other.startIndex;
					(thisPosition < this.endIndex) && (otherPosition < other.endIndex);
					thisPosition++, otherPosition++) {
				
				int charDiff = this.sourceChars[thisPosition] - other.sourceChars[otherPosition]; 
				if (charDiff < 0) {
					return -1;
				}
				else if (charDiff > 0) {
					return 1;
				}
			}
			
			return Integer.signum(this.length() - other.length());
		}
		
		@Override
		public Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			return clone;
		}
		
		@Override
		public boolean equals(Object otherObject) {

	        SubString other = (SubString)otherObject;
	        return (this.hashCode == other.hashCode);
	        
//	        if (this.hashCode != other.hashCode) {
//	        	return false;
//	        }
//	        return true;
	        
//	        if ((endIndex - startIndex) != (other.endIndex - other.startIndex)) {
//	        	return false;
//	        }
	        
//	        int thisLength = this.length();
//			if (thisLength != other.length())
//				return false;
//			
//			for (int offset = 0; offset < thisLength; offset++) {
//				if (sourceChars[startIndex + offset] != other.sourceChars[other.startIndex + offset]) {
//					return false;
//				}
//			}
			
//			return true;
		}
	};
	
	@SuppressWarnings("serial")
	class SubStringMap extends HashSet<SubString> {
		public SubStringMap() {
			super(10000000);
		}
	};
	
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
		ArrayList<char[]> strings = new ArrayList<char[]>(numberOfStrings);
		for (int i=0; i<numberOfStrings; i++) {
			strings.add(inputStream.readLine().toCharArray());
		}
		
		ArrayList<Integer> queries = readQueries();
		
		SubStringMap subStringSet = getAllSubStringsOf(strings);
		//SubString[] sortedSubStrings = (SubString[]) subStringSet.toArray();
		
		SubString[] sortedSubStrings = new SubString[subStringSet.size()];
		int i=0;
		for (SubString subString : subStringSet) {
			sortedSubStrings[i++] = subString;
		}
		
		
		Arrays.sort(sortedSubStrings);
		System.out.println(sortedSubStrings.length);
		
		for (Integer query : queries) {
			int queryIndex = query.intValue() - 1;
			if (queryIndex < sortedSubStrings.length) {
				outputStream.println(sortedSubStrings[queryIndex].toString());
			}
			else {
				outputStream.println("INVALID");				
			}
		}
	}

	private SubStringMap getAllSubStringsOf(ArrayList<char[]> strings) throws CloneNotSupportedException {
		SubStringMap map = new SubStringMap();
		
		for (char[] sourceString : strings) {
			addSubStrings(sourceString, map);
		}
				
		return map;
	}

	private void addSubStrings(char[] sourceString, SubStringMap map) throws CloneNotSupportedException {
		int stringLength = sourceString.length;
		SubString workingSub = new SubString(sourceString);
		
		for (workingSub.startIndex = 0; workingSub.startIndex < stringLength; workingSub.startIndex++) {
			workingSub.hashCode = 0;
			
			for (workingSub.endIndex = workingSub.startIndex+1; workingSub.endIndex <= stringLength; workingSub.endIndex++) {
				
			    // s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]
				workingSub.hashCode *= 31;
				workingSub.hashCode += sourceString[workingSub.endIndex-1];

				if (map.add(workingSub)) {
					workingSub = (SubString) workingSub.clone();
				}
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
