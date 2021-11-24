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


	IAVLNode root;
	private int size = 0;


	/**
	 * public AVLTree()
	 *
	 * Constructor of empty AVL tree
	 *
	 */
	public AVLTree() {
		this.root = null;
	}

	/**
	 * public boolean empty()
	 *
	 * Returns true if and only if the tree is empty.
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
	 */
	public int insert(int k, String i) {
		// If tree is empty, set root as new node with key k and info i
		if(this.empty()) {
			this.root = new AVLNode(k, i, true);
			this.size++;
			return 0;
		}

		// if key exists in the tree return -1
		if(this.search(k) != null) {
			return -1;
		}

		// Perform normal BST insertion
		IAVLNode ptr = this.root;

		while(ptr.isRealNode()) {
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
		ptr = parent;
		int balance = getBalanceFactor(ptr);

		while(-1 <= balance && balance <= 1) {
			ptr = ptr.getParent();
			if(ptr == null) {
				break;
			}
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
	 * public int delete(int k)
	 *
	 * Deletes an item with key k from the binary tree, if it is there.
	 * The tree must remain valid, i.e. keep its invariants.
	 * Returns the number of re-balancing operations, or 0 if no re-balancing operations were necessary.
	 * A promotion/rotation counts as one re-balance operation, double-rotation is counted as 2.
	 * Returns -1 if an item with key k was not found in the tree.
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
		return rebalanceAfterDeletion(parentOfDeleted);
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree,
	 * or null if the tree is empty.
	 */
	public String min()
	{
		if(this.empty()) {
			return null;
		}

		IAVLNode ptr = this.root;

		while (ptr.getLeft().isRealNode()) {
			ptr = ptr.getLeft();
		}
		return ptr.getValue();
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree,
	 * or null if the tree is empty.
	 */
	public String max()
	{
		if(this.empty()) {
			return null;
		}

		IAVLNode ptr = this.root;

		while(ptr.getRight().isRealNode()) {
			ptr = ptr.getRight();
		}
		return ptr.getValue();
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree,
	 * or an empty array if the tree is empty.
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
	 */
	public AVLTree[] split(int x)
	{
		AVLTree t1=new AVLTree();
		AVLTree t2=new AVLTree();

		IAVLNode splitNode=findNode(x);
		while(splitNode.getParent()!=root){
		}



		AVLTree[] resultArr={t1,t2};
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
	 */
	public int join(IAVLNode x, AVLTree t)
	{
		return -1;
	}


	/**
	 * public int getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
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
	 **/
	private int getBalanceFactor(IAVLNode node) {
		if(node == null) return 0;
		return node.getLeft().getHeight() - node.getRight().getHeight();
	}

	/**
	 * private void rotateRight(IAVLNode x)
	 *
	 * Rotate right according to a given node and update height
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

	}

	/**
	 * private void rotateLeft(IAVLNode x)
	 *
	 * Rotate left according to a given node and update height
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
	}

	/**
	 * private void updateHeights(IAVLNode node)
	 *
	 * Traverse up the tree updating the heights
	 * Return number of promotions
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
	 * @pre parentOfDeleted != null
	 * @return number of re-balancing operations.
	 */
	private int rebalanceAfterDeletion(IAVLNode parentOfDeleted) {
		IAVLNode ptr = parentOfDeleted;
		int operations = 0; // Rotations, promotions and demotions

		while(ptr != null) { // Traverse up the tree
			int oldHeight = ptr.getHeight();
			int newHeight = 1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight());
			ptr.setHeight(newHeight);

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
	 * @pre: nodeA.isRealNode() == true  == nodeB.isRealNode()
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
	}

	/**
	 * @pre: child.getParent() == parent
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
		}
	}

	/**
	 * private IAVLNode findNode(int k)
	 *
	 * Returns the AVLNode that contain k
	 * If k doesn't exist in the tree - return null
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
	 * @pre : node is a leaf node && not the root
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
	 * @pre: node is an unary node
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
	 * @pre: node is an internal node.
	 * Internal node has a right child so its successor is a leaf node or unary.
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

	private int removeRootNode() {
		IAVLNode root = this.root;
		if(this.size == 1) { // If the tree only has one node, remove it and set the tree as empty
			this.root = null;
			this.size = 0;
			return 0;
		}

		if(!root.getLeft().isRealNode()) { // If the tree has only a right child
			root.getRight().setParent(null);
			this.root = root.getRight();
			this.size = 1;
			return 0;
		}
		else if(!root.getRight().isRealNode()) { // If the tree only has a left child
			root.getLeft().setParent(null);
			this.root = root.getLeft();
			this.size = 1;
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
			this.size--;
			return rebalanceAfterDeletion(deletedParent);
		}
	}

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
			}
		}


		public int getKey()
		{
			return this.key;
		}
		public String getValue()
		{
			return this.info;
		}
		public void setLeft(IAVLNode node) {
			this.left = node;
		};

		public IAVLNode getLeft()
		{
			return this.left;
		}
		public void setRight(IAVLNode node)
		{
			this.right = node;
		}
		public IAVLNode getRight()
		{
			return this.right;
		}
		public void setParent(IAVLNode node)
		{
			this.parent = node;
		}
		public IAVLNode getParent()
		{
			return this.parent;
		}
		public boolean isRealNode()
		{
			return this.isRealNode;
		}
		public void setHeight(int height)
		{
			this.height = height;
		}
		public int getHeight()
		{
			return this.height;
		}
	}
}