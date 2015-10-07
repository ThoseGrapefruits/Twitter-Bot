package me.loganmoore.twitter;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

import me.loganmoore.poems.PoemGenerator;

public class TwitterBot {

  private static final String
      PROTECTED_RESOURCE_URL =
      "https://api.twitter.com/1.1/account/verify_credentials.json";
  Token accessToken;
  String tokenPath = "accesstoken";
  private OAuthService service = new ServiceBuilder()
      .provider(TwitterApi.class)
      .apiKey("UCm05JKfpI4fH1PpFQSP58ONn")
      .apiSecret("6nnQp6pGrbWNXdka86BiMW4txWhhVcE8awKxv1mpVeoctecjwp")
      .build();

  public TwitterBot() throws IOException, ClassNotFoundException {
    try {
      accessToken = deserializeToken();
      if (!testAuth()) {
        auth();
      }
    } catch (IOException e) {
      auth();
    }
  }

  public static void main(String[] args) throws IOException, ClassNotFoundException {
    TwitterBot bot = new TwitterBot();
    PoemGenerator pg = new PoemGenerator("data/2147-0-poe-1.txt", "test");
  }

  public Response tweet(String status) throws UnsupportedEncodingException {
    return new Tweet(this, status).post();
  }

  private void auth() throws IOException {
    // If you choose to use a callback, "oauth_verifier" will be the return value by Twitter (request param)
    Scanner in = new Scanner(System.in);

    System.out.println("=== Twitter's OAuth Workflow ===");
    System.out.println();
    // Obtain the Request Token
    System.out.println("Fetching the Request Token...");
    Token requestToken = service.getRequestToken();
    System.out.println("Got the Request Token!");
    System.out.println();

    System.out.println("Go authorize here, please:");
    System.out.println(service.getAuthorizationUrl(requestToken));
    System.out.println("And paste the verifier here");
    System.out.print(">>");
    Verifier verifier = new Verifier(in.nextLine());
    System.out.println();

    // Trade the Request Token and Verfier for the Access Token
    System.out.println("Trading the Request Token for an Access Token...");
    accessToken = service.getAccessToken(requestToken, verifier);
    System.out.println("Got the Access Token!");
    serializeToken();
    System.out.println("Cached token.");
  }

  private boolean testAuth() {
    // Now let's go and ask for a protected resource!
    System.out.println("Testing authentication...");

    Response response = sendRequest(new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL));
    System.out.println(response.isSuccessful() ? "Success" : "Failure");
    return response.isSuccessful();
  }

  private void serializeToken() throws IOException {
    new Serializer(tokenPath, accessToken).serialize();
  }

  private Token deserializeToken() throws IOException, ClassNotFoundException {
    return (Token) new Deserializer(tokenPath).deserialize();
  }

  public Response sendRequest(OAuthRequest r) {
    service.signRequest(accessToken, r);
    return r.send();
  }
}
