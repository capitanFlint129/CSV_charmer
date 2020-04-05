package main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;

public class OSManager {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static HashSet<String> findPythonInterpreters(String[] pythonVersions) {
        if (isWindows()) {
            return findPythonInterpretersInWindows(pythonVersions);
        } else if (isMac()) {
            return findPythonInterpretersInMac(pythonVersions);
        } else if (isUnix()) {
            return findPythonInterpretersInUnix(pythonVersions);
        } else {
            return new HashSet<>();
        }
    }

    private static HashSet<String> findPythonInterpretersInWindows(String[] pythonVersions) {
        return new HashSet<>();
    }

    private static HashSet<String> findPythonInterpretersInMac(String[] pythonVersions) {
        return new HashSet<>();
    }

    public static HashSet<String> findPythonInterpretersInUnix(String[] pythonVersions) {
        HashSet<String> interpreters = new HashSet<>();
        HashSet<String> pathVar = new HashSet<>(Arrays.asList(System.getenv("PATH").split(":")));
        for (String path : pathVar) {
            for (String version : pythonVersions){
                Path tempPath = Paths.get(path, version);
                File interpreter = new File(tempPath.toString());
                if (interpreter.exists() && interpreter.isFile()) {
                    interpreters.add(tempPath.toString());
                }
            }
        }
        return interpreters;
    }

    public static String getRunCommand(String interpreter) {
        if (isWindows()) {
            return getRunCommandInWindows(interpreter);
        } else if (isMac()) {
            return getRunCommandInMac(interpreter);
        } else if (isUnix()) {
            return getRunCommandInUnix(interpreter);
        } else {
            return new String();
        }
    }

    private static String getRunCommandInUnix(String interpreter) {
        return interpreter;
    }

    private static String getRunCommandInMac(String interpreter) {
        return new String();
    }

    private static String getRunCommandInWindows(String interpreter) {
        return new String();
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
    }
}