package randomgenerator.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PythonExecutor {
    public static void execute(String pythonFile, String hist) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("python3.10", pythonFile, hist);
        Process p = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

//        int exitCode = p.waitFor();
//        System.out.println("Exit code: " + exitCode);
    }
}
