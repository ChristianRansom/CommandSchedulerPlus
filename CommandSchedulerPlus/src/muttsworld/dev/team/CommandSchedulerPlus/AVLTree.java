package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;


public class AVLTree<E extends Comparable<E>> extends BST<E> implements Serializable {

	private static final long serialVersionUID = 1L;

	public AVLTree() {
		super();
	}

	public AVLTree(ArrayList<E> objects) {
		super(objects);
		// refers back to BST class
	}

	public AVLTreeNode<E> createNewNode(E e) { // override
		return new AVLTreeNode<E>(e);
	}

	public boolean insert(E e) {
		// My solution seems to work well
		// without changing insert or delete
		//System.out.println("Before Insert:");
		//preOrder();
		boolean successful = super.insert(e);
		if (!successful)
			return false;
		else {
			//currentPath = path(e);
			//System.out.println();
			for(TreeNode<E> node : currentPath){
				((AVLTreeNode<E>)node).size += 1; //adds 1 to the size of each node along the insertion path
			}
			//System.out.println("After Insert but before balancing and height update:");
			//preOrder();
			balancePath(e);
			//System.out.println("After balancing:");
			//preOrder();
		}
		return true;
	}

	// updates Height
	private void updateHeight(AVLTreeNode<E> node) {
		if (node.left == null && node.right == null) {
			node.height = 0;

		} else if (node.left == null)
			node.height = 1 + ((AVLTreeNode<E>) (node.right)).height;
		else if (node.right == null)
			node.height = 1 + ((AVLTreeNode<E>) (node.left)).height;
		else
			node.height = 1 + Math.max(((AVLTreeNode<E>) (node.right)).height, ((AVLTreeNode<E>) (node.left)).height);

	}
	
	private void updateSize(AVLTreeNode<E> node)
	{
       
		if(node.left == null && node.right == null) 
		{
			node.size = 1;
           
		}
		else if (node.left == null)
			node.size = 1 + ((AVLTreeNode<E>)(node.right)).size;
		else if (node.right == null)
			node.size = 1 + ((AVLTreeNode<E>)(node.left)).size;
		else
			node.size = 1 + 
                   ((AVLTreeNode<E>)(node.right)).size +
                   ((AVLTreeNode<E>)(node.left)).size;
   	}

	// this method calls
	// balanceLR, balance,RR etc
	private void balancePath(E e) {
		ArrayList<TreeNode<E>> path = path(e);
		for (int i = path.size() - 1; i >= 0; i--) {
			AVLTreeNode<E> A = (AVLTreeNode<E>) (path.get(i));
			updateHeight(A);
			
			AVLTreeNode<E> parentOfA = (A == root) ? null : (AVLTreeNode<E>) (path.get(i - 1));

			switch (balanceFactor(A)) {
			case -2:
				if (balanceFactor((AVLTreeNode<E>) A.left) <= 0) {
					balanceLL(A, parentOfA);
				} else
					balanceLR(A, parentOfA);
				break;
			case +2:
				if (balanceFactor((AVLTreeNode<E>) A.right) >= 0)
					balanceRR(A, parentOfA);
				else
					balanceRL(A, parentOfA);
			}
		}
	}

	// tells balancePath is anything is uneven
	// that is -2 or +2 at any given
	// root
	public int balanceFactor(AVLTreeNode<E> node) {
		if (node.right == null)
			return -node.height;
		else if (node.left == null)
			return +node.height;
		else
			return ((AVLTreeNode<E>) node.right).height - ((AVLTreeNode<E>) node.left).height;
	}

	private void balanceLL(TreeNode<E> A, TreeNode<E> parentOfA) {
		TreeNode<E> B = A.left;
		
		//Promotes B
		if (A == root) {
			root = B;
		} else {
			if (parentOfA.left == A) {
				parentOfA.left = B;
			} else
				parentOfA.right = B;
		}
		//B.size = A.size;

		//Trade Adopted Node
		A.left = B.right;
		B.right = A;
		
		updateSize((AVLTreeNode<E>) A);
		updateSize((AVLTreeNode<E>) B);
		
		updateHeight((AVLTreeNode<E>) A);
		updateHeight((AVLTreeNode<E>) B);

	}

	private void balanceLR(TreeNode<E> A, TreeNode<E> parentOfA) {
		TreeNode<E> B = A.left;
		TreeNode<E> C = B.right;

		//Promote C
		if (A == root) {
			root = C;
		} else {
			if (parentOfA.left == A) {
				parentOfA.left = C;
			} else {
				parentOfA.right = C;
			}
		}

		A.left = C.right;
		B.right = C.left;
		C.left = B;
		C.right = A;

		updateSize((AVLTreeNode<E>)A);
		updateSize((AVLTreeNode<E>)B);
		updateSize((AVLTreeNode<E>)C);

		updateHeight((AVLTreeNode<E>) A);
		updateHeight((AVLTreeNode<E>) B);
		updateHeight((AVLTreeNode<E>) C);
	}

	private void balanceRR(TreeNode<E> A, TreeNode<E> parentOfA) {
		TreeNode<E> B = A.right;

		//Promotes B
		if (A == root) {
			root = B;
		}
		else {
			if (parentOfA.left == A) {
				parentOfA.left = B;
			} else {
				parentOfA.right = B;

			}
		}
		//B.size = A.size;

		//Transfers child
		A.right = B.left;
		B.left = A;
		
		updateSize((AVLTreeNode<E>) A);
		updateSize((AVLTreeNode<E>) B);

		updateHeight((AVLTreeNode<E>) A);
		updateHeight((AVLTreeNode<E>) B);

	}

	private void balanceRL(TreeNode<E> A, TreeNode<E> parentOfA) {
		TreeNode<E> B = A.right;
		TreeNode<E> C = B.left;

		//Double Promotes C
		if (A == root) {
			root = C;
		}

		else {
			if (parentOfA.left == A) {
				parentOfA.left = C;
			} else {
				parentOfA.right = C;
			}
		}
		C.size = A.size;
		
		A.right = C.left;
		B.left = C.right;
		C.left = A;
		C.right = B;

		updateSize((AVLTreeNode<E>) A);
		updateSize((AVLTreeNode<E>) B);
		updateSize((AVLTreeNode<E>) C);
		
		updateHeight((AVLTreeNode<E>) A);
		updateHeight((AVLTreeNode<E>) B);
		updateHeight((AVLTreeNode<E>) C);
	}

	public boolean delete(E element) {
		//System.out.println("Deleting an element");
		if (root == null)
			return false;

		TreeNode<E> parent = null;
		TreeNode<E> current = root;
		while (current != null) {
			//System.out.println("Delete: element.compareTo(current.element) : " + (element.compareTo(current.element)));
			if (element.compareTo(current.element) < 0) {
				parent = current;
				current = current.left;
			} else if (element.compareTo(current.element) > 0) {
				parent = current;
				current = current.right;
			} else
				break;
		}
		if (current == null) {
			//System.out.println("Element not found. Deletion failed. ");
			return false;
		}

		if (current.left == null) {
			if (parent == null)
				root = current.right;

			else {
				if (element.compareTo(parent.element) < 0)
					parent.left = current.right;
				else
					parent.right = current.right;

				balancePath(parent.element);
			}
		} else {
			TreeNode<E> parentOfRightMost = current;
			TreeNode<E> rightMost = current.left;

			while (rightMost.right != null) {
				parentOfRightMost = rightMost;
				rightMost = rightMost.right;
			}

			current.element = rightMost.element;

			if (parentOfRightMost.right == rightMost)
				parentOfRightMost.right = rightMost.left;
			else
				parentOfRightMost.left = rightMost.left;

			balancePath(parentOfRightMost.element);
		}
		return true;
	}

	public E find(int k) {
		// make a reference to the root
		// AVLTreeNode<E> test = (AVLTreeNode)root;
		// invoke recursive helper method
		E o = (E) find(k, (AVLTreeNode<E>) this.root);

		return o;
	}

	public E find(int k, AVLTreeNode<E> node) {

		// key players
		AVLTreeNode<E> A = (AVLTreeNode<E>)(node.left);
		AVLTreeNode<E> B = (AVLTreeNode<E>)(node.right);
		//System.out.println("Finding(k) K = " + k);
		if (A != null) {
			//System.out.print("A.size : " + A.size);
		}
		if (A == null && k == 1) { //root.element, if A is null and k is 1;
			//System.out.println("A == null & k = 1 returning node");
			return node.element;
		} else if (A == null && k == 2) { //B.element, if A is null and k is 2;
			//System.out.println("A == null & k = 2 returning node");
			return B.element;
		} else if (k <= A.size) { //find(k, A), if k <= A.size;
			//System.out.println("K < A.size so it must be a child of A");
			return find(k, A);
		}
		else if (k == (A.size + 1)) { //root.element, if k = A.size + 1;
			return node.element;
		} else if (k > (A.size + 1)) {
			return find(k - A.size - 1, B); //find(k - A.size - 1, B), if k > A.size + 1;
		} else
			return null;
	}

	public static class AVLTreeNode<E extends Comparable<E>> extends BST.TreeNode<E> implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int height = 0;
		/* To maintain the size of each node, for insertions, every node on the path from the 
		 * root to the insertion the size is incremented. 
		 * Then during balancing, the size for specific nodes values are simply swapped. 
		 * 
		*/
		protected int size = 1;

		public AVLTreeNode(E e) {
			super(e);
		}

	}
}
