package edu.upc.eetac.dsa.vargaft.books;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;

import edu.upc.eetac.dsa.vargaft.books.api.AppException;
import edu.upc.eetac.dsa.vargaft.books.api.Book;
import edu.upc.eetac.dsa.vargaft.books.api.BookCollection;
import edu.upc.eetac.dsa.vargaft.books.api.BooksAPI;


public class BooksMainActivity extends ListActivity {

    private final static String TAG = BooksMainActivity.class.toString();
    private static final String[] items = { "lorem", "ipsum", "dolor", "sit",
            "amet", "consectetuer", "adipiscing", "elit", "morbi", "vel",
            "ligula", "vitae", "arcu", "aliquet", "mollis", "etiam", "vel",
            "erat", "placerat", "ante", "porttitor", "sodales", "pellentesque",
            "augue", "purus" };
    private ArrayList<Book> booksList;
    private BookAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_main);

        booksList = new ArrayList<Book>();
        adapter = new BookAdapter(this, booksList);
        setListAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("beeter-profile",
                Context.MODE_PRIVATE);
        final String username = prefs.getString("username", null);
        final String password = prefs.getString("password", null);
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password
                        .toCharArray());
            }
        });
        (new FetchStingsTask()).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_books_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miWrite:
                Intent intent = new Intent(this, WriteBookActivity.class);
                startActivityForResult(intent, WRITE_ACTIVITY);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private final static int WRITE_ACTIVITY = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case WRITE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle res = data.getExtras();
                    String jsonSting = res.getString("json-sting");
                    Book book = new Gson().fromJson(jsonSting, Book.class);
                    booksList.add(0, book);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private class FetchStingsTask extends
            AsyncTask<Void, Void, BookCollection> {
        private ProgressDialog pd;

        @Override
        protected BookCollection doInBackground(Void... params) {
            BookCollection books = null;
            try {
                books = BooksAPI.getInstance(BooksMainActivity.this)
                        .getBooks();
            } catch (AppException e) {
                e.printStackTrace();
            }
            return books;
        }

        @Override
        protected void onPostExecute(BookCollection result) {
            addBooks(result);
            if (pd != null) {
                pd.dismiss();
            }
        }

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(BooksMainActivity.this);
            pd.setTitle("Searching...");
            pd.setCancelable(false);
            pd.setIndeterminate(true);
            pd.show();
        }

    }
    private void addBooks(BookCollection books){
        booksList.addAll(books.getBooks());
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Book book = booksList.get(position);
        Log.d(TAG, book.getLinks().get("self").getTarget());

        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra("url", book.getLinks().get("self").getTarget());
        startActivity(intent);
    }

}
