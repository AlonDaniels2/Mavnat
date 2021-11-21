import java.lang.reflect.Array;

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


		int rotations = 0;

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

		// Update parent height
		parent.setHeight(1 + Math.max(parent.getLeft().getHeight(), parent.getRight().getHeight()));

		// Check if node is unbalanced
		ptr = parent;
		int balance = getBalanceFactor(ptr);

		while(-1 <= balance && balance <= 1) {
			ptr = ptr.getParent();
			if(ptr == null) {
				break;
			}
			ptr.setHeight(1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight()));
			balance = getBalanceFactor(ptr);
		}
		if(ptr == null) { // No rebalance needed
			return 0;
		}
		else {
			this.size++;

			// Case 1 - rotate right
			if(balance > 1 && k < ptr.getLeft().getKey()) {
				rotateRight(ptr);
				updateHeights(ptr.getParent());
				return 1;
			}
			// Case 2 - rotate left
			else if(balance < -1 && k > ptr.getRight().getKey()) {
				rotateLeft(ptr);
				updateHeights(ptr.getParent());
				return 1;
			}
			// Case 3 - rotate left right
			else if(balance > 1 && k > ptr.getLeft().getKey()) {
				IAVLNode left = ptr.getLeft();
				rotateLeft(left);
				ptr.setLeft(left.getParent());

				updateHeights(ptr.getParent());

				return 2;
			}
			// Case 4 - rotate right left
			else {
				IAVLNode right = ptr.getRight();
				rotateRight(right);
				ptr.setRight(right.getParent());
				rotateLeft(ptr);

				updateHeights(ptr.getParent());
				return 2;
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
		if(this.empty())
			return -1;

		IAVLNode nodeToDelete=FindNode(k);

		// if k doesnt exist in the tree- return -1
		if(nodeToDelete == null) {
			return -1;
		}

		if(nodeToDelete==this.getRoot())
		{
			this.root=null;
		}

		IAVLNode nodeParent=nodeToDelete.getParent();

		// the node that after deletion may cause a problem
		IAVLNode nodeToFix=null;

		// k exists- check the type of the node and delete it,
		// every type has different type of deleteation
		int nodeType=typeOfNode(nodeToDelete);
		switch(nodeType) {
			// replace the leaf to be a virtual son for his parent
			case LEAF_NODE:
				IAVLNode virtualNode=new AVLNode(-1, null, false);
				if (nodeParent.getRight() == nodeToDelete) {
					nodeParent.setRight(virtualNode);
				} else {
					nodeParent.setLeft(virtualNode);
				}
				virtualNode.setParent(nodeParent);
				nodeToFix = nodeParent;
				break;
			// replace the unary node with his son
			case UNARY_NODE:
				//find which side of the parent the node we need to delete is on
				// and which side the son of the node we want to delete is on
				// delete the node and conect his son to his parent
				if (nodeParent.getRight() == nodeToDelete) {
					if (nodeToDelete.getRight().isRealNode()) {
						nodeParent.setRight(nodeToDelete.getRight());
						nodeToDelete.getRight().setParent(nodeParent);
					} else {
						nodeParent.setRight(nodeToDelete.getLeft());
						nodeToDelete.getLeft().setParent(nodeParent);
					}
				} else {
					if (nodeToDelete.getLeft().isRealNode()) {
						nodeParent.setLeft(nodeToDelete.getLeft());
						nodeToDelete.getLeft().setParent(nodeParent);
					} else {
						nodeParent.setLeft(nodeToDelete.getRight());
						nodeToDelete.getRight().setParent(nodeParent);
					}
				}
				nodeToFix = nodeParent;
				break;
			// find it's successor, make the deletion and get the problematic node
			case INTERNAL_NODE:
				IAVLNode nodeSuccessor = FindNodeSuccessor(nodeToDelete);
				nodeToFix = ReplaceAndDelete(nodeToDelete, nodeSuccessor);
				break;
		}

		if (nodeToFix==null)
			return 0;

		// the parent of the deleted node has the same height
		// so all the heights are kept, no changes needed- valid Avl Tree
		if(!IsHeightChanged(nodeToFix))
			return 0;

		nodeToFix.setHeight(1+Math.max(nodeToFix.getLeft().getHeight(), nodeToFix.getRight().getHeight()));
		// add 1 for the demote and rebalance tree if needed.
		return RebalanceDel(nodeToFix,0);
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

		IAVLNode splitNode=FindNode(x);
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
		IAVLNode rightTree = y.getRight();
		y.setRight(x);
		x.setLeft(rightTree);
		y.setParent(x.getParent());
		x.setParent(y);
		rightTree.setParent(x);

		if (parent != null) {
			if(parent.getRight() == x) {
				parent.setRight(y);
			}
			else {
				parent.setLeft(y);
			}
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
		IAVLNode leftTree = x.getLeft();
		y.setLeft(x);
		x.setRight(leftTree);
		y.setParent(x.getParent());
		x.setParent(y);
		leftTree.setParent(x);

		if (parent != null) {
			if(parent.getLeft() == x) {
				parent.setLeft(y);
			}
			else {
				parent.setRight(y);
			}
		}
		// Update heights
		x.setHeight(1 + Math.max(x.getLeft().getHeight(), x.getRight().getHeight()));
		y.setHeight(1 + Math.max(y.getLeft().getHeight(), y.getRight().getHeight()));
	}


	/**
	 * private Boolean IsHeightChanged(IAVLNode node)
	 *
	 * Returns true if the current value in the height's node is different
	 * from the current height of the node
	 *
	 */
	private Boolean IsHeightChanged(IAVLNode node)
	{
		//check if node's height hasnt changed
		int tempHeight=1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight());
		if(tempHeight==node.getHeight())
			// hasn't changed
			return false;
		return true;
	}

	/**
	 * private void updateHeights(IAVLNode node)
	 *
	 * Traverse up the tree updating the heights
	 *
	 **/
	private void updateHeights(IAVLNode node) {
		IAVLNode ptr = node;
		while(ptr != null) {
			ptr.setHeight(1 + Math.max(ptr.getLeft().getHeight(), ptr.getRight().getHeight()));
			ptr = ptr.getParent();
		}
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
	 * private IAVLNode FindNode(int k)
	 *
	 * Returns the AVLNode that contain k
	 * if k doesnt exist in the tree- return null
	 *
	 */
	private IAVLNode FindNode(int k){

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
	 * private IAVLNode FindNodeSuccessor(IAVLNode node)
	 *
	 * Returns node's successor
	 *
	 */
	private IAVLNode FindNodeSuccessor(IAVLNode node){
		IAVLNode ptr;

		// if node has right son- return the minimum from it sub-tree
		if (node.getRight().isRealNode()){
			ptr=node.getRight();
			while (ptr.getLeft().isRealNode()) {
				ptr = ptr.getLeft();
			}
			return ptr;
		}

		IAVLNode tempNode=node;
		ptr=node.getParent();
		while(ptr!=null && tempNode==ptr.getRight()){
			tempNode=ptr;
			ptr=tempNode.getParent();
		}

		// if x is the maximum it return null
		return ptr;
	}

	/**
	 * private IAVLNode ReplaceAndDelete(IAVLNode nodeToDel,IAVLNode nodeSec)
	 *
	 * make the deletion of a node with 2 sons- the successor has no left child
	 * so we remove it from the tree and put him instead of the node we want do delete
	 *
	 */
	private IAVLNode ReplaceAndDelete(IAVLNode nodeToDel,IAVLNode nodeSeccessor){

		IAVLNode nodeParent=nodeSeccessor.getParent();
		IAVLNode nodeChild=nodeSeccessor.getRight();

		// Check which type of son the seccessor is for his parent and remove it
		if(nodeParent.getRight()==nodeSeccessor)
			nodeParent.setRight(nodeChild);

		else
			nodeParent.setLeft(nodeChild);

		// Update new parent and child
		nodeChild.setParent(nodeParent);

		// put the nodeSeccessor in the place of nodeToDel
		nodeSeccessor.setParent(nodeToDel.getParent());
		nodeSeccessor.setLeft(nodeToDel.getLeft());
		nodeSeccessor.setRight(nodeToDel.getRight());

		return nodeParent;
	}

	/**
	 * private int Rotate(IAVLNode node,int direction1,int direction2)
	 *
	 * make right/left rotation, update tree
	 * and return number of promote+demote/rotation
	 * that has been made
	 *
	 */
	private int Rotate(IAVLNode node,int direction) {

		int count = 0;

		if (direction == RIGHT) {
			// get the node/sub-trees that will be replaced
			IAVLNode x = node;
			IAVLNode y = x.getLeft();
			IAVLNode rightTreeY = y.getRight();

			// connect y to x parent or make it a root
			if (node.getParent() == null) {
				this.root = y;
			} else {
				// get the node/sub-trees that will be replaced
				IAVLNode parentX = node.getParent();

				if (parentX.getRight() == node) {
					parentX.setRight(y);
					y.setParent(parentX);
				} else {
					parentX.setLeft(y);
					y.setParent(parentX);
				}
			}

			y.setRight(x);
			x.setParent(y);
			x.setLeft(rightTreeY);
			rightTreeY.setParent(x);

			// Update height and count demote/promote
			if(IsHeightChanged(x)) {
				int tempHeight=x.getHeight();
				x.setHeight(1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));
				count += (tempHeight-x.getHeight());
			}
			if(IsHeightChanged(y)) {
				int tempHeight=y.getHeight();
				y.setHeight(1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));
				count += (y.getHeight()-tempHeight);
			}
		}
		else {
			if (direction == LEFT) {

				// get the node/sub-trees that will be replaced
				IAVLNode x = node;
				IAVLNode y = x.getLeft();
				IAVLNode leftTreeY = y.getLeft();

				if (node.getParent() == null) {
					this.root = y;
				} else {

					IAVLNode parentX = node.getParent();

					if (parentX.getRight() == node) {
						parentX.setRight(y);
						y.setParent(parentX);
					} else {
						parentX.setLeft(y);
						y.setParent(parentX);
					}

					y.setLeft(x);
					x.setParent(y);
					x.setRight(leftTreeY);
					leftTreeY.setParent(x);
				}
				y.setLeft(x);
				x.setParent(y);
				x.setRight(leftTreeY);
				leftTreeY.setParent(x);

				// Update height and count demote/promote
				if (IsHeightChanged(x)) {
					int tempHeight = x.getHeight();
					x.setHeight(1 + Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));
					count += (tempHeight - x.getHeight());
				}
				if (IsHeightChanged(y)) {
					int tempHeight = y.getHeight();
					y.setHeight(1 + Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));
					count += (y.getHeight() - tempHeight);
				}
			}
		}
		return 1+count;
	}

	/**
	 * private int RebalanceDel(IAVLNode node)
	 *
	 * Rebalance the tree until its fixed and return number
	 * of rotation that has been made along the way
	 *
	 */
	private int RebalanceDel(IAVLNode node,int rot){

		//node isnt a root
		if(node.getParent()!=null) {

			boolean isHeightChanged=IsHeightChanged(node);
			int balanceParent=this.getBalanceFactor(node);

			// BF is less then 2 and bigger then -2
			if(balanceParent<2 && balanceParent>-2)
			{
				// the parent of the changed node has the
				// same height so all the heights are kept,
				// no changes needed- valid Avl Tree
				if(!isHeightChanged)
					return rot;

				// deomote- update height
				node.setHeight(1+Math.max(node.getLeft().getHeight(), node.getRight().getHeight()));

				// the height is changed, we need to go up the tree and
				// fix if needed
				return RebalanceDel(node.getParent(),rot+1);
			}
			else if (balanceParent==-2)
			{
				int balanceSon=this.getBalanceFactor(node.getRight());
				if(balanceSon==-1 || balanceSon==0)
				{
					rot+=this.Rotate(node,LEFT);
					return RebalanceDel(node.getParent(),rot);
				}
				else
				if(balanceSon==1)
				{
					rot+=this.Rotate(node,RIGHT);
					rot+=this.Rotate(node,LEFT);
					return RebalanceDel(node.getParent(),rot);
				}
			}
			else
			if(balanceParent==2)
			{
				int balanceSon=this.getBalanceFactor(node.getLeft());
				if(balanceSon==1 || balanceSon==0)
				{
					rot+=this.Rotate(node,RIGHT);
					return RebalanceDel(node.getParent(),rot);
				}
				else if(balanceSon==-1)
				{
					rot+=this.Rotate(node,LEFT);
					rot+=this.Rotate(node,RIGHT);
					return RebalanceDel(node.getParent(),rot);
				}
			}
		}
		return rot;
	}

	/**
	 * private int getRankDifference(IAVLNode node) {
	 *
	 *
	 * Rebalance the tree until its fixed and return number
	 * of rotation that has been made along the way
	 *
	 */
	private int getRankDifference(IAVLNode node) {
		if(node == this.root) {
			return -2;
		}
		else {
			return node.getParent().getHeight() - node.getHeight();
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
			return this.isRealNode();
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
  
