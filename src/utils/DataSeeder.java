package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataSeeder {
    
    // RENAMED from 'run()' to 'seedIfEmpty()' to match your Main.java
    public static void seedIfEmpty() {
        // Ensure log file exists
        createFileIfNotExists("activity_log.txt");

        // Check if users exist using FileHandler. If not, seed all data.
        if (FileHandler.getAllRecords("users.txt").isEmpty()) {
            System.out.println("Seeding initial data...");

            // --- 1. Seed Users ---
            // Seed Admin
            String adminPass = Security.hashPassword("AD0000101011990");
            FileHandler.appendLine("users.txt", "AD00001", adminPass, "Admin", 
                "John Admin", "Male", "admin@afs.edu", "0123456789", "34", "01/01/1990");

            // Seed Academic Leaders (Minimum 3)
            String alPass1 = Security.hashPassword("AL1000101011985");
            FileHandler.appendLine("users.txt", "AL10001", alPass1, "Leader", 
                "Alice Leader", "Female", "alice@afs.edu", "0123456780", "39", "01/01/1985");
            
            String alPass2 = Security.hashPassword("AL1000201011986");
            FileHandler.appendLine("users.txt", "AL10002", alPass2, "Leader", 
                "Bob Leader", "Male", "bob@afs.edu", "0123456781", "38", "01/01/1986");
            
            String alPass3 = Security.hashPassword("AL1000301011987");
            FileHandler.appendLine("users.txt", "AL10003", alPass3, "Leader", 
                "Carol Leader", "Female", "carol@afs.edu", "0123456782", "37", "01/01/1987");

            // Seed Lecturers (Minimum 9)
            for (int i = 1; i <= 9; i++) {
                String id = String.format("LC2000%d", i);
                String password = Security.hashPassword(id + "01011980");
                // Assign supervisors: 3 lecturers per leader
                String supervisorId = i <= 3 ? "AL10001" : (i <= 6 ? "AL10002" : "AL10003");
                FileHandler.appendLine("users.txt", id, password, "Lecturer", 
                    "Lecturer " + i, "Male", "lec" + i + "@afs.edu", 
                    "012345678" + i, "44", "01/01/1980", supervisorId);
            }

            // Seed Students (15 students)
            for (int i = 1; i <= 15; i++) {
                String id = String.format("TP3000%d", i);
                String password = Security.hashPassword(id + "01012000");
                FileHandler.appendLine("users.txt", id, password, "Student", 
                    "Student " + i, (i % 2 == 0 ? "Female" : "Male"), 
                    "student" + i + "@student.afs.edu", "011234567" + String.format("%02d", i), 
                    "24", "01/01/2000");
            }

            // --- 2. Seed Modules ---
            String[] moduleNames = {
                "Programming Fundamentals", "Data Structures", "Database Systems",
                "Web Development", "Software Engineering", "Operating Systems",
                "Computer Networks", "Artificial Intelligence", "Mobile Development"
            };
            // One module - one lecturer. One academic leader - many modules (3 each).
            for (int i = 1; i <= 9; i++) {
                String moduleId = String.format("MOD%03d", i);
                String lecturerId = String.format("LC2000%d", i);
                String leaderId = i <= 3 ? "AL10001" : (i <= 6 ? "AL10002" : "AL10003");
                FileHandler.appendLine("modules.txt", moduleId, moduleNames[i - 1], 
                    lecturerId, leaderId);
            }

            // --- 3. Seed Classes ---
            // One module - one class / no classes.
            FileHandler.appendLine("classes.txt", "CLS001", "Computer Science Year 1", "MOD001");
            FileHandler.appendLine("classes.txt", "CLS002", "Computer Science Year 2", "MOD002");
            FileHandler.appendLine("classes.txt", "CLS003", "Computer Science Year 3", "MOD003");

            // --- 4. Seed Grading System ---
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

            // --- 5. Seed Enrollments (One student - many classes) ---
            // Enroll first 5 students in CLS001
            for (int i = 1; i <= 5; i++) {
                FileHandler.appendLine("enrollments.txt", String.format("TP3000%d", i), "CLS001");
            }
            // Enroll next 5 students in CLS002
            for (int i = 6; i <= 10; i++) {
                FileHandler.appendLine("enrollments.txt", String.format("TP3000%d", i), "CLS002");
            }
            // Enroll last 5 students in CLS003
            for (int i = 11; i <= 15; i++) {
                FileHandler.appendLine("enrollments.txt", String.format("TP3000%d", i), "CLS003");
            }
            // Enroll a student in a second class to demonstrate "many classes"
            FileHandler.appendLine("enrollments.txt", "TP30001", "CLS002");

            // --- 6. Seed Assessments ---
            // Create assessments for the modules with classes
            FileHandler.appendLine("assessments.txt", "ASS001", "MOD001", "Assignment 1", "100");
            FileHandler.appendLine("assessments.txt", "ASS002", "MOD001", "Quiz 1", "50");
            FileHandler.appendLine("assessments.txt", "ASS003", "MOD002", "Midterm Exam", "100");
            FileHandler.appendLine("assessments.txt", "ASS004", "MOD003", "Final Project", "100");

            // --- 7. Seed Results (Ensures "Analysed report - minimum 5") ---
            Random rand = new Random();
            // Results for ASS001 & ASS002 (MOD001 - students TP30001-5)
            for (int i = 1; i <= 5; i++) {
                String studentId = String.format("TP3000%d", i);
                FileHandler.appendLine("results.txt", "ASS001", studentId, String.valueOf(60 + rand.nextInt(41)), "Good effort.");
                FileHandler.appendLine("results.txt", "ASS002", studentId, String.valueOf(30 + rand.nextInt(21)), "Keep it up.");
            }
            // Results for ASS003 (MOD002 - students TP30006-10 + TP30001)
            for (int i = 6; i <= 10; i++) {
                FileHandler.appendLine("results.txt", "ASS003", String.format("TP3000%d", i), String.valueOf(50 + rand.nextInt(51)), "Well done.");
            }
            FileHandler.appendLine("results.txt", "ASS003", "TP30001", String.valueOf(75), "Excellent work.");
            // Results for ASS004 (MOD003 - students TP30011-15)
            for (int i = 11; i <= 15; i++) {
                FileHandler.appendLine("results.txt", "ASS004", String.format("TP3000%d", i), String.valueOf(55 + rand.nextInt(46)), "Nice project.");
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