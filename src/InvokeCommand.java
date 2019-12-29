import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InvokeCommand {
    public static final int LINUX_SHELL_COMMAND = 1;
    public static final int LINUX_SHELL_SCRIPT = 2;
    public static final int WINDOWS_RUN_COMMAND = 3;
    public static final int WINDOWS_RUN_BAT_FILE = 4;

    public static void exec(int flag, String command, boolean trimOutput){
        ProcessBuilder processBuilder = new ProcessBuilder();

        // -- Linux --
        // Run a shell command
        if(flag == LINUX_SHELL_COMMAND)
            processBuilder.command("bash", "-c", command);

        // Run a shell script
        if(flag == LINUX_SHELL_SCRIPT)
            processBuilder.command(command);

        // -- Windows --
        // Run a command
        if(flag == WINDOWS_RUN_COMMAND)
            processBuilder.command("cmd.exe", "/c", command);

        // Run a bat file
        if(flag == WINDOWS_RUN_BAT_FILE)
            processBuilder.command(command);

        try {
            Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
                if(!trimOutput) output.append("\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.print(output);
                if(!trimOutput) System.out.println();
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void exec(int flag, String command){
        exec(flag, command, false);
    }

    public static void deleteBlinkingCursor(){
        if(OSDetector.isUnix())
            exec(LINUX_SHELL_COMMAND, "tput civis", true);
        else if(OSDetector.isWindows())
            exec(WINDOWS_RUN_COMMAND, "CursorSize 0", true);
    }

    public static void recoverBlinkingCursor(){
        if(OSDetector.isUnix())
            exec(LINUX_SHELL_COMMAND, "tput cnorm", true);
        else if(OSDetector.isWindows())
            exec(WINDOWS_RUN_COMMAND, "CursorSize /L", true);
    }
}
