package com.example.javalib;

/**
 * List:是一个接口，它继承于Collection的接口。它代表着有序的队列。
 *
 * 1. ArrayList:是一个数组队列，相当于动态数组。它由数组实现，随机访问效率高，随机插入、随机删除效率低。
 * 2. LinkList:是双向链表。它也可被当作堆栈、队列或双端队列进行操作。LinkedList随机访问效率低，但随机插入、随机删除效率低。
 * 3. Vector:是矢量队列，和ArrayList一样，它也是一个动态数组，由数组实现。但是ArrayList是非线程安全的，而Vector是线程安全的。
 * 4. Stack:是栈，它继承于Vector。它的特性是：先进后出(FILO, First In Last Out)。
 *
 * Map:
 *
 * 1. HashMap               : 允许null键
 * 2. LinkedHashMap         : 底层数据结构是链表和哈希表，存储的元素有序且唯一，线程不安全，效率高
 * 3. TreeMap               : 基于红黑树实现，可以对键进行排序且键是唯一的，不允许null键，线程不安全，效率高
 * 4. HashTable             :
 * 5. ConcurrentHashMap     : 1.7采用分段锁实现线程安全的机制  1.8采用的是CAS乐观锁
 *
 */
public class ListDetail {
}
