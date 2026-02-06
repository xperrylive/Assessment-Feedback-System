/**
 * User - Abstract base class for all user types
 * Attributes: ID, Username, Password, FullName, Role, Gender, Email, Phone, Age
 */
public abstract class User {
    
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String gender;
    private String email;
    private String phone;
    private int age;
    
    // Constructor
    public User(String id, String username, String password, String role, 
                String fullName, String gender, String email, String phone, int age) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.gender = gender;
        this.email = email;
        this.phone = phone;
        this.age = age;
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public String getRole() {
        return role;
    }
    
    public String getGender() {
        return gender;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public int getAge() {
        return age;
    }
    
    // Setters
    public void setId(String id) {
        this.id = id;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    // Abstract method to be implemented by subclasses
    public abstract String getSpecificInfo();
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", age=" + age +
                '}';
    }
}
