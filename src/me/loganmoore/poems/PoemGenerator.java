package me.loganmoore.poems;

import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import me.loganmoore.constants.Constants;

public class PoemGenerator {

  public PoemGenerator(String dataSource) throws IOException {
    File sentenceModelFile = File.createTempFile("en-sent", ".bin");
    File tokenModelFile = File.createTempFile("en-token", ".bin");

    trainSentences(sentenceModelFile);
    trainTokens(tokenModelFile);

    DataGatherer dg = new DataGatherer(sentenceModelFile, tokenModelFile, dataSource);
  }

  private void trainSentences(File sentenceModelFile) throws IOException {
    ObjectStream<String> lineStream =
        new PlainTextByLineStream(new FileInputStream("en-sent.train"), Constants.CHARSET);
    ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

    SentenceModel model;
    SentenceDetectorFactory sdFactory = new SentenceDetectorFactory("en", true, null, null);

    try {
      model = SentenceDetectorME
          .train("en", sampleStream, sdFactory, TrainingParameters.defaultParams());
    } finally {
      sampleStream.close();
    }

    OutputStream modelOut = null;
    try {
      File tempModel = File.createTempFile("en-sent", ".bin");
      modelOut = new BufferedOutputStream(new FileOutputStream(sentenceModelFile));
      model.serialize(modelOut);
    } finally {
      if (modelOut != null) {
        modelOut.close();
      }
    }
  }

  private void trainTokens(File tokenModelFile) throws IOException {

  }

  public String generate(int poemLength, int lineLength) {
    return generate(poemLength, lineLength, null);
  }

  public String generate(int poemLength, int lineLength, String subject) {

  }
}
