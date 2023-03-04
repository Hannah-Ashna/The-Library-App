package com.example.libraryapp;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class FetchBookData extends AsyncTask<String, Void, String> {

    private TextView bookISBN;
    private TextView bookAuthor;
    private TextView bookTitle;
    private TextView bookSummary;

    private String queryString;

    public FetchBookData(TextView bookISBN, TextView bookAuthor, TextView bookTitle, TextView bookSummary){
        this.bookISBN       = bookISBN;
        this.bookAuthor     = bookAuthor;
        this.bookTitle      = bookTitle;
        this.bookSummary    = bookSummary;

    }

    @Override
    protected String doInBackground(String... strings){
        queryString = strings[0];
        return NetworkUtility.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObj = new JSONObject(s);
            JSONArray jsonArray = jsonObj.getJSONArray("items");

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject book = jsonArray.getJSONObject(i);
                String title = null;
                String authors = null;
                String summary = null;
                JSONObject volumeData = book.getJSONObject("volumeInfo");

                try {
                    title = volumeData.getString("title");
                    authors = volumeData.getString("authors");

                    // Only get the first bit of the description.
                    String description = volumeData.getString("description");
                    try {
                        String[] descriptionParts = description.split("\\.", 2);
                        summary = descriptionParts[0] + ".";
                    }  catch (Exception e) {
                        summary = description;
                    }

                } catch (Exception e){

                }

                if (title != null && authors != null && summary != null){
                    bookTitle.setText(title);
                    bookAuthor.setText(authors);
                    bookSummary.setText(summary);
                    bookISBN.setText(queryString);
                    return;
                }
            }

            bookTitle.setText("");
            bookAuthor.setText("");
            bookSummary.setText("");

        } catch (Exception e){
            bookTitle.setText("");
            bookAuthor.setText("");
            bookSummary.setText("");
        }
    }
}
