package leader;

import common.User;

public class AcademicLeader extends User {
    public AcademicLeader() {
        super();
    }

    public AcademicLeader(String id, String password, String role, String fullName, 
                          String gender, String email, String phone, String age, String dob) {
        super(id, password, role, fullName, gender, email, phone, age, dob);
    }

    @Override
    public String[] toFileFormat() {
        return new String[]{id, password, role, fullName, gender, email, phone, age, dob};
    }
}
