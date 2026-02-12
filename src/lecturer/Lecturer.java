package lecturer;

import common.User;

public class Lecturer extends User {
    private String supervisorId;

    public Lecturer() {
        super();
    }

    public Lecturer(String id, String password, String role, String fullName, 
                    String gender, String email, String phone, String age, String dob) {
        super(id, password, role, fullName, gender, email, phone, age, dob);
    }

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }

    @Override
    public String[] toFileFormat() {
        if (supervisorId != null && !supervisorId.isEmpty()) {
            return new String[]{id, password, role, fullName, gender, email, phone, age, dob, supervisorId};
        }
        return new String[]{id, password, role, fullName, gender, email, phone, age, dob};
    }
}
