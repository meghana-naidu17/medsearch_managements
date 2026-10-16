import java.util.*;

public class ActivitySelection {

    public static class Slot {
        public final int id;
        public final String title;
        public final int startHour;
        public final int endHour;

        public Slot(int id, String title, int startHour, int endHour) {
            this.id = id;
            this.title = title;
            this.startHour = startHour;
            this.endHour = endHour;
        }
    }

    public static void selectAppointments() {
        System.out.println("\n==========================================================");
        System.out.println("   GREEDY ACTIVITY SELECTION - OPTIMAL APPOINTMENT SLOTS");
        System.out.println("==========================================================");
        System.out.println("Maximizing non-overlapping medical consultation slots for Dr. Arun (Cardiology):\n");

        List<Slot> candidateSlots = new ArrayList<>();
        candidateSlots.add(new Slot(101, "Patient Consultation - Hypertension Review", 9, 10));
        candidateSlots.add(new Slot(102, "Cardiac Echocardiogram Evaluation", 9, 11));
        candidateSlots.add(new Slot(103, "Routine Blood Pressure & ECG Check", 10, 11));
        candidateSlots.add(new Slot(104, "Emergency Ward Rounds", 11, 13));
        candidateSlots.add(new Slot(105, "Heart Disease Follow-up Consultation", 12, 13));
        candidateSlots.add(new Slot(106, "Post-Operative Angioplasty Review", 13, 14));
        candidateSlots.add(new Slot(107, "Telemetry & Holter Monitor Analysis", 14, 16));
        candidateSlots.add(new Slot(108, "Outpatient Preventive Cardiology", 15, 16));

        // Sort greedily by finish time (endHour)
        candidateSlots.sort(Comparator.comparingInt(s -> s.endHour));

        System.out.printf("%-6s | %-16s | %-45s\n", "SLOT", "TIME WINDOW", "APPOINTMENT DESCRIPTION");
        System.out.println("-------------------------------------------------------------------------");

        List<Slot> selected = new ArrayList<>();
        if (!candidateSlots.isEmpty()) {
            Slot first = candidateSlots.get(0);
            selected.add(first);
            int lastEnd = first.endHour;

            for (int i = 1; i < candidateSlots.size(); i++) {
                Slot current = candidateSlots.get(i);
                if (current.startHour >= lastEnd) {
                    selected.add(current);
                    lastEnd = current.endHour;
                }
            }
        }

        for (Slot s : selected) {
            System.out.printf("#%-5d | %02d:00 - %02d:00   | %-45s\n", s.id, s.startHour, s.endHour, s.title);
        }

        System.out.println("-------------------------------------------------------------------------");
        System.out.printf("Greedy Choice Property: Selected %d maximum compatible appointments out of %d candidates.\n",
                selected.size(), candidateSlots.size());
        System.out.println("Algorithm Time Complexity: O(N log N) for sorting + O(N) linear selection.\n");
    }
}
