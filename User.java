public class User {
    private int patientId;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String password;

    public User(int patientId, String name, int age, String gender, String phone, String password) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.phone = phone;
        this.password = password;
    }

    public int getPatientId() { return patientId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    public void display() {
        System.out.println("Patient ID : " + patientId);
        System.out.println("Name       : " + name);
        System.out.println("Age        : " + age);
        System.out.println("Gender     : " + gender);
        System.out.println("Phone      : " + phone);
    }
}
