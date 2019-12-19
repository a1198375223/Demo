package com.example.androidxdemo.mianshi;

import android.text.TextUtils;
import android.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 算法题总结：
 * 1. 迭代和递归的特点，并比较优缺点？
 *    递归就是通过重复调用函数自身实现循环；满足终止条件时会逐层返回来结束循环
 *    迭代通过函数内某段代码实现循环；使用计数器结束循环
 *
 *    递归的优点:代码清晰简洁, 可读性好
 *         缺点:需要调用函数, 会造成空间浪费. 如果循环太多会造成栈溢出
 *
 *    迭代的优点:效率高, 无额外开销, 节省空间
 *         缺点:代码不简洁
 *
 * 2. 快排的基本思路是什么？最差的时间复杂度是多少？如何优化？
 *    快速排序使用分治的思想，通过一趟排序将待排序列分割成两部分，其中一部分记录的关键字均比另一部分记录的关键字小；
 *    再分别对这两部分记录继续进行排序，以达到整个序列有序的目的。当待排序列有序时会出现最坏时间复杂度O(n2)。几种优化方式：
 *       * 当待排序序列的长度较小时采用直接插入排序
 *       * 优化所选取数轴的计算方法，如三数取中
 *       * 迭代取代递归，效率高
 *       * 存储数轴值，节省无必要的交换
 */
public class AlgorithmDetail {

    // query
    // 二分查找
    public static int binartSearch(int[] a, int key) {
        int low, mid, high;
        low = 0;
        high = a.length - 1;

        while (low <= high) {
            mid = (low + high) / 2;
            if (key > a[mid]) {
                low = mid + 1;
            } else if (key < a[mid]) {
                low = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }


    public static class ListNode {
        int value;
        ListNode next;


        ListNode(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "ListNode{" +
                    "value=" + value +
                    ", next=" + next +
                    '}';
        }
    }

    // 反转链表
    // 方法1 将首节点的下一个节点放在前面(循环)
    public static ListNode reverseLinkedList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode start = head;
        ListNode temp;

        while (start.next != null) {
            temp = start.next;
            start.next = temp.next;
            temp.next = head;
            head = temp;
        }
        return head;

//        if (head == null || head.next == null){
//            return head;
//        }
//        ListNode p = new ListNode(-1);//拟一个头节点
//        p.next = head;
//        ListNode nextNode = head.next;
//        while (nextNode != null){
//            //后一个节点调整到最前
//            head.next = nextNode.next;
//            nextNode.next = p.next;
//            p.next = nextNode;
//            nextNode = head.next;
//        }
//        return p.next;
    }

    // 方法2 递归 使链表从尾节点开始指向前一个节点
    public static ListNode reverseLinkedList2(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode pNode = reverseLinkedList2(head.next);
        head.next.next = head;
        head.next = null;
        return pNode;
    }



    // 用两个栈实现一个队列，完成队列的Push和Pop操作
    // 入队：将元素进栈A
    // 出队：判断栈B是否为空，如果为空，则将栈A中所有元素pop，并push进栈B，栈B出栈， 反之栈B直接出栈
    public static class StackQueue {
        public Stack<Integer> stackIn = new Stack<>();
        public Stack<Integer> stackOut = new Stack<>();

        public void push(int element) {
            stackIn.push(element);
        }

        public int pop() {
            if (!stackOut.empty()) {
                return stackOut.pop();
            }

            if (stackIn.empty()) {
                throw new RuntimeException("queue has no element.");
            }

            while (!stackIn.empty()) {
                stackOut.push(stackIn.pop());
            }

            return stackOut.pop();
        }
    }



    // 用两个队列实现一个栈，完成栈的Push和Pop操作
    public static class QueueStack {
        private Queue<Integer> in = new ArrayDeque<>();
        private Queue<Integer> out = new ArrayDeque<>();

        public void push(int element) {
            in.add(element);
        }

        public int pop() {
            if (in.isEmpty() && out.isEmpty()) {
                throw new RuntimeException("Stack has no element");
            }

            if (in.size() == 1) {
                return in.poll();
            }

            while (in.size() > 1) {
                out.add(in.poll());
            }
            int popElement = in.poll();

            while (!out.isEmpty()) {
                in.add(out.poll());
            }
            return popElement;
        }
    }


    // 用三个线程，顺序打印字母A-Z，输出结果是1A、2B、3C、1D 2E...
    public static class PrintThread {
        private Thread thread1, thread2, thread3;
        private char ch = 'A';
        private int i = 0;


        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    int threadId = Integer.parseInt(Thread.currentThread().getName());
                    while (i < 26) {
                        if (i % 3 == threadId - 1) {
                            System.out.print(threadId + "" + ch++ + "、 ");
                            i++;
                            notifyAll();
                        } else {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        };

        public PrintThread() {
            thread1 = new Thread(runnable, "1");
            thread2 = new Thread(runnable, "2");
            thread3 = new Thread(runnable, "3");
        }

        public void startPrint() {
            thread1.start();
            thread2.start();
            thread3.start();
        }
    }

    // 如何判断一个链有环，请找出该链表的环的入口结点
    public static ListNode isLinkHasCircle(ListNode head) {
        if (head == null || head.next == null || head.next.next == null) {
            return null;
        }

        int count = 0;
        ListNode p1 = head;
        ListNode p2 = head;

        while (true) {
            p1 = p1.next;
            p2 = p2.next.next;
            if (p2 == null || p2.next == null) {
                return null;
            }
            count++;
            if (p1 == p2) {
                System.out.println("环的大小: " + count);
                if (p2 == head) {
                    return p2;
                }
                p2 = head;
                break;
            }
        }

        while (true) {
            p2 = p2.next;
            p1 = p1.next;
            if (p1 == p2) {
                return p2;
            }
        }
    }

    // 快速排序 quick sort
    public static void quickSort(int[] array, int low, int high) {
        int left = low;
        int right = high;
        if (left < right) {
            int index = array[left];
            // 寻找基准数
            while (left < right) {
                while (left < right && array[right] >= index) {
                    right--;
                }

                array[left] = array[right];

                while (left < right && array[left] <= index) {
                    left++;
                }
                array[right] = array[left];
            }
            array[left] = index;
            System.out.println(Arrays.toString(array));

            quickSort(array, low, left - 1);
            quickSort(array, left + 1, high);
        }
    }

    // 快速从一组无序数中找到第k大的数（或前k个大的数）
    private static int findMaxK(int k, int[] array, int low, int high) {
        int left = low;
        int right = high;

        int index = array[low];
        while (left < right) {
            while (array[right] >= index && left < right) {
                right--;
            }

            array[left] = array[right];

            while (array[left] <= index && left < right) {
                left++;
            }

            array[right] = array[left];
        }
        array[left] = index;
        System.out.println("left=" + left);
        if (left == k - 1) {
            return array[left];
        } else if (left > (k - 1)) {
            return findMaxK(k, array, low, left - 1);
        } else {
            return findMaxK(k, array, left + 1, high);
        }
    }


    // 从字符串中找出一个最长的不包含重复数字的子字符串的长度。例如在字符串
    private static int findLongestSubstringLength(String str) {
        if (str == null || str.equals("")) {
            return 0;
        }

        int maxLength = 0;
        int[] position = new int[26];

        for (int i = 0; i < 26; i++) {
            position[i] = -1;
        }

        int[] length = new int[str.length()];
        position[str.charAt(0) - 'a'] = 0;
        length[0] = 1;
        maxLength = 1;
        for (int i = 1; i < str.length(); i++) {
            int prePosition = position[str.charAt(i) - 'a'];
            if (prePosition > 0) {
                if ((i - prePosition) > length[i - 1]) {
                    length[i] = length[i - 1] + 1;
                } else {
                    length[i] = i - prePosition;
                }
            } else {
                length[i] = length[i - 1] + 1;
            }

            position[str.charAt(i) - 'a'] = i;
            if (maxLength < length[i]) {
                maxLength = length[i];
            }
        }

        return maxLength;
    }


    // 输入一个链表，按链表值从尾到头的顺序返回一个ArrayList
    private static void getListFromNode(ListNode head, ArrayList<Integer> list) {
        if (head == null) {
            return;
        }

        if (head.next != null) {
            getListFromNode(head.next, list);
        }

        list.add(head.value);
    }

    // 在一个排好序的链表中存在重复的结点，请删除该链表中重复的结点，重复的结点不保留，并返回链表头指针
    private static ListNode removeRepeatNode(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode preNode = head;
        ListNode currentNode = head.next;
        while (currentNode != null) {
            if (currentNode.value == preNode.value) {
                preNode.next = currentNode.next;
                currentNode = preNode.next;
            } else {
                preNode = currentNode;
                currentNode = currentNode.next;
            }
        }

        return head;
    }

    // 输入一个链表，输出该链表中倒数第k个结点
    private static ListNode findKNode(ListNode head, int k) {
        if (head == null) {
            return null;
        }

        ListNode resultNode = head;
        ListNode tailNode = head;
        int i=1;

        while (i < k) {
            if (tailNode == null) {
                return null;
            }
            tailNode = tailNode.next;
            i++;
        }

        while (tailNode.next != null) {
            tailNode = tailNode.next;
            resultNode = resultNode.next;
        }

        return resultNode;
    }

    // 输入两个单调递增的链表，输出两个链表合成后的链表，并保证满足单调不减规则
    private static ListNode mergeList(ListNode head1, ListNode head2) {
        ListNode p1 = head1;
        ListNode p2 = head2;
        ListNode newList;
        if (p1.value <= p2.value) {
            newList = new ListNode(p1.value);
            p1 = p1.next;
        } else {
            newList = new ListNode(p2.value);
            p2 = p2.next;
        }

        ListNode p = newList;
        while (p1 != null && p2 != null) {
            while (p1 != null && p1.value <= p2.value) {
                p.next = p1;
                p1 = p1.next;
                p = p.next;
            }

            while (p2 != null && p1 != null && p1.value >= p2.value) {
                p.next = p2;
                p2 = p2.next;
                p = p.next;
            }
        }

        if (p1 != null) {
            p.next = p1;
        }

        if (p2 != null) {
            p.next = p2;
        }

        return newList;
    }

    // 给定一个数组和滑动窗口的大小，找出所有滑动窗口里数值的最大值
    private static ArrayList<Integer> maxInWindows(int[] num, int size) {
        ArrayList<Integer> res = new ArrayList<>();
        if(size == 0){
            return res;
        }
        int begin;
        ArrayDeque<Integer> q = new ArrayDeque<>();//双端队列
        for(int i = 0; i < num.length; i++){
            begin = i - size + 1;//表示当前窗口最左元素在原始数组中的下标
            if(q.isEmpty()){
                q.add(i);
            }
            //从队头开始把不存在当前窗口的都移除队列
            while((!q.isEmpty()) &&begin > q.peekFirst()){
                q.pollFirst();
            }
            //从队尾开始把比当前元素小的都移除队列
            while((!q.isEmpty()) && num[q.peekLast()] <= num[i]){
                q.pollLast();
            }
            q.add(i);
            //窗口有效时插入最大值
            if(begin >= 0){
                res.add(num[q.peekFirst()]);
            }
        }
        return res;
    }

    // 定义栈的数据结构，请在该类型中实现一个能够得到栈中所含最小元素的min函数
    private static class CustomStack {
        Stack<Integer> stack = new Stack<>();
        Stack<Integer> minStack = new Stack<>();

        public void push(int element) {
            stack.push(element);
            if (minStack.empty()) {
                minStack.push(element);
            } else {
                if (minStack.peek() > element) {
                    minStack.push(element);
                }
            }
        }

        public int pop() {
            int popElement = stack.pop();
            if (popElement == minStack.peek()) {
                return minStack.pop();
            }

            return popElement;
        }

        public int min() {
            if (minStack.empty()) {
                throw new RuntimeException("Stack has no elements");
            }

            return minStack.peek();
        }
    }

    // 输入两个整数序列，第一个序列表示栈的压入顺序，请判断第二个序列是否可能为该栈的弹出顺序
    private static boolean isPopList(int[] pushList, int[] popList) {
        int popIndex = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < pushList.length; i++) {
            stack.push(pushList[i]);
            while (!stack.empty()) {
                if (stack.peek() == popList[popIndex]) {
                    stack.pop();
                    popIndex++;
                } else {
                    break;
                }
            }
        }

        return stack.empty();
    }

    // 翻转单词顺序列，如输入“student. a am I”，输出“I am a student.”
    private static String revertString(String string) {
        String[] groups = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = groups.length - 1; i >= 0; i--) {
            builder.append(groups[i]);
            if (i != 0) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }


    // 请实现一个函数用来找出字符流中第一个只出现一次的字符，若没有返回#
    private static char findFirstAppearingOnceChar(String string) {
        int[] hashTable = new int[256];
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i] = 0;
        }
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            hashTable[((int) ch)]++;
        }

        for (int i = 0; i < string.length(); i++) {
            if (hashTable[((int) string.charAt(i))] == 1) {
                return string.charAt(i);
            }
        }
        return '#';
    }


    // 在一个字符串（全部由字母组成）中找到第一个只出现一次的字符，并返回它的位置，如果没有则返回 -1（需要区分大小写）
    private static int findFirstAppearingOnceCharPosition(String string) {
        int[] hashTable = new int[256];
        for (int i = 0; i < hashTable.length; i++) {
            hashTable[i] = 0;
        }
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            hashTable[((int) ch)]++;
        }

        for (int i = 0; i < string.length(); i++) {
            if (hashTable[((int) string.charAt(i))] == 1) {
                return i;
            }
        }
        return -1;
    }

    // 输入一个字符串，按字典序打印出该字符串中字符的所有排列
    public static List<String> permutation(String string) {
        List<String> result = new ArrayList<>();
        if (string.length() == 0) {
            return result;
        }
        fun(string.toCharArray(), result, 0);
        Collections.sort(result);
        return result;
    }

    private static void fun(char[] ch, List<String> list, int i) {
        if (i == ch.length - 1) {
            if (!list.contains(new String(ch))) {
                list.add(String.valueOf(ch));
            }
        } else {
            for (int j = i; j < ch.length; j++) {
                swap(ch, i, j);
                fun(ch, list, i + 1);
                swap(ch, i, j);
            }
        }
    }

    private static void swap(char[] str, int i, int j) {
        if (i != j) {
            char t = str[i];
            str[i] = str[j];
            str[j] = t;
        }
    }

    // 在一个二维数组中，每一行都按照从左到右递增的顺序排序，每一列都按照从上到下递增的顺序排序。请完成一个函数，
    // 输入这样的一个二维数组和一个整数，判断数组中是否含有该整数。
    private static boolean isContainNumber(int[][] sortArrays, int num) {
        int allHang = sortArrays.length;
        int allRaw = sortArrays[0].length;

        int currentRaw = 0;
        int currentHang = allHang - 1;
        while (currentHang >= 0 && currentRaw < allRaw) {
            int temp = sortArrays[currentHang][currentRaw];
            if (temp > num) {
                currentHang--;
            } else if (temp < num) {
                currentRaw++;
            } else {
                return true;
            }
        }
        return false;
    }

    // 找出数组中有出现的次数超过数组长度的一半的数字，如果不存在则输出0。例如输入一个长度为9的数组
    // {1,2,3,2,2,2,5,4,2}，由于数字2在数组中出现了5次，超过数组长度的一半，因此输出2。
    public static int findOverHalfNum(int[] array) {
        int half = array.length / 2;
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        int result = 0;
        int max = 0;
        for (int key : array) {
            if (hashMap.containsKey(key)) {
                int value = hashMap.get(key);
                hashMap.put(key, ++value);
                if (max < value) {
                    max = value;
                    result = key;
                }
            } else {
                hashMap.put(key, 1);
                if (max < 1) {
                    max = 1;
                    result = key;
                }
            }
        }
        if (max > half) {
            return result;
        }
        return 0;
    }


    // 输入一个正整数数组，把数组里所有数字拼接起来排成一个数，打印能拼接出的所有数字中最小的一个。例如输入数组
    // {3，32，321}，则打印出这三个数字能排成的最小数字为321323。
    // 通过一个比较器对数据进行两两比较，规则为若a＋b<b+a，则a排在前
    private static String minNum(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int item : array) {
            list.add(item);
        }

        Collections.sort(list, (integer, t1) -> {
            String item1 = integer + "" + t1;
            String item2 = t1 + "" + integer;
            return item1.compareTo(item2);
        });
        StringBuilder result = new StringBuilder();
        for (int item : list) {
            result.append(item);
        }
        return result.toString();
    }

    // 输入一个递增排序的数组和一个数字S，在数组中查找两个数，使得他们的和正好是S，如果有多对数字的和等于S，输出两个数的乘积最小的
    private static List<Integer> getSumS(int[] array, int s) {
        int start = 0;
        int end = array.length - 1;

        while (start < end) {
            if (array[start] + array[end] < s) {
                start++;
            } else if (array[start] + array[end] > s) {
                end--;
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(array[start]);
                list.add(array[end]);
                return list;
            }
        }
        return null;
    }

    public static int[][] setOfStacks(int[][] ope, int size) {
        // write code here
        int index = size - 1;
        int raw = ope.length * ope[0].length / size;
        int rawIndex = raw -1;
        int[][] stackArray = new int[raw][size];
        int[] stack = createStack(size);
        for (int i = 0; i < ope.length; i++) {
            switch (ope[i][0]) {
                case 1:
                    if (index < 0) {
                        stackArray[rawIndex--] = stack;
                        stack = createStack(size);
                        index = size - 1;
                    }
                    stack[index--] = ope[i][1];
                    break;
                case 2:
                    if (index > size - 1) {
                        stack = stackArray[rawIndex++];
                        index = size - 1;
                    }
                    stack[index++] = -1;
                    break;
                default:
            }
        }
        return stackArray;
    }

    private static int[] createStack(int size) {
        int[] stack = new int[size];
        for (int i = 0; i < stack.length; i++) {
            stack[i] = -1;
        }
        return stack;
    }


    //两路归并算法，两个排好序的子序列合并为一个子序列
    public void merge(int []a,int left,int mid,int right){
        int []tmp=new int[a.length];//辅助数组
        int p1=left,p2=mid+1,k=left;//p1、p2是检测指针，k是存放指针

        while(p1<=mid && p2<=right){
            if(a[p1]<=a[p2])
                tmp[k++]=a[p1++];
            else
                tmp[k++]=a[p2++];
        }

        while(p1<=mid) tmp[k++]=a[p1++];//如果第一个序列未检测完，直接将后面所有元素加到合并的序列中
        while(p2<=right) tmp[k++]=a[p2++];//同上

        //复制回原素组
        for (int i = left; i <=right; i++)
            a[i]=tmp[i];
    }

    public void mergeSort(int [] a,int start,int end){
        if(start<end){//当子序列中只有一个元素时结束递归
            int mid=(start+end)/2;//划分子序列
            mergeSort(a, start, mid);//对左侧子序列进行递归排序
            mergeSort(a, mid+1, end);//对右侧子序列进行递归排序
            merge(a, start, mid, end);//合并
        }
    }



    public static void main(String[] args) {
        ListNode one = new ListNode(1);
        ListNode two = new ListNode(2);
        ListNode three = new ListNode(3);
        ListNode four = new ListNode(4);
        one.next = two;
        two.next = three;
        three.next = four;
        four.next = null;

        System.out.println(one);

        ListNode reverse = reverseLinkedList2(one);
        System.out.println(one);

        System.out.println();
        System.out.println(reverse);
        System.out.println("----------------------------------");


        StackQueue queue = new StackQueue();
        queue.push(1);
        queue.push(2);
        queue.push(3);
        queue.push(4);
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
        System.out.println(queue.pop());
//        System.out.println(queue.pop());

        System.out.println("----------------------------------");
        QueueStack stack = new QueueStack();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
//        System.out.println(stack.pop());

        System.out.println("----------------------------------");
//        PrintThread thread = new PrintThread();
//        thread.startPrint();

        System.out.println("----------------------------------");

        ListNode a = new ListNode(1);
        ListNode b = new ListNode(2);
        ListNode c = new ListNode(3);
        ListNode d = new ListNode(4);
        ListNode e = new ListNode(5);
        ListNode f = new ListNode(6);
        a.next = b;
        b.next = c;
        c.next = d;
        d.next = e;
        e.next = f;
        f.next = null;
        ListNode node = isLinkHasCircle(a);
        System.out.println("is circle? " + (node != null) + ((node == null? "" : " enter=" + node.value)));
        f.next = b;
        node = isLinkHasCircle(a);
        System.out.println("is circle? " + (node != null) + ((node == null? "" : " enter=" + node.value)));

        System.out.println("----------------------------------");

        int[] array = new int[]{8, 6, 3, 2, 6, 11, 23, 19, 7, 4, 10};
        quickSort(array, 0, array.length - 1);
        System.out.println("final: " + Arrays.toString(array));

        System.out.println("----------------------------------");
        int[] array1 = new int[]{8, 6, 3, 2, 6, 11, 23, 19, 7, 4, 10};
        int k = 10;
        int kValue = findMaxK(k, array1, 0, array1.length - 1);
        System.out.println(Arrays.toString(array1));
        System.out.println("第" + k + "大的:" + kValue);
        System.out.println("----------------------------------");

        String str = "gjaiononxzojvpjpjqwer";
        System.out.println("Max length for str:" + str + " is: " + findLongestSubstringLength(str));

        System.out.println("----------------------------------");
        a.next = b;
        b.next = c;
        c.next = d;
        d.next = e;
        e.next = f;
        f.next = null;
        ArrayList<Integer> list = new ArrayList<>();
        getListFromNode(a, list);
        System.out.println(list.toString());


        System.out.println("----------------------------------");
        ListNode h = new ListNode(1);
        ListNode i = new ListNode(2);
        ListNode j = new ListNode(1);
        ListNode kk = new ListNode(1);
        ListNode l = new ListNode(6);
        ListNode m = new ListNode(8);
        ListNode n = new ListNode(7);
        h.next = i;
//        i.next = null;
        i.next = j;
//        j.next = null;
        j.next = kk;
//        kk.next = null;
        kk.next = l;
        l.next = m;
        m.next = n;
        n.next = null;
        System.out.println(removeRepeatNode(h).toString());
        System.out.println("----------------------------------");

        System.out.println(findKNode(h, 6).toString());
        System.out.println("----------------------------------");
        ListNode list1_1 = new ListNode(1);
        ListNode list1_2 = new ListNode(3);
        ListNode list1_3 = new ListNode(4);
        ListNode list1_4 = new ListNode(7);
        ListNode list1_5 = new ListNode(13);
        ListNode list1_6 = new ListNode(14);
        ListNode list1_7 = new ListNode(15);
        list1_1.next = list1_2;
        list1_2.next = list1_3;
        list1_3.next = list1_4;
        list1_4.next = list1_5;
        list1_5.next = list1_6;
        list1_6.next = list1_7;
        list1_7.next = null;

        ListNode list2_1 = new ListNode(1);
        ListNode list2_2 = new ListNode(2);
        ListNode list2_3 = new ListNode(5);
        ListNode list2_4 = new ListNode(8);
        ListNode list2_5 = new ListNode(9);
        ListNode list2_6 = new ListNode(10);
        ListNode list2_7 = new ListNode(12);
        list2_1.next = list2_2;
        list2_2.next = list2_3;
        list2_3.next = list2_4;
        list2_4.next = list2_5;
        list2_5.next = list2_6;
        list2_6.next = list2_7;
        list2_7.next = null;
        System.out.println(mergeList(list1_1, list2_1).toString());
        System.out.println("----------------------------------");
        int[] aaaa = new int[]{8, 6, 3, 2, 6, 11, 23, 19, 7, 4, 10};
        System.out.println(maxInWindows(aaaa, 3).toString());

        System.out.println("----------------------------------");

        CustomStack customStack = new CustomStack();
        customStack.push(13);
        customStack.push(5);
        customStack.push(6);
        customStack.push(3);
        customStack.push(5);
        System.out.println("min=" + customStack.min());
        System.out.println("pop:" + customStack.pop());
        System.out.println("min=" + customStack.min());
        System.out.println("pop:" + customStack.pop());
        System.out.println("min=" + customStack.min());
        System.out.println("pop:" + customStack.pop());
        System.out.println("min=" + customStack.min());
        System.out.println("----------------------------------");

        int[] pushList = new int[]{1, 2, 3, 4, 5};
        int[] popList = new int[]{3, 4, 2, 1, 5};
        System.out.println("isPopList?" + isPopList(pushList, popList));
        System.out.println("----------------------------------");
        String origin = "student. a am I";
        System.out.println("origin str:" + origin + " --------revert str:" + revertString(origin));
        System.out.println("----------------------------------");
        String text = "this is my home. yes man. t h";
        System.out.println("find only appearing once char:(" + findFirstAppearingOnceChar(text) + ") from text: " + text);
        System.out.println("----------------------------------");
        System.out.println("find only appearing once char position:(" + findFirstAppearingOnceCharPosition(text) + ") from text: " + text);
        System.out.println("----------------------------------");
        System.out.println(permutation("abcd"));
        System.out.println("----------------------------------");
        int[][] arrays = new int[][] {
                {1, 2, 6, 8, 10, 12, 14},
                {3, 5, 7, 10, 12, 18, 21},
                {7, 8, 10, 12, 15, 19, 25},
                {9, 10, 13, 15, 17, 25, 27},
                {10, 14, 18, 21, 24, 26, 30},
                {12, 17, 19, 22, 25, 27, 33}
        };
        int num = 31;
        System.out.println(num + " is container in arrays? " + isContainNumber(arrays, num));
        System.out.println("----------------------------------");
        int[] allArrays = new int[]{1, 2, 3, 2, 2, 2, 5, 4, 2};
        System.out.println("over half in array=" + findOverHalfNum(allArrays));
        System.out.println("----------------------------------");
        int[] arrayNum = new int[]{4, 512, 17, 24, 46};
        System.out.println("min num in merge array:" + minNum(arrayNum));
        System.out.println("----------------------------------");

        int[] arrayS = new int[]{4, 12, 17, 24, 46};
        int s = 21;
        System.out.println("in array find " + s + " result=" + getSumS(arrayS, s));
        System.out.println("----------------------------------");
        int x = 0;
        int y = 0;
        Scanner in = new Scanner(System.in);
        if (in.hasNextLine()) {
            String ch = in.nextLine();
            String[] group = ch.split(";");
            for (String item : group) {
                if (item.matches("[WSAD]([1-9]*)")){
                    char head = item.charAt(0);
                    int number = Integer.valueOf(item.substring(1));
                    switch (head) {
                        case 'W':
                            y += number;
                            break;
                        case 'S':
                            y -= number;
                            break;
                        case 'A':
                            x -= number;
                            break;
                        case 'D':
                            x += number;
                            break;
                        default:
                    }
                }
            }
        }
        System.out.println(x + " , " + y);
    }
}
