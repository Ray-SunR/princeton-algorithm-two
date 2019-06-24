/******************************************************************************
 *  Compilation:  javac TrieSET.java
 *  Execution:    java TrieSET < words.txt
 *  Dependencies: StdIn.java
 *  Data files:   https://algs4.cs.princeton.edu/52trie/shellsST.txt
 *
 *  An set for extended ASCII strings, implemented  using a 256-way trie.
 *
 *  Sample client reads in a list of words from standard input and
 *  prints out each word, removing any duplicates.
 *
 ******************************************************************************/

/**
 *  The {@code TrieSET} class represents an ordered set of strings over
 *  the extended ASCII alphabet.
 *  It supports the usual <em>add</em>, <em>contains</em>, and <em>delete</em>
 *  methods. It also provides character-based methods for finding the string
 *  in the set that is the <em>longest prefix</em> of a given prefix,
 *  finding all strings in the set that <em>start with</em> a given prefix,
 *  and finding all strings in the set that <em>match</em> a given pattern.
 *  <p>
 *  This implementation uses a 256-way trie.
 *  The <em>add</em>, <em>contains</em>, <em>delete</em>, and
 *  <em>longest prefix</em> methods take time proportional to the length
 *  of the key (in the worst case). Construction takes constant time.
 *  <p>
 *  For additional documentation, see
 *  <a href="https://algs4.cs.princeton.edu/52trie">Section 5.2</a> of
 *  <i>Algorithms in Java, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class MyTrie {
    private static final int R = 26;        // extended ASCII

    private Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    public static class Node {
        public Node[] next = new Node[R];
        public boolean isString;
    }

    /**
     * Initializes an empty set of strings.
     */
    public MyTrie() {
    }

    /**
     * Does the set contain the given key?
     *
     * @param key the key
     * @return {@code true} if the set contains {@code key} and
     * {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        Node node = root;
        int index = 0;
        while (node != null && index != key.length()) {
            node = node.next[key.charAt(index) - 'A'];
            index++;
        }
        return node != null && node.isString;
    }

    /**
     * Adds the key to the set if it is not already present.
     *
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    public Node root() {
        return root;
    }

    public Node prefixNode(char c, Node prevNode) {
        return prevNode == null ? null : prevNode.next[c - 'A'];
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        } else {
            int c = key.charAt(d) - 'A';
            x.next[c] = add(x.next[c], key, d + 1);
        }
        return x;
    }
}

