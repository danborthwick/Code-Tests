
public class SuffixTree 
{
	protected ExplicitNode root;
	protected Adder adder;
	
	public SuffixTree() {
		this.root = new ExplicitNode("");
		this.root.suffixLink = root;
		//this.adder = new NaiiveAdder();
		//this.adder = new UkkonenAdder();
		this.adder = new MyAdder();
	}
	
	public SuffixTree(String string) {
		this();
		add(string);
	}
	
	SuffixTree(Node... children) {
		root = new ExplicitNode("", children);
	}
	
	public void add(String stringToAdd) {

		if (!root.suffix.isEmpty()) {
			// TODO: Multiple strings?
			throw new RuntimeException("Multiple strings not supported");
		}
		root.suffix.source = stringToAdd;
		adder.add(stringToAdd);
	}
	
	public interface Adder {
		public void add(String stringToAdd);
	}
	
	public class NaiiveAdder implements Adder {
	
		ActivePoint<Node> activePoint = new ActivePoint<Node>();
		int remainder;
		
		@Override
		public void add(String suffix) {
			activePoint.node = root;
			activePoint.edge = new SubString(root.suffix.source, 0, 0);
			remainder = 1;
			
			for (SubString nextCharacter = new SubString(suffix, 0, 1); 
					!nextCharacter.isEmpty(); 
					nextCharacter.extendByOneCharacter()) {
				
				add(nextCharacter);
			}
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
		private void add(SubString suffixChar) {
			
			root.append(suffixChar.firstCharacter());
			
			activePoint.edge.append(suffixChar.firstCharacter());
			remainder++;
			
			while ((activePoint.edge.length() > 0)
					&& !contains(activePoint.edge.toString())) {
				
				ExplicitNode nodeToAddTo = root;	// TODO: Active point?
				
				Node nodeToSplit = root.child(activePoint.edge.firstCharacter());
				if (nodeToSplit != null) {
					SubString splitPoint = stringExcludingLastCharacter(activePoint.edge);
					ExplicitNode splitParent = nodeToSplit.split(splitPoint);
					nodeToAddTo.addNode(splitParent);
					nodeToAddTo = splitParent;
				}
				
				nodeToAddTo.addNode(new LeafNode(suffixChar));
				deleteFirstCharacters(activePoint.edge, 1);
				remainder--;
			}
			
			if ((remainder == 1) && (activePoint.node == root)) {
				root.addNode(new LeafNode(suffixChar));
			}
		}
	}
	
	public class UkkonenAdder implements Adder {

		ActivePoint<Node> activePoint = new ActivePoint<Node>();
		
		@Override
		public void add(String stringToAdd) {
			activePoint.node = root;
			activePoint.edge = new SubString();

			LeafNode newNode = new LeafNode(new SubString(stringToAdd, 0, 1)); 
			root.addNode(newNode);
			newNode.suffixLink = root;
			
			for (SubString nextCharacter = new SubString(stringToAdd, 0, 1); 
					!nextCharacter.isEmpty(); 
					nextCharacter.extendByOneCharacter()) {
				
				add(nextCharacter);
			}
		}
		
		/**
		 *  Create Tree(t1);   slink(root) := root
		     (v, α) := (root, ε)    // (v, α) is the start node
		     for i := 2  to  n+1  do 
		          v´ := 0
		          while  there is no arc from  v  with label prefix  αti   do
		                   if  α ≠ ε  then    // divide the arc w = son(v, αη) into two
		                            son(v, α) := v´´;  son(v´´,ti) := v´´´;   son(v´´,η) := w 
		                  else
		                            son(v,ti) := v´´´;   v´´ := v
		                  if  v´ ≠  0  then  slink(v´)  :=  v´´
		                  v´ := v´´;  v := slink(v);  (v, α) := Canonize(v, α)
		          if  v´ ≠  0   then  slink(v´) := v
		          (v, α)  := Canonize(v, αti)   // (v, α) = start node of the next round
		*/
		private void add(SubString charToAdd) {
			// Since we don't have a magic end point
			root.append(charToAdd.firstCharacter());
			
			//TODO: Avoid alloc
			while (!activePoint.node.contains(activePointEdgePlusNextChar(charToAdd))) {
				Node vd = null;
				Node vdd = null;
				if (activePoint.edge.length() > 0) {
					vd = activePoint.node.split(activePoint.edge);
					((ExplicitNode) vd).addNode(new LeafNode(charToAdd));
				}
				else {
					LeafNode newLeaf = new LeafNode(charToAdd);
					((ExplicitNode) activePoint.node).addNode(newLeaf);
					vdd = activePoint.node;
				}
				
				if (vd != null) {
					vd.suffixLink = vdd;
				}
				vd = vdd;
				activePoint.node = activePoint.node.suffixLink;
				canoniseActivePoint();
			}
			activePoint.edge.append(charToAdd.firstCharacter());
			canoniseActivePoint();
		}

		private SubString activePointEdgePlusNextChar(SubString charToAdd) {
			SubString activePointEdgePlusNextChar = new SubString(activePoint.edge);
			activePointEdgePlusNextChar.append(charToAdd.firstCharacter());
			return activePointEdgePlusNextChar;
		}

		/**
			Function Canonize(v, α):
				while son(v, α´) ≠ 0   where   α = α´ α´´, | α´| > 0   do
					v := son(v, α´);  α := α´´
				return (v, α)
		 */
		private void canoniseActivePoint() {
			while (activePoint.edge.length() > 0) {
				Node child = activePoint.node.child(activePoint.edge.firstCharacter());
				if (child != null) {
					activePoint.node = child;
					if (!activePoint.edge.startsWith(child.suffix)) {
						throw new RuntimeException("eh?");
					}
					deleteFirstCharacters(activePoint.edge, child.suffix.length());
				}
			}
		}
	}
	
	public class MyAdder implements Adder {
		
		public ActivePoint<ExplicitNode> activePoint = new ActivePoint<ExplicitNode>();

		@Override
		public void add(String stringToAdd) {
			activePoint.node = root;
			activePoint.edge = new SubString(stringToAdd, 0, 0);
			
			for (SubString nextCharacter = new SubString(stringToAdd, 0, 1); 
					!nextCharacter.isEmpty(); 
					nextCharacter.advanceOneCharacter()) {
				
				addChar(nextCharacter);
			}
		}

		private void addChar(SubString charToAdd) {
			
			//TODO: Global end index
			root.append(charToAdd.firstCharacter());
			
			Node childNodeForChar = activePoint.node.child(charToAdd.firstCharacter());
			if (childNodeForChar == null) {
				// New node
				Node newLeaf = new LeafNode(charToAdd);
				activePoint.node.addNode(newLeaf);
				//TODO: Are leaf suffix links necessary?
				newLeaf.suffixLink = activePoint.node;
				//TODO: Is this right?
				if (activePoint.edgeNode() != newLeaf) {
					activePoint.edgeNode().suffixLink = newLeaf;
				}
				
				if (activePoint.edge.length() > 1) {

					if (activePoint.edge.startIndex == activePoint.node.suffix.startIndex) {
						activePoint.edgeNode().suffixLink = newLeaf;						
						ExplicitNode newParent = activePoint.node.split(activePoint.edge);
					}
					else {
						// Need to split
						activePoint.node.split(activePoint.edge);
						followSuffixLinksAndSplit(activePoint.node, newLeaf);
					}
				}
			}
			else {
				// Node exists, update activePoint to point to it
				if (activePoint.edge.nextCharacter() == charToAdd.firstCharacter()) {
					// nextCharacter duplicates the next character anyway, set activePoint to that character
					if (activePoint.edge.startIndex == childNodeForChar.suffix.startIndex) {
						// Reset activePoint to start of childNodeForChar
						activePoint.edge.startIndex = activePoint.edge.endIndex;
					}
					activePoint.edge.extendByOneCharacter();
				}
				else {
					// Need to split activePoint.edge
					Node newChild = activePoint.edgeNode();
					ExplicitNode newParent = newChild.split(activePoint.edge);
					Node newLeaf = new LeafNode(charToAdd);
					newParent.addNode(newLeaf);
					
					// Now follow the suffix link
					followSuffixLinksAndSplit(newChild, newLeaf);
				}
			}
		}

		private void followSuffixLinksAndSplit(Node childToAdd, Node newLeaf) {
			ExplicitNode newParent;
			for (ExplicitNode nodeToSplit = (ExplicitNode) childToAdd.suffixLink;
					nodeToSplit != root;
					nodeToSplit = (ExplicitNode) nodeToSplit.suffixLink) {
				newParent = nodeToSplit.split(activePoint.edge);
				newParent.addNode(newLeaf);
				newParent.addNode(childToAdd);
			}
		}
		
	}
	
	private SubString stringExcludingLastCharacter(SubString edge) {
		return new SubString(edge.source, edge.startIndex, edge.endIndex-1);
	}
	
	public static void deleteFirstCharacters(SubString suffix, int numberOfCharacters) {
		suffix.startIndex += numberOfCharacters;
	}

	private static boolean startsWith(SubString suffix, SubString subString) {
		return suffix.source.regionMatches(
				suffix.startIndex, subString.source,
				subString.startIndex, subString.length());
	}
	
	public boolean contains(String string) {
		return root.contains(new SubString(string));
	}
	
	
	
	public class ActivePoint<NodeType extends Node> {
		private NodeType node;
		private SubString edge;
		
		public Node edgeNode() {
			return node.child(edge.firstCharacter());
		}
	}
	
	public char[] suffixAtPosition(int queryIndex) {
		throw new IndexOutOfBoundsException("Not yet implemented");
	}

	//-------------------------------------------------------------------------
	public static class SubString {
		public String source;
		public int startIndex;
		public int endIndex;
		
		public SubString(String source, int startIndex, int endIndex) {
			this.source = source;
			this.startIndex = startIndex;
			this.endIndex = endIndex;
		}
		
		public SubString(SubString suffix) {
			this(suffix.source, suffix.startIndex, suffix.endIndex);
		}

		public SubString(String string) {
			this(string, 0, string.length());
		}
		
		public SubString() {
			this("");
		}
		
		public boolean isEmpty() {
			return startIndex >= endIndex; 
		}

		public int length() {
			return endIndex - startIndex;
		}
		
		public char firstCharacter() {
			return source.charAt(startIndex);
		}
		
		public char nextCharacter() {
			return source.charAt(endIndex);
		}

		public void advanceOneCharacter() {
			startIndex++;
			extendByOneCharacter();
		}

		private void extendByOneCharacter() {
			endIndex = Math.min(endIndex+1, source.length());
		}
		

		public void append(char character) {
			if (source.charAt(endIndex) == character) {
				endIndex++;
			}
			else {
				throw new RuntimeException("Appending " + character + " but expected " + source.charAt(endIndex));
			}
		}
		
		public boolean startsWith(SubString other) {
			return source.regionMatches(startIndex, other.source, other.startIndex, other.length());
		}

		@Override
		public String toString() {
			return source.substring(startIndex, endIndex);
		}
	}
	
	//-------------------------------------------------------------------------
	public static abstract class Node {

		protected SubString suffix;
		//TODO: ExplicitNode?
		protected Node suffixLink;

		public Node(SubString suffix) {
			this.suffix = new SubString(suffix);
		}

		public Node(String suffix) {
			this.suffix = new SubString(suffix);
		}

		public abstract Node child(char index);
		
		//TODO: No point passing the character?
		public abstract void append(char character);
		
		public boolean contains(SubString searchString) {
			
			// TODO: Avoid recursion
			boolean found = false;
			Node child = child(searchString.firstCharacter());
			
			if (child != null) {
				if (searchString.length() <= child.suffix.length()) {
					found = startsWith(child.suffix, searchString);
				}
				else {
					if (startsWith(searchString, child.suffix)) {
						searchString.startIndex += child.suffix.length();
						found = child.contains(searchString);
						searchString.startIndex -= child.suffix.length();
					}
				}
			}

			return found;
		}
		
		public ExplicitNode split(SubString splitPoint) {
			ExplicitNode newParent = new ExplicitNode(splitPoint);
			deleteFirstCharacters(suffix, splitPoint.length());
			newParent.addNode(this);
			return newParent;				
		}
			
		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			describeTo(buffer);
			return buffer.toString();
		}
		
		protected void describeTo(StringBuffer buffer) {
			buffer.append(suffix);
		}
	}
	
	public static class ExplicitNode extends Node {

		protected Node[] children = new Node[26];

		private ExplicitNode(String suffix, Node... children) {
			super(suffix);
			
			for (Node child : children) {
				addNode(child);
			}
		}

		public ExplicitNode(SubString suffix) {
			super(suffix);
		}

		@Override
		public Node child(char index) {
			return children[index - 'a'];
		}
		
		public void addNode(Node childNode) {
			children[childNode.suffix.firstCharacter() - 'a'] = childNode;
		}
		
		@Override
		public void append(char character) {
			for (Node child : children) {
				if (child != null) {
					child.append(character);
				}
			}
		}

		@Override
		public ExplicitNode split(SubString splitPoint) {
			
			if (splitPoint.length() < suffix.length()) {
				return super.split(splitPoint);
			}
			else {
				// TODO: Avoid alloc?
				SubString childSplitPoint = new SubString(splitPoint.source, 
						splitPoint.startIndex + suffix.length(), 
						splitPoint.endIndex);
				
				Node childToSplit = child(childSplitPoint.firstCharacter());
				ExplicitNode newChild = childToSplit.split(childSplitPoint);
				addNode(newChild);
				return newChild;
			}
		}

		@Override
		protected void describeTo(StringBuffer buffer) {
			super.describeTo(buffer);
			
			buffer.append(" [");
			boolean isFirstChild = true;
			for (Node child : children) {
				if (child != null) {
					if (!isFirstChild) {
						buffer.append(", ");
					}
					child.describeTo(buffer);
					isFirstChild = false;
				}
			}
			buffer.append("]");
		}
	}
	
	//TODO: Do leaf nodes even need a suffix or is its index in the parent enough?
	public static class LeafNode extends Node {

		public LeafNode(String suffix) {
			super(suffix);
		}

		public LeafNode(SubString suffix) {
			super(suffix);
		}

		@Override
		public Node child(char index) {
			return null;
		}

		@Override
		public void append(char character) {
			suffix.append(character);
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
		root.describeTo(buffer);
		return buffer.toString();
	}
}
