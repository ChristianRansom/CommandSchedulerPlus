package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import muttsworld.dev.team.CommandSchedulerPlus.AVLTree.AVLTreeNode;

class BST<E extends Comparable<E>> implements Iterable<E>, Serializable {

	private static final long serialVersionUID = 1L;

	TreeNode<E> root, current, parent;
	ArrayList<TreeNode<E>> currentPath;

	private int i;

	public BST() {
		//ArrayList<TreeNode<E>> currentPath = new ArrayList<TreeNode<E>>();
	}

	public BST(ArrayList<E> objects) {
		for (E entry : objects)
			insert(entry);
	}

	public TreeNode<E> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<E> root) {
		this.root = root;
	}

    public ArrayList<TreeNode<E>> path(E e) {
        ArrayList<TreeNode<E>> list = new ArrayList<>();
        TreeNode<E> current = root; // Start from the root
        while (current != null) {
            list.add(current); // Add the node to the list
            if (e.compareTo(current.element) < 0) {
                current = current.left;
            } else if (e.compareTo(current.element) > 0) {
                current = current.right;
            } else {
                break;
            }
        }
        return list; // Return an array of nodes
    }

	public java.util.Iterator<E> iterator() {
		return new InorderIterator();
	}

	public TreeNode<E> createNewNode(E e) {
		return new TreeNode<>(e);

	}

	public boolean search(E e) {
		TreeNode<E> current = root;

		while (current != null) {
			if (e.compareTo(current.element) < 0)
				current = current.left;
			else if (e.compareTo(current.element) > 0)
				current = current.right;
			else
				return true;
		}
		return false;
	}

	// Check for various cases
	public boolean delete(E e) {
		TreeNode<E> parent = null;
		TreeNode<E> current = root;
		while (current != null) {
			if (e.compareTo(current.element) < 0) {
				parent = current;
				current = current.left;
			} else if (e.compareTo(current.element) > 0) {
				parent = current;
				current = current.right;
			} else
				break;
		}
		if (current == null)
			return false;

		return deleteNode(e, parent, current);
	}

	private boolean deleteNode(E e, TreeNode<E> parent, TreeNode<E> current) {
		if (current.left == null) {
			if (parent == null) {
				root = current.right;
			} else {
				if (e.compareTo(parent.element) < 0)
					parent.left = current.right;
				else
					parent.right = current.right;
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
		}
		return true;
	}

	// way to make sure
	// two different search trees are the same
	// contentwise
	//Needed to import and cast as AVLTreeNodes to preserver proper size values
	public void inOrder(CommandSender sender) {
		i = 1;
		inOrder((AVLTreeNode<E>)root, sender);
		//System.out.println();
	}

	//TODO create a method that returns an array... this class should only be the data structure...
	public void inOrder(AVLTreeNode<E> root, CommandSender sender) {
		if (root == null)
			return;
		inOrder((AVLTreeNode<E>)root.left, sender);
		sender.sendMessage(PluginMessages.prefix + (i++) + ". " + root.element.toString());
		inOrder((AVLTreeNode<E>)root.right, sender);
	}

	public void preOrder(CommandSender sender) {
		i = 1;
		preOrder((AVLTreeNode<E>)root, sender);
	}

	public void preOrder(AVLTreeNode<E> root, CommandSender sender) {
		if (root == null)
			return;
		sender.sendMessage(PluginMessages.prefix + (i++) + " size = " + root.size + " height = " + root.height + " | " + root.element);
		preOrder((AVLTreeNode<E>)root.left, sender);
		preOrder((AVLTreeNode<E>)root.right, sender);
	}

	/////////////////////////////////////////////////////////
	// use preOrder to clone the tree
	// with the objects in the exact
	// same spots
	public BST<E> clone() {
		// generate an arraylist
		ArrayList<E> list1 = new ArrayList<>();
		helpClone(list1, root);

		return new BST<E>(list1);
	}

	// essentially doing what preOrder was doing
	// except adding to the arraylist
	// instead of printing
	public void helpClone(ArrayList<E> list1, TreeNode<E> root) {
		if (root == null)
			return;
		list1.add(root.element);
		helpClone(list1, root.left);
		helpClone(list1, root.right);
	}
	/////////////////////////////////////////////////////////

	////////////////////////////////////////////////////
	// use inOrdert to test
	// if the trees have the same
	// elements
	public boolean equals(BST<E> s) {
		// quick check
		// if(s.size != size) return false;

		// helperEquals1 returns an arraylist
		// of the BST in order
		ArrayList<E> self = helperEquals1();
		ArrayList<E> other = s.helperEquals1();

		// compare as Objects
		Object[] me = self.toArray();
		Object[] you = other.toArray();
		for (int i = 0; i < me.length; i++) {
			// Stop once there is
			// a discrepancy
			if (me[i] != you[i])
				return false;
		}

		return true;
	}

	// helper 1 and helper 2
	// are essentially doing
	// what inOrder was doing
	// except adding to an arraylist instead of
	// printing
	public ArrayList<E> helperEquals1() {
		ArrayList<E> list2 = new ArrayList<>();
		helperEquals2(root, list2);
		return list2;
	}

	public void helperEquals2(TreeNode<E> root, ArrayList<E> list2) {
		if (root == null)
			return;
		helperEquals2(root.left, list2);
		list2.add(root.element);
		helperEquals2(root.right, list2);
	}

	///////////////////////////////////////////////////////

	public int getSize() {
		//System.out.println("Root " + root.element);
		//System.out.println("Root.size " + root.size);
		return ((AVLTreeNode<E>)root).size;
	}

	public boolean isEmpty() {
		return root == null;
	}

	public boolean insert(E o) {
		currentPath = new ArrayList<TreeNode<E>>();
		//System.out.println("Inserting into the tree");
		if (root == null) {
			root = createNewNode(o);
		} 
		else {
			parent = null;
			current = root;
			while (current != null) {
				currentPath.add(current);
				if (o.compareTo(current.element) < 0) {
					parent = current;
					current = current.left;
				} else if (o.compareTo(current.element) > 0) {
					parent = current;
					current = current.right;
				} else {
					//System.out.println("Found duplicate node. Can't insert. Returning False");
					return false;
				}
			}
			if (o.compareTo(parent.element) < 0)
				parent.left = createNewNode(o);
			else
				parent.right = createNewNode(o);
		}
		
		//System.out.println("CurrentPath after insert: " + currentPath);
		return true;
	}

	public void clear() {
		root = null;
		root.size = 0;
	}

	public static class TreeNode<E extends Comparable<E>> implements Serializable {
		public int size = 1;
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		E element;
		TreeNode<E> left;
		TreeNode<E> right;

		public TreeNode(E e) {
			element = e;
		}

		// Way to compare
		public int compareTo(TreeNode<E> o) {
			return 1;
		}
	}

	private class InorderIterator implements java.util.Iterator<E> {
		private ArrayList<E> list = new ArrayList<>();
		private int current = 0;

		public InorderIterator() {
			inOrder();
		}

		// easy reference to the other
		// overloading
		private void inOrder() {
			inOrder(root);
		}

		// traverses in order (for Strings, alphabetically
		private void inOrder(TreeNode<E> root) {
			if (root == null)
				return;
			inOrder(root.left);
			list.add(root.element);
			inOrder(root.right);
		}

		// returns true or false
		// depending on whether or not
		// there is another node further down.
		public boolean hasNext() {
			if (current < list.size())
				return true;
			return false;
		}

		public E next() {
			return list.get(current++);
		}

		public void remove() {
			delete(list.get(current));
			list.clear();
		}
	}
}
