import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WordNet {
    private Digraph digraph;
    private HashMap<String, List<Integer>> nounToSynsetIdMap;
    private HashMap<Integer, String> synsetsMap;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException();
        }
        nounToSynsetIdMap = new HashMap<>();
        synsetsMap = new HashMap<>();
        final In synsetsReader = new In(synsets);
        final In hypernymsReader = new In(hypernyms);
        int totalNumOfSynset = 0;
        while (synsetsReader.hasNextLine()) {
            totalNumOfSynset++;
            final String line = synsetsReader.readLine();
            final String splitted[] = line.split(",");
            final Integer index = Integer.parseInt(splitted[0]);
            final String allNouns = splitted[1];
            final String allNounsSplitted[] = allNouns.split(" ");

            synsetsMap.put(index, allNouns);
            for (final String noun : allNounsSplitted) {
                if (nounToSynsetIdMap.containsKey(noun)) {
                    nounToSynsetIdMap.get(noun).add(index);
                } else {
                    final ArrayList<Integer> synsetIds = new ArrayList<>();
                    nounToSynsetIdMap.put(noun, synsetIds);
                    synsetIds.add(index);
                }
            }
        }
        digraph = new Digraph(totalNumOfSynset);
        while (hypernymsReader.hasNextLine()) {
            final String line = hypernymsReader.readLine();
            final String splitted[] = line.split(",");
            final Integer synsetId = Integer.parseInt(splitted[0]);
            for (int i = 1; i < splitted.length; i++) {
                digraph.addEdge(synsetId, Integer.parseInt(splitted[i]));
            }
        }

        final DagUtil dagUtil = new DagUtil(digraph);
        if (!dagUtil.isDag()) {
            throw new IllegalArgumentException();
        }

        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToSynsetIdMap.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }

        return nounToSynsetIdMap.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        final List<Integer> synsetsA = nounToSynsetIdMap.get(nounA);
        final List<Integer> synsetsB = nounToSynsetIdMap.get(nounB);
        return sap.length(synsetsA, synsetsB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        final List<Integer> synsetsA = nounToSynsetIdMap.get(nounA);
        final List<Integer> synsetsB = nounToSynsetIdMap.get(nounB);
        int v = sap.ancestor(synsetsA, synsetsB);
        return synsetsMap.get(v);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        final WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
        final String nounA = "Brown_Swiss";
        final String nounB = "barrel_roll";
        final String nounC = "white_marlin";
        final String nounD = "mileage";
        final String nounE = "Black_Plague";
        final String nounF = "black_marlin";
        final String nounG = "American_water_spaniel";
        final String nounH = "histology";
        String synset = wordNet.sap(nounA, nounB);
        int distance = wordNet.distance(nounA, nounB);
        System.out.println("synset: " + synset + " distance: " + distance);
        synset = wordNet.sap(nounC, nounD);
        distance = wordNet.distance(nounC, nounD);
        System.out.println("synset: " + synset + " distance: " + distance);
        synset = wordNet.sap(nounE, nounF);
        distance = wordNet.distance(nounE, nounF);
        System.out.println("synset: " + synset + " distance: " + distance);
        synset = wordNet.sap(nounG, nounH);
        distance = wordNet.distance(nounG, nounH);
        System.out.println("synset: " + synset + " distance: " + distance);
    }
}
