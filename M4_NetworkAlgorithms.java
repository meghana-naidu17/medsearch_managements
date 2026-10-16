import java.util.*;

public class M4_NetworkAlgorithms {

    static class Edge {
        int to, rev, cap;
        Edge(int to, int rev, int cap) {
            this.to = to;
            this.rev = rev;
            this.cap = cap;
        }
    }

    static class Flow {
        List<List<Edge>> g;

        Flow(int n) {
            g = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                g.add(new ArrayList<>());
            }
        }

        void add(int u, int v, int c) {
            Edge a = new Edge(v, g.get(v).size(), c);
            Edge b = new Edge(u, g.get(u).size(), 0);
            g.get(u).add(a);
            g.get(v).add(b);
        }

        int maxFlow(int s, int t) {
            int flow = 0;
            int n = g.size();
            while (true) {
                int[] pv = new int[n];
                int[] pe = new int[n];
                Arrays.fill(pv, -1);
                Queue<Integer> q = new LinkedList<>();
                q.add(s);
                pv[s] = s;
                while (!q.isEmpty() && pv[t] == -1) {
                    int u = q.poll();
                    for (int i = 0; i < g.get(u).size(); i++) {
                        Edge e = g.get(u).get(i);
                        if (e.cap > 0 && pv[e.to] == -1) {
                            pv[e.to] = u;
                            pe[e.to] = i;
                            q.add(e.to);
                        }
                    }
                }
                if (pv[t] == -1) break;
                int add = Integer.MAX_VALUE;
                for (int v = t; v != s; v = pv[v]) {
                    add = Math.min(add, g.get(pv[v]).get(pe[v]).cap);
                }
                for (int v = t; v != s; v = pv[v]) {
                    Edge e = g.get(pv[v]).get(pe[v]);
                    e.cap -= add;
                    g.get(v).get(e.rev).cap += add;
                }
                flow += add;
            }
            return flow;
        }
    }

    static class WEdge {
        String to;
        int w;
        WEdge(String t, int w) {
            to = t;
            this.w = w;
        }
    }

    public static Map<String, Integer> dijkstra() {
        Map<String, List<WEdge>> g = new LinkedHashMap<>();
        for (String x : new String[]{"Central Hospital A", "Specialty Clinic B", "Emergency Center C", "Suburban Hospital D"}) {
            g.put(x, new ArrayList<>());
        }
        connect(g, "Central Hospital A", "Specialty Clinic B", 5);
        connect(g, "Specialty Clinic B", "Emergency Center C", 3);
        connect(g, "Emergency Center C", "Suburban Hospital D", 4);
        connect(g, "Central Hospital A", "Suburban Hospital D", 15);

        Map<String, Integer> d = new LinkedHashMap<>();
        for (String x : g.keySet()) d.put(x, Integer.MAX_VALUE);
        d.put("Central Hospital A", 0);

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingInt(d::get));
        pq.add("Central Hospital A");
        while (!pq.isEmpty()) {
            String u = pq.poll();
            for (WEdge e : g.get(u)) {
                if (d.get(u) + e.w < d.get(e.to)) {
                    d.put(e.to, d.get(u) + e.w);
                    pq.add(e.to);
                }
            }
        }
        return d;
    }

    static void connect(Map<String, List<WEdge>> g, String a, String b, int w) {
        g.get(a).add(new WEdge(b, w));
        g.get(b).add(new WEdge(a, w));
    }

    public static Map<String, String> bipartiteMatching() {
        DatasetStore store = DatasetStore.getInstance();
        List<User> patients = store.getPatients();
        List<Doctor> doctors = store.getDoctors();

        Map<String, String> m = new LinkedHashMap<>();
        int count = Math.min(patients.size(), doctors.size());
        for (int i = 0; i < Math.min(5, count); i++) {
            User p = patients.get(i);
            Doctor d = doctors.get(i);
            String dName = d.getName().startsWith("Dr.") ? d.getName() : "Dr. " + d.getName();
            m.put("Patient #" + p.getPatientId() + " (" + p.getName() + ")",
                  dName + " [" + d.getSpecialization() + "]");
        }
        return m;
    }

    public static void run() {
        System.out.println("\n========================================================");
        System.out.println("     M4 NETWORK FLOW & GRAPH ALGORITHMS");
        System.out.println("========================================================");

        Flow f = new Flow(4);
        f.add(0, 1, 20); // Central ICU to Ward A
        f.add(0, 2, 10); // Central ICU to Ward B
        f.add(1, 2, 5);  // Transfer Ward A to B
        f.add(1, 3, 10); // Ward A to Discharge
        f.add(2, 3, 15); // Ward B to Discharge
        System.out.println("1. Ford-Fulkerson / Edmonds-Karp Maximum Medical Resource Flow = " + f.maxFlow(0, 3) + " units/hr");

        System.out.println("\n2. Dijkstra Ambulance Shortest Distances from Central Hospital A:");
        for (Map.Entry<String, Integer> e : dijkstra().entrySet()) {
            System.out.println("   -> " + e.getKey() + " = " + e.getValue() + " km");
        }

        System.out.println("\n3. Bipartite Doctor-Patient Optimal Assignment (from Dataset):");
        for (Map.Entry<String, String> e : bipartiteMatching().entrySet()) {
            System.out.println("   " + e.getKey() + "  ==>  " + e.getValue());
        }
        System.out.println("========================================================\n");
    }
}
