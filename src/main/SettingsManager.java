package main;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;

public class SettingsManager {
    private File configFile;
    private String[] pythonVersions;
    private static String scriptPath = "python-files/csv_loader.py";
    Properties properties;

    SettingsManager(String configFilePath, String[] pythonVersions) throws IOException {
        configFile = new File(configFilePath);
        if (!configFile.exists()){
            configFile.createNewFile();
        }
        this.pythonVersions = pythonVersions;
        properties = new Properties();
    }

    private HashSet<String> findPythonInterpreters() {
        return OSManager.findPythonInterpreters(pythonVersions);
    }

    void setCurrentFile(String currentFilePath) throws IOException {
        properties.setProperty("currentFile", currentFilePath);
        saveProperties();
    }

    void setPythonInterpreter(String pythonInterpreterPath) throws IOException {
        properties.setProperty("interpreter", pythonInterpreterPath);
        addFoundInterpreter(pythonInterpreterPath);
        saveProperties();
    }

    void setFoundInterpreters(HashSet<String> interpreters) throws IOException {
        properties.setProperty("foundInterpreters", String.join(":", interpreters));
        saveProperties();
    }

    void firstLaunchInit() throws IOException {
        setFoundInterpreters(findPythonInterpreters());
        properties.setProperty("isNotFirstLaunch", "true");
        saveProperties();
    }

    void saveProperties() throws IOException {
        FileWriter writer = new FileWriter(configFile);
        properties.store(writer, "app settings");
        writer.close();
    }

    void addFoundInterpreter(String newInterpreter) throws IOException {
        HashSet<String> interpreters = new HashSet<>(getFoundInterpreters());
        interpreters.add(newInterpreter);
        setFoundInterpreters(interpreters);
    }

    HashSet<String> getFoundInterpreters() {
        return new HashSet<>(Arrays.asList(properties.getProperty("foundInterpreters").split(":")));
    }

    String getInterpreter() {
        return properties.getProperty("interpreter");
    }

    String getCurrentFile() {
        return properties.getProperty("currentFile");
    }

    boolean isNotFirstLaunch() {
        return Boolean.parseBoolean(properties.getProperty("isNotFirstLaunch"));
    }

    String getScriptPath() {
        return scriptPath;
    }

    public void loadProperties() throws IOException {
        InputStream input = new FileInputStream(configFile);
        properties = new Properties();
        properties.load(input);
    }

    void setDelimiter(String delimeter) throws IOException {
        properties.setProperty("delimiter", delimeter);
        saveProperties();
    }

    String getDelimiter() {
        return properties.getProperty("delimiter");
    }
}