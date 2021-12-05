import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * AVLTree
 *
 * An implementation of aמ AVL Tree with
 * distinct integer keys and info.
 *
 */

public class AVLTree {

	private final static int VIRTUAL_NODE = 0;
	private final static int LEAF_NODE = 1;
	private final static int UNARY_NODE = 2;
	private final static int INTERNAL_NODE = 3;
	private final static int LEFT=1;
	private final static int RIGHT=2;


	private IAVLNode root;
	private int size = 0;
	private IAVLNode min;
	private IAVLNode max;

	/**
	 * public AVLTree()
	 *
	 * Constructor of empty AVL tree
	 * Complexity - O(1)
	 *
	 */
	public AVLTree() {
		this.root = null;
		this.size=0;
		this.min=null;
		this.max=null;
	}

	/**
	 * public boolean empty()
	 *
	 * Returns true if and only if the tree is empty.
	 * Complexity - O(1)
	 *
	 */
	public boolean empty() {
		return this.size == 0;
	}

	/**
	 * public String search(int k)
	 *
	 * Returns the info of an item with key k if it exists in the tree.
	 * otherwise, returns null.
	 *
	 * Complexity - O(log n)
	 *
	 */
	public String search(int k)
	{
		// Standard binary search
		IAVLNode ptr = this.root;
		while(ptr != null) {
			int key = ptr.getKey();
			if(key == k) {
				return ptr.getValue();
			}
			else {
				if(k > key) {
					ptr = ptr.getRight();
				}
				else {
					ptr = ptr.getLeft();
				}
			}
		}
		return null;
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * Inserts an item with key k and info i to the AVL tree.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k already exists in the tree.
	 *
	 * Complexity - O(log n)
	 *
	 */
	public int insert(int k, String i) {
		// If tree is empty, set root as new node with key k and info i
		if(this.empty()) {
			this.root = new AVLNode(k, i, true);
			this.size++;

			// update min,max=root
			this.min=this.root;
			this.max=this.root;
			return 0;
		}

		// if key exists in the tree return -1
		if(this.search(k) != null) {
			return -1;
		}

		// Perform normal BST insertion
		IAVLNode ptr = this.root;

		while(ptr.isRealNode()) {
			// update size while down
			ptr.setSize(ptr.getSize()+1);

			int key = ptr.getKey();
			if(k < key) {
				ptr = ptr.getLeft();
			}
			else {
				ptr = ptr.getRight();
			}
		}

		// Insert new node
		IAVLNode toInsert = new AVLNode(k, i, true);

		// update min or max if needed
		if(k < this.min.getKey())
			this.min=toInsert;
		if(k > this.max.getKey())
			this.max=toInsert;

		IAVLNode parent = ptr.getParent();
		boolean isLeftChild = k < parent.getKey();

		if(isLeftChild) {
			parent.setLeft(toInsert);
			toInsert.setParent(parent);
		}
		else {
			parent.setRight(toInsert);
			toInsert.setParent(parent);
		}

		this.size++;
		return rebalanceAfterInsertion(parent,k);
	}

	/**
	 * public int delete(int k)
	 *
	 * Deletes an item with key k from the binary tree, if it is there.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k was not found in the tree.
	 *
	 * Complexity - O(log n)
	 *
	 */
	public int delete(int k)
	{
		IAVLNode toDelete = findNode(k);

		if(toDelete == null) { 				// If node not found.
			return -1;
		}

		if(toDelete == this.root) {
			return removeRootNode();
		}

		IAVLNode parentOfDeleted = toDelete.getParent();
		this.size--;

		switch(typeOfNode(toDelete)) {
			case LEAF_NODE:
				removeLeafNode(toDelete);
				break;
			case UNARY_NODE:
				removeUnaryNode(toDelete);
				break;
			case INTERNAL_NODE:
				parentOfDeleted = findNodeSuccessor(toDelete).getParent();
				removeInternalNode(toDelete);
				break;
		}
		if(this.max.getKey() == k) {
			this.max = searchMax();
		}
		if(this.min.getKey() == k) {
			this.min = searchMin();
		}
		return rebalanceAfterDeletion(parentOfDeleted);
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty.
	 *
		 * Complexity - O(1)
	 *
	 */
	public String min()
	{
		if(this.empty()) {
			return null;
		}

		return min.getValue();
	}

	/**
	 * public String searchMin()
	 *
	 * Returns the node with the smallest key, null if tree is empty.
	 *
	 * Complexity - O(log n)
	 *
	 */
	private IAVLNode searchMin(){
		if(this.empty()) {
			return null;
		}

		IAVLNode ptr = this.root;

		while (ptr.getLeft().isRealNode()) {
			ptr = ptr.getLeft();
		}
		return ptr;
	}

	/**
	 * public String searchMax()
	 *
	 * Returns the node with the biggest key, null if tree is empty.
	 *
	 * Complexity - O(log n)
	 *
	 */
	private IAVLNode searchMax(){
		if(this.empty()) {
			return null;
		}

		IAVLNode ptr = this.root;

		while (ptr.getRight().isRealNode()) {
			ptr = ptr.getRight();
		}
		return ptr;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty.
	 *
	 * Complexity - O(1)
	 *
	 */
	public String max()
	{
		if(this.empty()) {
			return null;
		}
		return max.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
	 *
	 * Complexity - O(n)
	 */

	public int[] keysToArray() {
		if(this.empty()) {
			return new int[0];
		}

		int[] retArray = new int[this.size];
		keysToArrayRec(this.root, 0, retArray);

		return retArray;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree,
	 * sorted by their respective keys,
	 * or an empty array if the tree is empty.
	 *
	 * Complexity - O(n)
	 *
	 */
	public String[] infoToArray()
	{
		if(this.empty()) {
			return new String[0];
		}

		String[] retArray = new String[this.size];
		infoToArrayRec(this.root, 0, retArray);

		return retArray;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * Complexity - O(1)
	 *
	 */
	public int size()
	{

		return this.size;
	}

	/**
	 * public AVLTree[] split(int x)
	 *
	 * splits the tree into 2 trees according to the key x.
	 * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	 *
	 * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
	 * postcondition: none
	 *
	 * Complexity - O(log n)
	 *
	 */
	public AVLTree[] split(int x)
	{
		// smaller then x
		AVLTree T1=new AVLTree();

		// bigger then x
		AVLTree T2=new AVLTree();

		IAVLNode splitNode=findNode(x);

		// split the node
		if(splitNode==this.root)
		{
			T1.setSubtree(this.root.getLeft());
			T2.setSubtree(this.root.getRight());

			T1.max=T1.searchMax();
			T1.min=T1.searchMin();
			T2.max=T2.searchMax();
			T2.min=T2.searchMin();

			AVLTree[] resultArr={T1,T2};
			return resultArr;
		}

		// set the sub-tree of splitNode
		if(splitNode.getLeft().isRealNode()){
			T1.setSubtree(splitNode.getLeft());
		}
		if(splitNode.getRight().isRealNode()){
			T2.setSubtree(splitNode.getRight());
		}

		IAVLNode nodeToJoin=splitNode.getParent();

		boolean isLeftSon=true;
		if(nodeToJoin.getRight()==splitNode)
			isLeftSon=false;

		while(nodeToJoin!=null){
			AVLTree joinTree=new AVLTree();

			// ptr is a left son
			if(isLeftSon){
				// update next node to join
				if(nodeToJoin.getParent()!=null) {
					isLeftSon = true;
					if (nodeToJoin.getParent().getRight() == nodeToJoin)
						isLeftSon = false;
				}

				IAVLNode tempNode = nodeToJoin;
				nodeToJoin = nodeToJoin.getParent();

				joinTree.setSubtree(tempNode.getRight());

				tempNode.setParent(null);
				tempNode.setRight(new AVLNode(-1, null, false));
				tempNode.setLeft(new AVLNode(-1, null, false));
				tempNode.setHeight(0);
				tempNode.updateSize();

				T2.join(tempNode,joinTree);
			}
			// ptr is a right son
			else
			{
				if(nodeToJoin.getParent()!=null) {
					isLeftSon = true;
					if (nodeToJoin.getParent().getRight() == nodeToJoin)
						isLeftSon = false;
				}

				IAVLNode tempNode = nodeToJoin;
				nodeToJoin = nodeToJoin.getParent();

				joinTree.setSubtree(tempNode.getLeft());

				tempNode.setParent(null);
				tempNode.setRight(new AVLNode(-1, null, false));
				tempNode.setLeft(new AVLNode(-1, null, false));
				tempNode.setHeight(0);
				tempNode.updateSize();

				T1.join(tempNode,joinTree);
			}
		}

		// Set right IAVLNodes to min max
		T1.max=T1.searchMax();
		T1.min=T1.searchMin();
		T2.max=T2.searchMax();
		T2.min=T2.searchMin();


		AVLTree[] resultArr={T1,T2};
		return resultArr;
	}

	/**
	 * public int join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree.
	 * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	 *
	 * precondition: keys(t) < x < keys() or keys(t) > x > keys(). t/tree might be empty (rank = -1).
	 * postcondition: none
	 *
	 * Complexity - O(|tree.root.height - t.root.height| + 1)
	 *
	 */
	public int join(IAVLNode x, AVLTree t)
	{
		IAVLNode T1=this.getRoot();
		IAVLNode T2=t.getRoot();

		// x is a single node
		x.setHeight(0);
		x.setSize(1);

		this.size=this.size+t.size+1;
		//System.out.println("this.size= "+this.size);
		int result=0;

		// at least one tree is empty
		if(T1==null || T2==null)
		{
			//both trees are empty
			if(T1==null && T2==null) {
				this.root = x;
				x.setParent(null);
				//update min/max to be x
				this.max=this.root;
				this.min=this.root;

				// |-1-(-1)|+1=1
				return 1;
			}

			//the tree is empty and t doesn't
			if(T1==null){
				this.root=T2;
				// |t.rank-(-1)|+1=T2.getHeight()+2
				result=T2.getHeight()+2;

				// update min/max to be t's min/max
				this.min=t.min;
				this.max=t.max;
			}
			else{// else- t is empty and the tree doesn't
				// |this.rank-(-1)|+1=T1.getHeight()+2
				result=T1.getHeight()+2;
			}

			if(this.min!=null && this.max!=null) {
				// update x to be min/max if needed
				if (x.getKey() > this.max.getKey())
					this.max = x;
				if (x.getKey() < this.min.getKey())
					this.min = x;
			}

			// insert x to the merged tree
			insertionForJoin(x);
			return result;
		}

		// if we are in split and use join then it could be 2 un-empty trees with
		// null min-max,we dont care about updating min/max- just at the end of split
		if(t.min!=null && t.max!=null && this.min!=null && this.max!=null) {
			// both trees are not empty so x isnt a min/max
			if (t.min.getKey() < this.min.getKey()) {
				this.min = t.min;
			}
			if (t.max.getKey() > this.max.getKey()) {
				this.max = t.max;
			}
		}

		// make T2  higher then T1
		if(T2.getHeight()<T1.getHeight()){
			T1=t.getRoot();
			T2=this.getRoot();
		}

		int h1=T1.getHeight();
		int h2=T2.getHeight();

		if(h1==h2){
			if(T1.getKey()<x.getKey()){
				x.setRight(T2);
				x.setLeft(T1);
			}
			else{
				x.setLeft(T2);
				x.setRight(T1);
			}
			this.root=x;
			x.updateSize();
			T1.setParent(x);
			T2.setParent(x);
			x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));

			return 1;
		}

		result=h2-h1+1;

		// the tree we'll "travel" on to find the right node to connect
		IAVLNode ptr=T2;
		this.root=T2;

		// move right or left- depend on the keys
		// if T1<x<T2 move left
		if(T1.getKey()<x.getKey()){
			if(!ptr.getLeft().isRealNode())
			{
				this.root=x;
				x.setLeft(T2);
				x.setRight(T1);

				T2.setParent(x);
				T1.setParent(x);

				x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
				x.updateSize();
				this.size=x.getSize();

				return result;
			}
			else{
				// down till ptr.getHeight()<=h)
				while (ptr.getHeight() > h1) {
					//update size of every node we pass through
					ptr.setSize(ptr.getSize()+1+T1.getSize());
					ptr = ptr.getLeft();
				}

				// Now the ptr is the node we need to place as X's right son
				x.setRight(ptr);
				x.setLeft(T1);

				ptr.getParent().setLeft(x);
				x.setParent(ptr.getParent());

				// connect to x
				ptr.setParent(x);
				T1.setParent(x);
			}
		}
		// move right
		else
		{
			if(!ptr.getRight().isRealNode())
			{
				this.root=x;
				x.setLeft(T1);
				x.setRight(T2);

				T2.setParent(x);
				T1.setParent(x);

				x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
				x.updateSize();
				this.size=x.getSize();

				return result;
			}
			else {
				// down till ptr.getHeight()<=h)
				while (ptr.getHeight() > h1) {
					//update size of every node we pass through
					ptr.setSize(ptr.getSize()+1+T1.getSize());
					ptr = ptr.getRight();
				}

				// Now the ptr is the node we need to place as X's right son
				x.setRight(T1);
				x.setLeft(ptr);

				ptr.getParent().setRight(x);
				x.setParent(ptr.getParent());

				// connect to x
				ptr.setParent(x);
				T1.setParent(x);
			}
		}

		x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
		x.updateSize();

		rebalanceAfterJoin(ptr.getParent());

		return result;

	}


	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * Complexity - O(1)
	 *
	 */
	public IAVLNode getRoot()
	{
		return this.root;
	}


	/**
	 * private int keysToArrayRec(IAVLNode node, int i, int[] arr)
	 *
	 * Recursive helper function to insert the keys to the array
	 *
	 * Complexity - O(1)
	 *
	 * */
	private int keysToArrayRec(IAVLNode node, int i, int[] arr) {
		if(node.isRealNode()) {
			i = keysToArrayRec(node.getLeft(), i, arr);
			arr[i] = node.getKey();
			i = keysToArrayRec(node.getRight(), i + 1, arr);
		}
		return i;
	}

	/**
	 * private int infoToArrayRec(IAVLNode node, int i, String[] arr)
	 *
	 * Recursive helper function to insert the info to the array
	 *
	 * Complexity - O(1)
	 *
	 * */
	private int infoToArrayRec(IAVLNode node, int i, String[] arr) {
		if(node.isRealNode()) {
			i = infoToArrayRec(node.getLeft(), i, arr);
			arr[i] = node.getValue();
			i = infoToArrayRec(node.getRight(), i + 1, arr);
		}
		return i;
	}

	/**
	 * private int getBalanceFactor(IAVLNode node)
	 *
	 * Returns node.left.height - node.right.height. Results between -1 and 1 imply balanced.
	 *
	 * Complexity - O(1)
	 *
	 **/
	private int getBalanceFactor(IAVLNode node) {
		if(node == null)
			return 0;
		return node.getLeft().getHeight() - node.getRight().getHeight();
	}

	/**
	 * private void rotateRight(IAVLNode x)
	 *
	 * Rotate right according to a given node and update height
	 *
	 * Complexity - O(1)
	 *
	 **/
	private void rotateRight(IAVLNode x) {
		// Rotate right
		IAVLNode parent = x.getParent();
		IAVLNode y = x.getLeft();

		y.getRight().setParent(x);
		x.setLeft(y.getRight());
		y.setRight(x);
		y.setParent(parent);
		x.setParent(y);

		if (parent != null) {
			if(parent.getRight() == x) {
				parent.setRight(y);
			}
			else {
				parent.setLeft(y);
			}
		}

		// Update root if necessary
		if(x == this.root) {
			this.root = y;
		}

		// Update heights
		x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
		y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));

		// Update sizes
		x.updateSize();
		y.updateSize();

	}

	/**
	 * private void rotateDouble(IAVLNode x)
	 *
	 * Double Rotation- according to a given node and 2 directions
	 * @pre r1!=r2 && (r1==LEFT || r1==RIGHT) && (r2==LEFT || r2==RIGHT)
	 * Complexity - O(1)
	 *
	 **/
	private void rotateDouble(IAVLNode y, int r1,int r2) {
		if(r1==LEFT && r2==RIGHT) {
			IAVLNode parent=y.getParent();
			boolean isRoot=y==this.root;

			IAVLNode x = y.getLeft();
			IAVLNode z = x.getRight();

			x.setRight(z.getLeft());
			y.setLeft(z.getRight());

			z.setLeft(x);
			z.setRight(y);

			x.getRight().setParent(x);
			y.getLeft().setParent(y);
			x.setParent(z);
			y.setParent(z);

			if(!isRoot){
				if(parent.getLeft()==y){
					parent.setLeft(z);
					z.setParent(parent);
				}
				else{
					parent.setRight(z);
					z.setParent(parent);
				}
			}
			else{
				this.root=z;
				z.setParent(null);
			}

			// Update Heights
			x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
			y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
			z.setHeight(1 + Math.max(z.getLeft().getHeight(), z.getRight().getHeight()));

			// Update sizes
			x.updateSize();
			y.updateSize();
			z.updateSize();
		}
		else if(r1==RIGHT && r2==LEFT){
			IAVLNode parent=y.getParent();
			boolean isRoot=y==this.root;

			IAVLNode x = y.getRight();
			IAVLNode z = x.getLeft();

			x.setLeft(z.getRight());
			y.setRight(z.getLeft());

			z.setLeft(y);
			z.setRight(x);

			x.getLeft().setParent(x);
			y.getRight().setParent(y);
			x.setParent(z);
			y.setParent(z);

			if(!isRoot){
				if(parent.getLeft()==y){
					parent.setLeft(z);
					z.setParent(parent);
				}
				else{
					parent.setRight(z);
					z.setParent(parent);
				}
			}
			else{
				this.root=z;
				z.setParent(null);
			}

			x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
			y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
			z.setHeight(1 + Math.max(z.getLeft().getHeight(), z.getRight().getHeight()));


			// Update sizes
			x.updateSize();
			y.updateSize();
			z.updateSize();

		}
	}

	/**
	 * private void rotateLeft(IAVLNode x)
	 *
	 * Rotate left according to a given node and update height and size
	 *
	 * Complexity - O(1)
	 *
	 **/
	private void rotateLeft(IAVLNode x) {
		// Rotate left
		IAVLNode parent = x.getParent();
		IAVLNode y = x.getRight();

		y.getLeft().setParent(x);
		x.setRight(y.getLeft());
		y.setLeft(x);
		y.setParent(parent);
		x.setParent(y);

		if (parent != null) {
			if(parent.getLeft() == x) {
				parent.setLeft(y);
			}
			else {
				parent.setRight(y);
			}
		}

		// Update root if necessary
		if(x == this.root) {
			this.root = y;
		}

		// Update heights
		x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
		y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));

		// Update sizes
		x.updateSize();
		y.updateSize();

	}

	/**
	 * private void updateHeights(IAVLNode node)
	 *
	 * Traverse up the tree updating the heights
	 * Return number of promotions
	 *
	 * Complexity - O(1)
	 *
	 **/
	private int updateHeights(IAVLNode node) {
		IAVLNode ptr = node;
		int promotions = 0;

		while(ptr != null) {
			int before = ptr.getHeight();
			ptr.setHeight(1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight()));
			promotions += ptr.getHeight() == before ? 0 : 1;
			ptr = ptr.getParent();
		}
		return promotions;
	}

	/**
	 * private int typeOfNode(IAVLNode)
	 *
	 * Returns 0 if the node is a virtual node
	 * 		   1 if the node is a leaf
	 * 		   2 if the node is an unary node
	 * 		   3- else (the node is an internal node)
	 *
	 * Complexity - O(1)
	 *
	 */
	private int typeOfNode(IAVLNode node){
		if (!node.isRealNode())
			return VIRTUAL_NODE;
		if (!node.getRight().isRealNode() && !node.getLeft().isRealNode())
			return LEAF_NODE;
		if ((node.getRight().isRealNode() && !node.getLeft().isRealNode()) ||
				(!node.getRight().isRealNode() && node.getLeft().isRealNode()))
			return UNARY_NODE;
		return INTERNAL_NODE;
	}

	/**
	 *
	 * private int rebalanceAfterInsertion(IAVLNode parent, int k)
	 *
	 * Rebalnce the tree after insertion operation
	 * @return number of re-balancing operations.
	 *
	 * Complexity - O(log n)
	 *
	 */
	private int rebalanceAfterInsertion(IAVLNode parent, int k) {
		int promotions = 0;

		// Update parent height
		if(parent.getHeight() != 1) {
			promotions++;
		}
		else {
			return 0;
		}

		parent.setHeight(1 + Math.max(parent.getLeft().getHeight(), parent.getRight().getHeight()));

		// Check if node is unbalanced
		IAVLNode ptr = parent;
		int balance = getBalanceFactor(ptr);

		while(-1 <= balance && balance <= 1) {
			ptr = ptr.getParent();

			if(ptr == null) {
				break;
			}

			//ptr.updateSize();

			int newHeight = 1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight());
			if(ptr.getHeight() != newHeight) {
				promotions += 1;
				ptr.setHeight(newHeight);
			}
			else {
				return promotions;
			}
			balance = getBalanceFactor(ptr);
		}
		if(ptr == null) { // No balancing needed
			return promotions;
		}
		else {
			// Case 1 - rotate right
			if(balance > 1 && k < ptr.getLeft().getKey()) {
				rotateRight(ptr);

				return updateHeights(ptr.getParent()) + promotions;
			}
			// Case 2 - rotate left
			else if(balance < -1 && k > ptr.getRight().getKey()) {
				rotateLeft(ptr);

				return updateHeights(ptr.getParent()) + promotions;
			}
			// Case 3 - rotate left right
			else if(balance > 1 && k > ptr.getLeft().getKey()) {
				IAVLNode left = ptr.getLeft();
				rotateLeft(left);
				ptr.setLeft(left.getParent());
				rotateRight(ptr);

				return 1 + updateHeights(ptr.getParent()) + promotions;
			}
			// Case 4 - rotate right left
			else {
				IAVLNode right = ptr.getRight();
				rotateRight(right);
				ptr.setRight(right.getParent());
				rotateLeft(ptr);

				return 1 + updateHeights(ptr.getParent()) + promotions;
			}
		}
	}

	/**
	 *
	 * private void insertionForJoin(IAVLNode toInsert)
	 *
	 * insert the node to the right place and rebalnce the tree after join operation
	 *
	 * Complexity - O(log n)
	 *
	 */
	private void insertionForJoin(IAVLNode toInsert) {

		// Perform normal BST insertion
		IAVLNode ptr = this.root;

		while(ptr.isRealNode()) {
			int key = ptr.getKey();

			// update size while down
			ptr.setSize(ptr.getSize()+1);

			if(toInsert.getKey() < key) {
				ptr = ptr.getLeft();
			}
			else {
				ptr = ptr.getRight();
			}
		}

		IAVLNode parent = ptr.getParent();
		boolean isLeftChild = toInsert.getKey() < parent.getKey();

		if(isLeftChild) {
			parent.setLeft(toInsert);
			toInsert.setParent(parent);
		}
		else {
			parent.setRight(toInsert);
			toInsert.setParent(parent);
		}


		rebalanceAfterInsertion(parent,toInsert.getKey());
	}

	/**
	 *private void rebalanceAfterJoin(IAVLNode node)
	 *
	 * Rebalnce the tree after join operation
	 *
	 * Complexity - O(node.height)=O(log n)
	 *
	 */
	private void rebalanceAfterJoin(IAVLNode node) {

		// if node is'nt the root
		if(node!=null) {
			int right=node.getHeight()-node.getRight().getHeight();
			int left=node.getHeight()-node.getRight().getHeight();

			if ((right==1 && left==0) || (right==0 && left==1)){

				// promote- and go up
				node.setHeight(1+node.getHeight());

				rebalanceAfterJoin(node.getParent());
			}
			else if(right==2 && left==0){
				IAVLNode x=node.getLeft();
				IAVLNode a=x.getLeft();
				IAVLNode b=x.getRight();

				if(x.getHeight()-a.getHeight()==1 && x.getHeight()-b.getHeight()==2){
					rotateRight(node);
				}

				if(x.getHeight()-a.getHeight()==2 && x.getHeight()-b.getHeight()==1){
					rotateDouble(node,LEFT,RIGHT);
				}

				if(x.getHeight()-a.getHeight()==1 && x.getHeight()-b.getHeight()==1){
					rotateRight(node);
				}
			}
			else if(right==0 && left==2){
				IAVLNode x=node.getRight();
				IAVLNode a=x.getLeft();
				IAVLNode b=x.getRight();

				if(x.getHeight()-a.getHeight()==2 && x.getHeight()-b.getHeight()==1){
					rotateLeft(node);
				}

				if(x.getHeight()-a.getHeight()==1 && x.getHeight()-b.getHeight()==2){
					rotateDouble(node,RIGHT,LEFT);
				}

				if(x.getHeight()-a.getHeight()==1 && x.getHeight()-b.getHeight()==1){
					rotateLeft(node);
				}
			}
		}

	}

	/**
	 *
	 * private int rebalanceAfterDeletion(IAVLNode parentOfDeleted)
	 *
	 * Rebalnce the tree after delete operation
	 * @pre parentOfDeleted != null
	 * @return number of re-balancing operations.
	 *
	 * Complexity - O(log n)
	 *
	 */
	private int rebalanceAfterDeletion(IAVLNode parentOfDeleted) {
		IAVLNode ptr = parentOfDeleted;
		int operations = 0; // Rotations, promotions and demotions

		while(ptr != null) { // Traverse up the tree
			int oldHeight = ptr.getHeight();
			int newHeight = 1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight());

			ptr.setHeight(newHeight);
			ptr.updateSize();

			int balance = getBalanceFactor(ptr);

			if(-1 <= balance && balance <= 1) {
				if(oldHeight == newHeight) {
					return operations;
				}
				else {
					operations++; 											// Case 0 : Demotion
				}
			}
			else if(balance > 1 && getBalanceFactor(ptr.getLeft()) >= 0) { // Case 1 : rotate right
				rotateRight(ptr);
				operations++;
				ptr = ptr.getParent();
			}
			else if(balance < -1 && getBalanceFactor(ptr.getRight()) <= 0) { // Case 2 : rotate left
				rotateLeft(ptr);
				operations++;
				ptr = ptr.getParent();
			}
			else if(balance > 1 && getBalanceFactor(ptr.getLeft()) < 0) { // Case 3 : left right
				IAVLNode left = ptr.getLeft();
				rotateLeft(left);
				ptr.setLeft(left.getParent());
				rotateRight(ptr);
				operations += 2;
				ptr = ptr.getParent();
			}
			else { 														   // Case 4 : right left
				IAVLNode right = ptr.getRight();
				rotateRight(right);
				ptr.setRight(right.getParent());
				rotateLeft(ptr);
				operations += 2;
				ptr = ptr.getParent();
			}

			ptr = ptr.getParent();
		}
		return operations;
	}

	/**
	 * private void swapNodes(IAVLNode nodeA, IAVLNode nodeB)
	 *
	 * @pre: nodeA.isRealNode() == true  == nodeB.isRealNode()
	 *
	 * Complexity - O(1)
	 *
	 */
	private void swapNodes(IAVLNode nodeA, IAVLNode nodeB) {
		// Cases where one node is parent of other node
		if(nodeB.getParent() == nodeA) {
			swapParentAndChild(nodeA, nodeB);
			return;
		}
		if(nodeA.getParent() == nodeB) {
			swapParentAndChild(nodeB, nodeA);
			return;
		}

		boolean isNodeARoot = nodeA == this.root;
		boolean isNodeBRoot = nodeB == this.root;

		IAVLNode saveParentA = nodeA.getParent();
		IAVLNode saveParentB = nodeB.getParent();

		IAVLNode saveLeft = nodeA.getLeft();
		IAVLNode saveRight = nodeA.getRight();
		int saveHeight = nodeA.getHeight();

		nodeA.getLeft().setParent(nodeB);
		nodeA.getRight().setParent(nodeB);
		nodeA.setParent(nodeB.getParent());
		nodeA.setLeft(nodeB.getLeft());
		nodeA.setRight(nodeB.getRight());
		nodeA.setHeight(nodeB.getHeight());

		if(isNodeARoot) {
			this.root = nodeB;

			boolean isNodeBLeftChild = saveParentB.getLeft() == nodeB;
			if(isNodeBLeftChild) {
				saveParentB.setLeft(nodeA);
			}
			else {
				saveParentB.setRight(nodeA);
			}
		}
		else if(isNodeBRoot) {
			this.root = nodeA;

			boolean isNodeALeftChild = saveParentA.getLeft() == nodeA;
			if(isNodeALeftChild) {
				saveParentA.setLeft(nodeB);
			}
			else {
				saveParentA.setRight(nodeB);
			}
		}
		else {
			boolean isNodeBLeftChild = saveParentB.getLeft() == nodeB;
			boolean isNodeALeftChild = saveParentA.getLeft() == nodeA;
			if(isNodeALeftChild) {
				saveParentA.setLeft(nodeB);
			}
			else {
				saveParentA.setRight(nodeB);
			}
			if(isNodeBLeftChild) {
				saveParentB.setLeft(nodeA);
			}
			else {
				saveParentB.setRight(nodeA);
			}
		}

		nodeB.getLeft().setParent(nodeA);
		nodeB.getRight().setParent(nodeA);
		nodeB.setLeft(saveLeft);
		nodeB.setRight(saveRight);
		nodeB.setParent(saveParentA);
		nodeB.setHeight(saveHeight);

		nodeB.updateSize();
	}

	/**
	 * private void swapParentAndChild(IAVLNode parent, IAVLNode child)
	 *
	 * @pre: child.getParent() == parent
	 *
	 * Complexity - O(1)
	 *
	 */
	private void swapParentAndChild(IAVLNode parent, IAVLNode child) {
		if(parent.getLeft() == child) {
			IAVLNode right = parent.getRight();
			IAVLNode grandparent = parent.getParent();
			int height = parent.getHeight();

			parent.setLeft(child.getLeft());
			parent.setRight(child.getRight());
			parent.setHeight(child.getHeight());
			right.setParent(child);
			parent.setParent(child);

			if(grandparent != null) {
				if(grandparent.getLeft() == parent) {
					grandparent.setLeft(child);
				}
				else {
					grandparent.setRight(child);
				}
			}
			else {
				this.root = child;
			}
			child.setRight(right);
			child.setParent(grandparent);
			child.setHeight(height);
			child.setLeft(parent);

			child.updateSize();
		}
		else {
			IAVLNode left = parent.getLeft();
			IAVLNode grandparent = parent.getParent();
			int height = parent.getHeight();

			parent.setRight(child.getRight());
			parent.setLeft(child.getLeft());
			parent.setHeight(child.getHeight());
			left.setParent(child);
			parent.setParent(child);

			if(grandparent != null) {
				if(grandparent.getLeft() == parent) {
					grandparent.setLeft(child);
				}
				else {
					grandparent.setRight(child);
				}
			}
			else {
				this.root = child;
			}
			child.setLeft(left);
			child.setParent(grandparent);
			child.setHeight(height);
			child.setRight(parent);

			child.updateSize();
		}
	}

	/**
	 * private IAVLNode findNode(int k)
	 *
	 * Returns the AVLNode that contain k
	 * If k doesn't exist in the tree - return null
	 *
	 * Complexity - O(log n)
	 *
	 */
	private IAVLNode findNode(int k) {

		IAVLNode ptr = this.root;

		while (ptr != null) {
			int key = ptr.getKey();
			if (key == k) {
				return ptr;
			}
			else{
				if (k > key) {
					ptr = ptr.getRight();
				}
				else{
					ptr = ptr.getLeft();
				}
			}
		}
		return null;
	}

	/**
	 * private IAVLNode findNodeSuccessor(IAVLNode node)
	 *
	 * Returns node's successor
	 *
	 * Complexity - O(logn)
	 *
	 */
	private IAVLNode findNodeSuccessor(IAVLNode node){
		IAVLNode ptr;

		// if node has right son- return the minimum from its sub-tree
		if (node.getRight().isRealNode()){
			ptr = node.getRight();
			while (ptr.getLeft().isRealNode()) {
				ptr = ptr.getLeft();
			}
			return ptr;
		}

		IAVLNode tempNode = node;
		ptr = node.getParent();
		while(ptr != null && tempNode == ptr.getRight()){
			tempNode = ptr;
			ptr = tempNode.getParent();
		}

		// if x is the maximum it return null
		return ptr;
	}

	/**
	 * private void removeLeafNode(IAVLNode node)
	 *
	 * @pre : node is a leaf node && not the root
	 *
	 * Complexity - O(1)
	 *
	 */
	private void removeLeafNode(IAVLNode node) {
		boolean isLeftChild = node.getParent().getLeft() == node;
		node.setLeft(null);
		node.setRight(null);

		if(isLeftChild) {
			node.getParent().setLeft(new AVLNode(-1, null, false));
			node.getParent().getLeft().setParent(node.getParent());
		}
		else {
			node.getParent().setRight(new AVLNode(-1, null, false));
			node.getParent().getRight().setParent(node.getParent());
		}
		node.setParent(null);
	}

	/**
	 * private void removeUnaryNode(IAVLNode node)
	 *
	 * @pre: node is an unary node
	 *
	 * Complexity - O(1)
	 *
	 */
	private void removeUnaryNode(IAVLNode node) {
		boolean isLeftChild = node.getParent().getLeft() == node;

		if(node.getLeft().isRealNode()) { // If left unary
			if(isLeftChild) {
				node.getParent().setLeft(node.getLeft());
			}
			else {
				node.getParent().setRight(node.getLeft());
			}
			node.getLeft().setParent(node.getParent());

		}
		else {                           // Right unary
			if(isLeftChild) {
				node.getParent().setLeft(node.getRight());
			}
			else {
				node.getParent().setRight(node.getRight());
			}
			node.getRight().setParent(node.getParent());
		}

		node.setParent(null);
		node.setLeft(null);
		node.setRight(null);
	}

	/**
	 * private void removeInternalNode(IAVLNode node)
	 *
	 * Internal node has a right child so its successor is a leaf node or unary.
	 * @pre: node is an internal node.
	 *
	 * Complexity - O(logn)
	 *
	 */
	private void removeInternalNode(IAVLNode node) {
		IAVLNode successor = findNodeSuccessor(node);
		swapNodes(node, successor);
		switch(typeOfNode(node)) {
			case LEAF_NODE:
				removeLeafNode(node);
				break;
			case UNARY_NODE:
				removeUnaryNode(node);
				break;
		}
	}

	/**
	 * private void setSubtree(IAVLNode root)
	 *
	 * make nodeToRoot to be an AVLTree
	 *
	 * Complexity -O(1)
	 *
	 */
	private void setSubtree(IAVLNode nodeToRoot) {
		if(nodeToRoot.isRealNode()) {
			this.root = nodeToRoot;
			nodeToRoot.setParent(null);
			this.size = nodeToRoot.getSize();
		}
		else{
			this.root=null;
			this.size=0;
		}
	}

	/**
	 * private int removeRootNode()
	 *
	 * Removes the root node and returns number of re-balancing operations done.
	 *
	 * Complexity - O(logn)
	 *
	 */
	private int removeRootNode() {
		IAVLNode root = this.root;
		if(this.size == 1) { // If the tree only has one node, remove it and set the tree as empty
			this.root = null;
			this.size = 0;
			this.min = null;
			this.max = null;
			return 0;
		}

		if(!root.getLeft().isRealNode()) { // If the tree has only a right child
			root.getRight().setParent(null);
			this.root = root.getRight();
			this.size = 1;
			this.min = this.root;

			return 0;
		}
		else if(!root.getRight().isRealNode()) { // If the tree only has a left child
			root.getLeft().setParent(null);
			this.root = root.getLeft();
			this.size = 1;
			this.max = this.root;
			return 0;
		}
		else { // root has both children
			IAVLNode successor = findNodeSuccessor(root);
			swapNodes(root, successor);
			IAVLNode deletedParent = root.getParent();
			switch(typeOfNode(root)) {
				case LEAF_NODE:
					removeLeafNode(root);
					break;
				case UNARY_NODE:
					removeUnaryNode(root);
					break;

			}
			this.min = searchMin();
			this.max = searchMax();
			this.size--;
			return rebalanceAfterDeletion(deletedParent);
		}
	}

	/**
	 * ==================================================FOR US-TO DELETE================================================================
	 *
	 */
	public void print() {
		IAVLNode root=this.root;
		List<List<String>> lines = new ArrayList<List<String>>();

		List<IAVLNode> level = new ArrayList<IAVLNode>();
		List<IAVLNode> next = new ArrayList<IAVLNode>();

		level.add(root);
		int nn = 1;

		int widest = 0;

		while (nn != 0) {
			List<String> line = new ArrayList<String>();

			nn = 0;

			for (IAVLNode n : level) {
				if (n==null) {
					line.add(null);

					next.add(null);
					next.add(null);
				} else {
					String aa = Integer.toString(n.getKey());
					line.add(aa);
					if (aa.length() > widest) widest = aa.length();

					next.add(n.getLeft());
					next.add(n.getRight());

					if (n.getLeft() != null) nn++;
					if (n.getRight() != null) nn++;
				}
			}

			if (widest % 2 == 1) widest++;

			lines.add(line);

			List<IAVLNode> tmp = level;
			level = next;
			next = tmp;
			next.clear();
		}

		int perpiece = lines.get(lines.size() - 1).size() * (widest + 4);
		for (int i = 0; i < lines.size(); i++) {
			List<String> line = lines.get(i);
			int hpw = (int) Math.floor(perpiece / 2f) - 1;

			if (i > 0) {
				for (int j = 0; j < line.size(); j++) {

					// split node
					char c = ' ';
					if (j % 2 == 1) {
						if (line.get(j - 1) != null) {
							c = (line.get(j) != null) ? '┴' : '┘';
						} else {
							if (j < line.size() && line.get(j) != null) c = '└';
						}
					}
					System.out.print(c);

					// lines and spaces
					if (line.get(j) == null) {
						for (int k = 0; k < perpiece - 1; k++) {
							System.out.print(" ");
						}
					} else {

						for (int k = 0; k < hpw; k++) {
							System.out.print(j % 2 == 0 ? " " : "─");
						}
						System.out.print(j % 2 == 0 ? "┌" : "┐");
						for (int k = 0; k < hpw; k++) {
							System.out.print(j % 2 == 0 ? "─" : " ");
						}
					}
				}
				System.out.println();
			}

			// print line of numbers
			for (int j = 0; j < line.size(); j++) {

				String f = line.get(j);
				if (f == null) f = "";
				int gap1 = (int) Math.ceil(perpiece / 2f - f.length() / 2f);
				int gap2 = (int) Math.floor(perpiece / 2f - f.length() / 2f);

				// a number
				for (int k = 0; k < gap1; k++) {
					System.out.print(" ");
				}
				System.out.print(f);
				for (int k = 0; k < gap2; k++) {
					System.out.print(" ");
				}
			}
			System.out.println();

			perpiece /= 2;
		}
	}

	public AVLTree[] splitQ2(int x)
	{
		// smaller then x
		AVLTree T1=new AVLTree();

		// bigger then x
		AVLTree T2=new AVLTree();

		IAVLNode splitNode=findNode(x);
		List<Integer> costArr=new ArrayList<>();

		// split the node
		if(splitNode==this.root)
		{
			T1.setSubtree(this.root.getLeft());
			T2.setSubtree(this.root.getRight());

			T1.max=T1.searchMax();
			T1.min=T1.searchMin();
			T2.max=T2.searchMax();
			T2.min=T2.searchMin();


			System.out.println("All costs: "+ Arrays.toString(costArr.toArray()));
			System.out.println("max cost: "+ 0);
			System.out.println("average cost: "+ 0);

			AVLTree[] resultArr={T1,T2};
			return resultArr;
		}

		// set the sub-tree of splitNode
		if(splitNode.getLeft().isRealNode()){
			T1.setSubtree(splitNode.getLeft());
		}
		if(splitNode.getRight().isRealNode()){
			T2.setSubtree(splitNode.getRight());
		}

		IAVLNode nodeToJoin=splitNode.getParent();

		boolean isLeftSon=true;
		if(nodeToJoin.getRight()==splitNode)
			isLeftSon=false;

		while(nodeToJoin!=null){
			AVLTree joinTree=new AVLTree();

			// ptr is a left son
			if(isLeftSon){
				// update next node to join
				if(nodeToJoin.getParent()!=null) {
					isLeftSon = true;
					if (nodeToJoin.getParent().getRight() == nodeToJoin)
						isLeftSon = false;
				}

				IAVLNode tempNode = nodeToJoin;
				nodeToJoin = nodeToJoin.getParent();

				joinTree.setSubtree(tempNode.getRight());

				tempNode.setParent(null);
				tempNode.setRight(new AVLNode(-1, null, false));
				tempNode.setLeft(new AVLNode(-1, null, false));
				tempNode.setHeight(0);
				tempNode.updateSize();

				costArr.add(T2.join(tempNode,joinTree));
			}
			// ptr is a right son
			else
			{
				if(nodeToJoin.getParent()!=null) {
					isLeftSon = true;
					if (nodeToJoin.getParent().getRight() == nodeToJoin)
						isLeftSon = false;
				}

				IAVLNode tempNode = nodeToJoin;
				nodeToJoin = nodeToJoin.getParent();

				joinTree.setSubtree(tempNode.getLeft());

				tempNode.setParent(null);
				tempNode.setRight(new AVLNode(-1, null, false));
				tempNode.setLeft(new AVLNode(-1, null, false));
				tempNode.setHeight(0);
				tempNode.updateSize();

				costArr.add(T1.join(tempNode,joinTree));
			}
		}

		// Set right IAVLNodes to min max
		T1.max=T1.searchMax();
		T1.min=T1.searchMin();
		T2.max=T2.searchMax();
		T2.min=T2.searchMin();

		int max=0;
		int count=0;
		for(int i=0;i<costArr.size();i++){
			if(costArr.get(i)>max)
				max=costArr.get(i);
			count+=costArr.get(i);
		}

		System.out.println("All costs: "+ Arrays.toString(costArr.toArray()));
		System.out.println("max cost: "+ max);
		double average=count/(float)costArr.size();
		System.out.println("average cost: "+ average);

		AVLTree[] resultArr={T1,T2};
		return resultArr;
	}

	//================================================================================================================

	/**
	 * public interface IAVLNode
	 * ! Do not delete or modify this - otherwise all tests will fail !
	 */
	public interface IAVLNode{
		public int getKey(); // Returns node's key (for virtual node return -1).
		public String getValue(); // Returns node's value [info], for virtual node returns null.
		public void setLeft(IAVLNode node); // Sets left child.
		public IAVLNode getLeft(); // Returns left child, if there is no left child returns null.
		public void setRight(IAVLNode node); // Sets right child.
		public IAVLNode getRight(); // Returns right child, if there is no right child return null.
		public void setParent(IAVLNode node); // Sets parent.
		public IAVLNode getParent(); // Returns the parent, if there is no parent return null.
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node.
		public void setHeight(int height); // Sets the height of the node.
		public int getHeight(); // Returns the height of the node (-1 for virtual nodes).
		public int getSize();// Returns the size of the node's subtree.
		public void updateSize();// Updates the size of the node's subtree.
		public void setSize(int size);// Sets the size of the node's subtree.
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree
	 * (for example AVLNode), do it in this file, not in another file.
	 *
	 * This class can and MUST be modified (It must implement IAVLNode).
	 */
	public class AVLNode implements IAVLNode{

		private int key;
		private String info;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private final boolean isRealNode;
		private int height;
		private int size;

		/**
		 * public AVLNode(int key, String info, boolean isRealNode)
		 *
		 * Constructor of a node- virtual or real.
		 * if real- has size 1 (at the beginning), has its key and info, no parent, 2 virtual sons and height=0
		 * if virtual- has no sons, size=0, height= -1 the info will be null and key= -1.
		 *
		 * Complexity - O(1)
		 */
		public AVLNode(int key, String info, boolean isRealNode) {
			this.key = key;
			this.info = info;
			this.left = isRealNode ? new AVLNode(-1, null, false) : null;
			this.right = isRealNode ? new AVLNode(-1, null, false) : null;
			this.isRealNode = isRealNode;
			this.height = isRealNode ? 0 : -1;
			this.parent = null;

			if(this.isRealNode) {
				this.left.setParent(this);
				this.right.setParent(this);
				updateSize();
			}
			else{
				this.size=0;
			}

		}

		/**
		 * public int getKey()
		 * Returns node's key (for virtual node return -1).
		 * Complexity --O(1)
		 */
		public int getKey()
		{
			return this.key;
		}

		/**
		 * public String getValue()
		 * Returns node's value [info], for virtual node returns null.
		 * Complexity --O(1)
		 */
		public String getValue()
		{
			return this.info;
		}

		/**
		 * public void setLeft(IAVLNode node)
		 * Sets left child.
		 * Complexity --O(1)
		 */
		public void setLeft(IAVLNode node)
		{
			this.left = node;
		}

		/**
		 * public IAVLNode getLeft()
		 * Returns left child, if there is no left child returns null.
		 * Complexity --O(1)
		 */
		public IAVLNode getLeft()
		{
			return this.left;
		}

		/**
		 * public void setRight(IAVLNode node)
		 * Sets right child.
		 * Complexity --O(1)
		 */
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}

		/**
		 * public IAVLNode getRight()
		 * Returns right child, if there is no right child returns null.
		 * Complexity --O(1)
		 */
		public IAVLNode getRight()
		{
			return this.right;
		}

		/**
		 * public void setParent(IAVLNode node)
		 * Sets parent.
		 * Complexity --O(1)
		 */
		public void setParent(IAVLNode node)
		{
			this.parent = node;
		}

		/**
		 * public IAVLNode getParent()
		 * Returns the parent, if there is no parent return null.
		 * Complexity --O(1)
		 */
		public IAVLNode getParent()
		{
			return this.parent;
		}

		/**
		 * public boolean isRealNode()
		 * Returns True if this is a non-virtual AVL node.
		 * Complexity --O(1)
		 *
		 */
		public boolean isRealNode()
		{
			return this.isRealNode;
		}

		/**
		 * public void setHeight(int height)
		 * Sets the height of the node.
		 * Complexity --O(1)
		 */
		public void setHeight(int height)
		{
			this.height = height;
		}

		/**
		 * public int getHeight()
		 * Returns the height of the node (-1 for virtual nodes).
		 * Complexity --O(1)
		 */
		public int getHeight()
		{
			return this.height;
		}

		/**
		 * public int getSize()
		 * Returns the size of the node's subtree.
		 * Complexity --O(1)
		 */
		public int getSize()
		{
			return this.size;
		}

		/**
		 * public void updateSize()
		 * Update the size according to it's sons
		 * Complexity --O(1)
		 */
		public void updateSize()
		{
			if(this.isRealNode){
				this.size=this.left.getSize()+this.right.getSize()+1;
			}
			else
				this.size=0;
		}

		/**
		 * public void updateSize()
		 * Set the size according to given size
		 * Complexity --O(1)
		 */
		public void setSize(int size)
		{
			this.size=size;
		}
	}
}