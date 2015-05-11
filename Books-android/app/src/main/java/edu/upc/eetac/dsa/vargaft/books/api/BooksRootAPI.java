package edu.upc.eetac.dsa.vargaft.books.api;

import java.util.HashMap;
import java.util.Map;

public class BooksRootAPI {

    private Map<String, Link> links;

    public BooksRootAPI() {
        links = new HashMap<String, Link>();
    }

    public Map<String, Link> getLinks() {
        return links;
    }

}