package com.example.libraryapp.ui.library;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.libraryapp.R;
import java.util.List;

public class LibraryListAdapter extends BaseAdapter {
    Context context;
    private final List<String> title;
    private final List<String> author;
    private final List<String> summary;
    private final List<Boolean> available;

    public LibraryListAdapter(Context context, List<String> title, List<String> author, List<String> summary, List<Boolean> available){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.title = title;
        this.author = author;
        this.summary = summary;
        this.available = available;
    }

    @Override
    public int getCount() {
        return title.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_list_item,
                    parent, false);
            viewHolder.txtName = (TextView)
                    convertView.findViewById(R.id.aTitleTxt);
            viewHolder.txtVersion = (TextView)
                    convertView.findViewById(R.id.aSummaryTxt);
            viewHolder.icon = (ImageView)
                    convertView.findViewById(R.id.appIconIV);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtName.setText(title.get(position) + " - " + author.get(position));
        viewHolder.txtVersion.setText(summary.get(position));

        // Display Icon based on Availability
        if (available.get(position)){
            viewHolder.icon.setImageResource(R.drawable.ic_list_book_available);
        } else {
            viewHolder.icon.setImageResource(R.drawable.ic_list_book_taken);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView txtName;
        TextView txtVersion;
        ImageView icon;
    }
}
