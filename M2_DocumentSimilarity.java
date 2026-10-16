import java.util.*;

public class M2_DocumentSimilarity {
    public static int[] suffixArray(String s) {
        if (s == null || s.isEmpty()) return new int[0];
        Integer[] a = new Integer[s.length()];
        for (int i = 0; i < s.length(); i++) a[i] = i;
        Arrays.sort(a, (i, j) -> s.substring(i).compareTo(s.substring(j)));
        int[] out = new int[a.length];
        for (int i = 0; i < a.length; i++) out[i] = a[i];
        return out;
    }

    public static String longestCommonSubstring(String a, String b) {
        if (a == null || b == null || a.isEmpty() || b.isEmpty()) return "";
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        int best = 0, end = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (Character.toLowerCase(a.charAt(i - 1)) == Character.toLowerCase(b.charAt(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > best) {
                        best = dp[i][j];
                        end = i;
                    }
                }
            }
        }
        return a.substring(end - best, end);
    }

    public static Set<String> suffixTreePhrases(String text, int minLength) {
        Set<String> phrases = new LinkedHashSet<>();
        if (text == null || text.length() < minLength) return phrases;
        for (int i = 0; i < text.length(); i++) {
            for (int j = i + 1; j < text.length(); j++) {
                int k = 0;
                while (i + k < text.length() && j + k < text.length() &&
                        Character.toLowerCase(text.charAt(i + k)) == Character.toLowerCase(text.charAt(j + k))) {
                    k++;
                }
                if (k >= minLength) phrases.add(text.substring(i, i + k));
            }
        }
        return phrases;
    }

    static class SAMState {
        int link = -1, len;
        Map<Character, Integer> next = new HashMap<>();
    }

    public static int suffixAutomatonDistinctSubstrings(String s) {
        if (s == null || s.isEmpty()) return 0;
        List<SAMState> st = new ArrayList<>();
        st.add(new SAMState());
        int last = 0;
        for (char c : s.toLowerCase().toCharArray()) {
            int cur = st.size();
            st.add(new SAMState());
            st.get(cur).len = st.get(last).len + 1;
            int p = last;
            while (p != -1 && !st.get(p).next.containsKey(c)) {
                st.get(p).next.put(c, cur);
                p = st.get(p).link;
            }
            if (p == -1) {
                st.get(cur).link = 0;
            } else {
                int q = st.get(p).next.get(c);
                if (st.get(p).len + 1 == st.get(q).len) {
                    st.get(cur).link = q;
                } else {
                    int clone = st.size();
                    st.add(new SAMState());
                    st.get(clone).len = st.get(p).len + 1;
                    st.get(clone).next.putAll(st.get(q).next);
                    st.get(clone).link = st.get(q).link;
                    while (p != -1 && st.get(p).next.getOrDefault(c, -1) == q) {
                        st.get(p).next.put(c, clone);
                        p = st.get(p).link;
                    }
                    st.get(q).link = st.get(cur).link = clone;
                }
            }
            last = cur;
        }
        long count = 0;
        for (int i = 1; i < st.size(); i++) count += st.get(i).len - st.get(i).link;
        return (int) Math.min(Integer.MAX_VALUE, count);
    }

    public static double similarity(String a, String b) {
        String x = a.toLowerCase().replaceAll("\\s+", " ").trim();
        String y = b.toLowerCase().replaceAll("\\s+", " ").trim();
        if (x.isEmpty() && y.isEmpty()) return 100.0;
        if (x.isEmpty() || y.isEmpty()) return 0.0;
        int[][] dp = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= y.length(); j++) dp[0][j] = j;
        for (int i = 1; i <= x.length(); i++) {
            for (int j = 1; j <= y.length(); j++) {
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + (x.charAt(i - 1) == y.charAt(j - 1) ? 0 : 1));
            }
        }
        int max = Math.max(x.length(), y.length());
        return 100.0 * (max - dp[x.length()][y.length()]) / max;
    }

    public static void run(Scanner sc, MedicalRecordManagement records) {
        System.out.println("\n===== M2 SUFFIX STRUCTURES & DOCUMENT SIMILARITY =====");
        List<MedicalRecord> all = records.getAllRecords();
        if (all.isEmpty()) {
            System.out.println("No medical records available for analysis.");
            return;
        }

        System.out.print("Available Report IDs in dataset: ");
        for (int i = 0; i < Math.min(8, all.size()); i++) {
            System.out.print("#" + all.get(i).getRecordId() + " (" + all.get(i).getDiagnosis() + ") ");
        }
        System.out.println("...");

        int a = readIntSafe(sc, "Enter first Report ID: ");
        int b = readIntSafe(sc, "Enter second Report ID: ");

        String x = records.getReportText(a);
        String y = records.getReportText(b);

        if (x.isEmpty() || y.isEmpty()) {
            System.out.println("One or both reports were not found in the dataset.");
            return;
        }

        System.out.println("\nReport #" + a + ": " + x);
        System.out.println("Report #" + b + ": " + y);
        System.out.println("------------------------------------------------------");
        System.out.println("Document Similarity                  : " + String.format("%.2f", similarity(x, y)) + "%");
        System.out.println("Longest Common Substring Phrase      : \"" + longestCommonSubstring(x, y) + "\"");
        System.out.println("Suffix Array Sample for Doc 1        : " + Arrays.toString(Arrays.copyOf(suffixArray(x), Math.min(10, x.length()))));
        System.out.println("Suffix Tree Common Phrases (len>=5)  : " + suffixTreePhrases(x, 5));
        System.out.println("Suffix Automaton Distinct Substrings : " + suffixAutomatonDistinctSubstrings(x));
    }

    private static int readIntSafe(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (Exception e) {
                System.out.println("Please enter a valid numeric ID.");
            }
        }
    }
}
