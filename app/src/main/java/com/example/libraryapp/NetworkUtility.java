package com.example.libraryapp;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtility {
    private static final String LOG_TAG = NetworkUtility.class.getSimpleName();
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY = "q";
    private static final String MAX_SEARCH_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    static String getBookInfo(String queryString){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        try {
            // Search for the book using provided ISBN
            Uri buildURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY, queryString)
                    .appendQueryParameter(MAX_SEARCH_RESULTS, "1")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL requestURL = new URL(buildURI.toString());
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read through API response to pull relevant data
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer stringBuffer = new StringBuffer();

            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null){
                stringBuffer.append(line + "\n");
            }
            if (stringBuffer.length() == 0){
                return null;
            }

            bookJSONString = stringBuffer.toString();

        } catch  (Exception e){
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    // Do NOTHING
                }
            }
            return bookJSONString;
        }
    }

}
