package app.util;

import java.io.*;

public class BackendRunner {

    public static final String BACKEND_PATH = "../backend_cpp/backend.exe";
    public static final String DATA_DIR = "../data/";

    public static void run(String mode) throws Exception {

        // Get current working directory
        String currentDir = System.getProperty("user.dir");
        File projectRoot = new File(currentDir);

        // Build backend path
        File backend = new File(projectRoot, BACKEND_PATH);

        System.out.println("[System] Searching backend at: " + backend.getAbsolutePath());

        if (!backend.exists()) {
            throw new FileNotFoundException(
                "Backend executable NOT found!\nExpected: " + backend.getAbsolutePath()
            );
        }

        // 👉 Only declare ProcessBuilder ONCE
        ProcessBuilder builder = new ProcessBuilder(
                backend.getAbsolutePath(),
                mode
        );

        // Make backend work inside its folder
        builder.directory(backend.getParentFile());
        builder.redirectErrorStream(true);

        Process process = builder.start();

        // Read backend console output
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[C++ Backend] " + line);
            }
        }

        int code = process.waitFor();
        if (code != 0) {
            throw new RuntimeException("Backend exited with code: " + code);
        }
    }
}
