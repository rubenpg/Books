package edu.upc.eetac.dsa.vargaft.books;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import edu.upc.eetac.dsa.vargaft.books.api.Book;

public class BookAdapter extends BaseAdapter {
    ArrayList<Book> data;
    LayoutInflater inflater;


    public BookAdapter(Context context, ArrayList<Book> data) {
        super();
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvUsername;
        TextView tvDate;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Book) getItem(position)).getBookid();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_book, null);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = (TextView) convertView
                    .findViewById(R.id.tvTitle);
            viewHolder.tvUsername = (TextView) convertView
                    .findViewById(R.id.tvUsername);
            viewHolder.tvDate = (TextView) convertView
                    .findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String subject = data.get(position).getTitle();
        String username = data.get(position).getUsername();
        String date = SimpleDateFormat.getInstance().format(
                data.get(position).getLastModified());
        viewHolder.tvTitle.setText(subject);
        viewHolder.tvUsername.setText(username);
        viewHolder.tvDate.setText(date);
        return convertView;
    }
}
