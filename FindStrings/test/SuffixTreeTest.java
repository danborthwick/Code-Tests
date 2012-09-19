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
		tree.add("a");
		
		assertTrue(tree.contains("a"));
	}
	
	@Test
	public void testWhenASingleLetterIsAdded_thenTreeHasRootNodeWithLeafChild() {
		SuffixTree tree = new SuffixTree();
		tree.add("a");
		
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
	public void testWhenStringWithNoRepetitionsIsAdded_thenTreeContainsPartialSuffices() {
		SuffixTree tree = new SuffixTree();
		tree.add("abc");
		
		assertTrue(tree.contains("a"));
		assertTrue(tree.contains("b"));
		assertTrue(tree.contains("c"));
		assertTrue(tree.contains("ab"));
		assertTrue(tree.contains("bc"));
	}

	@Test
	public void testWhenStringWithNoRepetitionsIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree();
		actual.add("abc");

		SuffixTree expected = B.tree(
				B.leaf("abc"),
				B.leaf("bc"),
				B.leaf("c")
				);

		assertThat(actual, isEqualToTree(expected));
	}

	@Test
	// Case matches example in 
	// http://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
	public void testWhenABCABXABCDIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree();
		actual.add("abcabx");

		SuffixTree expected = B.tree(
				B.explicit("ab",
						B.leaf("x"),
						B.leaf("cabx")),
				B.explicit("b",
						B.leaf("cabx"),
						B.leaf("x")),
				B.leaf("cabx"),
				B.leaf("x")
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
