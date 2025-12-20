package app.util;

import java.io.*;

public class BackendRunner {

    // Simplified paths - this assumes you run the project from the root folder
    public static final String BACKEND_PATH = "../backend_cpp/backend.exe"; 
    public static final String DATA_DIR = "../data/";

    public static void run(String mode) throws Exception {
        // Get the current directory where the program is running
        String currentDir = System.getProperty("user.dir");
        File projectRoot = new File(currentDir);
        
        // Construct the file reference
        File backend = new File(projectRoot, BACKEND_PATH);

        // --- DEBUG PRINT: Check your terminal to see this path! ---
        System.out.println("[System] Searching for backend at: " + backend.getAbsolutePath());

        if (!backend.exists()) {
            throw new FileNotFoundException("Backend executable not found!\n" +
                "Expected Path: " + backend.getAbsolutePath() + "\n\n" +
                "FIX: Compile your C++ code and ensure 'backend.exe' is in the 'backend_cpp' folder.");
        }

        ProcessBuilder pb = new ProcessBuilder(backend.getAbsolutePath(), mode);
        
        // Important: Set the working directory to the backend folder 
        // so the C++ code can find the 'data' folder relatively.
        pb.directory(backend.getParentFile());
        pb.redirectErrorStream(true);

        Process p = pb.start();

        // Read backend output to help debug C++ logic
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[C++ Backend] " + line);
            }
        }

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Backend crashed or exited with code: " + exitCode);
        }
    }
}
