package app.util;

import java.io.*;

public class FileUtil {

    public static void writeText(String path, String content) throws IOException {
        File file = new File(path);
        file.getParentFile().mkdirs();

        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
        }
    }

    public static String readText(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) return "";

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}
