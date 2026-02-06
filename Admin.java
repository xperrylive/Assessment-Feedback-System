/**
 * Admin - Manages all users, classes, and grading system
 * Schema: ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
 * For Admin, SupervisorID is "N/A"
 */
public class Admin extends User {
    
    public Admin(String id, String username, String password, 
                 String fullName, String gender, String email, String phone, int age) {
        super(id, username, password, "Admin", fullName, gender, email, phone, age);
    }
    
    @Override
    public String getSpecificInfo() {
        return "Administrator - Full System Access";
    }
    
    /**
     * Converts Admin object to file format
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
            "N/A"  // SupervisorID not applicable for Admin
        };
    }
    
    /**
     * Creates an Admin object from file data
     * @param data Array of fields from file
     * @return Admin object
     */
    public static Admin fromFileFormat(String[] data) {
        return new Admin(
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
