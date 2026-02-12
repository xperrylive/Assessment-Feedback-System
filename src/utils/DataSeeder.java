package utils;

import java.io.File;

public class DataSeeder {
    
    public static void seedIfEmpty() {
        // Ensure log file exists
        createFileIfNotExists("activity_log.txt");

        // Check if users exist using FileHandler
        if (FileHandler.getAllRecords("users.txt").isEmpty()) {
            System.out.println("Seeding initial data...");

            // Seed Admin (Hashed Password)
            String adminPass = Security.hashPassword("AD0000101011990");
            FileHandler.appendLine("users.txt", "AD00001", adminPass, "Admin", 
                "John Admin", "Male", "admin@afs.edu", "0123456789", "34", "01/01/1990");

            // Seed Academic Leaders (Hashed Passwords)
            String alPass1 = Security.hashPassword("AL1000101011985");
            FileHandler.appendLine("users.txt", "AL10001", alPass1, "Leader", 
                "Alice Leader", "Female", "alice@afs.edu", "0123456780", "39", "01/01/1985");
            
            String alPass2 = Security.hashPassword("AL1000201011986");
            FileHandler.appendLine("users.txt", "AL10002", alPass2, "Leader", 
                "Bob Leader", "Male", "bob@afs.edu", "0123456781", "38", "01/01/1986");
            
            String alPass3 = Security.hashPassword("AL1000301011987");
            FileHandler.appendLine("users.txt", "AL10003", alPass3, "Leader", 
                "Carol Leader", "Female", "carol@afs.edu", "0123456782", "37", "01/01/1987");

            // Seed Lecturers
            for (int i = 1; i <= 9; i++) {
                String id = String.format("LC2000%d", i);
                // Hash the password: ID + 01011980
                String password = Security.hashPassword(id + "01011980");
                String supervisorId = i <= 3 ? "AL10001" : (i <= 6 ? "AL10002" : "AL10003");
                FileHandler.appendLine("users.txt", id, password, "Lecturer", 
                    "Lecturer " + i, "Male", "lec" + i + "@afs.edu", 
                    "012345678" + i, "44", "01/01/1980", supervisorId);
            }

            // Seed Students
            for (int i = 1; i <= 15; i++) {
                String id = String.format("TP3000%d", i);
                // Hash the password: ID + 01012000
                String password = Security.hashPassword(id + "01012000");
                FileHandler.appendLine("users.txt", id, password, "Student", 
                    "Student " + i, (i % 2 == 0 ? "Female" : "Male"), 
                    "student" + i + "@student.afs.edu", "011234567" + String.format("%02d", i), 
                    "24", "01/01/2000");
            }

            // Seed Modules
            String[] moduleNames = {
                "Programming Fundamentals", "Data Structures", "Database Systems",
                "Web Development", "Software Engineering", "Operating Systems",
                "Computer Networks", "Artificial Intelligence", "Mobile Development"
            };
            for (int i = 1; i <= 9; i++) {
                String moduleId = String.format("MOD%03d", i);
                String lecturerId = String.format("LC2000%d", i);
                String leaderId = i <= 3 ? "AL10001" : (i <= 6 ? "AL10002" : "AL10003");
                FileHandler.appendLine("modules.txt", moduleId, moduleNames[i - 1], 
                    lecturerId, leaderId);
            }

            // Seed Classes
            FileHandler.appendLine("classes.txt", "CLS001", "Computer Science Year 1", "MOD001");
            FileHandler.appendLine("classes.txt", "CLS002", "Computer Science Year 2", "MOD002");
            FileHandler.appendLine("classes.txt", "CLS003", "Computer Science Year 3", "MOD003");

            // Seed Grading System
            if(FileHandler.getAllRecords("grading.txt").isEmpty()) {
                FileHandler.appendLine("grading.txt", "A+", "80", "100");
                FileHandler.appendLine("grading.txt", "A", "75", "79");
                FileHandler.appendLine("grading.txt", "A-", "70", "74");
                FileHandler.appendLine("grading.txt", "B+", "65", "69");
                FileHandler.appendLine("grading.txt", "B", "60", "64");
                FileHandler.appendLine("grading.txt", "B-", "55", "59");
                FileHandler.appendLine("grading.txt", "C+", "50", "54");
                FileHandler.appendLine("grading.txt", "C", "45", "49");
                FileHandler.appendLine("grading.txt", "F", "0", "44");
            }

            System.out.println("Data seeding completed!");
        }
    }
    
    private static void createFileIfNotExists(String filename) {
        try {
            File f = new File("data/" + filename);
            if(!f.exists()) {
                f.getParentFile().mkdirs();
                f.createNewFile();
            }
        } catch(Exception e) { e.printStackTrace(); }
    }
}