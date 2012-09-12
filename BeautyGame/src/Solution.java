import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;



public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	final boolean FIRST_PLAYER = true;
	final boolean SECOND_PLAYER = false;
		
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
		
		for (int i=0; i<numberOfCases; i++) {
			int n = readInt();
			boolean winner = playerWinning(n, FIRST_PLAYER);
			outputStream.println(displayNameOfPlayer(winner));
		}
	}

	private boolean playerWinningOld(int n, boolean playerToMove) {
		int beautyOfN = beautyOf(n);
		int kMax = highestKFor(n);
		
		for (int k=kMax; k >= 0; k--) {
			int twoToTheK = 1 << k;
			int nMinusTwoToTheK = n - twoToTheK;
			
			if (	(beautyOfN == beautyOf(nMinusTwoToTheK))
				&& (playerWinning(nMinusTwoToTheK, !playerToMove) == playerToMove)) {
				return playerToMove;
			}
		}
		
		return !playerToMove;
	}
	
	private boolean playerWinning(int n, boolean playerToMove) {
		int possibleMoves = possibleMovesFor(n);
		return ((possibleMoves & 1) == 1) ? playerToMove : !playerToMove;
	}
	
	private int possibleMovesFor(int n) {
		boolean[] bits = bitsFromNumber(n);
		int possibleMoves = 0;
		for (int bitToMove=0; bitToMove < 31; bitToMove++) {
			if (bits[bitToMove]) {
				possibleMoves += zeroesToTheRightOf(bitToMove, bits);
			}
		}
		return possibleMoves;
	}

	
	private boolean[] bitsFromNumber(int n) {
		boolean[] bits = new boolean[31];
		for (int bit=0; bit<31; bit++) {
			bits[bit] = (((n >> bit) & 1) == 1);
		}
		return bits;
	}

	private int zeroesToTheRightOf(int highBit, boolean[] bits) {
		int count = 0;
		for (int bit = highBit-1; bit >= 0; bit--) {
			if (!bits[bit]) {
				count++;
			}
		}
		return count;
	}


	private int highestKFor(int n) {
		int k=0;
		while (n > 1) {
			k++;
			n >>= 1;
		}
		return k;
	}

	private int beautyOf(int n) {
		int beauty = 0;
		while (n != 0) {
			if ((n & 1) == 1) {
				beauty++;
			}
			n >>= 1;
		}
		return beauty;
	}
	
	private String displayNameOfPlayer(boolean player) {
		return (player == FIRST_PLAYER) ? "First Player" : "Second Player";
	}

	private int readInt() throws IOException {
		String lineAsString = inputStream.readLine();
		return Integer.parseInt(lineAsString);
	}

}
