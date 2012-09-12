import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;



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
		
		for (KeyIterator iterator = new KeyIterator(numberOfDigits, maximumDigit);
				iterator.hasNext();
				) 
		{
			IntArray key = iterator.next();
			
			boolean keyIsValid = true;
			for (Guess guess : guesses) {
				if (!keyMatchesGuess(key, guess)) {
					keyIsValid = false;
					break;
				}
			}
			
			if (keyIsValid)
				return true;
		}
		return false;
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
			guess.score = guess.digits.remove(numberOfDigits-1).value;
			guesses.add(guess);
		}
		return guesses;
	}
	
	class KeyIterator implements Iterator<IntArray>
	{
		int numberOfDigits;
		int maximumDigit;
		IntArray currentGuess;
		
		public KeyIterator(int numberOfDigits, int maximumDigit) {
			this.numberOfDigits = numberOfDigits;
			this.maximumDigit = maximumDigit;
			this.currentGuess = new IntArray(numberOfDigits);
			
			for (int digit=0; digit<numberOfDigits; digit++) {
				MyInt myInt = new MyInt();
				myInt.value = 1;
				currentGuess.add(myInt);
			}
		}
		
		@Override
		public boolean hasNext() {
			for (MyInt digit : currentGuess) {
				if (digit.value != maximumDigit) {
					return true;
				}
			}
			return false;
		}

		@Override
		public IntArray next() {
			for (MyInt digit : currentGuess) {
				if (digit.value < maximumDigit) {
					digit.value++;
					break;
				}
				else {
					digit.value = 1;
				}
			}
			return currentGuess;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
		}
		
	}
}
