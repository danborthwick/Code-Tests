import static org.junit.Assert.*;

import static org.hamcrest.core.IsInstanceOf.*;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class SuffixTreeTest {

	class B extends SuffixTree.Builder {};
	
	private static TypeSafeMatcher<SuffixTree> isEqualToTree(SuffixTree expected) {
		return IsEqualToSuffixTree.equalToTree(expected);
	}
	
	@Test
	public void testWhenASingleLetterIsAdded_thenTreeContainsLetter() {
		SuffixTree tree = new SuffixTree();
		tree.add('a');
		
		assertTrue(tree.contains("a"));
	}
	
	@Test
	public void testWhenASingleLetterIsAdded_thenTreeHasRootNodeWithLeafChild() {
		SuffixTree tree = new SuffixTree();
		tree.add('a');
		
		assertThat(tree.root, instanceOf(SuffixTree.ExplicitNode.class));
		assertThat(tree.root.child('a'), instanceOf(SuffixTree.LeafNode.class));
	}

	@Test
	public void testWhenTwoLettersAreAdded_thenTreeContainsFullSuffix() {
		SuffixTree tree = new SuffixTree();
		tree.add("ab");
		
		assertTrue(tree.contains("ab"));
	}
	
	@Test
	public void testWhenTwoLettersAreAdded_thenTreeContainsPartialSuffices() {
		SuffixTree tree = new SuffixTree();
		tree.add("ab");
		
		assertTrue(tree.contains("a"));
		assertTrue(tree.contains("b"));
	}

	@Test
	public void testWhenABIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree();
		actual.add("ab");

		SuffixTree expected = B.tree(
				B.leaf("ab"),
				B.leaf("b")
				);

		assertThat(actual, isEqualToTree(expected));
	}

	@Test
	public void testWhenBookIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree();
		actual.add("book");

		SuffixTree expected = B.tree(
				B.leaf("book"),
				B.leaf("k"),
				B.explicit("o", 
						B.leaf("k"), B.leaf("ok")
						)
				);
		
		assertThat(actual, isEqualToTree(expected));
	}

}
