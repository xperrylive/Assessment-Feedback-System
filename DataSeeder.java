/**
 * DataSeeder - Auto-generates sample data if files are empty
 * Creates:
 * - 1 Admin
 * - 3 Academic Leaders
 * - 9 Lecturers (3 per Academic Leader)
 * - 15 Students
 * - Sample Modules, Classes, Assessments, Results
 * - Default Grading System
 */
public class DataSeeder {
    
    private static final String USERS_FILE = "users.txt";
    private static final String MODULES_FILE = "modules.txt";
    private static final String CLASSES_FILE = "classes.txt";
    private static final String ASSESSMENTS_FILE = "assessments.txt";
    private static final String RESULTS_FILE = "results.txt";
    private static final String GRADING_FILE = "grading.txt";
    
    /**
     * Seeds all data if users.txt is empty
     */
    public static void seedIfEmpty() {
        if (FileHandler.readFile(USERS_FILE).isEmpty()) {
            System.out.println("Seeding initial data...");
            seedUsers();
            seedGradingSystem();
            seedModules();
            seedClasses();
            seedAssessments();
            seedResults();
            System.out.println("Data seeding completed!");
        }
    }
    
    /**
     * Seeds users: 1 Admin, 3 Academic Leaders, 9 Lecturers, 15 Students
     */
    private static void seedUsers() {
        // Admin
        FileHandler.appendLine(USERS_FILE, 
            "ADM001", "admin", "admin123", "Admin", "System Administrator", 
            "Male", "admin@university.edu", "+60123456789", "35", "N/A");
        
        // Academic Leaders (3)
        String[] leaderIds = {"AL001", "AL002", "AL003"};
        String[] leaderNames = {"Dr. Sarah Johnson", "Dr. Michael Chen", "Dr. Aisha Rahman"};
        String[] leaderGenders = {"Female", "Male", "Female"};
        String[] leaderEmails = {"sarah.johnson@university.edu", "michael.chen@university.edu", "aisha.rahman@university.edu"};
        String[] leaderPhones = {"+60123456701", "+60123456702", "+60123456703"};
        
        for (int i = 0; i < 3; i++) {
            FileHandler.appendLine(USERS_FILE,
                leaderIds[i], "leader" + (i+1), "pass123", "AcademicLeader", leaderNames[i],
                leaderGenders[i], leaderEmails[i], leaderPhones[i], String.valueOf(40 + i), "N/A");
        }
        
        // Lecturers (9 - 3 per Academic Leader)
        String[] lecturerNames = {
            "Dr. James Wilson", "Dr. Emily Taylor", "Dr. David Brown",
            "Dr. Lisa Anderson", "Dr. Robert Martinez", "Dr. Jennifer Garcia",
            "Dr. William Lee", "Dr. Maria Rodriguez", "Dr. Christopher White"
        };
        String[] lecturerGenders = {"Male", "Female", "Male", "Female", "Male", "Female", "Male", "Female", "Male"};
        
        int lecturerCounter = 1;
        for (int leader = 0; leader < 3; leader++) {
            for (int lec = 0; lec < 3; lec++) {
                int index = leader * 3 + lec;
                String lecId = String.format("LEC%03d", lecturerCounter);
                FileHandler.appendLine(USERS_FILE,
                    lecId, "lecturer" + lecturerCounter, "pass123", "Lecturer", lecturerNames[index],
                    lecturerGenders[index], "lecturer" + lecturerCounter + "@university.edu", 
                    "+6012345" + String.format("%04d", 6800 + lecturerCounter), String.valueOf(30 + lecturerCounter),
                    leaderIds[leader]);
                lecturerCounter++;
            }
        }
        
        // Students (15)
        String[] studentNames = {
            "Alice Tan", "Bob Lee", "Charlie Wong", "Diana Lim", "Edward Ng",
            "Fiona Yap", "George Ong", "Hannah Teo", "Ian Chua", "Jessica Koh",
            "Kevin Tan", "Laura Sim", "Marcus Goh", "Nancy Chan", "Oliver Lim"
        };
        String[] studentGenders = {
            "Female", "Male", "Male", "Female", "Male",
            "Female", "Male", "Female", "Male", "Female",
            "Male", "Female", "Male", "Female", "Male"
        };
        
        for (int i = 0; i < 15; i++) {
            String stuId = String.format("STU%04d", i + 1);
            FileHandler.appendLine(USERS_FILE,
                stuId, "student" + (i+1), "pass123", "Student", studentNames[i],
                studentGenders[i], "student" + (i+1) + "@student.university.edu",
                "+6012345" + String.format("%04d", 7000 + i), String.valueOf(20 + (i % 5)), "N/A");
        }
    }
    
    /**
     * Seeds default grading system (A+ to F-)
     */
    private static void seedGradingSystem() {
        FileHandler.appendLine(GRADING_FILE, "A+", "90", "100");
        FileHandler.appendLine(GRADING_FILE, "A", "85", "89");
        FileHandler.appendLine(GRADING_FILE, "A-", "80", "84");
        FileHandler.appendLine(GRADING_FILE, "B+", "75", "79");
        FileHandler.appendLine(GRADING_FILE, "B", "70", "74");
        FileHandler.appendLine(GRADING_FILE, "B-", "65", "69");
        FileHandler.appendLine(GRADING_FILE, "C+", "60", "64");
        FileHandler.appendLine(GRADING_FILE, "C", "55", "59");
        FileHandler.appendLine(GRADING_FILE, "C-", "50", "54");
        FileHandler.appendLine(GRADING_FILE, "D+", "45", "49");
        FileHandler.appendLine(GRADING_FILE, "D", "40", "44");
        FileHandler.appendLine(GRADING_FILE, "D-", "35", "39");
        FileHandler.appendLine(GRADING_FILE, "F", "0", "34");
    }
    
    /**
     * Seeds modules (9 modules - 1 per lecturer)
     * Schema: ModuleCode|ModuleName|LeaderID|LecturerID
     */
    private static void seedModules() {
        String[] moduleCodes = {
            "CS101", "CS102", "CS103", "CS201", "CS202", "CS203", "CS301", "CS302", "CS303"
        };
        String[] moduleNames = {
            "Introduction to Programming", "Data Structures", "Database Systems",
            "Web Development", "Software Engineering", "Computer Networks",
            "Artificial Intelligence", "Machine Learning", "Cybersecurity"
        };
        String[] leaderIds = {"AL001", "AL001", "AL001", "AL002", "AL002", "AL002", "AL003", "AL003", "AL003"};
        
        for (int i = 0; i < 9; i++) {
            String lecId = String.format("LEC%03d", i + 1);
            FileHandler.appendLine(MODULES_FILE,
                moduleCodes[i], moduleNames[i], leaderIds[i], lecId);
        }
    }
    
    /**
     * Seeds classes (1 class per module)
     * Schema: ClassID|ModuleCode|IntakeCode
     */
    private static void seedClasses() {
        String[] moduleCodes = {
            "CS101", "CS102", "CS103", "CS201", "CS202", "CS203", "CS301", "CS302", "CS303"
        };
        String[] intakeCodes = {
            "2024-JAN", "2024-JAN", "2024-JAN", "2024-MAY", "2024-MAY", 
            "2024-MAY", "2024-SEP", "2024-SEP", "2024-SEP"
        };
        
        for (int i = 0; i < 9; i++) {
            String classId = "CLS" + String.format("%03d", i + 1);
            FileHandler.appendLine(CLASSES_FILE,
                classId, moduleCodes[i], intakeCodes[i]);
        }
    }
    
    /**
     * Seeds sample assessments
     * Schema: AssessmentID|ModuleCode|Type|Title|MaxMarks
     */
    private static void seedAssessments() {
        // 3 assessments for first 3 modules (to enable report generation)
        String[] modules = {"CS101", "CS102", "CS103"};
        String[] types = {"Quiz", "Assignment", "Exam"};
        String[] titles = {
            "Programming Basics Quiz", "Data Structure Implementation", "Database Final Exam",
            "HTML/CSS Quiz", "Web App Project", "Web Development Exam",
            "AI Concepts Quiz", "ML Algorithm Assignment", "AI Final Exam"
        };
        int[] maxMarks = {20, 40, 100, 20, 40, 100, 20, 40, 100};
        
        int assessmentCounter = 1;
        for (int mod = 0; mod < 3; mod++) {
            for (int assess = 0; assess < 3; assess++) {
                String assessId = "ASS" + String.format("%04d", assessmentCounter);
                int titleIndex = mod * 3 + assess;
                FileHandler.appendLine(ASSESSMENTS_FILE,
                    assessId, modules[mod], types[assess], titles[titleIndex], 
                    String.valueOf(maxMarks[titleIndex]));
                assessmentCounter++;
            }
        }
    }
    
    /**
     * Seeds sample results (minimum 5 results per module for report generation)
     * Schema: ResultID|AssessmentID|StudentID|Marks|Feedback
     */
    private static void seedResults() {
        int resultCounter = 1;
        
        // Generate results for first 3 modules (each has 3 assessments)
        for (int mod = 0; mod < 3; mod++) {
            for (int assess = 0; assess < 3; assess++) {
                String assessId = "ASS" + String.format("%04d", mod * 3 + assess + 1);
                
                // Create 5 results per assessment for first 5 students
                for (int stu = 0; stu < 5; stu++) {
                    String stuId = String.format("STU%04d", stu + 1);
                    String resultId = "RES" + String.format("%05d", resultCounter);
                    
                    // Generate random-ish marks
                    int baseMarks = 50 + (stu * 10) + (mod * 5);
                    String marks = String.valueOf(Math.min(100, baseMarks));
                    
                    String feedback = "Good effort. " + getFeedbackByMarks(Integer.parseInt(marks));
                    
                    FileHandler.appendLine(RESULTS_FILE,
                        resultId, assessId, stuId, marks, feedback);
                    resultCounter++;
                }
            }
        }
    }
    
    /**
     * Generates feedback based on marks
     */
    private static String getFeedbackByMarks(int marks) {
        if (marks >= 85) return "Excellent work!";
        else if (marks >= 70) return "Well done!";
        else if (marks >= 55) return "Satisfactory performance.";
        else if (marks >= 40) return "Needs improvement.";
        else return "Please seek additional help.";
    }
}
