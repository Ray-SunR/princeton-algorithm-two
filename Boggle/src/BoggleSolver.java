import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class BoggleSolver {
    private MyTrie dictionary;
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        this.dictionary = new MyTrie();
        for (final String string : dictionary) {
            this.dictionary.add(string);
        }
    }

    private void dfs(BoggleBoard board, String currentString, char lookforChar, Coordinate coordinate, boolean[][] onStack,
                     Set<String> result, MyTrie.Node prefixNode) {
        prefixNode = dictionary.prefixNode(lookforChar, prefixNode);
        if (onStack[coordinate.row][coordinate.col] || prefixNode == null) {
            return;
        }

        if (lookforChar == 'Q') {
            // Now prefixNode is at 'Q' node, there can only be 'U' in its next array availble, so advance
            prefixNode = dictionary.prefixNode('U', prefixNode);
            if (prefixNode == null) {
                return;
            }
            currentString = currentString + "U";
        }

        onStack[coordinate.row][coordinate.col] = true;
        if (currentString.length() >= 3 && prefixNode.isString && !result.contains(currentString)) {
            result.add(currentString);
        }

        List<Coordinate> coordinates = getDirectNeighborCoordinates(coordinate, board);
        for (Coordinate mcoordinate : coordinates) {
            String toAdd = String.valueOf(board.getLetter(mcoordinate.row, mcoordinate.col));
            dfs(board, currentString + toAdd, board.getLetter(mcoordinate.row, mcoordinate.col),
                    mcoordinate, onStack, result, prefixNode);
        }
        onStack[coordinate.row][coordinate.col] = false;
    }

    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // Holds all possible paths whose length is greater than 3.
        final Set<String> paths = new TreeSet<>();
        boolean onStack[][] = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                String currentString = String.valueOf(board.getLetter(row, col));
                dfs(board, currentString, board.getLetter(row, col), new Coordinate(row, col), onStack, paths, dictionary.root());
            }
        }

        return paths;
    }

    private List<Coordinate> getDirectNeighborCoordinates(final Coordinate coordinate, final BoggleBoard board) {
        List<Coordinate> result = new ArrayList<>();
        final Coordinate left = new Coordinate(coordinate.row, coordinate.col - 1);
        final Coordinate right = new Coordinate(coordinate.row, coordinate.col + 1);

        final Coordinate top = new Coordinate(coordinate.row - 1, coordinate.col);
        final Coordinate bottom = new Coordinate(coordinate.row + 1, coordinate.col);

        final Coordinate topLeft = new Coordinate(coordinate.row - 1, coordinate.col - 1);
        final Coordinate topRight = new Coordinate(coordinate.row - 1, coordinate.col + 1);

        final Coordinate bottomLeft = new Coordinate(coordinate.row + 1, coordinate.col - 1);
        final Coordinate bottomRight = new Coordinate(coordinate.row + 1, coordinate.col + 1);

        // left
        if (left.col >= 0) {
            result.add(left);
        }

        // right
        if (right.col < board.cols()) {
            result.add(right);
        }

        // top
        if (top.row >= 0) {
            result.add(top);
        }

        // bottom
        if (bottom.row < board.rows()) {
            result.add(bottom);
        }

        // top left
        if (topLeft.row >= 0 && topLeft.col >= 0) {
            result.add(topLeft);
        }

        // top right
        if (topRight.row >= 0 && topRight.col < board.cols() ) {
            result.add(topRight);
        }

        // bottom left
        if (bottomLeft.row < board.rows() && bottomLeft.col >= 0) {
            result.add(bottomLeft);
        }

        // bottom right
        if (bottomRight.row < board.rows() && bottomRight.col < board.cols()) {
            result.add(bottomRight);
        }
        return result;
    }

    private class Coordinate {
        private int row;
        private int col;

        public Coordinate(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other instanceof Coordinate) {
                return this.row == ((Coordinate) other).row && this.col == ((Coordinate) other).col;
            }
            return false;
        }

    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    // 0–2	0
    // 3–4	1
    // 5	2
    // 6	3
    // 7	5
    // 8+	11
    public int scoreOf(String word) {
        if (dictionary.contains(word)) {
            if (word.length() >= 0 && word.length() <= 2) {
                return 0;
            } else if (word.length() >= 3 && word.length() <= 4) {
                return 1;
            } else if (word.length() == 5) {
                return 2;
            } else if (word.length() == 6) {
                return 3;
            } else if (word.length() == 7) {
                return 5;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        long base = System.currentTimeMillis();
        Iterable<String> wordIter = solver.getAllValidWords(board);
        StdOut.println((System.currentTimeMillis() - base) / 1000.0);
        for (String word : wordIter) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
