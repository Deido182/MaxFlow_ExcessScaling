import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Pattern;

import static java.lang.Math.*;
import static java.lang.Math.min;
import static java.lang.Math.max;

class ExcessScaling {
	public static class Edge {
		int j;
		long rc;
		Edge rev;
		
		public Edge(int jj, long rcc) {
			j = jj;
			rc = rcc;
		}
	}
	
	public static int[] exactDist(int t, int n, ArrayList <Edge>[] g, final int MAXD, int toexclude) {
		int[] d = new int[n];
		Arrays.fill(d, MAXD);
		LinkedList <Integer> lvl = new LinkedList <> ();
		lvl.add(t);
		d[t] = 0;
		while(!lvl.isEmpty()) {
			int i = lvl.removeFirst();
			for(Edge e : g[i]) {
				int j = e.j;
				if(d[j] != MAXD || e.rev.rc == 0 || j == toexclude)
					continue;
				d[j] = d[i] + 1;
				lvl.add(j);
			}
		}
		return d;
	}
	
	public static long uppb(long emax) {
		long ans = 1;
		while(ans <= emax)
			ans <<= 1;
		return ans;
	}
	
	public static long excessScaling(int s, int t, int n, ArrayList <Edge>[] g, final long CMAX) {
		/*
		 * This code finds max-flow-value only (it performs the first phase 
		 * of the algorithm, without fixing preflow)
		 * If you want the second one as well compute exactDist only once 
		 * here (without excluding s from the paths) and set d[s] = n
		 * 
		 * The time complexity does not change, but it is slower
		 */
		final int MAXD = 2 * n;
		long[] e = new long[n];
		int[] from = new int[n];
		LinkedList <Integer>[] b = new LinkedList[MAXD + 1];
		for(int i = 0; i <= MAXD; i ++)
			b[i] = new LinkedList <> ();
		for(Edge ed : g[s]) {
			e[s] -= ed.rc;
			e[ed.j] += ed.rc;
			ed.rev.rc += ed.rc;
			ed.rc = 0;
		}
		long delta = uppb(CMAX * n);
		while(delta > 1) {
			int[] d = exactDist(t, n, g, MAXD, s); // It would be enough doing it just once. However (used as an heuristic) it REALLY improves performance.
			for(int i = 0; i < n; i ++)
				if(i != s && i != t && e[i] >= delta / 2)
					b[d[i]].addFirst(i);
			int lambda = 0;
			while(lambda < MAXD) {
				if(b[lambda].isEmpty()) {
					lambda ++;
					continue;
				}
				int i = b[lambda].removeFirst();
				boolean found = false;
				while(from[i] < g[i].size()) {
					Edge ed = g[i].get(from[i]);
					int j = ed.j;
					if(d[i] != d[j] + 1 || ed.rc == 0) {
						from[i] ++;
						continue;
					}
					found = true;
					long x = min(ed.rc, j == s || j == t ? e[i] : min(e[i], delta - e[j]));
					e[i] -= x;
					e[j] += x;
					ed.rc -= x;
					ed.rev.rc += x;
					if(j != s && j != t && e[j] >= delta / 2)
						b[-- lambda].addFirst(j);
					if(e[i] >= delta / 2)
						b[d[i]].addFirst(i);
					break;
				}
				if(found)
					continue;
				d[i] = MAXD;
				for(Edge ed : g[i]) {
					int j = ed.j;
					if(ed.rc != 0)
						d[i] = min(d[i], d[j] + 1);
				}
				from[i] = 0;
				b[d[i]].addFirst(i);
			}
			delta >>= 1;
		}
		return e[t];
	}
	
	public static void main(String[] args) throws IOException {
		int n = 10000;
		int m = 80000;
		int s = 0;
		int t = n - 1;
		long cmax = 1000;
		ArrayList <Edge>[] g = new ArrayList[n];
		long[][] rc = new long[n][n];
		for(int i = 0; i < n; i ++)
			g[i] = new ArrayList <> ();
		for(int i = 0; i < m; i ++) {
			int u = -1, v = -1;
			while(u == v || rc[u][v] != 0) {
				u = new Random().nextInt(n);
				v = new Random().nextInt(n);
			}
			long c = new Random().nextInt((int) cmax) + 1;
			rc[u][v] = c;
			rc[v][u] = c;
			g[u].add(new Edge(v, c));
			g[v].add(new Edge(u, c));
			g[u].get(g[u].size() - 1).rev = g[v].get(g[v].size() - 1);
			g[v].get(g[v].size() - 1).rev = g[u].get(g[u].size() - 1);
		}
		long st = System.currentTimeMillis();
		System.out.println(excessScaling(s, t, n, g, cmax) + "\n" + (System.currentTimeMillis() - st) + " ms");
    } 
 
    public static FastReader in = new FastReader();
    public static BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));
}
 
class FastReader {
    BufferedReader br;
    StringTokenizer st;
 
    public FastReader() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public String next() {
        while (st == null || !st.hasMoreElements()) {
            try {
                st = new StringTokenizer(br.readLine());
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
        return st.nextToken();
    }
 
    public char nextChar() {
        return next().charAt(0);
    }
    
    public int nextInt() {
        return Integer.parseInt(next());
    }
 
    public long nextLong() {
        return Long.parseLong(next());
    }
 
    public double nextDouble() {
        return Double.parseDouble(next());
    }
 
    public String nextLine() {
        String str = "";
        try {
            str = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    
    public int[] nextIntArray(int n) {
        int[] a = new int[n];
        for(int i = 0; i < n; i ++)
            a[i] = nextInt();
        return a;
    }
    
    public Integer[] nextIntegerArray(int n) {
        Integer[] a = new Integer[n];
        for(int i = 0; i < n; i ++)
            a[i] = nextInt();
        return a;
    }
    
    public long[] nextLongArray(int n) {
        long[] a = new long[n];
        for(int i = 0; i < n; i ++)
            a[i] = nextLong();
        return a;
    }
    
    public double[] nextDoubleArray(int n) {
        double[] a = new double[n];
        for(int i = 0; i < n; i ++)
            a[i] = nextDouble();
        return a;
    }
    
    public String[] nextStringArray(int n) {
        String[] a = new String[n];
        for(int i = 0; i < n; i ++)
            a[i] = next();
        return a;
    }
}
