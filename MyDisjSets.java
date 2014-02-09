// Minerva Chen, Max Peterson
// 11/3/2013
// MyDisjSets
//
// Implements disjoint sets. Uses weighted union and path compression to
// improve asymptotic speed.

public class MyDisjSets implements DisjointSets {
	private int[] up;
	private int numElements;
	private int numSets;
	
	/**
	* Required constructor.
	* @param numElements is the total number of elements, each element is 
	* initially in its own set.
	*/
	public MyDisjSets(int numElements) {
		up = new int[numElements];
		for (int i = 0; i < numElements; i++)
			up[i] = -1; // Negative denotes root. Each element is initially a tree of size 1
		this.numElements = numElements;
		numSets = numElements;
	}

	// returns the total number of sets
	public int numSets() {
		return numSets;
	}
	
	// set1 and set2 must be valid elements of a set. Otherwise, throws 
	// InvalidElementException. They must also be valid names of sets. Otherwise,
	// throws InvalidSetNameException.	
	// combines set1 and set2 into a larger set containing all elements of both sets.
	// Makes the smaller set a part of the larger set, i.e. performs weighted union.
	public void union(int set1, int set2) {
		checkException(set1);
		checkException(set2);
		// perform weighted union - make the smaller set part of the larger set,
		// never vice-versa.
		if (up[set1] < up[set2]) { // set1 is the larger set
			up[set1] += up[set2];
			up[set2] = set1; 
		} else { // set2 is larger than set1 or same size
			up[set2] += up[set1];
			up[set1] = set2;
		}
		numSets--;
	}

	// setNum must be a valid element of a set. Otherwise, throws 
	// InvalidElementException. It must also be a valid name of a set. Otherwise,
	// throws InvalidSetNameException.	
	// returns total number of elements in setNum.
	public int numElements(int setNum) {
		checkException(setNum);
		return -up[setNum]; // roots have size as a negative number, so negate for positive
	}
	
	// setNum must be a valid element of a set. Otherwise, throws 
	// InvalidElementException. It must also be a valid name of a set. Otherwise,
	// throws InvalidSetNameException.		
	// returns true if setName is the name of a set, false otherwise
	public boolean isSetName(int setName) {
		checkValidElement(setName);
		return up[setName] < 0;
	}

	// setNum must be a valid element of a set. Otherwise, throws 
	// InvalidElementException. It must also be a valid name of a set. Otherwise,
	// throws InvalidSetNameException. Prints the elements of the set in the form 
	// {<element>, <element>, ..., <element>}.
	public void printSet(int setNum) {
		checkException(setNum);
		int[] elements = getElements(setNum);
		System.out.print("{");
		for (int i = 0; i < elements.length - 1; i++)
			System.out.print(elements[i] + ", ");
		System.out.println(elements[elements.length - 1] + "}");
	}
		
	// x must be a valid element of a set. Otherwise, throws 
	// InvalidElementException.
	// returns the name of the set x is in. Performs path compression to make
	// future finds faster.
	public int find(int x) {
		checkValidElement(x);
		int root = x; 
		while (root >= 0 && up[root] >= 0) // find element
			root = up[root]; 
		if (x != root) { // if x not a root, do path compression
			int oldParent = up[x]; // prime loop
			while (oldParent != root) { 
				up[x] = root; // point directly at your root; this is the path compression. 
			 	x = oldParent; // go one level up the tree
			 	oldParent = up[x]; 
		 	} 
		}
		return root;
	}
	
	// throws InvalidElementException if setNum is not a valid element; throws
	// InvalidSetName exception if setNum is not the name of a set.
	// returns the an array of the elements of setNum. Performs path compression 
	// to make future calls on finds and getElements faster.
	public int[] getElements(int setNum) {
		checkException(setNum);
		int[] result = new int[-up[setNum]];
		int indexSoFar = 0;
		int i = 0;
		while (i < up.length && indexSoFar < result.length) { // don't check all of up
			if (find(i) == setNum) { // if the ith element is a member of setNum
				result[indexSoFar] = i;
				indexSoFar++; 
			}
			i++;
		}
		return result;
	}

	// if n is not an element of any set, throws InvalidElementException.
	// if n is a valid element, but not the name of a set, throws 
	// InvalidSetNameException.
	private void checkException(int n) {
		checkValidElement(n);
		checkName(n);
	}

	// if setName is not the name of a set, throws InvalidSetNameException.
	private void checkName(int setName) {
		if (!isSetName(setName))
			throw new InvalidSetNameException();
	}

	// if element is not an element of any set (i.e. not in the range 0 to N - 1,
	// where N is the integer passed to the constructor), throws InvalidElementException.
	private void checkValidElement(int element) {
		if (up.length < element)
			throw new InvalidElementException();
	}
}
