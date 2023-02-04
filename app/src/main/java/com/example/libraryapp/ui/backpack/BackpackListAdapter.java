package com.example.libraryapp.ui.backpack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.libraryapp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BackpackListAdapter extends BaseAdapter {
    Context context;
    private final List<String> title;
    private final List<String> author;
    private final List<Date> duration;

    public BackpackListAdapter(Context context, List<String> title, List<String> author, List<Date> duration){
        //super(context, R.layout.single_list_app_item, utilsArrayList);
        this.context = context;
        this.title = title;
        this.author = author;
        this.duration = duration;
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
        BackpackListAdapter.ViewHolder viewHolder;
        final View result;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.single_loan_item,
                    parent, false);
            viewHolder.txtTitle = (TextView)
                    convertView.findViewById(R.id.aLoanTitleTxt);
            viewHolder.txtDuration = (TextView)
                    convertView.findViewById(R.id.aLoanDurationTxt);
            viewHolder.icon = (ImageView)
                    convertView.findViewById(R.id.appIconIV);
            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.txtTitle.setText(title.get(position) + " - " + author.get(position));
        viewHolder.txtDuration.setText("Due for Return: \n" + duration.get(position));
        viewHolder.icon.setImageResource(R.drawable.ic_loan_clock);

        // Display Icon based on Loan Duration Remaining
        viewHolder.icon.setImageResource(R.drawable.ic_loan_clock);
        /*if (duration.get(position) > 0){
            viewHolder.icon.setImageResource(R.drawable.ic_loan_clock);
        } else {
            viewHolder.icon.setImageResource(R.drawable.ic_loan_clock_due);
        }*/

        return convertView;
    }

    private static class ViewHolder {
        TextView txtTitle;
        TextView txtDuration;
        ImageView icon;
    }
}