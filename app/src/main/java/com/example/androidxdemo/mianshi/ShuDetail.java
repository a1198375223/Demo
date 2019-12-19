package com.example.androidxdemo.mianshi;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * 树
 *
 * 二叉树：二叉树是指子结点数目不超过2个的树
 *
 * 二叉搜索树(BST)是有序的二叉树，BST需要满足如下条件：
 * 1. 若任意结点的左子树不空，则左子树上所有节点的值均小于它的根节点的值；
 * 2. 若任意结点的右子树 不空，则右子树上所有节点的值均大于或等于它的根节点的值；(有些书里面定义为BST不能有相同值结点，本文将相同值结点插入到右子树)
 * 3. 任意结点的左、右子树也分别为二叉查找树；
 */
public class ShuDetail {

    // 定义一颗树
    public static class TreeNode {
        int value;
        TreeNode left;
        TreeNode right;

        public TreeNode(int value) {
            this(value, null);
        }

        public TreeNode(int value, TreeNode left) {
            this(value, left, null);
        }

        public TreeNode(int value, TreeNode left, TreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }

        public boolean hasLeftNode() {
            return left != null;
        }

        public boolean hasRightNode() {
            return right != null;
        }
    }

    // 创建一个Node
    private static TreeNode createNode(int value) {
        return new TreeNode(value);
    }

    // 插入节点
    private static TreeNode insertNode(TreeNode tree, int value) {
        TreeNode insertNode = createNode(value);
        if (tree == null)
            return insertNode;

        TreeNode currentNode = tree;
        TreeNode parent = null;

        while (currentNode != null) {
            parent = currentNode;
            if (currentNode.value > value) {
                currentNode = currentNode.left;
            } else {
                currentNode = currentNode.right;
            }
        }

        if (parent.value > value) {
            parent.left = insertNode;
        } else {
            parent.right = insertNode;
        }

        return tree;
    }

    // 删除节点
    private static TreeNode deleteNode(TreeNode tree, int value) {
        TreeNode[] nodeArray = searchNode(tree, value);
        if (nodeArray == null) {
            return tree;
        }

        TreeNode deleteNode = nodeArray[0];
        TreeNode parentNode = nodeArray[1];

        boolean isLeft = false;
        if (parentNode.left.value == deleteNode.value) {
            isLeft = true;
        }

        if (deleteNode.hasLeftNode() && deleteNode.hasRightNode()) {
            TreeNode temp = findMinRightNode(deleteNode);
            int tempValue = temp.value;
            deleteNode(tree, temp.value);
            deleteNode.value = tempValue;
        } else if (!deleteNode.hasRightNode() && !deleteNode.hasLeftNode()) {
            if (isLeft) {
                parentNode.left = null;
            } else {
                parentNode.right = null;
            }
        } else {
            if (isLeft) {
                parentNode.left = deleteNode.left == null ? deleteNode.right : deleteNode.left;
            } else {
                parentNode.right = deleteNode.left == null ? deleteNode.right : deleteNode.left;
            }
        }
        return tree;
    }

    private static TreeNode[] searchNode(TreeNode tree, int value) {
        TreeNode currentNode = tree;
        TreeNode parentNode = null;
        while (currentNode != null) {
            parentNode = currentNode;
            if (currentNode.value > value) {
                currentNode = currentNode.left;
            } else if (currentNode.value < value) {
                currentNode = currentNode.right;
            } else {
                return new TreeNode[]{currentNode, parentNode};
            }
        }
        return null;
    }

    private static TreeNode findMinRightNode(TreeNode root) {
        TreeNode currentNode = root.right;
        while (currentNode.hasLeftNode()) {
            currentNode = currentNode.left;
        }
        return currentNode;
    }

    private static TreeNode findMin(TreeNode tree) {
        TreeNode currentNode = tree;
        while (currentNode.hasLeftNode()) {
            currentNode = currentNode.left;
        }
        return currentNode;
    }

    private static TreeNode findMax(TreeNode tree) {
        TreeNode currentNode = tree;
        while (currentNode.hasRightNode()) {
            currentNode = currentNode.right;
        }
        return currentNode;
    }

    private static int size(TreeNode tree) {
        if (tree == null) {
            return 0;
        }
        return size(tree.left) + size(tree.right) + 1;
    }

    private static int height(TreeNode tree) {
        if (tree == null) {
            return 0;
        }
        int leftHeight = height(tree.left);
        int rightHeight = height(tree.right);
        return leftHeight > rightHeight ? leftHeight+1 : rightHeight+1;
    }


    // 遍历
    // 先序
    private static void preOrder(TreeNode tree) {
        if (tree == null) {
            return;
        }
        System.out.println("value->" + tree.value);
        preOrder(tree.left);
        preOrder(tree.right);
    }

    private static void preOrderOther(TreeNode tree) {
        if (tree == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(tree);
        while (!stack.empty()) {
            TreeNode node = stack.pop();
            System.out.println("value->" + node.value);
            if (node.hasRightNode()) {
                stack.push(node.right);
            }

            if (node.hasLeftNode()) {
                stack.push(node.left);
            }
        }
    }

    // 中序
    private static void inOrder(TreeNode tree) {
        if (tree == null) {
            return;
        }
        inOrder(tree.left);
        System.out.println("value->" + tree.value);
        inOrder(tree.right);
    }


    private static void inOrderOther(TreeNode tree) {
        if (tree == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        TreeNode currentNode = tree;
        while (currentNode != null || !stack.empty()) {
            if (currentNode != null){
                stack.push(currentNode);
                currentNode = currentNode.left;
            } else {
                TreeNode node = stack.pop();
                System.out.println("value->" + node.value);
                currentNode = node.right;
            }
        }
    }

    // 后续
    private static void postOrder(TreeNode tree) {
        if (tree == null) {
            return;
        }
        postOrder(tree.left);
        postOrder(tree.right);
        System.out.println("value->" + tree.value);
    }


    private static void postOrderOther(TreeNode tree) {
        if (tree == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        Stack<TreeNode> outputStack = new Stack<>();
        stack.push(tree);
        TreeNode currentNode;
        while (!stack.empty()) {
            currentNode = stack.pop();
            outputStack.push(currentNode);

            if (currentNode.hasLeftNode())
                stack.push(currentNode.left);
            if (currentNode.hasRightNode())
                stack.push(currentNode.right);
        }

        while (!outputStack.empty()) {
            System.out.println("value->" + outputStack.pop().value);
        }
    }

    // 判断是否是搜索二叉树
    private static boolean isBST(TreeNode tree) {
        if (tree == null) {
            return false;
        }

        Stack<TreeNode> stack = new Stack<>();
        TreeNode currentNode = tree;
        TreeNode preNode = null;
        while (currentNode != null || !stack.empty()) {
            if (currentNode != null){
                stack.push(currentNode);
                currentNode = currentNode.left;
            } else {
                TreeNode node = stack.pop();
                if (preNode != null && preNode.value > node.value)
                    return false;
                preNode = node;
                currentNode = node.right;
            }
        }

        return true;
    }


    // 深度优先遍历
    private static void deepSort(TreeNode tree) {
        if (tree == null) {
            return;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(tree);

        while (!stack.empty()) {
            TreeNode node = stack.pop();
            System.out.println(node.value + " ");
            if (node.hasRightNode())
                stack.push(node.right);
            if (node.hasLeftNode())
                stack.push(node.left);
        }
    }

    private static void broadSorte(TreeNode tree) {
        if (tree == null) {
            return;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(tree);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            System.out.println(node.value + ", ");
            if (node.hasLeftNode())
                queue.add(node.left);
            if (node.hasRightNode())
                queue.add(node.right);
        }
    }


    public static int[] heapSort(int[] array) {
        //这里元素的索引是从0开始的,所以最后一个非叶子结点array.length/2 - 1
        for (int i = array.length / 2 - 1; i >= 0; i--) {
            adjustHeap(array, i, array.length);  //调整堆
        }

        // 上述逻辑，建堆结束
        // 下面，开始排序逻辑
        for (int j = array.length - 1; j > 0; j--) {
            // 元素交换,作用是去掉大顶堆
            // 把大顶堆的根元素，放到数组的最后；换句话说，就是每一次的堆调整之后，都会有一个元素到达自己的最终位置
            swap(array, 0, j);
            // 元素交换之后，毫无疑问，最后一个元素无需再考虑排序问题了。
            // 接下来我们需要排序的，就是已经去掉了部分元素的堆了，这也是为什么此方法放在循环里的原因
            // 而这里，实质上是自上而下，自左向右进行调整的
            adjustHeap(array, 0, j);
        }
        return array;
    }

    /**
     * 整个堆排序最关键的地方
     * @param array 待组堆
     * @param i 起始结点
     * @param length 堆的长度
     */
    public static void adjustHeap(int[] array, int i, int length) {
        // 先把当前元素取出来，因为当前元素可能要一直移动
        int temp = array[i];
        for (int k = 2 * i + 1; k < length; k = 2 * k + 1) {  //2*i+1为左子树i的左子树(因为i是从0开始的),2*k+1为k的左子树
            // 让k先指向子节点中最大的节点
            if (k + 1 < length && array[k] < array[k + 1]) {  //如果有右子树,并且右子树大于左子树
                k++;
            }
            //如果发现结点(左右子结点)大于根结点，则进行值的交换
            if (array[k] > temp) {
                swap(array, i, k);
                // 如果子节点更换了，那么，以子节点为根的子树会受到影响,所以，循环对子节点所在的树继续进行判断
                i  =  k;
            } else {  //不用交换，直接终止循环
                break;
            }
        }
    }

    /**
     * 交换元素
     * @param arr
     * @param a 元素的下标
     * @param b 元素的下标
     */
    public static void swap(int[] arr, int a, int b) {
        int temp = arr[a];
        arr[a] = arr[b];
        arr[b] = temp;
    }

    public static void quickSort(int[] arr,int low,int high){
        int i,j,temp,t;
        if(low>high){
            return;
        }
        i=low;
        j=high;
        //temp就是基准位
        temp = arr[low];

        while (i<j) {
            //先看右边，依次往左递减
            while (temp<=arr[j]&&i<j) {
                j--;
            }
            //再看左边，依次往右递增
            while (temp>=arr[i]&&i<j) {
                i++;
            }
            //如果满足条件则交换
            if (i<j) {
                t = arr[j];
                arr[j] = arr[i];
                arr[i] = t;
            }

        }
        //最后将基准为与i和j相等位置的数字交换
        arr[low] = arr[i];
        arr[i] = temp;
        //递归调用左半数组
        quickSort(arr, low, j-1);
        //递归调用右半数组
        quickSort(arr, j+1, high);
    }

}
