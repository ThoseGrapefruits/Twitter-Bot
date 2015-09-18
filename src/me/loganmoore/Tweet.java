package me.loganmoore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Tweet {

  // CONSTANTS
  public static final String URL = "https://api.twitter.com/1.1/statuses/update.json";
  public static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

  String status;
  TwitterBot bot;

  public Tweet(TwitterBot bot, String status) {
    this.bot = bot;
    this.status = status;
  }

  public Response post() throws UnsupportedEncodingException {
    System.out.println("Posting status:\n---");
    System.out.println(status);
    System.out.println("---\n");

    OAuthRequest req = new OAuthRequest(Verb.POST, URL);
    req.addBodyParameter("status", status);
    return bot.sendRequest(req);
  }

  @Deprecated
  public boolean post_old() throws IOException {
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
