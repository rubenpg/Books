package edu.upc.eetac.dsa.rubenpg.Books.api;

public interface MediaType {
	public final static String BOOKS_API_USER = "application/vnd.books.api.user+json";
	public final static String BOOKS_API_USER_COLLECTION = "application/vnd.books.api.user.collection+json";
	public final static String BOOKS_API_BOOK = "application/vnd.books.api.book+json";
	public final static String BOOKS_API_BOOK_COLLECTION = "application/vnd.books.api.book.collection+json";
	public final static String BOOKS_API_REVIEW = "application/vnd.books.api.review+json";
	public final static String BOOKS_API_REVIEW_COLLECTION = "application/vnd.books.api.review.collection+json";
	public final static String BOOKS_API_AUTHOR = "application/vnd.books.api.author+json";
	public final static String BOOKS_API_AUTHOR_COLLECTION = "application/vnd.books.api.author.collection+json";		
	public final static String BOOKS_API_ERROR = "application/vnd.dsa.books.error+json";

}