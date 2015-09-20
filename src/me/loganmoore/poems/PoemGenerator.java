package me.loganmoore.poems;

import java.io.IOException;

public class PoemGenerator {

  private static final String modelPath = "data/en-sent.bin";

  private String subject;
  private String[] data;

  public PoemGenerator(String dataSource, String subject) throws IOException {
    this.subject = subject;

    DataGatherer dg = new DataGatherer(modelPath, dataSource);
    data = dg.getSentences();
  }

  public Poem generate() {
    return new Poem(data, subject);
  }
}
