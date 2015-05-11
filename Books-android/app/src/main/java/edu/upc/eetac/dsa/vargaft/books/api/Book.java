package edu.upc.eetac.dsa.vargaft.books.api;

import java.util.HashMap;
import java.util.Map;

public class Book {
    private int bookid;
    private String title;
    private String author;
    private String language;
    private String edition;
    private String editiondate;
    private String impresiondate;
    private String editorial;
    private String username;
    private long lastModified;
    private long creationTimestamp;
    private Map<String, Link> links = new HashMap<String, Link>();
    private String eTag;

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

    public String getEditiondate() {
        return editiondate;
    }

    public void setEditiondate(String editiondate) {
        this.editiondate = editiondate;
    }

    public String getImpresiondate() {
        return impresiondate;
    }

    public void setImpresiondate(String impresiondate) {
        this.impresiondate = impresiondate;
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

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(long creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Map<String, Link> getLinks() {
        return links;
    }

    public String getETag() {
        return eTag;
    }

    public void setETag(String ETag) {
        this.eTag = ETag;
    }
}