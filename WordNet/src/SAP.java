import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new IllegalArgumentException();
        }
        digraph = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        int ancestor = ancestor(v, w);
        if (ancestor == -1) {
            return -1;
        }
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsW = new MyBreadthFirstDirectedPaths(digraph, w);
        return myBreadthFirstDirectedPathsV.distTo(ancestor) + myBreadthFirstDirectedPathsW.distTo(ancestor);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        return myBreadthFirstDirectedPathsV.ancestor(digraph, w);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        for (Integer i : v) {
            validateVertex(i);
        }

        for (Integer i : w) {
            validateVertex(i);
        }

        int ancestor = ancestor(v, w);
        if (ancestor == -1) {
            return -1;
        }
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsW = new MyBreadthFirstDirectedPaths(digraph, w);
        return myBreadthFirstDirectedPathsV.distTo(ancestor) + myBreadthFirstDirectedPathsW.distTo(ancestor);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        for (Integer i : v) {
            validateVertex(i);
        }

        for (Integer i : w) {
            validateVertex(i);
        }

        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        return myBreadthFirstDirectedPathsV.ancestor(digraph, w);
    }

    private void validateVertex(int v) {
        if (v < 0 || v >= digraph.V()) {
            throw new IllegalArgumentException();
        }
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
