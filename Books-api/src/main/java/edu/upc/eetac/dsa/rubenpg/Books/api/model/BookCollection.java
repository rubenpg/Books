package edu.upc.eetac.dsa.rubenpg.Books.api.model;

import java.util.ArrayList;
import java.util.List;

public class BookCollection {
	private List<Book> books;
	 
	public BookCollection() {
		super();
		books = new ArrayList<>();
	}
 
	public List<Book> getBooks() {
		return books;
	}
 
	public void setBooks(List<Book> books) {
		this.books = books;
	}
 
	public void addBook(Book book) {
		books.add(book);
	}
}
