/**
 * Session - Manages the currently logged-in user
 * Singleton pattern to ensure only one user is logged in at a time
 */
public class Session {
    
    private static Session instance;
    private User currentUser;
    
    // Private constructor for singleton
    private Session() {
        this.currentUser = null;
    }
    
    /**
     * Gets the singleton instance
     * @return Session instance
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    /**
     * Sets the current logged-in user
     * @param user User object
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Gets the current logged-in user
     * @return Current user or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Gets the current user's ID
     * @return User ID or null if not logged in
     */
    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
    
    /**
     * Gets the current user's role
     * @return User role or null if not logged in
     */
    public String getCurrentUserRole() {
        return currentUser != null ? currentUser.getRole() : null;
    }
    
    /**
     * Checks if current user has a specific role
     * @param role Role to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return currentUser != null && currentUser.getRole().equals(role);
    }
}
