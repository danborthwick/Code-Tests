
public class SuffixTree 
{
	protected ExplicitNode root;
	protected Adder adder;
	
	public SuffixTree() {
		this.root = new ExplicitNode("");
		this.root.suffixLink = root;
		this.adder = new NaiiveAdder();
		//this.adder = new UkkonenAdder();
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
	
		ActivePoint activePoint = new ActivePoint();
		int remainder;
		
		@Override
		public void add(String suffix) {
			activePoint.node = root;
			activePoint.edge = new SubString(root.suffix.source, 0, 0);
			remainder = 1;
			
			for (SubString nextCharacter = new SubString(suffix, 0, 1); 
					!nextCharacter.isEmpty(); 
					nextCharacter.advanceOneCharacter()) {
				
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

		ActivePoint activePoint = new ActivePoint();
		
		@Override
		public void add(String stringToAdd) {
			activePoint.node = root;
			activePoint.edge = new SubString();

			LeafNode newNode = new LeafNode(new SubString(stringToAdd, 0, 1)); 
			root.addNode(newNode);
			newNode.suffixLink = root;
			
			for (SubString nextCharacter = new SubString(stringToAdd, 0, 1); 
					!nextCharacter.isEmpty(); 
					nextCharacter.advanceOneCharacter()) {
				
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
		
		public ActivePoint activePoint = new ActivePoint();

		@Override
		public void add(String stringToAdd) {
			activePoint.node = root;
			activePoint.edge = new SubString("");
			
			for (char charToAdd : stringToAdd.toCharArray()) {
				addChar(charToAdd);
			}
		}

		private void addChar(char charToAdd) {
			root.append(charToAdd);
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
	
	
	
	public class ActivePoint {
		private Node node;
		private SubString edge;
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
		
		public void advanceOneCharacter() {
			startIndex++;
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
