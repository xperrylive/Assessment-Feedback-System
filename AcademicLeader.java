/**
 * AcademicLeader - Manages multiple lecturers and modules
 * Schema: ID|Username|Password|Role|Name|Gender|Email|Phone|Age|SupervisorID
 * For Academic Leader, SupervisorID is "N/A"
 */
public class AcademicLeader extends User {
    
    public AcademicLeader(String id, String username, String password, 
                          String fullName, String gender, String email, String phone, int age) {
        super(id, username, password, "AcademicLeader", fullName, gender, email, phone, age);
    }
    
    @Override
    public String getSpecificInfo() {
        return "Academic Leader - Manages Lecturers and Modules";
    }
    
    /**
     * Converts AcademicLeader object to file format
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
            "N/A"  // SupervisorID not applicable for Academic Leader
        };
    }
    
    /**
     * Creates an AcademicLeader object from file data
     * @param data Array of fields from file
     * @return AcademicLeader object
     */
    public static AcademicLeader fromFileFormat(String[] data) {
        return new AcademicLeader(
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
