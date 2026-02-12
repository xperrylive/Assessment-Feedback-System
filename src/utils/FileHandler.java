package utils;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileHandler {
    private static final String DATA_DIR = "data/";

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        String filepath = DATA_DIR + filename;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
                return lines;
            }
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static boolean appendLine(String filename, String... data) {
        String filepath = DATA_DIR + filename;
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(String.join(" | ", data));
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String[]> getAllRecords(String filename) {
        List<String[]> records = new ArrayList<>();
        List<String> lines = readFile(filename);
        for (String line : lines) {
            records.add(line.split(" \\| "));
        }
        return records;
    }

    public static boolean updateLine(String filename, int idIndex, String idValue, String... newData) {
        String filepath = DATA_DIR + filename;
        try {
            List<String> lines = readFile(filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            boolean updated = false;
            for (String line : lines) {
                String[] parts = line.split(" \\| ");
                if (parts.length > idIndex && parts[idIndex].equals(idValue)) {
                    writer.write(String.join(" | ", newData));
                    updated = true;
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
            writer.close();
            return updated;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteLine(String filename, int idIndex, String idValue) {
        String filepath = DATA_DIR + filename;
        try {
            List<String> lines = readFile(filename);
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
            boolean deleted = false;
            for (String line : lines) {
                String[] parts = line.split(" \\| ");
                if (parts.length > idIndex && parts[idIndex].equals(idValue)) {
                    deleted = true;
                    continue;
                }
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            return deleted;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
