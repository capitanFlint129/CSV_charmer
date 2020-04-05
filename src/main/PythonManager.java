package main;

import java.io.*;
import java.util.ArrayList;

public class PythonManager {
    private BufferedReader reader;
    private BufferedReader errors;
    private BufferedWriter writer;
    private Process pythonProcess;
    private boolean isEndReached = false;

    public void startPython(String interpreter, String scriptPath) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(OSManager.getRunCommand(interpreter), scriptPath);
        try {
            pythonProcess = processBuilder.start();
        } catch (IOException e) {
        }
        if (isStarted()) {
            writer = new BufferedWriter(new OutputStreamWriter(pythonProcess.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(pythonProcess.getInputStream()));
        }
    }

    public String getError() {
        String line = "";
        StringBuilder msg = new StringBuilder("");
        while (line != null){
            try {
                line = errors.readLine();
                msg.append("\n" + line);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return msg.toString();
    }

    public boolean isStarted(){
        return pythonProcess != null;
    }

    public void inputFilePath(String filePath) {
        input(filePath);
    }

    public void inputDelimiter(String delimiter) {
        input(delimiter);
    }

    private void input(String input) {
        try {
            writer.write(input + "\n");
            writer.flush();
        } catch (IOException e) {
        }
    }

    public String getMessage() {
        try {
            return reader.readLine();
        } catch (Exception ignored) {
        }
        return "";
    }

    public String[] getColumnsNames(){
        String[] ret;
        input("get_columns");
        try {
            String input = reader.readLine();
            if (input == null) {
                return null;
            }
            ret = input.split(" ");
            return ret;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<String[]> getRows(Integer columnsNum, Integer limit, Integer offset) {
        ArrayList<String[]> rows = new ArrayList<>();
        if (isEndReached){
            return rows;
        }
        input(String.join(" ", "get_rows", limit.toString(), offset.toString()));
        try {
            Integer dataLen = Integer.valueOf(reader.readLine());
            if (dataLen < limit) {
                isEndReached = true;
            }
            for (int j = 0; j < dataLen; j++){
                String[] row = new String[columnsNum];
                for (int i = 0; i < columnsNum; i++){
                    StringBuilder line = new StringBuilder();
                    String currentLine = reader.readLine();
                    if (currentLine == null) {
                        return null;
                    }
                    while (!currentLine.equals("END_OF_VALUE")) {
                        line.append('\n').append(currentLine);
                        currentLine = reader.readLine();
                    }
                    row[i] = line.toString();
                }
                rows.add(row);
            }
        } catch (IOException e) {
            return null;
        }
        return rows;
    }
}