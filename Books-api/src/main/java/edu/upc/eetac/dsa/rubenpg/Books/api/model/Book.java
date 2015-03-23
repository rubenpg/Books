package edu.upc.eetac.dsa.rubenpg.Books.api.model;

import java.util.List;

import javax.ws.rs.core.Link;
 


import org.glassfish.jersey.linking.Binding;
import org.glassfish.jersey.linking.InjectLink;
import org.glassfish.jersey.linking.InjectLink.Style;
import org.glassfish.jersey.linking.InjectLinks;
 


import edu.upc.eetac.dsa.rubenpg.Books.api.MediaType;
import edu.upc.eetac.dsa.rubenpg.Books.api.BookResource;
 
public class Book {
	@InjectLinks({
			@InjectLink(resource = BookResource.class, style = Style.ABSOLUTE, rel = "books", title = "Books List", type = MediaType.BOOKS_API_BOOK_COLLECTION),
			@InjectLink(value = "/reviews?title={title}", style = Style.ABSOLUTE, rel = "reviews", title = "Reviews List", type = MediaType.BOOKS_API_REVIEW_COLLECTION, bindings = { @Binding(name = "title", value = "${instance.title}") }),
			@InjectLink(resource = BookResource.class, style = Style.ABSOLUTE, rel = "self edit", title = "Book", type = MediaType.BOOKS_API_BOOK, method = "getbook", bindings = @Binding(name = "bookid", value = "${instance.bookid}")) })
	private List<Link> links;
	private int bookid;
	private String title;
	private String author;
	private String language;
	private String edition;
	private String editiondate;
	private String impresiondate;
	private String editorial;
	private String username;
 
	public List<Link> getLinks() {
		return links;
	}
 
	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public int getBookid() {
		return bookid;
	}

	public void setBookid(int bookid) {
		this.bookid = bookid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getEdition() {
		return edition;
	}

	public void setEdition(String edition) {
		this.edition = edition;
	}

	public String getImpresiondate() {
		return impresiondate;
	}

	public void setImpresiondate(String impresiondate) {
		this.impresiondate = impresiondate;
	}

	public String getEditiondate() {
		return editiondate;
	}

	public void setEditiondate(String editondate) {
		this.editiondate = editondate;
	}

	public String getEditorial() {
		return editorial;
	}

	public void setEditorial(String editorial) {
		this.editorial = editorial;
	}
	
	public String getUsername() {
		return username;
	}
 
	public void setUsername(String username) {
		this.username = username;
	}
}