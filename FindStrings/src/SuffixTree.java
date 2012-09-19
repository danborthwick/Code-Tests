
public class SuffixTree 
{
	protected ExplicitNode root;
	
	private ActivePoint activePoint;
	private int remainder;
	
	public SuffixTree() {
		this.root = new ExplicitNode("");
		
		this.activePoint = new ActivePoint();
		this.activePoint.node = root;
		this.activePoint.edge = new StringBuilder();
		this.activePoint.length = 0;
	}
	
	SuffixTree(Node... children) {
		root = new ExplicitNode("", children);
	}
	
	/**
    Update( new_suffix )
    {
      current_suffix = active_point
      test_char = last_char in new_suffix
      done = false;
      while ( !done ) {
        if current_suffix ends at an explicit node {
          if the node has no descendant edge starting with test_char
            create new leaf edge starting at the explicit node
          else
            done = true;
        } else {
          if the implicit node's next char isn't test_char {
            split the edge at the implicit node
            create new leaf edge starting at the split in the edge
          } else
            done = true;
        }
        if current_suffix is the empty string
          done = true;
        else
           current_suffix = next_smaller_suffix( current_suffix )
      }
      active_point = current_suffix
    }

	 * @param suffix
	 */
	private void add(char suffixChar) {
		
		appendCharacterToAllChildren(suffixChar);
		
		activePoint.edge.append(suffixChar);
		if (contains(activePoint.edge.toString())) {
		}
		else {
			while (activePoint.edge.length() > 0) {
				
				ExplicitNode nodeToAddTo = root;	// TODO: Active point?
				
				Node nodeToSplit = root.child(activePoint.edge.charAt(0));
				if (nodeToSplit != null) {
					ExplicitNode splitParent = splitNode(nodeToSplit, activePoint.edge.length()-1);
					nodeToAddTo.addNode(splitParent);
					nodeToAddTo = splitParent;
				}
				
				nodeToAddTo.addNode(new LeafNode(suffixChar));
				deleteFirstCharacters(activePoint.edge, 1);
			}
		}
	}
	
	private ExplicitNode splitNode(Node nodeToSplit, int splitPoint) {
		ExplicitNode newParent = new ExplicitNode(nodeToSplit.suffix.substring(0, splitPoint));
		deleteFirstCharacters(nodeToSplit.suffix, splitPoint);
		newParent.addNode(nodeToSplit);
		return newParent;
	}
	
	
	public static void deleteLastCharOf(StringBuilder stringBuilder) {
		stringBuilder.deleteCharAt(stringBuilder.length()-1);
	}
	
	public static void deleteFirstCharacters(StringBuilder stringBuilder, int numberOfCharacters) {
		stringBuilder.delete(0, numberOfCharacters);
	}
	
	private char characterAfterActivePoint() {
		if (activePoint.node.suffix.length() > activePoint.edge.length()) {
			return activePoint.node.suffix.charAt(activePoint.edge.length());			
		}
		else {
			return 0;
		}
	}

	private void appendCharacterToAllChildren(char suffixChar) {
		for (Node child : root.children) {
			if (child != null) {
				child.suffix.append(suffixChar);
			}
		}
	}

	public void add(String suffix) {
		remainder = 1;
		for (char nextCharacter : suffix.toCharArray()) {
			add(nextCharacter);
		}
	}

	public boolean contains(String string) {
		Node child = root.child(string.charAt(0));
		return (child != null) && (child.suffix.indexOf(string) == 0);
	}
	
	
	
	public class ActivePoint {
		private ExplicitNode node;
		private StringBuilder edge;
		private int length;
	}
	
	//-------------------------------------------------------------------------
	public static abstract class Node {

		protected StringBuilder suffix;

		public Node(String suffix) {
			this.suffix = new StringBuilder(suffix);
		}

		public Node(char suffix) {
			this.suffix = new StringBuilder(String.valueOf(suffix));
		}

		public abstract Node child(char index);
		
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			appendDescriptionTo(buffer);
			return buffer.toString();
		}
		
		protected void appendDescriptionTo(StringBuffer buffer) {
			buffer.append(suffix);
		}
	}
	
	public static class ExplicitNode extends Node {

		protected Node[] children = new Node[26];

		public ExplicitNode(String suffix) {
			super(suffix);
		}

		public ExplicitNode(char suffix) {
			super(suffix);
		}
		
		private ExplicitNode(String suffix, Node... children) {
			super(suffix);
			
			for (Node child : children) {
				addNode(child);
			}
		}

		@Override
		public Node child(char index) {
			return children[index - 'a'];
		}
		
		public void addNode(Node childNode) {
			children[childNode.suffix.charAt(0) - 'a'] = childNode;
		}

		@Override
		protected void appendDescriptionTo(StringBuffer buffer) {
			super.appendDescriptionTo(buffer);
			
			buffer.append(" [");
			boolean isFirstChild = true;
			for (Node child : children) {
				if (child != null) {
					if (!isFirstChild) {
						buffer.append(", ");
					}
					child.appendDescriptionTo(buffer);
					isFirstChild = false;
				}
			}
			buffer.append("]");
		}
	}
	
	public static class LeafNode extends Node {

		public LeafNode(String suffix) {
			super(suffix);
		}

		public LeafNode(char suffix) {
			super(suffix);
		}

		@Override
		public Node child(char index) {
			return null;
		}
	}

	//-------------------------------------------------------------------------
	public static class Builder {
		public static SuffixTree tree(Node... children) {
			return new SuffixTree(children);
		}
		
		public static ExplicitNode explicit(String suffix, Node... children) {
			return new ExplicitNode(suffix, children);
		}
		
		public static LeafNode leaf(String suffix) {
			return new LeafNode(suffix);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("SuffixTree: ");
		root.appendDescriptionTo(buffer);
		return buffer.toString();
	}
}
