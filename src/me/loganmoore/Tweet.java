package me.loganmoore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Tweet {

  // CONSTANTS
  public static final String URL = "https://api.twitter.com/1.1/statuses/update.json";
  public static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

  String status;

  public Tweet(String status) {
    this.status = status;
  }

  public boolean post() throws IOException {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(URL);

// Request parameters and other properties.
    List<NameValuePair> params = new ArrayList<NameValuePair>(2);
    params.add(new BasicNameValuePair("status", status));
    httppost.setEntity(new UrlEncodedFormEntity(params, CHARSET));

//Execute and get the response.
    HttpResponse response = httpclient.execute(httppost);
    HttpEntity entity = response.getEntity();

    if (entity != null) {
      InputStream instream = entity.getContent();
      try {
        // Parse JSON response
        return true;
      } finally {
        instream.close();
      }
    }
    return false;
  }
}
