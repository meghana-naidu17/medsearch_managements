public class Doctor {
    private int doctorId; private String name,specialization,department,availability,password;
    public Doctor(int doctorId,String name,String specialization,String department,String availability,String password){this.doctorId=doctorId;this.name=name;this.specialization=specialization;this.department=department;this.availability=availability;this.password=password;}
    public int getDoctorId(){return doctorId;} public String getName(){return name;} public String getSpecialization(){return specialization;} public String getDepartment(){return department;} public String getAvailability(){return availability;} public String getPassword(){return password;}
    public void display(){System.out.println("Doctor ID     : "+doctorId);System.out.println("Name          : "+name);System.out.println("Specialization: "+specialization);System.out.println("Department    : "+department);System.out.println("Availability  : "+availability);}
}
