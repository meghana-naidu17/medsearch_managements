import java.util.*;

/**
 * Advanced Medical Search Engine implementing classic string matching
 * and pattern recognition algorithms on healthcare datasets.
 */
public class MedicalSearchEngine {

    public enum SearchMethod {
        KMP("Knuth-Morris-Pratt (KMP)"),
        RABIN_KARP("Rabin-Karp Rolling Hash"),
        BOYER_MOORE("Boyer-Moore (Bad Character Heuristic)"),
        Z_ALGORITHM("Z-Algorithm Linear Pattern Match"),
        AHO_CORASICK("Aho-Corasick Multi-Pattern Automaton"),
        FUZZY_LEVENSHTEIN("Fuzzy Matching (Levenshtein Distance)"),
        BENCHMARK_ALL("Benchmark All Algorithms");

        private final String displayName;
        SearchMethod(String name) { this.displayName = name; }
        public String getDisplayName() { return displayName; }
    }

    public static class SearchResult<T> {
        public final T item;
        public final String fieldName;
        public final String fieldValue;
        public final List<Integer> matchIndices;
        public final String highlightedText;
        public final double similarityScore; // For fuzzy

        public SearchResult(T item, String fieldName, String fieldValue, List<Integer> matchIndices, String highlightedText, double similarityScore) {
            this.item = item;
            this.fieldName = fieldName;
            this.fieldValue = fieldValue;
            this.matchIndices = matchIndices;
            this.highlightedText = highlightedText;
            this.similarityScore = similarityScore;
        }
    }

    // =========================================================================
    // 1. KNUTH-MORRIS-PRATT (KMP)
    // =========================================================================
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int[] lps = computeKMPPrefixTable(p);

        int i = 0, j = 0;
        while (i < t.length()) {
            if (t.charAt(i) == p.charAt(j)) {
                i++;
                j++;
                if (j == p.length()) {
                    matches.add(i - j);
                    j = lps[j - 1];
                }
            } else {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }

    private static int[] computeKMPPrefixTable(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    // =========================================================================
    // 2. RABIN-KARP ROLLING HASH
    // =========================================================================
    public static List<Integer> rabinKarpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int n = t.length();
        int m = p.length();
        long prime = 1_000_000_007L;
        long base = 256;

        long pHash = 0;
        long tHash = 0;
        long h = 1;

        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }

        for (int i = 0; i < m; i++) {
            pHash = (base * pHash + p.charAt(i)) % prime;
            tHash = (base * tHash + t.charAt(i)) % prime;
        }

        for (int i = 0; i <= n - m; i++) {
            if (pHash == tHash) {
                if (t.regionMatches(true, i, p, 0, m)) {
                    matches.add(i);
                }
            }
            if (i < n - m) {
                tHash = (base * (tHash - t.charAt(i) * h) + t.charAt(i + m)) % prime;
                if (tHash < 0) {
                    tHash = tHash + prime;
                }
            }
        }
        return matches;
    }

    // =========================================================================
    // 3. BOYER-MOORE (BAD CHARACTER HEURISTIC)
    // =========================================================================
    public static List<Integer> boyerMooreSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        String t = text.toLowerCase();
        String p = pattern.toLowerCase();
        int n = t.length();
        int m = p.length();

        int[] badChar = new int[256];
        Arrays.fill(badChar, -1);
        for (int i = 0; i < m; i++) {
            char c = p.charAt(i);
            if (c < 256) {
                badChar[c] = i;
            }
        }

        int shift = 0;
        while (shift <= (n - m)) {
            int j = m - 1;
            while (j >= 0 && p.charAt(j) == t.charAt(shift + j)) {
                j--;
            }
            if (j < 0) {
                matches.add(shift);
                char nextChar = shift + m < n ? t.charAt(shift + m) : 0;
                int bc = nextChar < 256 ? badChar[nextChar] : -1;
                shift += (shift + m < n) ? m - bc : 1;
            } else {
                char mismatchChar = t.charAt(shift + j);
                int bc = mismatchChar < 256 ? badChar[mismatchChar] : -1;
                shift += Math.max(1, j - bc);
            }
        }
        return matches;
    }

    // =========================================================================
    // 4. Z-ALGORITHM
    // =========================================================================
    public static List<Integer> zAlgorithmSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        if (text == null || pattern == null || pattern.isEmpty() || text.length() < pattern.length()) {
            return matches;
        }
        String concat = pattern.toLowerCase() + "$" + text.toLowerCase();
        int len = concat.length();
        int[] z = new int[len];
        int l = 0, r = 0;

        for (int i = 1; i < len; i++) {
            if (i <= r) {
                z[i] = Math.min(r - i + 1, z[i - l]);
            }
            while (i + z[i] < len && concat.charAt(z[i]) == concat.charAt(i + z[i])) {
                z[i]++;
            }
            if (i + z[i] - 1 > r) {
                l = i;
                r = i + z[i] - 1;
            }
            if (z[i] == pattern.length()) {
                matches.add(i - pattern.length() - 1);
            }
        }
        return matches;
    }

    // =========================================================================
    // 5. AHO-CORASICK MULTI-PATTERN SEARCH
    // =========================================================================
    public static Map<String, List<Integer>> ahoCorasickSearch(String text, List<String> patterns) {
        class ACTrieNode {
            Map<Character, Integer> next = new HashMap<>();
            int failure = 0;
            List<Integer> patternIndices = new ArrayList<>();
        }

        List<ACTrieNode> trie = new ArrayList<>();
        trie.add(new ACTrieNode());

        for (int p = 0; p < patterns.size(); p++) {
            String pat = patterns.get(p).toLowerCase().trim();
            if (pat.isEmpty()) continue;
            int cur = 0;
            for (char c : pat.toCharArray()) {
                if (!trie.get(cur).next.containsKey(c)) {
                    trie.add(new ACTrieNode());
                    trie.get(cur).next.put(c, trie.size() - 1);
                }
                cur = trie.get(cur).next.get(c);
            }
            trie.get(cur).patternIndices.add(p);
        }

        Queue<Integer> q = new LinkedList<>();
        for (int child : trie.get(0).next.values()) {
            trie.get(child).failure = 0;
            q.add(child);
        }

        while (!q.isEmpty()) {
            int state = q.poll();
            for (Map.Entry<Character, Integer> entry : trie.get(state).next.entrySet()) {
                char c = entry.getKey();
                int child = entry.getValue();
                q.add(child);

                int failure = trie.get(state).failure;
                while (failure != 0 && !trie.get(failure).next.containsKey(c)) {
                    failure = trie.get(failure).failure;
                }
                if (trie.get(failure).next.containsKey(c) && trie.get(failure).next.get(c) != child) {
                    failure = trie.get(failure).next.get(c);
                }
                trie.get(child).failure = failure;
                trie.get(child).patternIndices.addAll(trie.get(failure).patternIndices);
            }
        }

        Map<String, List<Integer>> result = new LinkedHashMap<>();
        for (String p : patterns) {
            result.put(p, new ArrayList<>());
        }

        String t = text.toLowerCase();
        int state = 0;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            while (state != 0 && !trie.get(state).next.containsKey(c)) {
                state = trie.get(state).failure;
            }
            if (trie.get(state).next.containsKey(c)) {
                state = trie.get(state).next.get(c);
            }
            for (int pIdx : trie.get(state).patternIndices) {
                String pat = patterns.get(pIdx);
                result.get(pat).add(i - pat.length() + 1);
            }
        }
        return result;
    }

    // =========================================================================
    // 6. FUZZY MATCHING (LEVENSHTEIN DISTANCE)
    // =========================================================================
    public static int levenshteinDistance(String a, String b) {
        String s1 = a.toLowerCase();
        String s2 = b.toLowerCase();
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }

    public static double calculateSimilarity(String a, String b) {
        if (a == null || b == null) return 0.0;
        int maxLen = Math.max(a.length(), b.length());
        if (maxLen == 0) return 100.0;
        int dist = levenshteinDistance(a, b);
        return ((double) (maxLen - dist) / maxLen) * 100.0;
    }

    public static boolean fuzzyWordMatch(String text, String pattern, int maxDistance) {
        if (text == null || pattern == null) return false;
        String p = pattern.trim().toLowerCase();
        if (p.isEmpty()) return false;
        int allowedDist = (p.length() <= 3) ? 0 : (p.length() <= 5 ? 1 : Math.min(maxDistance, 2));
        String[] words = text.toLowerCase().split("[\\s,;:.()#-]+");
        for (String w : words) {
            if (w.isEmpty()) continue;
            if (levenshteinDistance(w, p) <= allowedDist) return true;
        }
        return false;
    }

    public static double bestWordSimilarity(String text, String pattern) {
        if (text == null || pattern == null) return 0.0;
        String p = pattern.trim().toLowerCase();
        if (p.isEmpty()) return 0.0;
        double best = calculateSimilarity(text, p);
        String[] words = text.toLowerCase().split("[\\s,;:.()#-]+");
        for (String w : words) {
            if (w.isEmpty()) continue;
            double s = calculateSimilarity(w, p);
            if (s > best) best = s;
        }
        return best;
    }

    // =========================================================================
    // UNIFIED PATTERN MATCHER
    // =========================================================================
    public static List<Integer> matchPattern(String text, String pattern, SearchMethod method) {
        if (text == null || pattern == null || pattern.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return switch (method) {
            case KMP -> kmpSearch(text, pattern);
            case RABIN_KARP -> rabinKarpSearch(text, pattern);
            case BOYER_MOORE -> boyerMooreSearch(text, pattern);
            case Z_ALGORITHM -> zAlgorithmSearch(text, pattern);
            default -> kmpSearch(text, pattern);
        };
    }

    public static String highlightMatches(String text, List<Integer> matchIndices, int patternLen) {
        if (matchIndices == null || matchIndices.isEmpty()) return text;
        StringBuilder sb = new StringBuilder();
        int last = 0;
        for (int idx : matchIndices) {
            if (idx < last || idx >= text.length()) continue;
            sb.append(text, last, idx);
            sb.append("[**");
            int end = Math.min(idx + patternLen, text.length());
            sb.append(text, idx, end);
            sb.append("**]");
            last = end;
        }
        if (last < text.length()) {
            sb.append(text.substring(last));
        }
        return sb.toString();
    }

    // =========================================================================
    // HIGH-LEVEL SEARCH FUNCTIONS FOR HEALTHCARE ENTITIES
    // =========================================================================

    public static List<SearchResult<Doctor>> searchDoctors(List<Doctor> doctors, String query, SearchMethod method) {
        List<SearchResult<Doctor>> results = new ArrayList<>();
        if (doctors == null || query == null || query.isBlank()) return results;
        query = query.trim();

        for (Doctor d : doctors) {
            if (method == SearchMethod.FUZZY_LEVENSHTEIN) {
                double simName = bestWordSimilarity(d.getName(), query);
                double simSpec = bestWordSimilarity(d.getSpecialization(), query);
                double simDept = bestWordSimilarity(d.getDepartment(), query);
                double maxSim = Math.max(simName, Math.max(simSpec, simDept));

                if (maxSim >= 60.0 || fuzzyWordMatch(d.getName(), query, 2) || fuzzyWordMatch(d.getSpecialization(), query, 2)) {
                    String matchedField = (simSpec >= simName && simSpec >= simDept) ? "Specialization" : (simDept >= simName ? "Department" : "Name");
                    String val = matchedField.equals("Name") ? d.getName() : (matchedField.equals("Specialization") ? d.getSpecialization() : d.getDepartment());
                    results.add(new SearchResult<>(d, matchedField, val, List.of(0), val, maxSim));
                }
            } else {
                List<Integer> mName = matchPattern(d.getName(), query, method);
                if (!mName.isEmpty()) {
                    results.add(new SearchResult<>(d, "Name", d.getName(), mName, highlightMatches(d.getName(), mName, query.length()), 100.0));
                    continue;
                }
                List<Integer> mSpec = matchPattern(d.getSpecialization(), query, method);
                if (!mSpec.isEmpty()) {
                    results.add(new SearchResult<>(d, "Specialization", d.getSpecialization(), mSpec, highlightMatches(d.getSpecialization(), mSpec, query.length()), 100.0));
                    continue;
                }
                List<Integer> mDept = matchPattern(d.getDepartment(), query, method);
                if (!mDept.isEmpty()) {
                    results.add(new SearchResult<>(d, "Department", d.getDepartment(), mDept, highlightMatches(d.getDepartment(), mDept, query.length()), 100.0));
                    continue;
                }
                List<Integer> mAv = matchPattern(d.getAvailability(), query, method);
                if (!mAv.isEmpty()) {
                    results.add(new SearchResult<>(d, "Availability", d.getAvailability(), mAv, highlightMatches(d.getAvailability(), mAv, query.length()), 100.0));
                }
            }
        }
        return results;
    }

    public static List<SearchResult<User>> searchPatients(List<User> patients, String query, SearchMethod method) {
        List<SearchResult<User>> results = new ArrayList<>();
        if (patients == null || query == null || query.isBlank()) return results;
        query = query.trim();

        for (User p : patients) {
            if (method == SearchMethod.FUZZY_LEVENSHTEIN) {
                double simName = bestWordSimilarity(p.getName(), query);
                if (simName >= 65.0 || fuzzyWordMatch(p.getName(), query, 2)) {
                    results.add(new SearchResult<>(p, "Name", p.getName(), List.of(0), p.getName(), Math.max(simName, 65.0)));
                }
            } else {
                List<Integer> mName = matchPattern(p.getName(), query, method);
                if (!mName.isEmpty()) {
                    results.add(new SearchResult<>(p, "Name", p.getName(), mName, highlightMatches(p.getName(), mName, query.length()), 100.0));
                    continue;
                }
                List<Integer> mPhone = matchPattern(p.getPhone(), query, method);
                if (!mPhone.isEmpty()) {
                    results.add(new SearchResult<>(p, "Phone", p.getPhone(), mPhone, highlightMatches(p.getPhone(), mPhone, query.length()), 100.0));
                    continue;
                }
                List<Integer> mGender = matchPattern(p.getGender(), query, method);
                if (!mGender.isEmpty()) {
                    results.add(new SearchResult<>(p, "Gender", p.getGender(), mGender, highlightMatches(p.getGender(), mGender, query.length()), 100.0));
                    continue;
                }
                String idStr = String.valueOf(p.getPatientId());
                List<Integer> mId = matchPattern(idStr, query, method);
                if (!mId.isEmpty()) {
                    results.add(new SearchResult<>(p, "Patient ID", idStr, mId, highlightMatches(idStr, mId, query.length()), 100.0));
                }
            }
        }
        return results;
    }

    public static List<SearchResult<Appointment>> searchAppointments(List<Appointment> appointments, String query, SearchMethod method, DatasetStore store) {
        List<SearchResult<Appointment>> results = new ArrayList<>();
        if (appointments == null || query == null || query.isBlank()) return results;
        query = query.trim();

        for (Appointment a : appointments) {
            User patient = store.getPatient(a.getPatientId());
            Doctor doctor = store.getDoctor(a.getDoctorId());
            String patientName = patient != null ? patient.getName() : "ID:" + a.getPatientId();
            String doctorName = doctor != null ? doctor.getName() : "ID:" + a.getDoctorId();
            String composite = "App#" + a.getAppointmentId() + " " + patientName + " " + doctorName + " " + a.getAppointmentDate() + " " + a.getStatus();

            if (method == SearchMethod.FUZZY_LEVENSHTEIN) {
                double sim = bestWordSimilarity(composite, query);
                if (sim >= 65.0 || fuzzyWordMatch(composite, query, 2)) {
                    results.add(new SearchResult<>(a, "Appointment Details", composite, List.of(0), composite, Math.max(sim, 65.0)));
                }
            } else {
                List<Integer> mStatus = matchPattern(a.getStatus(), query, method);
                if (!mStatus.isEmpty()) {
                    results.add(new SearchResult<>(a, "Status", a.getStatus(), mStatus, highlightMatches(a.getStatus(), mStatus, query.length()), 100.0));
                    continue;
                }
                List<Integer> mDate = matchPattern(a.getAppointmentDate(), query, method);
                if (!mDate.isEmpty()) {
                    results.add(new SearchResult<>(a, "Date", a.getAppointmentDate(), mDate, highlightMatches(a.getAppointmentDate(), mDate, query.length()), 100.0));
                    continue;
                }
                List<Integer> mDoc = matchPattern(doctorName, query, method);
                if (!mDoc.isEmpty()) {
                    results.add(new SearchResult<>(a, "Doctor", doctorName, mDoc, highlightMatches(doctorName, mDoc, query.length()), 100.0));
                    continue;
                }
                List<Integer> mPat = matchPattern(patientName, query, method);
                if (!mPat.isEmpty()) {
                    results.add(new SearchResult<>(a, "Patient", patientName, mPat, highlightMatches(patientName, mPat, query.length()), 100.0));
                }
            }
        }
        return results;
    }

    public static List<SearchResult<MedicalRecord>> searchMedicalRecords(List<MedicalRecord> records, String query, SearchMethod method) {
        List<SearchResult<MedicalRecord>> results = new ArrayList<>();
        if (records == null || query == null || query.isBlank()) return results;
        query = query.trim();

        for (MedicalRecord r : records) {
            if (method == SearchMethod.FUZZY_LEVENSHTEIN) {
                double simDiag = bestWordSimilarity(r.getDiagnosis(), query);
                double simNotes = bestWordSimilarity(r.getNotes(), query);
                double maxSim = Math.max(simDiag, simNotes);
                if (maxSim >= 60.0 || fuzzyWordMatch(r.getDiagnosis(), query, 2) || fuzzyWordMatch(r.getNotes(), query, 2)) {
                    results.add(new SearchResult<>(r, "Diagnosis & Notes", r.getDiagnosis() + ": " + r.getNotes(), List.of(0), r.getDiagnosis(), Math.max(maxSim, 60.0)));
                }
            } else {
                List<Integer> mDiag = matchPattern(r.getDiagnosis(), query, method);
                if (!mDiag.isEmpty()) {
                    results.add(new SearchResult<>(r, "Diagnosis", r.getDiagnosis(), mDiag, highlightMatches(r.getDiagnosis(), mDiag, query.length()), 100.0));
                    continue;
                }
                List<Integer> mNotes = matchPattern(r.getNotes(), query, method);
                if (!mNotes.isEmpty()) {
                    results.add(new SearchResult<>(r, "Clinical Notes", r.getNotes(), mNotes, highlightMatches(r.getNotes(), mNotes, query.length()), 100.0));
                }
            }
        }
        return results;
    }

    // =========================================================================
    // BENCHMARK MODE: Runs all algorithms on sample text and compares performance
    // =========================================================================
    public static void runBenchmarkComparison(String corpus, String pattern) {
        System.out.println("\n----------------------------------------------------------------------------------");
        System.out.println("  STRING MATCHING ALGORITHM BENCHMARK & PERFORMANCE COMPARISON");
        System.out.println("  Pattern: \"" + pattern + "\" | Corpus Length: " + corpus.length() + " chars");
        System.out.println("----------------------------------------------------------------------------------");
        System.out.printf("%-18s | %-12s | %-15s | %-20s\n", "Algorithm", "Matches", "Time (us)", "Time Complexity");
        System.out.println("----------------------------------------------------------------------------------");

        // Warm up
        for (int i = 0; i < 50; i++) {
            kmpSearch(corpus, pattern);
            rabinKarpSearch(corpus, pattern);
            boyerMooreSearch(corpus, pattern);
            zAlgorithmSearch(corpus, pattern);
        }

        // KMP
        long start = System.nanoTime();
        List<Integer> kmpMatches = kmpSearch(corpus, pattern);
        long kmpTime = (System.nanoTime() - start) / 1000;
        System.out.printf("%-18s | %-12d | %-15d | %-20s\n", "1. KMP", kmpMatches.size(), kmpTime, "O(N + M) linear");

        // Rabin-Karp
        start = System.nanoTime();
        List<Integer> rkMatches = rabinKarpSearch(corpus, pattern);
        long rkTime = (System.nanoTime() - start) / 1000;
        System.out.printf("%-18s | %-12d | %-15d | %-20s\n", "2. Rabin-Karp", rkMatches.size(), rkTime, "O(N + M) avg, O(NM) wst");

        // Boyer-Moore
        start = System.nanoTime();
        List<Integer> bmMatches = boyerMooreSearch(corpus, pattern);
        long bmTime = (System.nanoTime() - start) / 1000;
        System.out.printf("%-18s | %-12d | %-15d | %-20s\n", "3. Boyer-Moore", bmMatches.size(), bmTime, "O(N/M) best, O(N+M) avg");

        // Z-Algorithm
        start = System.nanoTime();
        List<Integer> zMatches = zAlgorithmSearch(corpus, pattern);
        long zTime = (System.nanoTime() - start) / 1000;
        System.out.printf("%-18s | %-12d | %-15d | %-20s\n", "4. Z-Algorithm", zMatches.size(), zTime, "O(N + M) linear");

        // Aho-Corasick
        start = System.nanoTime();
        Map<String, List<Integer>> acMap = ahoCorasickSearch(corpus, List.of(pattern));
        long acTime = (System.nanoTime() - start) / 1000;
        int acCount = acMap.getOrDefault(pattern, Collections.emptyList()).size();
        System.out.printf("%-18s | %-12d | %-15d | %-20s\n", "5. Aho-Corasick", acCount, acTime, "O(N + sum(M)) trie");

        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Match Positions: " + kmpMatches);
        System.out.println("----------------------------------------------------------------------------------");
    }
}
