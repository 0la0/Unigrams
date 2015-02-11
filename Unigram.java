import java.io.*;
import java.util.*;

public class Unigram {

  private Map<String, Map<String, Double>> probabilityMap = new HashMap<String, Map<String, Double>>();
  
  public Unigram (String inFilePath) {
    buildModel(inFilePath);
  }
  
  /*
   * Given a string that occurs in the created model,
   * return a string based on the probability distribution of subsequent strings
   */
  public String getNextState (String inputString) {
    //for the current state, get the probability distribution of the next state
    TreeMap<String, Double> probVector = (TreeMap<String, Double>) probabilityMap.get(inputString);
    if (probVector.size() <= 0) {
      //terminal case
      return null;
    }
    String prevKey = probVector.firstKey();
    if (probVector.size() == 1) {
      return prevKey;
    }
   
    //generate a random value, iterate over the probabilities until a match is found
    Object[] keyArray = probVector.keySet().toArray();
    Object[] probArray = probVector.values().toArray();
    double randVal = Math.random();
    for (int i = 0; i < keyArray.length - 1; i++) {
      if (randVal >= (double) probArray[i] && randVal <= (double) probArray[i + 1]) {
        return (String) keyArray[i];
      }
    }
    return (String) keyArray[keyArray.length - 1];
  }
  
  /*
   * Create the Markov Chain probability distributions based on the input text.
   * The model is in the form of a hash map that has a string for a key and a
   * map for a value.  The value map has the next state probabilities.
   */
  private void buildModel (String filePath) {
    // Read in text from file
    // for each unique word in file, put unique into wordMap as key
    // put a map of unique following words and an occurrence count as value
    Map<String, Map<String, Integer>> wordMap = new TreeMap<String, Map<String, Integer>>();
   
    //followingWordTot is a count of the number of times a given word occurs in document
    Map<String, Integer> followingWordTot = new TreeMap<String, Integer>();
    Scanner input = null;
    try {
      input = new Scanner(new File(filePath));
      String thisString = input.next().replaceAll("[^a-zA-Z0-9_-]", "").toLowerCase();
      String nextString = null;
      while(input.hasNext()){
        nextString = input.next().replaceAll("[^a-zA-Z0-9_-]", "").toLowerCase();
      
        //case 1: thisString does not exist in map
        if (wordMap.get(thisString) == null) {
          Map<String, Integer> tempMap = new TreeMap<String, Integer>();
          tempMap.put(nextString, 1);
          wordMap.put(thisString, tempMap);
          followingWordTot.put(thisString, 1);
        }
      
        //case 2: thisString does exist, but nextString
        //has not yet been associated with thisString
        else if(wordMap.get(thisString).get(nextString) == null) {
          wordMap.get(thisString).put(nextString, 1);
          int followingWordInc = followingWordTot.get(thisString) + 1;
          followingWordTot.put(thisString, followingWordInc);
        }
      
        //case 3: thisString does exist in map, and nextString
        //has already been associated with it
        else if (wordMap.get(thisString).get(nextString) >= 1) {
          int incVal = wordMap.get(thisString).get(nextString) + 1;
          wordMap.get(thisString).put(nextString, incVal);
          int followingWordInc = followingWordTot.get(thisString) + 1;
          followingWordTot.put(thisString, followingWordInc);
        }
        thisString = nextString;
      }
    } catch (FileNotFoundException e) {
      System.out.println("invalid input file path, exiting");
      e.printStackTrace();
      System.exit(1);
    } finally {
      if (input != null) input.close();
    }
   
    //Iterate over wordMap and calculate the percentage of
    //times a given word will follow each unique word
    Iterator it = wordMap.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry mainPairs = (Map.Entry)it.next();
      String mainKey = (String) mainPairs.getKey();
      int nextWordCnt = followingWordTot.get(mainKey);
          
      Map<String, Integer> subMap = (TreeMap<String, Integer>) mainPairs.getValue();
      Iterator it2 = subMap.entrySet().iterator();
          
      Map<String, Double> probabilityVector = new TreeMap<String, Double>();
      double probTot = 0.0;
      while (it2.hasNext()) {
        Map.Entry subPairs = (Map.Entry)it2.next();
        String subKey = (String) subPairs.getKey();
        int cnt = (int) subPairs.getValue();
        double prob = cnt / (nextWordCnt * 1.0);
        probTot += prob;
        probabilityVector.put(subKey, probTot);
      }
        probabilityMap.put(mainKey, probabilityVector);          
    }
  }
  
  /*
   * Generate new content based on the built model
   */
  private String createNewContent (int numTokens) {
    if (numTokens < 1) {
      System.out.println("Unigrams.createNewContent requires a positive integer");
      return null;
    }
    StringBuilder outputText = new StringBuilder();
    StringBuilder currentLine = new StringBuilder();
    currentLine.append("\n");
    //start with random word
    List<String> keys = new ArrayList<String>(probabilityMap.keySet());
    int randomIndex = (int) Math.floor( (keys.size() - 1) * Math.random());
    String lastWord = keys.get(randomIndex);
   
    //get next word n times
    for (int i = 0; i < numTokens; i++) {
      String nextWord = getNextState(lastWord);
      if (lastWord != null) {
        //keep the output terminal friendly
        if (currentLine.length() + nextWord.length() > 78) {
          outputText.append(currentLine.toString());
          outputText.append("\n");
          currentLine = new StringBuilder();
        }
        //add the next word and move on to next iteration
        currentLine.append(nextWord);
        currentLine.append(" ");
        lastWord = nextWord;
      }
    }
    outputText.append(currentLine.toString());
    outputText.append("\n");
    return outputText.toString();
  }
  
  private static String getUsage () {
    StringBuilder sb = new StringBuilder();
    sb.append("usage: Unigram inFile sizeOfOutput\n");
    sb.append("\tinFile: path to the input text file from which to build a markov chain\n");
    sb.append("\tsizeOfOutput: number of strings to print to stdout\n");
    return sb.toString();
  }
  
  public static void main (String[] args) {
    if (args.length != 2) {
      System.out.print(getUsage());
      return;
    }
    Unigram uni = new Unigram(args[0]);
    System.out.print(uni.createNewContent(Integer.parseInt(args[1])));
  }
  
}
