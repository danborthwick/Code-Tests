import java.util.ArrayList;


public class ParenthesesSanitiser {
	
	private char[] input;
	private ArrayList<ScanResult> scanResults;
	private int depth;
	
	enum ScanResult {
		SKIP,
		COPY,
		OPEN_PARENTHESIS,
		CLOSE_PARENTHESIS
	};

	public ParenthesesSanitiser(String input) {
		this.input = input.toCharArray();
		this.scanResults = new ArrayList<ScanResult>(input.length());
	}
	
	public String sanitised() {
		scan();
		skipUnclosedOpeningParenthesesFromScanResults();
		return createOutput();
	}

	private void scan() {
		depth = 0;

		for (int i=0; i<input.length; i++) {
			ScanResult result = ScanResult.COPY;

			switch (input[i]) {
			case '(':
				result = ScanResult.OPEN_PARENTHESIS;
				depth++;
				break;

			case ')':
				if (depth > 0) {
					result = ScanResult.CLOSE_PARENTHESIS;
					depth--;
				}
				else {
					result = ScanResult.SKIP;
				}
				break;
			}
			scanResults.add(result);
		}
	}
	
	private void skipUnclosedOpeningParenthesesFromScanResults() {
		for (int position = scanResults.size()-1; 
				((depth > 0) && (position >= 0));
				position--) {
			
			if (scanResults.get(position) == ScanResult.OPEN_PARENTHESIS) {
				scanResults.set(position, ScanResult.SKIP);
				depth--;
			}
		}
	}

	private String createOutput() {
		StringBuffer output = new StringBuffer(input.length);

		for (int i=0; i<input.length; i++) {
			if (scanResults.get(i) != ScanResult.SKIP) {
				output.append(input[i]);
			}
		}
		
		return output.toString();
	}
}
