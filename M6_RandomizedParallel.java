import java.util.*;

public class M6_RandomizedParallel {

    public static List<Integer> reservoirSample(List<Integer> data, int k) {
        Random r = new Random();
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (i < k) res.add(data.get(i));
            else {
                int j = r.nextInt(i + 1);
                if (j < k) res.set(j, data.get(i));
            }
        }
        return res;
    }

    public static void randomizedQuickSort(int[] a) {
        if (a == null || a.length <= 1) return;
        quick(a, 0, a.length - 1, new Random());
    }

    private static void quick(int[] a, int l, int r, Random rnd) {
        if (l >= r) return;
        int p = l + rnd.nextInt(r - l + 1);
        int x = a[p], i = l, j = r;
        while (i <= j) {
            while (a[i] < x) i++;
            while (a[j] > x) j--;
            if (i <= j) {
                int t = a[i];
                a[i++] = a[j];
                a[j--] = t;
            }
        }
        quick(a, l, j, rnd);
        quick(a, i, r, rnd);
    }

    public static boolean millerRabin(long n) {
        if (n < 2) return false;
        for (int p : new int[]{2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37}) {
            if (n % p == 0) return n == p;
        }
        long d = n - 1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }
        for (long a : new long[]{2, 3, 5, 7}) {
            if (a >= n) continue;
            long x = pow(a, d, n);
            if (x == 1 || x == n - 1) continue;
            boolean ok = false;
            for (int r = 1; r < s; r++) {
                x = mul(x, x, n);
                if (x == n - 1) {
                    ok = true;
                    break;
                }
            }
            if (!ok) return false;
        }
        return true;
    }

    private static long mul(long a, long b, long m) {
        return (a * b) % m;
    }

    private static long pow(long a, long d, long m) {
        long r = 1;
        while (d > 0) {
            if ((d & 1) == 1) r = mul(r, a, m);
            a = mul(a, a, m);
            d >>= 1;
        }
        return r;
    }

    public static long parallelCountWords(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        String[] words = text.trim().split("\\s+");
        return Arrays.stream(words).parallel().count();
    }

    public static void run() {
        System.out.println("\n========================================================");
        System.out.println("     M6 RANDOMIZED & PARALLEL ALGORITHMS SUITE");
        System.out.println("========================================================");

        List<Integer> patientIds = new ArrayList<>();
        for (User u : DatasetStore.getInstance().getPatients()) {
            patientIds.add(u.getPatientId());
        }
        if (patientIds.size() < 5) {
            for (int i = 101; i <= 125; i++) patientIds.add(i);
        }

        List<Integer> sample = reservoirSample(patientIds, 5);
        System.out.println("1. Reservoir Sampling (Uniform random sample of 5 patients from dataset):");
        System.out.println("   Sampled Patient IDs: " + sample);

        int[] unsortedIds = {125, 101, 118, 104, 110, 102, 115, 109};
        System.out.println("\n2. Las Vegas Randomized QuickSort:");
        System.out.println("   Original Array: " + Arrays.toString(unsortedIds));
        randomizedQuickSort(unsortedIds);
        System.out.println("   Sorted Patient IDs: " + Arrays.toString(unsortedIds));

        long primeCandidate = 104729;
        System.out.println("\n3. Miller-Rabin Primality Test for Medical Data Encryption Key (" + primeCandidate + "):");
        System.out.println("   Verification Result: " + (millerRabin(primeCandidate) ? "PROBABLE PRIME (Secure)" : "COMPOSITE"));

        String clinicalCorpus = "patient diagnosed with hypertensive cardiovascular condition and severe bronchial inflammation undergoing pharmacotherapy with bronchodilator";
        long wordCount = parallelCountWords(clinicalCorpus);
        System.out.println("\n4. ForkJoin/Stream Parallel Word Processing on Clinical Record:");
        System.out.println("   Processed Words in Parallel: " + wordCount + " words");
        System.out.println("========================================================\n");
    }
}
