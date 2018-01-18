import edu.princeton.cs.algs4.Digraph;

import java.util.HashSet;
import java.util.Set;

public class DagUtil {
    private boolean[] marked;  // marked[v] = is there an s->v path?
    private Set<Integer> set;
    private boolean hasCycle;
    private Digraph digraph;

    public DagUtil(final Digraph digraph) {
        this.digraph = digraph;
        marked = new boolean[digraph.V()];
        set = new HashSet<>();
        hasCycle = false;
    }

    public boolean isDag() {
        for (int v = 0; v < digraph.V(); v++) {
            if (!marked[v]) {
                dfs(v);
            }
        }

        boolean hasOneOutDegreeZero = false;
        for (int i = 0; i < marked.length; i++) {
            if (!marked[i]) {
                return false;
            }
            if (digraph.outdegree(i) == 0) {
                if (!hasOneOutDegreeZero) {
                    hasOneOutDegreeZero = true;
                } else {
                    return false;
                }
            }
        }
        return !hasCycle && hasOneOutDegreeZero;
    }

    private void dfs(int v) {
        set.add(v);
        marked[v] = true;
        for (Integer vi : digraph.adj(v)) {
            if (set.contains(vi)) {
                hasCycle = true;
            }
            if (!marked[vi]) {
               dfs(vi);
            }
        }
        set.remove(v);
    }
}
