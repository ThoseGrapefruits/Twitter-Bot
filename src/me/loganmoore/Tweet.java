package me.loganmoore;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.UnsupportedEncodingException;

public class Tweet {

  // CONSTANTS
  public static final String URL = "https://api.twitter.com/1.1/statuses/update.json";
  public static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

  String status;
  TwitterBot bot;
  Response response;

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
    return response = bot.sendRequest(req);
  }

  Response getResponse() {
    return response;
  }
}
