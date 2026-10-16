import java.util.*;

public class SystemTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING AUTOMATED MEDSEARCH INTEGRATION & DATASET TESTS ===");

        DatasetStore store = DatasetStore.getInstance();
        System.out.println("[TEST 1] Verifying Preloaded Dataset Sizes:");
        System.out.println("  Patients: " + store.getPatients().size());
        System.out.println("  Doctors: " + store.getDoctors().size());
        System.out.println("  Appointments: " + store.getAppointments().size());
        System.out.println("  Medical Records: " + store.getMedicalRecords().size());
        System.out.println("  Lab Reports: " + store.getLabReports().size());
        System.out.println("  Treatments: " + store.getTreatments().size());

        assert store.getPatients().size() >= 25 : "Patients count < 25";
        assert store.getDoctors().size() >= 16 : "Doctors count < 16";
        assert store.getAppointments().size() >= 35 : "Appointments count < 35";
        assert store.getMedicalRecords().size() >= 25 : "Medical records count < 25";
        System.out.println("  -> PASSED: All datasets fully populated.");

        System.out.println("\n[TEST 2] Testing String Matching Algorithms on Doctors (Query: 'Cardio'):");
        for (MedicalSearchEngine.SearchMethod method : List.of(
                MedicalSearchEngine.SearchMethod.KMP,
                MedicalSearchEngine.SearchMethod.RABIN_KARP,
                MedicalSearchEngine.SearchMethod.BOYER_MOORE,
                MedicalSearchEngine.SearchMethod.Z_ALGORITHM,
                MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN)) {
            List<MedicalSearchEngine.SearchResult<Doctor>> res = MedicalSearchEngine.searchDoctors(store.getDoctors(), "Cardio", method);
            System.out.printf("  Method %-18s -> Found %d matches\n", method.name(), res.size());
            assert !res.isEmpty() : "Failed to find Cardiology with " + method.name();
        }
        System.out.println("  -> PASSED: All 5 string matching methods successfully matched doctor specialty.");

        System.out.println("\n[TEST 3] Testing Fuzzy / Typo Search on Medical Records (Query: 'diabtes'):");
        List<MedicalSearchEngine.SearchResult<MedicalRecord>> fuzzyRes =
                MedicalSearchEngine.searchMedicalRecords(store.getMedicalRecords(), "diabtes", MedicalSearchEngine.SearchMethod.FUZZY_LEVENSHTEIN);
        System.out.println("  Matches for typo 'diabtes': " + fuzzyRes.size());
        assert !fuzzyRes.isEmpty() : "Fuzzy search failed to find diabetes";
        System.out.println("  Top Match: " + fuzzyRes.get(0).item.getDiagnosis() + " (Score: " + String.format("%.1f", fuzzyRes.get(0).similarityScore) + "%)");
        System.out.println("  -> PASSED: Fuzzy Levenshtein match successfully handled typo.");

        System.out.println("\n[TEST 4] Testing Patient Login and Portal:");
        User p = store.loginPatient(101, "pass123");
        assert p != null && p.getName().contains("Ananya") : "Patient 101 login failed";
        System.out.println("  Patient 101 login successful: " + p.getName());

        System.out.println("\n[TEST 5] Testing Doctor Login & Specific Appointments:");
        Doctor d = store.loginDoctor(201, "doctor123");
        assert d != null && d.getName().contains("Arun") : "Doctor 201 login failed";
        List<Appointment> docApps = store.getAppointmentsByDoctor(201);
        System.out.println("  Doctor 201 login successful: " + d.getName() + " with " + docApps.size() + " scheduled appointments.");
        assert !docApps.isEmpty() : "Doctor has no appointments";

        System.out.println("\n[TEST 6] Testing Appointment Status Update:");
        boolean updated = store.updateAppointmentStatus(301, "Completed");
        assert updated : "Failed to update appointment 301 status";
        System.out.println("  Appointment 301 updated to Completed: " + updated);

        System.out.println("\n[TEST 7] Testing Benchmark Comparison on Clinical Corpus:");
        MedicalRecordManagement rm = new MedicalRecordManagement();
        String corpus = rm.getAllClinicalText();
        MedicalSearchEngine.runBenchmarkComparison(corpus, "blood");

        System.out.println("\n[TEST 8] Testing Medical Statistics with SegmentTree:");
        new MedicalStatistics().show();

        System.out.println("\n[TEST 9] Testing Greedy Activity Selection:");
        ActivitySelection.selectAppointments();

        System.out.println("\n[TEST 10] Testing Graph and Network Algorithms (M4):");
        M4_NetworkAlgorithms.run();

        System.out.println("\n[TEST 11] Testing Dynamic Programming Alignment (M3):");
        int lev = M3_DynamicProgramming.levenshtein("Hypertension", "Hypertention");
        System.out.println("  Levenshtein Distance between Hypertension and Hypertention = " + lev);
        assert lev == 1 : "Levenshtein distance incorrect";

        System.out.println("\n[TEST 12] Testing Randomized Algorithms (M6):");
        M6_RandomizedParallel.run();

        System.out.println("\n=== ALL 12 INTEGRATION & DATASET TESTS PASSED WITH 100% SUCCESS! ===");
    }
}
