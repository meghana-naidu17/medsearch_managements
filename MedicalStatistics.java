import java.util.*;

public class MedicalStatistics {

    public void show() {
        DatasetStore store = DatasetStore.getInstance();
        List<User> patients = store.getPatients();
        List<Doctor> doctors = store.getDoctors();
        List<Appointment> apps = store.getAppointments();
        List<MedicalRecord> records = store.getMedicalRecords();

        int totalPatients = patients.size();
        int totalDoctors = doctors.size();
        int totalApps = apps.size();
        int totalRecords = records.size();

        // Safe SegmentTree Demonstration
        int[] dailyAppVolume = new int[Math.max(1, totalApps)];
        for (int i = 0; i < totalApps; i++) {
            dailyAppVolume[i] = 1;
        }
        SegmentTree segTree = new SegmentTree(dailyAppVolume);
        int segSum = (totalApps > 0) ? segTree.getRangeSum(0, totalApps - 1) : 0;

        System.out.println("\n========================================================");
        System.out.println("           MEDSEARCH ADVANCED MEDICAL STATISTICS");
        System.out.println("========================================================");
        System.out.printf("  Total Patients Registered : %d\n", totalPatients);
        System.out.printf("  Total Doctors on Board   : %d\n", totalDoctors);
        System.out.printf("  Total Appointments Booked : %d (SegmentTree Sum: %d)\n", totalApps, segSum);
        System.out.printf("  Total Clinical Records    : %d\n", totalRecords);
        System.out.println("--------------------------------------------------------");

        // 1. Gender Demographics
        long maleCount = patients.stream().filter(p -> p.getGender().equalsIgnoreCase("Male")).count();
        long femaleCount = patients.stream().filter(p -> p.getGender().equalsIgnoreCase("Female")).count();
        double avgAge = patients.stream().mapToInt(User::getAge).average().orElse(0.0);
        System.out.printf("  Patient Demographics      : %d Males, %d Females | Avg Age: %.1f yrs\n", maleCount, femaleCount, avgAge);

        // 2. Appointment Status Distribution
        Map<String, Integer> statusMap = new LinkedHashMap<>();
        for (Appointment a : apps) {
            statusMap.put(a.getStatus(), statusMap.getOrDefault(a.getStatus(), 0) + 1);
        }
        System.out.println("  Appointment Statuses      : " + statusMap);

        // 3. Doctors by Department
        Map<String, Integer> deptMap = new LinkedHashMap<>();
        for (Doctor d : doctors) {
            deptMap.put(d.getDepartment(), deptMap.getOrDefault(d.getDepartment(), 0) + 1);
        }
        System.out.println("  Active Departments        : " + deptMap.size() + " distinct departments");

        // 4. Most Common Diagnoses
        Map<String, Integer> diagMap = new LinkedHashMap<>();
        for (MedicalRecord r : records) {
            diagMap.put(r.getDiagnosis(), diagMap.getOrDefault(r.getDiagnosis(), 0) + 1);
        }
        System.out.println("  Clinical Conditions       : " + diagMap.size() + " unique diagnoses recorded");

        // 5. SegmentTree Range Query Demonstration
        if (totalApps >= 5) {
            int rangeSum = segTree.getRangeSum(0, 4);
            System.out.printf("  SegmentTree Range Query   : Sum for appointments [0 to 4] = %d\n", rangeSum);
        }
        System.out.println("========================================================\n");
    }
}
