import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * FileHandler - Utility class for all text file operations
 * Uses pipe delimiter " | " for field separation
 */
public class FileHandler {
    
    private static final String DELIMITER = " \\| ";
    private static final String DELIMITER_WRITE = " | ";
    
    /**
     * Reads all lines from a file
     * @param filename Name of the file to read
     * @return List of lines, or empty list if file doesn't exist
     */
    public static List<String> readFile(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
                return new ArrayList<>();
            }
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Appends a new line to the file
     * @param filename Name of the file
     * @param data Array of data fields to write (will be joined with delimiter)
     * @return true if successful, false otherwise
     */
    public static boolean appendLine(String filename, String... data) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            String line = String.join(DELIMITER_WRITE, data);
            
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(line);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error appending to file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Updates a line in the file based on a condition
     * @param filename Name of the file
     * @param searchIndex Index of field to search (0-based)
     * @param searchValue Value to match
     * @param newData New data array to replace the matched line
     * @return true if line was found and updated, false otherwise
     */
    public static boolean updateLine(String filename, int searchIndex, String searchValue, String... newData) {
        try {
            List<String> lines = readFile(filename);
            boolean updated = false;
            
            for (int i = 0; i < lines.size(); i++) {
                String[] fields = lines.get(i).split(DELIMITER);
                if (searchIndex < fields.length && fields[searchIndex].equals(searchValue)) {
                    lines.set(i, String.join(DELIMITER_WRITE, newData));
                    updated = true;
                    break;
                }
            }
            
            if (updated) {
                Files.write(Paths.get(filename), lines);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Error updating file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deletes a line from the file based on a condition
     * @param filename Name of the file
     * @param searchIndex Index of field to search (0-based)
     * @param searchValue Value to match
     * @return true if line was found and deleted, false otherwise
     */
    public static boolean deleteLine(String filename, int searchIndex, String searchValue) {
        try {
            List<String> lines = readFile(filename);
            boolean deleted = false;
            
            Iterator<String> iterator = lines.iterator();
            while (iterator.hasNext()) {
                String line = iterator.next();
                String[] fields = line.split(DELIMITER);
                if (searchIndex < fields.length && fields[searchIndex].equals(searchValue)) {
                    iterator.remove();
                    deleted = true;
                    break;
                }
            }
            
            if (deleted) {
                Files.write(Paths.get(filename), lines);
                return true;
            }
            return false;
        } catch (IOException e) {
            System.err.println("Error deleting from file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Searches for a line in the file
     * @param filename Name of the file
     * @param searchIndex Index of field to search (0-based)
     * @param searchValue Value to match
     * @return Array of fields if found, null otherwise
     */
    public static String[] searchLine(String filename, int searchIndex, String searchValue) {
        List<String> lines = readFile(filename);
        for (String line : lines) {
            String[] fields = line.split(DELIMITER);
            if (searchIndex < fields.length && fields[searchIndex].equals(searchValue)) {
                return fields;
            }
        }
        return null;
    }
    
    /**
     * Gets all lines as arrays of fields
     * @param filename Name of the file
     * @return List of field arrays
     */
    public static List<String[]> getAllRecords(String filename) {
        List<String> lines = readFile(filename);
        List<String[]> records = new ArrayList<>();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                records.add(line.split(DELIMITER));
            }
        }
        return records;
    }
    
    /**
     * Clears all content from a file
     * @param filename Name of the file to clear
     * @return true if successful, false otherwise
     */
    public static boolean clearFile(String filename) {
        try {
            Files.write(Paths.get(filename), new ArrayList<>());
            return true;
        } catch (IOException e) {
            System.err.println("Error clearing file " + filename + ": " + e.getMessage());
            return false;
        }
    }
}
