import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class IsEqualToNode extends TypeSafeMatcher<SuffixTree.Node> {
	
	private Matcher<String> stringMatcher;
	
	private IsEqualToNode(SuffixTree.Node expected) {
		this.stringMatcher = CoreMatchers.is(expected.toString());
	}

	@Override
	public void describeTo(Description description) {
		stringMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(SuffixTree.Node actual) {
		return stringMatcher.matches(actual.toString());
	}
	
	@Factory
	public static <T> TypeSafeMatcher<SuffixTree.Node> equalToNode(SuffixTree.Node expected) {
		return new IsEqualToNode(expected);
	}
}
