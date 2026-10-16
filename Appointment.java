public class Appointment {
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private String appointmentDate;
    private String status;
    public Appointment(int appointmentId,int patientId,int doctorId,String appointmentDate,String status){this.appointmentId=appointmentId;this.patientId=patientId;this.doctorId=doctorId;this.appointmentDate=appointmentDate;this.status=status;}
    public int getAppointmentId(){return appointmentId;} public int getPatientId(){return patientId;} public int getDoctorId(){return doctorId;} public String getAppointmentDate(){return appointmentDate;} public String getStatus(){return status;}
    public void display(){System.out.println("Appointment ID : "+appointmentId);System.out.println("Patient ID     : "+patientId);System.out.println("Doctor ID      : "+doctorId);System.out.println("Date           : "+appointmentDate);System.out.println("Status         : "+status);}
}
