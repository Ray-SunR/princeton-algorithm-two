import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        digraph = G;
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
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
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        return myBreadthFirstDirectedPathsV.ancestor(digraph, w);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
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
        final MyBreadthFirstDirectedPaths myBreadthFirstDirectedPathsV = new MyBreadthFirstDirectedPaths(digraph, v);
        return myBreadthFirstDirectedPathsV.ancestor(digraph, w);
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
