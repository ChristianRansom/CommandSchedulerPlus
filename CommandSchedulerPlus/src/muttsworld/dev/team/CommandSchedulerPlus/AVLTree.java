package muttsworld.dev.team.CommandSchedulerPlus;

import java.io.Serializable;
import java.util.ArrayList;

public class AVLTree<E extends Comparable<E>> extends BST<E> implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AVLTree() {}
    public AVLTree(ArrayList<E> objects)
    {
        super(objects);
        // refers back to BST class
    }
    
    public AVLTreeNode<E> createNewNode(E e)
    { // override
        return new AVLTreeNode<E>(e);
    }
    
    public boolean insert(E e)
    {
        // My solution seems to work well
        // without changing insert or delete
        boolean successful = super.insert(e);
        if (!successful)
            return false;
        else
            balancePath(e);
        return true;
    }
    // updates Height
    private void updateHeight(AVLTreeNode<E> node)
    {
        if(node.left == null && node.right == null)
        {
            node.height =0;
            
        }
        else if (node.left == null)
            node.height = 1 + ((AVLTreeNode<E>)(node.right)).height;
        else if (node.right == null)
            node.height = 1 + ((AVLTreeNode<E>)(node.left)).height;
        else
            node.height = 1 + 
                    Math.max(((AVLTreeNode<E>)(node.right)).height,
                            ((AVLTreeNode<E>)(node.left)).height);
        // whenever height is updated
        // size is updated as well
        updateSize(node);
    }
    
    // This is the method that takes care
    // of my issues
    // This is similar to the update height
    // works by setting the size of a node
    // 1 + size of left + size of right
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
    private void balancePath(E e) 
    {
        ArrayList<TreeNode<E>> path = path(e);
        for (int i = path.size() - 1; i >= 0; i--)
        {
            AVLTreeNode<E> A = (AVLTreeNode<E>)(path.get(i));
            updateHeight(A);
            AVLTreeNode<E> parentOfA = (A == root) ? null :
                    (AVLTreeNode<E>)(path.get(i - 1));
            
            switch (balanceFactor(A)) 
            {
                case -2:
                    if (balanceFactor((AVLTreeNode<E>)A.left) <= 0) 
                    {
                        balanceLL(A,parentOfA);
                    }
                    else
                        balanceLR(A,parentOfA);
                    break;
                case +2:
                    if (balanceFactor((AVLTreeNode<E>)A.right) >= 0)
                        balanceRR(A,parentOfA);
                    else
                        balanceRL(A, parentOfA);
            }
        }
    }
    
    // tells balancePath is anything is uneven
    // that is -2 or +2 at any given
    // root
    public int balanceFactor(AVLTreeNode<E> node)
    {
        if (node.right == null)
            return -node.height;
        else if (node.left == null)
            return +node.height;
        else
            return
                    ((AVLTreeNode<E>)node.right).height -
                    ((AVLTreeNode<E>)node.left).height;
    }
    
    private void balanceLL(TreeNode<E> A, TreeNode<E> parentOfA) 
    {
        TreeNode<E> B = A.left; 
        if (A == root)
        {
            root = B;
        }
        else 
        {
            if (parentOfA.left == A)
            {
                parentOfA.left = B;
            }
            else
                parentOfA.right = B;
        }
        
        A.left = B.right;
        B.right = A;
        updateHeight((AVLTreeNode<E>)A);
        updateHeight((AVLTreeNode<E>)B);
        
    }
    
    private void balanceLR(TreeNode<E> A, TreeNode<E> parentOfA) 
    {
        TreeNode<E> B = A.left;
        TreeNode<E> C = B.right;
        
        if (A == root)
        {
            root = C;
        }
        else 
        {
            if (parentOfA.left == A)
            {
                parentOfA.left = C;
            }
            else
            {
                parentOfA.right = C;
            }
        }
        
        A.left = C.right;
        B.right = C.left;
        C.left = B;
        C.right = A;
        
        updateHeight((AVLTreeNode<E>)A);
        updateHeight((AVLTreeNode<E>)B);
        updateHeight((AVLTreeNode<E>)C);
    }
    
    private void balanceRR(TreeNode<E> A, TreeNode<E> parentOfA) 
    {
        TreeNode<E> B = A.right;
        
        if (A == root) 
        {
            root = B;
        }
        
        else
        {
            if (parentOfA.left == A)
            {
                parentOfA.left = B;
            }
            else
            {
                parentOfA.right = B;
                
            }
        }
        
        A.right = B.left;
        B.left = A;
        updateHeight((AVLTreeNode<E>)A);
        updateHeight((AVLTreeNode<E>)B);
        
        
    }
    
    
    private void balanceRL(TreeNode<E> A, TreeNode<E> parentOfA) 
    {
        TreeNode<E> B = A.right;
        TreeNode<E> C = B.left;
        
        if (A == root) 
        {
            root = C;
        }
        
        else
        {
            if (parentOfA.left == A)
            {
                parentOfA.left = C;
            }
            else
            {
                parentOfA.right = C;
            }
        }
        
        A.right = C.left;
        B.left = C.right;
        C.left = A;
        C.right = B;
        
        updateHeight((AVLTreeNode<E>)A);
        
        updateHeight((AVLTreeNode<E>)B);
        
        updateHeight((AVLTreeNode<E>)C);
    }
    
    public boolean delete(E element)
    {
        if (root == null)
            return false;
        
        TreeNode<E> parent = null;
        TreeNode<E> current = root;
        while(current != null)
        {
            if (element.compareTo(current.element) < 0)
            {
                parent = current;
                current = current.left;
            }
            else if (element.compareTo(current.element) > 0)
            {
                parent = current;
                current = current.right;
            }
            else break;
        }
        
        if (current == null) return false;
        
        if (current.left == null)
        {
            if(parent == null)
                root = current.right;
        
            else
            {
                if(element.compareTo(parent.element) < 0)
                    parent.left = current.right;
                else
                    parent.right = current.right;
            
            balancePath(parent.element);
            }
        }
        else
        {
            TreeNode<E> parentOfRightMost = current;
            TreeNode<E> rightMost = current.left;
            
            while (rightMost.right != null)
            {
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
        size--;
        return true;
    }
    
    public E find(int k)
    {
        // make a reference to the root
        //AVLTreeNode<E> test = (AVLTreeNode)root;
        // invoke recursive helper method
        E o = (E) find(k,(AVLTreeNode<E>)this.root);
       
        return o;
    }
    
    public E find(int k, AVLTreeNode<E> bla)
    {
        // key players
		AVLTreeNode<E> A = (AVLTreeNode<E>)(bla.left);
        AVLTreeNode<E> B = (AVLTreeNode<E>)(bla.right);
        
        
        if (A == null && k == 1)
        {
            return bla.element;
        }
        
        else if (A == null && k == 2)
        {
            return B.element;
        }
        
        else if (k <= A.size)
        {
            return find(k,A);
        }
        
        else if (k == (A.size + 1))
        {
            return bla.element;

        }
        else if (k > (A.size + 1)) 
        {
           return find(k - A.size - 1, B);
        }
        
        
        
        else
            return null;
            
            
            
    }
    
    public static class AVLTreeNode<E extends Comparable<E>> 
            extends BST.TreeNode<E> implements Serializable{
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		protected int height = 0;
        protected int size = 1;
        public AVLTreeNode(E e) 
        {
            super(e);
        }
            
    }
}

class BST<E extends Comparable<E>> implements Iterable<E>, Serializable
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	TreeNode<E> root, current, parent;
    
    int size = 0;
    
    
    public BST()
    {
    }
    
    public BST(ArrayList<E> objects)
    {
        for (E entry: objects)
            insert(entry);
    }
    
    
    public TreeNode<E> getRoot() {
		return root;
	}

	public void setRoot(TreeNode<E> root) {
		this.root = root;
	}

	public ArrayList<TreeNode<E>> path(E e)
    {
        ArrayList<TreeNode<E>> list =
                new ArrayList<>();
        TreeNode<E> current = root;
        
        while (current != null) 
        {
            list.add(current);
            if(e.compareTo(current.element) < 0)
            {
                current = current.left;
            }
            else if (e.compareTo(current.element) > 0)
            {
                current = current.right;
            }
            else
                break;
        }
        return list;
    }
    
    public java.util.Iterator<E> iterator()
    {
      return new InorderIterator();  
    }
    public TreeNode<E> createNewNode(E e)
    {
        size++;
        return new TreeNode<>(e);
        
    }
    
    public boolean search(E e) 
    {
        TreeNode<E> current = root;
        
        while(current != null)
        {
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
    public boolean delete(E e) 
    {
        TreeNode<E> parent = null;
        TreeNode<E> current = root;
        while(current != null) 
        {
            if (e.compareTo(current.element) < 0)
            {
                parent = current;
                current = current.left;
            }
            else if (e.compareTo(current.element) > 0)
            {
                parent = current;
                current = current.right;
            }
            else break;
        }
        if (current == null)
        return false;
        
        return deleteNode(e, parent, current);
    }

	private boolean deleteNode(E e, TreeNode<E> parent, TreeNode<E> current) {
		if (current.left == null)
        {
            if (parent == null)
            {
                root = current.right;
            }
            else
            {
                if (e.compareTo(parent.element) < 0)
                    parent.left = current.right;
                else
                    parent.right = current.right;
            }
        }
        else
        {
            TreeNode<E> parentOfRightMost = current;
            TreeNode<E> rightMost = current.left;
            
            while (rightMost.right != null)
            {
                parentOfRightMost = rightMost;
                rightMost = rightMost.right;
            }
            
            current.element = rightMost.element;
            
            if(parentOfRightMost.right == rightMost)
                parentOfRightMost.right = rightMost.left;
            else
                parentOfRightMost.left = rightMost.left;
        }
        size--;
        return true;
	}
    
    
    // way to make sure
    // two different search trees are the same
    // contentwise
    public void inOrder()
    {
        inOrder(root);
        System.out.println();
    }
    
    public void inOrder(TreeNode<E> root) 
    {
        if (root == null) return;
        inOrder(root.left);
        System.out.print(root.element + " ");
        inOrder(root.right);
    }
    
    
    
    public void preOrder()
    {
        preOrder(root);
    }
    public void preOrder(TreeNode<E> root) 
    {
        if (root == null) return;
            System.out.print(root.element + " ");
            preOrder(root.left);
            preOrder(root.right);  
    }
    
    
    /////////////////////////////////////////////////////////
    // use preOrder to clone the tree
    // with the objects in the exact
    // same spots
    public BST<E> clone()
    {
        // generate an arraylist
        ArrayList<E> list1 = new ArrayList<>();
        helpClone(list1, root);
        
        return new BST<E>(list1);
    }
    
    // essentially doing what preOrder was doing
    // except adding to the arraylist
    // instead of printing
    public void helpClone(ArrayList<E> list1, TreeNode<E> root)
    {
        if (root == null) return;
            list1.add(root.element);
            helpClone(list1, root.left);
            helpClone(list1, root.right);
    }
    /////////////////////////////////////////////////////////
    
    
    ////////////////////////////////////////////////////
    // use inOrdert to test
    // if the trees have the same
    // elements
    public boolean equals(BST<E> s)
    {
        // quick check
        if(s.size != size) return false;
        
        // helperEquals1 returns an arraylist
        // of the BST in order
        ArrayList<E> self = helperEquals1();
        ArrayList<E> other = s.helperEquals1();
        
        // compare as Objects
        Object[] me = self.toArray();
        Object[] you = other.toArray();
        for (int i = 0; i < me.length; i++)
        {
            // Stop once there is
            // a discrepancy
            if(me[i] != you[i])
                return false;
        }
        
        return true;
    }
    // helper 1 and helper 2
    // are essentially doing
    // what inOrder was doing
    // except adding to an arraylist instead of
    // printing
    public ArrayList<E> helperEquals1()
    {
        ArrayList<E> list2 = new ArrayList<>();
        helperEquals2(root, list2);
        return list2;
    }
    
    public void helperEquals2(TreeNode<E> root, ArrayList<E> list2)
    {
        if(root == null) return;
        helperEquals2(root.left, list2);
        list2.add(root.element);
        helperEquals2(root.right, list2);
    }
    
    ///////////////////////////////////////////////////////
    
    public int getSize() 
    {
        return size;
    }
    
    public boolean isEmpty()
    {
        return size == 0;
    }
    
    public boolean insert(E o)
    {
        if (root == null)
        {
            root = createNewNode(o);
        }
        else
        {
            parent = null;
            current = root;
            while (current != null)
            if (o.compareTo(current.element) < 0) 
            {
                parent = current;
                current = current.left;
                
            }
            else if (o.compareTo(current.element) > 0)
            {
                parent = current;
                current = current.right;
            }
            
            else
                return false;
            
            if (o.compareTo(parent.element) < 0)
                parent.left = createNewNode(o);
            else
                parent.right = createNewNode(o);
        }
        return true;
    }
    
    public void clear() 
    {
        root = null;
        size = 0;
    }

    public static class TreeNode<E extends Comparable<E>> implements Serializable
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		E element;
        TreeNode<E> left;
        TreeNode<E> right;
    
        public TreeNode(E e)
        {
            element = e;
        }
        
        // Way to compare
        public int compareTo(TreeNode<E> o)
        {
            return 1;
        }
    }
    
    private class InorderIterator implements java.util.Iterator<E> 
    {
        private ArrayList<E> list = new ArrayList<>();
        private int current = 0;
        public InorderIterator()
        {
            inOrder();
        }
        // easy reference to the other
        // overloading
        private void inOrder()
        {
            inOrder(root);
        }
        
        // traverses in order (for Strings, alphabetically
        private void inOrder(TreeNode<E> root)
        {
            if (root == null) return;
            inOrder(root.left);
            list.add(root.element);
            inOrder(root.right);
        }
        
        // returns true or false
        // depending on whether or not
        // there is another node further down.
        public boolean hasNext()
        {
            if (current < list.size()) return true;
            return false;
        }
        
        public E next()
        {
            return list.get(current++);
        }
        
        public void remove()
        {
            delete(list.get(current));
            list.clear();
        }
    }
}

