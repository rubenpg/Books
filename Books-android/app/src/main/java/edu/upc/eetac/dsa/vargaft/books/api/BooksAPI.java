package edu.upc.eetac.dsa.vargaft.books.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BooksAPI {
    private final static String TAG = BooksAPI.class.getName();
    private static BooksAPI instance = null;
    private URL url;

    private BooksRootAPI rootAPI = null;

    private BooksAPI(Context context) throws IOException, AppException {
        super();

        AssetManager assetManager = context.getAssets();
        Properties config = new Properties();
        config.load(assetManager.open("config.properties"));
        String urlHome = config.getProperty("Books.home");
        url = new URL(urlHome);

        Log.d("LINKS", url.toString());
        getRootAPI();
    }

    public final static BooksAPI getInstance(Context context) throws AppException {
        if (instance == null)
            try {
                instance = new BooksAPI(context);
            } catch (IOException e) {
                throw new AppException(
                        "Can't load configuration file");
            }
        return instance;
    }

    private void getRootAPI() throws AppException {
        Log.d(TAG, "getRootAPI()");
        rootAPI = new BooksRootAPI();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Books API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, rootAPI.getLinks());
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Books API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Books Root API");
        }

    }

    public BookCollection getBooks() throws AppException {
        Log.d(TAG, "getBooks()");
        BookCollection books = new BookCollection();

        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(rootAPI.getLinks().get("collection").getTarget()).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
        } catch (IOException e) {
            throw new AppException(
                    "Can't connect to Books API Web Service");
        }

        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            JSONObject jsonObject = new JSONObject(sb.toString());
            JSONArray jsonLinks = jsonObject.getJSONArray("links");
            parseLinks(jsonLinks, books.getLinks());

            books.setNewestTimestamp(jsonObject.getLong("newestTimestamp"));
            books.setOldestTimestamp(jsonObject.getLong("oldestTimestamp"));
            JSONArray jsonBooks = jsonObject.getJSONArray("books");
            for (int i = 0; i < jsonBooks.length(); i++) {
                Book book = new Book();
                JSONObject jsonBook = jsonBooks.getJSONObject(i);
                book.setBookid(jsonBook.getInt("bookid"));
                book.setTitle(jsonBook.getString("title"));
                book.setAuthor(jsonBook.getString("author"));
                book.setLanguage(jsonBook.getString("language"));
                book.setEdition(jsonBook.getString("edition"));
                book.setEditiondate(jsonBook.getString("editiondate"));
                book.setImpresiondate(jsonBook.getString("impresiondate"));
                book.setEditorial(jsonBook.getString("editorial"));
                book.setLastModified(jsonBook.getLong("lastModified"));
                book.setCreationTimestamp(jsonBook.getLong("creationTimestamp"));
                jsonLinks = jsonBook.getJSONArray("links");
                parseLinks(jsonLinks, book.getLinks());
                books.getBooks().add(book);
            }
        } catch (IOException e) {
            throw new AppException(
                    "Can't get response from Beeter API Web Service");
        } catch (JSONException e) {
            throw new AppException("Error parsing Beeter Root API");
        }

        return books;
    }

    private void parseLinks(JSONArray jsonLinks, Map<String, Link> map)
            throws AppException, JSONException {
        for (int i = 0; i < jsonLinks.length(); i++) {
            Link link = null;
            try {
                link = SimpleLinkHeaderParser
                        .parseLink(jsonLinks.getString(i));
            } catch (Exception e) {
                throw new AppException(e.getMessage());
            }
            String rel = link.getParameters().get("rel");
            String rels[] = rel.split("\\s");
            for (String s : rels)
                map.put(s, link);
        }
    }

    private Map<String, Book> booksCache = new HashMap<String, Book>();

    public Book getBook(String urlBook) throws AppException {
        Book book = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlBook);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);

            book = booksCache.get(urlBook);
            String eTag = (book == null) ? null : book.getETag();
            if (eTag != null)
                urlConnection.setRequestProperty("If-None-Match", eTag);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Log.d(TAG, "CACHE");
                return booksCache.get(urlBook);
            }
            Log.d(TAG, "NOT IN CACHE");
            book = new Book();
            eTag = urlConnection.getHeaderField("ETag");
            book.setETag(eTag);
            booksCache.put(urlBook, book);

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonBook = new JSONObject(sb.toString());
            book.setBookid(jsonBook.getInt("bookid"));
            book.setTitle(jsonBook.getString("title"));
            book.setAuthor(jsonBook.getString("author"));
            book.setLanguage(jsonBook.getString("language"));
            book.setEdition(jsonBook.getString("edition"));
            book.setEditiondate(jsonBook.getString("editiondate"));
            book.setImpresiondate(jsonBook.getString("impresiondate"));
            book.setEditorial(jsonBook.getString("editorial"));
            book.setLastModified(jsonBook.getLong("lastModified"));
            book.setCreationTimestamp(jsonBook.getLong("creationTimestamp"));
            JSONArray jsonLinks = jsonBook.getJSONArray("links");
            parseLinks(jsonLinks, book.getLinks());
        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Bad book url");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception when getting the book");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Exception parsing response");
        }

        return book;
    }

    /*public Book createBook(String title, String author, String language, String edition, String editiondate, String impresiondate, String editorial) throws AppException {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setLanguage(language);
        book.setEdition(edition);
        book.setEditiondate(editiondate);
        book.setImpresiondate(impresiondate);
        book.setEditorial(editorial);
        HttpURLConnection urlConnection = null;
        try {
            JSONObject jsonBook = createJsonBook(book);
            URL urlPostBooks = new URL(rootAPI.getLinks().get("create-books")
                    .getTarget());
            urlConnection = (HttpURLConnection) urlPostBooks.openConnection();
            urlConnection.setRequestProperty("Accept",
                    MediaType.BOOKS_API_BOOK);
            urlConnection.setRequestProperty("Content-Type",
                    MediaType.BOOKS_API_BOOK);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            PrintWriter writer = new PrintWriter(
                    urlConnection.getOutputStream());
            writer.println(jsonBook.toString());
            writer.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            jsonBook = new JSONObject(sb.toString());

            book.setBookid(jsonBook.getInt("bookid"));
            book.setTitle(jsonBook.getString("title"));
            book.setAuthor(jsonBook.getString("author"));
            book.setLanguage(jsonBook.getString("language"));
            book.setEdition(jsonBook.getString("edition"));
            book.setEditiondate(jsonBook.getString("editiondate"));
            book.setImpresiondate(jsonBook.getString("impresiondate"));
            book.setEditorial(jsonBook.getString("editorial"));
            book.setLastModified(jsonBook.getLong("lastModified"));
            JSONArray jsonLinks = jsonBook.getJSONArray("links");
            parseLinks(jsonLinks, book.getLinks());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error parsing response");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new AppException("Error getting response");
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return book;
    }

    private JSONObject createJsonBook(Book book) throws JSONException {
        JSONObject jsonBook = new JSONObject();
        jsonBook.put("title", book.getTitle());
        jsonBook.put("author", book.getAuthor());
        jsonBook.put("language", book.getLanguage());
        jsonBook.put("edition", book.getEdition());
        jsonBook.put("editiondate", book.getEditiondate());
        jsonBook.put("impresiondate", book.getImpresiondate());

        return jsonBook;
    }*/
}