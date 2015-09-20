package me.loganmoore.poems;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DataGatherer {

  private String[] sentences;

  public DataGatherer(String modelFilePath, String inputFilePath) throws IOException {
    InputStream modelIn = new FileInputStream(modelFilePath);

    SentenceModel model = null;

    try {
      model = new SentenceModel(modelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    if (model != null) {
      SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
      sentences = sentenceDetector
          .sentDetect(readFile(inputFilePath, java.nio.charset.StandardCharsets.UTF_8));
    }
  }

  public String[] getSentences() {
    return sentences.clone();
  }

  private String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }
}
