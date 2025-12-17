package app.util;

import java.io.File;

public class BackendRunner {

    public static final String BACKEND_EXE = "../../backend_cpp/backend.exe";
    public static final String DATA_DIR = "../../data/";

    public static void run(String mode) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(BACKEND_EXE, mode);
        pb.directory(new File("../../backend_cpp"));
        pb.redirectErrorStream(true);
        Process p = pb.start();
        p.waitFor();
    }
}