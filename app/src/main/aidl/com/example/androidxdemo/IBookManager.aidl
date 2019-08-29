// IBookManager.aidl
package com.example.androidxdemo;

// Declare any non-default types here with import statements
import com.example.androidxdemo.Book;

interface IBookManager {
    List<Book> getBookList();

    void addBook(in Book book);
}
