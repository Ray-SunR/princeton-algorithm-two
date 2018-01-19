import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordNet;
    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxDis = Integer.MIN_VALUE;
        String result = null;
        for (int i = 0; i < nouns.length; i++) {
            final String nounA = nouns[i];
            int totalDistance = 0;
            for (int j = 0; j < nouns.length; j++) {
                final String nounB = nouns[j];
                if (nounA == nounB) {
                    continue;
                }
                final int distance = wordNet.distance(nounA, nounB);
                totalDistance = totalDistance + distance;
            }
            if (totalDistance > maxDis) {
                maxDis = totalDistance;
                result = nounA;
            }
        }
        return result;
    }

    // see test client below
    public static void main(String[] args)  {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}