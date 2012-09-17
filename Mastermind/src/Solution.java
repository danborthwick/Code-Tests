import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;



public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	@SuppressWarnings("serial")
	class IntArray extends ArrayList<MyInt> {

		public IntArray(int numberOfDigits) {
			super(numberOfDigits);
		}
	}
	
	class MyInt {
		public int value;
	}
	
	class Guess {
		public IntArray digits;
		public int score;
	}
			
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
		
		for (int caseId=0; caseId<numberOfCases; caseId++) {
			IntArray configuration = readIntArray();
			
			int maximumDigit = configuration.get(0).value;
			int numberOfDigits = configuration.get(1).value;
			int numberOfGuesses = configuration.get(2).value;
			
			ArrayList<Guess> guesses = readGuesses(numberOfDigits, numberOfGuesses);
			
			
			boolean hasViableKey = hasViableKey(maximumDigit, numberOfDigits, guesses);
			outputStream.println(hasViableKey ? "Yes" : "No");
		}
	}

	private boolean hasViableKey(int maximumDigit, int numberOfDigits,
			ArrayList<Guess> guesses) {
		
		for (Guess firstGuess : guesses) {
			for (Guess secondGuess : guesses) {
				if (firstGuess == secondGuess)
					continue;
				
				int sharedElements = matchCount(firstGuess.digits, secondGuess.digits);
				int totalMatches = firstGuess.score + secondGuess.score;
				if ((totalMatches - sharedElements) > numberOfDigits) {
					return false;
				}
			}
		}
		
		return true;
	}

	private boolean keyMatchesGuess(IntArray key, Guess guess) {
		int score = matchCount(key, guess.digits);
		return (score == guess.score);
	}

	private int matchCount(IntArray keyA, IntArray keyB) {
		int keySize = keyA.size();
		int score = 0;
		
		for (int i=0; i<keySize; i++) {
			if (keyA.get(i).value == keyB.get(i).value) {
				score++;
			}
		}
		return score;
	}

	private int readInt() throws IOException {
		String lineAsString = inputStream.readLine();
		return Integer.parseInt(lineAsString);
	}
	
	private IntArray readIntArray() throws IOException {
		String[] strings = inputStream.readLine().split("\\s");
		IntArray ints = new IntArray(strings.length);
		for (int i=0; i<strings.length; i++) {
			MyInt myInt = new MyInt();
			myInt.value = Integer.parseInt(strings[i]);
			ints.add(myInt);
		}
		return ints;
	}

	private ArrayList<Guess> readGuesses(int numberOfDigits, int numberOfGuesses)
			throws IOException {
		ArrayList<Guess> guesses = new ArrayList<Guess>(numberOfGuesses);
		
		for (int guessId=0; guessId<numberOfGuesses; guessId++) {
			Guess guess = new Guess();
			guess.digits = readIntArray();
			guess.score = guess.digits.remove(numberOfDigits).value;
			guesses.add(guess);
		}
		return guesses;
	}
}
