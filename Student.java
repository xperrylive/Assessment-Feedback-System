/**
 * Student - Enrolls in multiple classes, views results and feedback
 * Schema: ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
 * For Student, SupervisorID is "N/A"
 */
public class Student extends User {
    
    public Student(String id, String username, String password, 
                   String fullName, String gender, String email, String phone, int age) {
        super(id, username, password, "Student", fullName, gender, email, phone, age);
    }
    
    @Override
    public String getSpecificInfo() {
        return "Student - Can enroll in classes and view results";
    }
    
    /**
     * Converts Student object to file format
     * @return String array ready for file writing
     */
    public String[] toFileFormat() {
        return new String[] {
            getId(),
            getUsername(),
            getPassword(),
            getRole(),
            getFullName(),
            getGender(),
            getEmail(),
            getPhone(),
            String.valueOf(getAge()),
            "N/A"  // SupervisorID not applicable for Student
        };
    }
    
    /**
     * Creates a Student object from file data
     * @param data Array of fields from file
     * @return Student object
     */
    public static Student fromFileFormat(String[] data) {
        return new Student(
            data[0],  // ID
            data[1],  // Username
            data[2],  // Password
            data[4],  // FullName
            data[5],  // Gender
            data[6],  // Email
            data[7],  // Phone
            Integer.parseInt(data[8])  // Age
        );
    }
}
