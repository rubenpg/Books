package edu.upc.eetac.dsa.rubenpg.Books.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import edu.upc.eetac.dsa.rubenpg.Books.api.DataSourceSPA;
import edu.upc.eetac.dsa.rubenpg.Books.api.model.Book;
import edu.upc.eetac.dsa.rubenpg.Books.api.model.BookCollection;
import edu.upc.eetac.dsa.rubenpg.Books.api.MediaType;
 
@Path("/books")
public class BookResource {
	private DataSource ds = DataSourceSPA.getInstance().getDataSource();
	private String GET_BOOKS_QUERY = "select b.* from books b where b.creation_timestamp < ifnull(?, now())  order by creation_timestamp desc limit ?";
	private String GET_BOOKS_QUERY_FROM_LAST = "select b.*, u.name from books b, users u where u.username=b.username and b.creation_timestamp > ? order by creation_timestamp desc";
	
	@GET
	@Produces(MediaType.BOOKS_API_BOOK_COLLECTION)
	public BookCollection getBooks(@QueryParam("length") int length,
			@QueryParam("before") long before, @QueryParam("after") long after) {
		BookCollection books = new BookCollection();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {	
			boolean updateFromLast = after > 0;
			stmt = updateFromLast ? conn
					.prepareStatement(GET_BOOKS_QUERY_FROM_LAST) : conn
					.prepareStatement(GET_BOOKS_QUERY);
			if (updateFromLast) {
				stmt.setTimestamp(1, new Timestamp(after));
			} else {
				if (before > 0)
					stmt.setTimestamp(1, new Timestamp(before));
				else
					stmt.setTimestamp(1, null);
				length = (length <= 0) ? 5 : length;
				stmt.setInt(2, length);
			}
			ResultSet rs = stmt.executeQuery();
			boolean first = true;
			long oldestTimestamp = 0;
			
			while (rs.next()) {
				Book book = new Book();
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getString("edition"));
				book.setEditiondate(rs.getString("editiondate"));
				book.setImpresiondate(rs.getString("impresiondate"));
				book.setEditorial(rs.getString("editorial"));
				book.setLastModified(rs.getTimestamp("last_modified").getTime());
				book.setCreationTimestamp(rs.getTimestamp("creation_timestamp").getTime());
				oldestTimestamp = rs.getTimestamp("creation_timestamp").getTime();
				book.setLastModified(oldestTimestamp);
				if (first) {
					first = false;
					books.setNewestTimestamp(book.getCreationTimestamp());
				}
				books.addBook(book);
			}
			books.setOldestTimestamp(oldestTimestamp);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return books;
	}
	
	
	private String GET_BOOK_BY_ID_QUERY = "select * from books where bookid = ?";
	 
	@GET
	@Path("/{bookid}")
	@Produces(MediaType.BOOKS_API_BOOK)
	public Response getBook(@PathParam("bookid") String bookid,
			@Context Request request) {
		// Create CacheControl
		CacheControl cc = new CacheControl();
		Book book = getBookFromDatabase(bookid);
		 
		// Calculate the ETag on last modified date of user resource
		EntityTag eTag = new EntityTag(Long.toString(book.getLastModified()));
	 
		// Verify if it matched with etag available in http request
		Response.ResponseBuilder rb = request.evaluatePreconditions(eTag);
	 
		// If ETag matches the rb will be non-null;
		// Use the rb to return the response without any further processing
		if (rb != null) {
			return rb.cacheControl(cc).tag(eTag).build();
		}
	 
		// If rb is null then either it is first time request; or resource is
		// modified
		// Get the updated representation and return with Etag attached to it
		rb = Response.ok(book).cacheControl(cc).tag(eTag);
	 
		return rb.build();
	}
	
	
	private String INSERT_BOOK_QUERY = "insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values (?, ?, ?, ?, ?, ?, ?)";
	 
	@POST
	@Consumes(MediaType.BOOKS_API_BOOK)
	@Produces(MediaType.BOOKS_API_BOOK)
	public Book createBook(Book book) {
		validateBook(book);
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_BOOK_QUERY,
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, security.getUserPrincipal().getName());	 
			stmt.setString(2, book.getTitle());
			stmt.setString(3, book.getAuthor());
			stmt.setString(4, book.getLanguage());
			stmt.setString(5, book.getEdition());
			stmt.setString(6, book.getEditiondate());
			stmt.setString(7, book.getImpresiondate());
			stmt.setString(8, book.getEditorial());
			stmt.executeUpdate();
			
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next()) {
				int bookid = rs.getInt(1);
	 
				book = getBookFromDatabase(Integer.toString(bookid));
			} else {
				// Something has failed...
			}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
	}
	
	private String UPDATE_BOOK_QUERY = "update books set titlet=ifnull(?, title), author=ifnull(?, author), language=ifnull(?, language), edition=ifnull(?, edition), editiondate=ifnull(?, editiondate), impresiondate=ifnull(?, impresiondate), editorial=ifnull(?, editorial) where bookid=?";
	 
	@PUT
	@Path("/{bookid}")
	@Consumes(MediaType.BOOKS_API_BOOK)
	@Produces(MediaType.BOOKS_API_BOOK)
	public Book updateBook(@PathParam("bookid") String bookid, Book book) {
		validateUser(bookid);
		validateUpdateBook(book);
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(UPDATE_BOOK_QUERY);
			stmt.setString(1, book.getTitle());
			stmt.setString(2, book.getAuthor());
			stmt.setString(3, book.getLanguage());
			stmt.setString(4, book.getEdition());
			stmt.setString(5, book.getEditiondate());
			stmt.setString(6, book.getImpresiondate());
			stmt.setString(7, book.getEditorial());
			stmt.executeUpdate();
	 
			int rows = stmt.executeUpdate();
			if (rows == 1)
				book = getBookFromDatabase(bookid);
			else {
				throw new NotFoundException("There's no sting with bookid="
						+ bookid);
			}
	 
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
		}
	
	
	private String DELETE_BOOK_QUERY = "delete from books where bookid=?";
	 
	@DELETE
	@Path("/{bookid}")
	public void deleteBook(@PathParam("bookid") String bookid) {
		
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_BOOK_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
	 
			int rows = stmt.executeUpdate();
			if (rows == 0)
				throw new NotFoundException("There's no sting with stingid="
						+ bookid);
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
		
	}
	private void validateBook(Book book) {
		if (book.getTitle() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getTitle().length() > 100)
			throw new BadRequestException("Subject can't be greater than 100 characters.");
		if (book.getAuthor() == null)
			throw new BadRequestException("Content can't be null.");
		if (book.getAuthor().length() > 60)
			throw new BadRequestException("Subject can't be greater than 60 characters.");
		if (book.getLanguage() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getLanguage().length() > 20)
			throw new BadRequestException("Subject can't be greater than 20 characters.");
		if (book.getEdition() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getEdition().length() > 70)
			throw new BadRequestException("Subject can't be greater than 70 characters.");
		if (book.getEditiondate() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getEditiondate().length() > 30)
			throw new BadRequestException("Subject can't be greater than 30 characters.");
		if (book.getImpresiondate() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getImpresiondate().length() > 30)
			throw new BadRequestException("Subject can't be greater than 30 characters.");
		if (book.getEditorial() == null)
			throw new BadRequestException("Subject can't be null.");
		if (book.getEditorial().length() > 30)
			throw new BadRequestException("Subject can't be greater than 30 characters.");
	}
	
	private void validateUpdateBook(Book book) {
		if (book.getTitle() != null && book.getTitle().length() > 100)
			throw new BadRequestException(
					"Subject can't be greater than 100 characters.");
		if (book.getAuthor() != null && book.getAuthor().length() > 60)
			throw new BadRequestException(
					"Subject can't be greater than 60 characters.");
		if (book.getLanguage() != null && book.getLanguage().length() > 20)
			throw new BadRequestException(
					"Subject can't be greater than 70 characters.");
		if (book.getEdition() != null && book.getEdition().length() > 70)
			throw new BadRequestException(
					"Subject can't be greater than 30 characters.");
		if (book.getEditiondate() != null && book.getEditiondate().length() > 30)
			throw new BadRequestException(
					"Subject can't be greater than 30 characters.");
		if (book.getImpresiondate() != null && book.getImpresiondate().length() > 30)
			throw new BadRequestException(
					"Subject can't be greater than 30 characters.");
		if (book.getEditorial() != null && book.getEditorial().length() > 30)
			throw new BadRequestException(
					"Subject can't be greater than 30 characters.");
	}
	
	private Book getBookFromDatabase(String bookid) {
		Book book = new Book();
	 
		Connection conn = null;
		try {
			conn = ds.getConnection();
		} catch (SQLException e) {
			throw new ServerErrorException("Could not connect to the database",
					Response.Status.SERVICE_UNAVAILABLE);
		}
	 
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(GET_BOOK_BY_ID_QUERY);
			stmt.setInt(1, Integer.valueOf(bookid));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				book.setBookid(rs.getInt("bookid"));
				book.setTitle(rs.getString("title"));
				book.setAuthor(rs.getString("author"));
				book.setLanguage(rs.getString("language"));
				book.setEdition(rs.getString("edition"));
				book.setEditiondate(rs.getString("editiondate"));
				book.setImpresiondate(rs.getString("impresiondate"));
				book.setEditorial(rs.getString("editorial"));
				book.setLastModified(rs.getTimestamp("last_modified").getTime());
				book.setCreationTimestamp(rs.getTimestamp("creation_timestamp").getTime());
			} else {
				throw new NotFoundException("There's no book with bookid="+ bookid);
				}
		} catch (SQLException e) {
			throw new ServerErrorException(e.getMessage(),
					Response.Status.INTERNAL_SERVER_ERROR);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				conn.close();
			} catch (SQLException e) {
			}
		}
	 
		return book;
	}

	private void validateUser(String bookid) {
	    Book book = getBookFromDatabase(bookid);
	    String username = book.getUsername();
		if (!security.getUserPrincipal().getName().equals(username))
			throw new ForbiddenException(
					"You are not allowed to modify this book.");
	}
	
	@Context
	private SecurityContext security;
	
}