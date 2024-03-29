import static org.junit.Assert.*;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.core.IsSame.*;

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
	public void whenASingleLetterIsAdded_thenTreeContainsLetter() {
		SuffixTree tree = new SuffixTree("a");
		
		assertTrue(tree.contains("a"));
	}
	
	@Test
	public void whenASingleLetterIsAdded_thenTreeHasRootNodeWithLeafChild() {
		SuffixTree tree = new SuffixTree("a");
		
		assertThat(tree.root, instanceOf(SuffixTree.ExplicitNode.class));
		assertThat(tree.root.child('a'), instanceOf(SuffixTree.LeafNode.class));
	}

	@Test
	public void whenTwoLettersAreAdded_thenTreeContainsFullSuffix() {
		SuffixTree tree = new SuffixTree("ab");
		
		assertTrue(tree.contains("ab"));
	}
	
	@Test
	public void whenSingleLetterIsAdded_thenSuffixLinkPointsToRoot() {
		SuffixTree tree = new SuffixTree("a");
		
		assertThat(tree.root.child('a').suffixLink, sameInstance((SuffixTree.Node)tree.root));
	}
	
	@Test
	public void whenTwoLettersAreAdded_thenSuffixLinksForLastLetterAreCorrect() {
		SuffixTree tree = new SuffixTree("ab");
		
		SuffixTree.Node fullStringExplicitNode = tree.root.child('a');
		SuffixTree.Node newCharacterNode = tree.root.child('b');
		
		assertThat(fullStringExplicitNode.suffixLink, sameInstance(newCharacterNode));
		assertThat(newCharacterNode.suffixLink, sameInstance((SuffixTree.Node)tree.root));
	}
	
	@Test
	public void whenStringWithNoRepetitionsIsAdded_thenTreeContainsPartialSuffices() {
		SuffixTree tree = new SuffixTree("abc");
		
		assertTrue(tree.contains("a"));
		assertTrue(tree.contains("b"));
		assertTrue(tree.contains("c"));
		assertTrue(tree.contains("ab"));
		assertTrue(tree.contains("bc"));
	}

	@Test
	public void whenStringWithNoRepetitionsIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree("abc");

		SuffixTree expected = B.tree(
				B.leaf("abc"),
				B.leaf("bc"),
				B.leaf("c")
				);

		assertThat(actual, isEqualToTree(expected));
	}

	@Test
	public void whenNodeWithChildrenIsSplitAgain_thenNodeHasExpectedForm() {
		SuffixTree.Node actual = B.explicit(
				"ab", 
				B.leaf("cabx"), 
				B.leaf("x"));
		
		SuffixTree.Node expected = B.explicit(
				"ab", 
				B.explicit("c",
						B.leaf("abx")), 
				B.leaf("x"));
		
		SuffixTree.Node returned = actual.split(SuffixTree.SubString.fromString("abc"));
		
		assertThat(actual, isEqualToNode(expected));
		assertEquals("c", returned.suffix.toString());
	}
	
	@Test
	public void whenNodeIsSplit_thenSuffixLinkIsCopiedToNewParent() {
		SuffixTree.Node child = B.leaf("ab");
		SuffixTree.Node linkDestination = B.leaf("linkdestination");
		child.suffixLink = linkDestination;
		
		SuffixTree.Node newParent = child.split(SuffixTree.SubString.fromString("a"));
		
		assertThat(newParent.suffixLink, sameInstance(linkDestination));
		
	}
	
	@Test
	// Case matches example in 
	// http://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
	public void whenTreeWithDepthTwoIsCreated_thenHasExpectedForm() {
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
	public void whenTreeWithDepthTwoIsCreated_thenContainsSplitSuffix() {
		SuffixTree actual = new SuffixTree("abcabx");
		
		assertTrue(actual.contains("abcabx"));
	}

	@Test
	public void whenTreeWithDepthTwoIsCreated_thenSuffixLinksAreCorrect() {
		SuffixTree tree = new SuffixTree("abcabx");
		
		assertThat(tree.root.child('b').suffixLink, sameInstance(tree.root.child('c')));
	}

	@Test
	// Case matches example in 
	// http://stackoverflow.com/questions/9452701/ukkonens-suffix-tree-algorithm-in-plain-english
	public void whenTreeWithDepthThreeIsCreated_thenHasExpectedForm() {
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
	
	/**
	 * Note the cbd - When adding the final d, should split after b.
	 * This case shows why node instances can't be shared since the aba.. node should not be split. 
	 */
	@Test
	public void whenStringWithRepetitionNotFromFirstCharIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree("abacbd");

		SuffixTree expected = B.tree(
				B.explicit("a",
						B.leaf("bacbd"),
						B.leaf("cbd")),
				B.explicit("b",
						B.leaf("acbd"),
						B.leaf("d")),
				B.leaf("cbd"),
				B.leaf("d")
				);
		
		assertThat(actual, isEqualToTree(expected));
	}
	


	@Test
	public void whenBookIsAdded_thenTreeHasExpectedForm() {
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
	
	@Test
	// From Ukkononen slides
	public void whenHattivattiIsAdded_thenTreeHasExpectedForm() {
		SuffixTree actual = new SuffixTree("hattivatti");

		SuffixTree expected = B.tree(
				B.leaf("hattivatti"),
				B.leaf("attivatti"),
				B.explicit("t",
						B.leaf("ivatti"),
						B.leaf("tivatti") 
						),
				B.leaf("ivatti"),
				B.leaf("vatti")
				);
		
		assertThat(actual, isEqualToTree(expected));
	}
	
	@Test
	public void whenTwoWordsAreAdded_thenTreeContainsSubStringsFromBothWords() {
		SuffixTree tree = new SuffixTree();
		tree.add("one");
		tree.add("two");
		
		assertTrue(tree.contains("one"));	
		assertTrue(tree.contains("two"));
		assertTrue(tree.contains("ne"));
		assertTrue(tree.contains("tw"));
	}
	
	@Test
	public void whenTwoWordsWithCommonPrefixAreAdded_thenNoErrorOccurs() {
		SuffixTree tree = new SuffixTree();
		tree.add("aab");
		tree.add("ar");
	}
	
	@Test
	public void givenASubString_thenToStringReturnsExpectedValue() {
		SuffixTree.SubString subString = SuffixTree.SubString.nonGlobal("abcdefghijkl", 3, 5);
		
		assertThat(subString.toString(), equalTo("de"));
	}
}
