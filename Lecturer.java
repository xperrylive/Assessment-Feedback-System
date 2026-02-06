/**
 * Lecturer - Manages exactly ONE module, adds assessments, inputs marks
 * Schema: ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
 * For Lecturer, SupervisorID contains the Academic Leader's ID
 */
public class Lecturer extends User {
    
    private String supervisorId;  // Academic Leader ID
    
    public Lecturer(String id, String username, String password, 
                    String fullName, String gender, String email, String phone, 
                    int age, String supervisorId) {
        super(id, username, password, "Lecturer", fullName, gender, email, phone, age);
        this.supervisorId = supervisorId;
    }
    
    // Getter and Setter for supervisorId
    public String getSupervisorId() {
        return supervisorId;
    }
    
    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }
    
    @Override
    public String getSpecificInfo() {
        return "Lecturer - Supervised by Academic Leader: " + supervisorId;
    }
    
    /**
     * Converts Lecturer object to file format
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
            supervisorId
        };
    }
    
    /**
     * Creates a Lecturer object from file data
     * @param data Array of fields from file
     * @return Lecturer object
     */
    public static Lecturer fromFileFormat(String[] data) {
        return new Lecturer(
            data[0],  // ID
            data[1],  // Username
            data[2],  // Password
            data[4],  // FullName
            data[5],  // Gender
            data[6],  // Email
            data[7],  // Phone
            Integer.parseInt(data[8]),  // Age
            data[9]   // SupervisorID
        );
    }
}
