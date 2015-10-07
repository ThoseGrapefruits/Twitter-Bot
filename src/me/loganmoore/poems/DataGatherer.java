package me.loganmoore.poems;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.loganmoore.constants.Constants;

public class DataGatherer {

  private Map<String, Map<String, TokenData>> frequencies = new HashMap<>();

  /**
   * Construct a new DataGatherer.
   *
   * @param sentenceModelFile the file for the OpenNPL Sentence Model File
   * @param tokenModelFile    the file for the OpenNPL Token Model File
   * @param inputFilePath     the path to the input file
   */
  public DataGatherer(File sentenceModelFile, File tokenModelFile, String inputFilePath)
      throws IOException {
    String[] sentences = new String[]{};
    String[][] tokens;

    InputStream sentenceModelIn = new FileInputStream(sentenceModelFile);
    InputStream tokenModelIn = new FileInputStream(tokenModelFile);

    SentenceModel sentenceModel = null;
    TokenizerModel tokenModel = null;

    try {
      sentenceModel = new SentenceModel(sentenceModelIn);
      tokenModel = new TokenizerModel(tokenModelIn);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (sentenceModelIn != null) {
        try {
          sentenceModelIn.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (tokenModelIn != null) {
        try {
          tokenModelIn.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    if (sentenceModel != null) {
      SentenceDetectorME sentenceDetector = new SentenceDetectorME(sentenceModel);
      sentences = sentenceDetector
          .sentDetect(readFile(inputFilePath, Constants.CHARSET));
    }

    tokens = new String[sentences.length][];

    if (tokenModel != null && sentences.length != 0) {
      TokenizerME tokenizer = new TokenizerME(tokenModel);
      for (int sentence = 0; sentence < sentences.length; sentence++) {
        tokens[sentence] = tokenizer.tokenize(sentences[sentence]);
      }
    }

    for (String[] token : tokens) {
      ArrayUtils.reverse(token);
    }

    buildFrequencyMap(tokens);
  }

  private void buildFrequencyMap(String[][] sentences) {
    for (String[] sentence : sentences) {
      for (int i = -1; i < sentence.length - 1; i++) {
        String token = (i == -1) ? null : sentence[i].trim().toLowerCase();
        String nextToken =
            (i + 2 == sentence.length) ? null : sentence[i + 1].trim().toLowerCase();
        Map<String, TokenData> occurrences = frequencies.get(token);
        if (occurrences == null) {
          Map<String, TokenData> newMap = new HashMap<>(1);
          newMap.put(nextToken, new TokenData(1));
          frequencies.put(token, newMap);
        } else if (occurrences.get(token) == null) {
          occurrences.put(nextToken, new TokenData(1));
        } else {
          occurrences.get(nextToken).addOccurence();
        }
      }
    }

    updateLikelihoods();
  }

  private void updateLikelihoods() {
    for (String startToken : frequencies.keySet()) {
      Map<String, TokenData> endTokens = frequencies.get(startToken);
      int totalOccurrences = getTotalOccurrences(endTokens);
      for (TokenData endToken : endTokens.values()) {
        endToken.setLikelihood(totalOccurrences);
      }
    }
  }

  private int getTotalOccurrences(Map<String, TokenData> endTokens) {
    int result = 0;
    for (TokenData tokenData : endTokens.values()) {
      result += tokenData.getOccurrences();
    }
    return result;
  }

  public String getNextToken(String token) {
    Map<String, TokenData> possibilities = frequencies.get(token);
    Set<String> keyset = possibilities.keySet();

    // Sort the possible words based on their number of occurrences
    List<String> possibilitiesList = Arrays.asList(keyset.toArray(
        new String[keyset.size()]));
    possibilitiesList.sort(new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return Integer.compare(possibilities.get(o1).getOccurrences(),
                               possibilities.get(o2).getOccurrences());
      }
    });

    // Get a randomly selected token from the top 5 tokens
    return possibilitiesList.get((int) (Math.random() * Math.min((keyset.size() - 1), 5)));
  }

  public String[] getSentences(int count, int maxLength, String subject) {
    // TODO add subject functionality
    // TODO add word pattern functionality
    String[] result = new String[count];
    result[0] = getSentence(maxLength, subject);
    for (int i = 1; i < result.length; i++) {
      result[i] = getSentence(maxLength);
    }
    result[result.length - 1] = getSentence(maxLength, subject);
    return result;
  }

  public String[] getSentences(int count, int maxLength) {
    return getSentences(count, maxLength, null);
  }

  public String getSentence(int maxLength, String subject) {
    StringBuilder sb = new StringBuilder();

    String token = subject;
    for (int i = 0; i < maxLength; i++) {
      token = getNextToken(token);
      if (token == null) {
        break;
      }
      sb.append(token);
    }

    return sb.toString();
  }

  public String getSentence(int maxLength) {
    return getSentence(maxLength, null);
  }

  private String readFile(String path, Charset encoding) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(path));
    return new String(encoded, encoding);
  }

  private class TokenData {

    private Double likelihood;
    private Integer occurrences;

    public void setLikelihood(int totalOccurrences) {
      likelihood = (double) occurrences / (double) totalOccurrences;
    }

    public Double getLikelihood() {
      return likelihood;
    }

    public Integer getOccurrences() {
      return occurrences;
    }

    public TokenData(Integer occurrences) {
      this.occurrences = occurrences;
    }

    public void addOccurence() {
      ++occurrences;
    }
  }
}
