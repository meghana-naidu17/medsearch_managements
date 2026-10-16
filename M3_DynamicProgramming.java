import java.util.*;

public class M3_DynamicProgramming {

    public static int levenshtein(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) d[i][0] = i;
        for (int j = 0; j <= b.length(); j++) d[0][j] = j;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (Character.toLowerCase(a.charAt(i - 1)) == Character.toLowerCase(b.charAt(j - 1))) ? 0 : 1;
                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);
            }
        }
        return d[a.length()][b.length()];
    }

    public static int needlemanWunsch(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        int[][] d = new int[a.length() + 1][b.length() + 1];
        for (int i = 1; i <= a.length(); i++) d[i][0] = d[i - 1][0] - 2;
        for (int j = 1; j <= b.length(); j++) d[0][j] = d[0][j - 1] - 2;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                boolean match = Character.toLowerCase(a.charAt(i - 1)) == Character.toLowerCase(b.charAt(j - 1));
                int score = d[i - 1][j - 1] + (match ? 2 : -1);
                d[i][j] = Math.max(score, Math.max(d[i - 1][j] - 2, d[i][j - 1] - 2));
            }
        }
        return d[a.length()][b.length()];
    }

    public static int smithWaterman(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        int[][] d = new int[a.length() + 1][b.length() + 1];
        int best = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                boolean match = Character.toLowerCase(a.charAt(i - 1)) == Character.toLowerCase(b.charAt(j - 1));
                int score = d[i - 1][j - 1] + (match ? 2 : -1);
                int v = Math.max(0, Math.max(score, Math.max(d[i - 1][j] - 2, d[i][j - 1] - 2)));
                d[i][j] = v;
                best = Math.max(best, v);
            }
        }
        return best;
    }

    public static void run(Scanner sc) {
        System.out.println("\n===== M3 ADVANCED DYNAMIC PROGRAMMING =====");
        System.out.print("Enter medical query/term: ");
        String a = sc.nextLine().trim();
        System.out.print("Enter possible dictionary term: ");
        String b = sc.nextLine().trim();

        int lev = levenshtein(a, b);
        double sim = MedicalSearchEngine.calculateSimilarity(a, b);
        System.out.println("-> Levenshtein Edit Distance  = " + lev);
        System.out.printf("-> String Similarity Match   = %.1f%%\n", sim);

        System.out.print("\nEnter genomic / biomarker sequence 1 (e.g. ATGCGATCG): ");
        String seq1 = sc.nextLine().trim();
        System.out.print("Enter genomic / biomarker sequence 2 (e.g. ATCGGATAG): ");
        String seq2 = sc.nextLine().trim();

        if (seq1.isEmpty()) seq1 = "ATGCGATCG";
        if (seq2.isEmpty()) seq2 = "ATCGGATAG";

        System.out.println("-> Needleman-Wunsch Global Alignment Score = " + needlemanWunsch(seq1, seq2));
        System.out.println("-> Smith-Waterman Local Alignment Score    = " + smithWaterman(seq1, seq2));
    }
}
