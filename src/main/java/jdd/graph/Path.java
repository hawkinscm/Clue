
package jdd.graph;

import java.util.HashMap;

import jdd.util.JDDConsole;

/**
 * A path in a graph.
 * <p> a node v1 is may be connected to another node v2.
 */
public class Path {
	HashMap hm;
	public Path() { hm = new HashMap();	}

	public void connect(Node n1, Node n2) { hm.put(n1,n2); }

	public Node next(Node from) { return (Node) hm.get(from) ; }

	public void show(Node source) {
		int size = 0, max = hm.size();
		do {
			JDDConsole.out.print(" " + source.id);
			source = next(source);
		} while(source != null && size < max);
		JDDConsole.out.println();
	}
}
