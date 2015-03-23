package edu.upc.eetac.dsa.rubenpg.Books.api.model;

public class BooksError {
	private int status;
	private String message;
 
	public BooksError() {
		super();
	}
 
	public BooksError(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}
 
	public int getStatus() {
		return status;
	}
 
	public void setStatus(int status) {
		this.status = status;
	}
 
	public String getMessage() {
		return message;
	}
 
	public void setMessage(String message) {
		this.message = message;
	}
}
