import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;



public class Solution {
	
	private BufferedReader inputStream;
	private PrintStream outputStream;
	
	int numberOfDiscs;
	int numberOfPegs;
	private int[] currentConfig;
	private int[] endConfig;
	private ArrayList<String> moveLog;
	
	static final int MAX_RADIUS = 8;
	static final int MAX_PEGS = 5;
	
	public static void main(String[] args) {
		try {
			BufferedReader sysIn = 
		          new BufferedReader(new InputStreamReader(System.in));
			
			Solution solution = new Solution(sysIn, System.out);
			solution.start();
			
		} catch (IOException e) {
	        System.err.println("Error:" + e.getMessage());
		}
	}
	
	public Solution(BufferedReader inputStream, PrintStream outputStream) throws IOException {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		this.moveLog = new ArrayList<String>();
	}
	
	public void start() throws IOException {
		readConfiguration();
		solve();
		writeMoveLog();
	}

	private void solve() {
		int largestMisplacedDisc = largestMisplacedDisc();
		
		while (largestMisplacedDisc() >= 0) {
			int targetPeg = endConfig[largestMisplacedDisc];
			moveDisc(largestMisplacedDisc, targetPeg);
			
			largestMisplacedDisc = largestMisplacedDisc();
		}
	}
	
	private void moveDisc(int discToMove, int targetPeg) {
		int discBlockingMove = discBlockingMoveOf(discToMove);
		
		if (isValidDisc(discBlockingMove)) {
			int pegToSwapOn = goodSwapPegExcluding(currentConfig[discToMove], targetPeg);
			moveDisc(discBlockingMove, pegToSwapOn);
		}
		else {
			performMove(discToMove, targetPeg);
		}
	}

	private void performMove(int discToMove, int targetPeg) {
		logMove(discToMove, targetPeg);
		currentConfig[discToMove] = targetPeg;
	}

	private void logMove(int discToMove, int targetPeg) {
		moveLog.add(logPegFromPegId(currentConfig[discToMove]) + " " + logPegFromPegId(targetPeg));
	}
	
	private String logPegFromPegId(int peg) {
		return Integer.toString(peg);
	}

	private int goodSwapPegExcluding(int excludePegA, int excludePegB) {
		
		for (int peg = 1; peg <= MAX_PEGS; peg++) {
			if (	(peg != excludePegA)
				&& (peg != excludePegB)
				&& isPegEmpty(peg)) {
				
				return peg;
			}
		}
		
		if ((excludePegA != 1) && (excludePegB != 1)) {
			return 1;
		}
		else if ((excludePegA != 2) && (excludePegB != 2)) {
			return 2;
		}
		else {
			return 3;
		}
	}

	private boolean isPegEmpty(int peg) {
		for (int disc=0; disc < currentConfig.length; disc++) {
			if (currentConfig[disc] == peg) {
				return false;
			}
		}
		return true;
	}

	private boolean isValidDisc(int disc) {
		return disc >= 0;
	}

	private int discBlockingMoveOf(int discToMove) {
		int candidateDisc = discToMove-1;
		while (isValidDisc(candidateDisc) && (currentConfig[candidateDisc] != currentConfig[discToMove])) {
			candidateDisc--;
		}
		return candidateDisc;
	}

	private int largestMisplacedDisc() {
		int disc = currentConfig.length-1;
		while (isValidDisc(disc) && (currentConfig[disc] == endConfig[disc])) {
			disc--;
		}
		return disc;
	}
	
	private void readConfiguration() throws IOException {
		int[] inputArgs = readIntArray();
		numberOfDiscs = inputArgs[0];
		numberOfPegs = inputArgs[1];

		currentConfig = readIntArray();
		endConfig = readIntArray();

//		outputStream.println("Discs: " + numberOfDiscs + " Pegs: " + numberOfPegs);
	}
	
	private int[] readIntArray() throws IOException {
		String[] strings = inputStream.readLine().split("\\s");
		int[] ints = new int[strings.length];
		for (int i=0; i<strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}
		return ints;
	}

	private void writeMoveLog() {
		outputStream.println(Integer.toString(moveLog.size()));
		for (String logEntry : moveLog) {
			outputStream.println(logEntry);
		}
	}
}
