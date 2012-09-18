import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.junit.internal.matchers.TypeSafeMatcher;

public class IsEqualToSuffixTree extends TypeSafeMatcher<SuffixTree> {
	
	private Matcher<String> stringMatcher;
	
	private IsEqualToSuffixTree(SuffixTree expected) {
		this.stringMatcher = CoreMatchers.is(expected.toString());
	}

	@Override
	public void describeTo(Description description) {
		stringMatcher.describeTo(description);
	}

	@Override
	public boolean matchesSafely(SuffixTree actual) {
		return stringMatcher.matches(actual.toString());
	}

	
	
	@Factory
	public static <T> TypeSafeMatcher<SuffixTree> equalToTree(SuffixTree expected) {
		return new IsEqualToSuffixTree(expected);
	}
}
