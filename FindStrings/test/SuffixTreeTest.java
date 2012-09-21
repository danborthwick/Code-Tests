import static org.junit.Assert.*;

import static org.hamcrest.core.IsInstanceOf.*;
import org.junit.Test;
import org.junit.internal.matchers.TypeSafeMatcher;

public class SuffixTreeTest {

	class B extends SuffixTree.Builder {};
	
	private static TypeSafeMatcher<SuffixTree> isEqualToTree(SuffixTree expected) {
		return IsEqualToSuffixTree.equalToTree(expected);
	}
	
	private static TypeSafeMatcher<SuffixTree.Node> isEqualToNode(SuffixTree.Node expected) {
		return IsEqualToNode.equalToNode(expected);
	}
	
	@Test
	public void testWhenASingleLetterIsAdded_thenTreeContainsLetter() {
		SuffixTree tree = new SuffixTree("a");
		
		assertTrue(tree.contains("a"));
	}
	
	@Test
	public void testWhenASingleLetterIsAdded_thenTreeHasRootNodeWithLeafChild() {
		SuffixTree tree = new SuffixTree("a");
		
		assertThat(tree.root, instanceOf(SuffixTree.ExplicitNode.class));
		assertThat(tree.root.child('a'), instanceOf(SuffixTree.LeafNode.class));
	}

	@Test
	public void testWhenTwoLettersAreAdded_thenTreeContainsFullSuffix() {
		SuffixTree tree = new SuffixTree("ab");
		
		assertTrue(tree.contains("ab"));
	}
	
	@Test
	public void testWhenStringWithNoRepetitionsIsAdded_thenTreeContainsPartialSuffices() {
		SuffixTree tree = new SuffixTree("abc");
		
		assertTrue(tree.contains("a"));
		assertTrue(tree.contains("b"));
		assertTrue(tree.contains("c"));
		assertTrue(tree.contains("ab"));
		assertTrue(tree.contains("bc"));
	}

	@Test
	public void testWhenStringWithNoRepetitionsIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree("abc");

		SuffixTree expected = B.tree(
				B.leaf("abc"),
				B.leaf("bc"),
				B.leaf("c")
				);

		assertThat(actual, isEqualToTree(expected));
	}

	/**
	 * 
	 */
	@Test
	public void testWhenNodeWithChildrenIsSplitAgain_thenNodeHasExpectedForm() {
		SuffixTree.Node actual = B.explicit(
				"ab", 
				B.leaf("cabx"), 
				B.leaf("x"));
		
		SuffixTree.Node expected = B.explicit(
				"ab", 
				B.explicit("c",
						B.leaf("abx")), 
				B.leaf("x"));
		
		SuffixTree.Node returned = actual.split(new StringBuilder("abc"));
		
		assertThat(actual, isEqualToNode(expected));
		assertEquals("c", returned.suffix.toString());
	}
	@Test
	// Case matches example in 
	// http://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
	public void testWhenTreeWithDepthTwoIsCreated_thenHasExpectedForm() {
		SuffixTree actual = new SuffixTree("abcabx");
		
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
	public void testWhenTreeWithDepthTwoIsCreated_thenContainsSplitSuffix() {
		SuffixTree actual = new SuffixTree("abcabx");
		
		assertTrue(actual.contains("abcabx"));
	}

	@Test
	// Case matches example in 
	// http://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
	public void testWhenTreeWithDepthThreeIsCreated_thenHasExpectedForm() {
		SuffixTree actual = new SuffixTree("abcabxabcd");

		SuffixTree expected = B.tree(
				B.leaf("xabcd"),
				B.leaf("d"),
				B.explicit("b",
						B.leaf("xabcd"),
						B.explicit("c", 
								B.leaf("d"),
								B.leaf("abxabcd"))
						),
				B.explicit("ab",
						B.leaf("xabcd"),
						B.explicit("c",
								B.leaf("d"),
								B.leaf("abxabcd"))
						),
				B.explicit("c",
						B.leaf("abxabcd"),
						B.leaf("d"))
				);
		
		assertThat(actual, isEqualToTree(expected));
	}
	

	@Test
	public void testWhenBookIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree("book");

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
