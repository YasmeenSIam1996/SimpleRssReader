package com.example.yasmeensiam.simplerssreader;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SimpleRssReaderActivity extends ListActivity {
    List headlines;
    List links;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initializing instance variables
        headlines = new ArrayList();
        links = new ArrayList();
        if (!InternetConnection.isInternetAvailable()) {
            Toast.makeText(this, "not Available", Toast.LENGTH_LONG).show();
        }
        else {
            try {
                URL url = new URL("http://feeds.pcworld.com/pcworld/latestnews");
                InputStream inputStream = getInputStream(url);
                if (inputStream != null) {

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    // We will get the XML from an input stream
                    xpp.setInput(inputStream, "UTF_8");

                    boolean insideItem = false;

                    // Returns the type of current event: START_TAG, END_TAG, etc..
                    int eventType = xpp.getEventType();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {

                            if (xpp.getName().equalsIgnoreCase("item")) {
                                insideItem = true;
                            } else if (xpp.getName().equalsIgnoreCase("title")) {
                                if (insideItem)
                                    headlines.add(xpp.nextText()); //extract the headline
                            } else if (xpp.getName().equalsIgnoreCase("link")) {
                                if (insideItem)
                                    links.add(xpp.nextText()); //extract the link of article
                            }
                        } else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = false;
                        }

                        eventType = xpp.next(); //move to next element
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "hhhh", Toast.LENGTH_LONG).show();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(SimpleRssReaderActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(SimpleRssReaderActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();

            }

// Binding data
            if (headlines == null) {
                Toast.makeText(this, "Headlines = null", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("Test", headlines.size() + "Size");

                ArrayAdapter adapter = new ArrayAdapter(this,
                        android.R.layout.simple_list_item_1, headlines);

                getListView().setAdapter(adapter);
            }

        }
    }
    public InputStream getInputStream(URL url) {
        try {
            return url.openConnection().getInputStream();
        } catch (IOException e) {
            // return null;
            Toast.makeText(SimpleRssReaderActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Uri uri = Uri.parse(String.valueOf(links.get(position)));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
