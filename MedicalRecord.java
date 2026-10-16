public class MedicalRecord {
    private int recordId, patientId, doctorId;
    private String diagnosis, notes, recordDate;
    public MedicalRecord(int recordId,int patientId,int doctorId,String diagnosis,String notes,String recordDate){this.recordId=recordId;this.patientId=patientId;this.doctorId=doctorId;this.diagnosis=diagnosis;this.notes=notes;this.recordDate=recordDate;}
    public int getRecordId(){return recordId;} public int getPatientId(){return patientId;} public int getDoctorId(){return doctorId;} public String getDiagnosis(){return diagnosis;} public String getNotes(){return notes;} public String getRecordDate(){return recordDate;}
    public void display(){System.out.println("Record ID : "+recordId);System.out.println("Patient ID: "+patientId);System.out.println("Doctor ID : "+doctorId);System.out.println("Diagnosis : "+diagnosis);System.out.println("Notes     : "+notes);System.out.println("Date      : "+recordDate);}
}
