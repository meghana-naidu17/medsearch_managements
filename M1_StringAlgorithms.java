import java.util.*;

public class M1_StringAlgorithms {

    public static int kmpSearch(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return 0;
        if (text == null || text.length() < pattern.length()) return -1;
        int[] lps = new int[pattern.length()];
        for (int i = 1, len = 0; i < pattern.length();) {
            if (Character.toLowerCase(pattern.charAt(i)) == Character.toLowerCase(pattern.charAt(len))) {
                lps[i++] = ++len;
            } else if (len > 0) {
                len = lps[len - 1];
            } else {
                lps[i++] = 0;
            }
        }
        for (int i = 0, j = 0; i < text.length();) {
            if (Character.toLowerCase(text.charAt(i)) == Character.toLowerCase(pattern.charAt(j))) {
                i++;
                j++;
                if (j == pattern.length()) return i - j;
            } else if (j > 0) {
                j = lps[j - 1];
            } else {
                i++;
            }
        }
        return -1;
    }

    public static int zSearch(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) return 0;
        if (text == null || text.length() < pattern.length()) return -1;
        String s = pattern + "$" + text;
        int[] z = new int[s.length()];
        int l = 0, r = 0;
        for (int i = 1; i < s.length(); i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < s.length() && Character.toLowerCase(s.charAt(z[i])) == Character.toLowerCase(s.charAt(i + z[i]))) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
            if (z[i] == pattern.length()) return i - pattern.length() - 1;
        }
        return -1;
    }

    public static List<Integer> rabinKarp(String text, String pattern) {
        List<Integer> out = new ArrayList<>();
        if (pattern == null || pattern.isEmpty() || text == null || pattern.length() > text.length()) return out;
        long mod = 1_000_000_007L, base = 256, ph = 0, th = 0, pow = 1;
        int m = pattern.length();
        for (int i = 0; i < m; i++) {
            ph = (ph * base + Character.toLowerCase(pattern.charAt(i))) % mod;
            th = (th * base + Character.toLowerCase(text.charAt(i))) % mod;
            if (i < m - 1) pow = (pow * base) % mod;
        }
        for (int i = 0; i <= text.length() - m; i++) {
            if (ph == th && text.regionMatches(true, i, pattern, 0, m)) out.add(i);
            if (i < text.length() - m) {
                th = (th - Character.toLowerCase(text.charAt(i)) * pow) % mod;
                if (th < 0) th += mod;
                th = (th * base + Character.toLowerCase(text.charAt(i + m))) % mod;
            }
        }
        return out;
    }

    public static Map<String, List<Integer>> ahoCorasick(String text, List<String> patterns) {
        class Node {
            Map<Character, Integer> next = new HashMap<>();
            int fail;
            List<Integer> out = new ArrayList<>();
        }
        List<Node> trie = new ArrayList<>();
        trie.add(new Node());
        for (int p = 0; p < patterns.size(); p++) {
            int cur = 0;
            String pat = patterns.get(p).toLowerCase();
            for (char c : pat.toCharArray()) {
                cur = trie.get(cur).next.computeIfAbsent(c, k -> {
                    trie.add(new Node());
                    return trie.size() - 1;
                });
            }
            trie.get(cur).out.add(p);
        }
        Queue<Integer> q = new LinkedList<>();
        for (int v : trie.get(0).next.values()) {
            trie.get(v).fail = 0;
            q.add(v);
        }
        while (!q.isEmpty()) {
            int v = q.poll();
            for (Map.Entry<Character, Integer> e : trie.get(v).next.entrySet()) {
                char c = e.getKey();
                int u = e.getValue();
                q.add(u);
                int f = trie.get(v).fail;
                while (f != 0 && !trie.get(f).next.containsKey(c)) f = trie.get(f).fail;
                if (trie.get(f).next.containsKey(c) && trie.get(f).next.get(c) != u) f = trie.get(f).next.get(c);
                trie.get(u).fail = f;
                trie.get(u).out.addAll(trie.get(f).out);
            }
        }
        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (String p : patterns) result.put(p, new ArrayList<>());
        int state = 0;
        String t = text.toLowerCase();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            while (state != 0 && !trie.get(state).next.containsKey(c)) state = trie.get(state).fail;
            if (trie.get(state).next.containsKey(c)) state = trie.get(state).next.get(c);
            for (int p : trie.get(state).out) result.get(patterns.get(p)).add(i - patterns.get(p).length() + 1);
        }
        return result;
    }

    public static List<Integer> boyerMoore(String text, String pattern) {
        return MedicalSearchEngine.boyerMooreSearch(text, pattern);
    }

    public static void run(Scanner sc, String text) {
        if (text == null || text.isBlank()) {
            text = "REPORT 401 Hypertension elevated blood pressure\nREPORT 402 Atopic Dermatitis allergic history\nREPORT 403 Acute Viral Bronchitis cough and fever";
        }
        int c;
        do {
            System.out.println("\n==============================================");
            System.out.println("          M1 STRING ALGORITHMS SUITE");
            System.out.println("==============================================");
            System.out.println("1. Knuth-Morris-Pratt (KMP) Search");
            System.out.println("2. Z-Algorithm Pattern Matching");
            System.out.println("3. Rabin-Karp Rolling Hash Search");
            System.out.println("4. Aho-Corasick Multi-Keyword Search");
            System.out.println("5. Boyer-Moore Search (Bad Character Heuristic)");
            System.out.println("6. Run Full Algorithm Benchmark Comparison");
            System.out.println("7. View Corpus Preview (" + text.length() + " chars)");
            System.out.println("8. Back");
            System.out.print("Enter Choice: ");
            String line = sc.nextLine().trim();
            try {
                c = Integer.parseInt(line);
            } catch (Exception e) {
                c = -1;
            }

            switch (c) {
                case 1 -> {
                    System.out.print("Enter medical term to search with KMP: ");
                    String p = sc.nextLine().trim();
                    long t0 = System.nanoTime();
                    int pos = kmpSearch(text, p);
                    long elapsed = (System.nanoTime() - t0) / 1000;
                    List<Integer> all = MedicalSearchEngine.kmpSearch(text, p);
                    System.out.println(pos >= 0 ? "KMP Result: First match at position " + pos + " (Total matches: " + all.size() + " at " + all + ") [" + elapsed + " µs]" : "KMP Result: NOT FOUND");
                }
                case 2 -> {
                    System.out.print("Enter phrase for Z-Function search: ");
                    String p = sc.nextLine().trim();
                    long t0 = System.nanoTime();
                    int pos = zSearch(text, p);
                    long elapsed = (System.nanoTime() - t0) / 1000;
                    System.out.println(pos >= 0 ? "Z-Function Result: FOUND at position " + pos + " [" + elapsed + " µs]" : "Z-Function Result: NOT FOUND");
                }
                case 3 -> {
                    System.out.print("Enter keyword for Rabin-Karp: ");
                    String p = sc.nextLine().trim();
                    long t0 = System.nanoTime();
                    List<Integer> matches = rabinKarp(text, p);
                    long elapsed = (System.nanoTime() - t0) / 1000;
                    System.out.println("Rabin-Karp Matches (" + matches.size() + " found in " + elapsed + " µs): " + matches);
                }
                case 4 -> {
                    System.out.print("Enter medical keywords separated by comma for Aho-Corasick (e.g. fever, cough, blood): ");
                    String[] arr = sc.nextLine().split(",");
                    List<String> ps = new ArrayList<>();
                    for (String x : arr) if (!x.trim().isEmpty()) ps.add(x.trim());
                    long t0 = System.nanoTime();
                    Map<String, List<Integer>> m = ahoCorasick(text, ps);
                    long elapsed = (System.nanoTime() - t0) / 1000;
                    System.out.println("Aho-Corasick Multi-Pattern Results (" + elapsed + " µs):");
                    for (String k : m.keySet()) {
                        System.out.println("  \"" + k + "\" -> " + (m.get(k).isEmpty() ? "NOT FOUND" : m.get(k)));
                    }
                }
                case 5 -> {
                    System.out.print("Enter keyword for Boyer-Moore: ");
                    String p = sc.nextLine().trim();
                    long t0 = System.nanoTime();
                    List<Integer> matches = boyerMoore(text, p);
                    long elapsed = (System.nanoTime() - t0) / 1000;
                    System.out.println("Boyer-Moore Matches (" + matches.size() + " found in " + elapsed + " µs): " + matches);
                }
                case 6 -> {
                    System.out.print("Enter keyword to benchmark across all algorithms: ");
                    String p = sc.nextLine().trim();
                    if (!p.isEmpty()) {
                        MedicalSearchEngine.runBenchmarkComparison(text, p);
                    }
                }
                case 7 -> {
                    System.out.println("\n--- CLINICAL CORPUS PREVIEW ---");
                    System.out.println(text.length() > 500 ? text.substring(0, 500) + "\n...[truncated " + (text.length() - 500) + " chars]..." : text);
                }
                case 8 -> System.out.println("Returning...");
                default -> System.out.println("Invalid Choice!");
            }
        } while (c != 8);
    }
}
