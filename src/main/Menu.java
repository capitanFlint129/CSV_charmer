package main;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Menu {
    private JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel controlPanel;
    private JTable table;
    private static final Integer DEFAULT_ROWS_LIMIT = 10;
    private static Integer offset = 0;
    private SettingsManager settingsManager;
    private PythonManager pythonManager;

    public Menu() {
        pythonManager = new PythonManager();
        try {
            settingsManager = new SettingsManager("config.properties", new String[]{"python3"});
            settingsManager.loadProperties();
            if (!settingsManager.isNotFirstLaunch()) {
                settingsManager.firstLaunchInit();
            }
        } catch (IOException e) {
            showError(e.getMessage());
        }
        prepareMainMenuGUI();
    }

    public static void main(String[] args) {
        Menu menu = new Menu();
        menu.showMainMenu();
    }

    private void prepareMainMenuGUI() {
        mainFrame = new JFrame("CSV Charmer");
        mainFrame.setSize(550, 400);
        mainFrame.setLayout(new GridLayout(2, 1));
        mainFrame.setResizable(false);

        headerLabel = new JLabel("", JLabel.CENTER);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        mainFrame.add(headerLabel);
        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);
    }

    private void showMainMenu() {
        headerLabel.setText("Hello! Choose python interpreter and *.csv file");

        ArrayList<String> foundInterpreters = new ArrayList<>(settingsManager.getFoundInterpreters());
        String currentInterpreter = settingsManager.getInterpreter();
        int selectedInterpreterIndex = Integer.max(foundInterpreters.indexOf(currentInterpreter), 0);

        // Choose found interpreter
        JComboBox interpretersComboBox = new JComboBox(foundInterpreters.toArray());
        interpretersComboBox.setPreferredSize(new Dimension(390, 20));
        interpretersComboBox.setSelectedIndex(selectedInterpreterIndex);
        JScrollPane interpretersListScroll = new JScrollPane(interpretersComboBox,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Set your interpreter
        JFileChooser interpreterChooser = new JFileChooser();
        JButton showInterpreterDialogButton = new JButton("Add interpreter");
        showInterpreterDialogButton.addActionListener(actionEvent -> {
            int returnVal = interpreterChooser.showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String selectedFilePath = interpreterChooser.getSelectedFile().getAbsolutePath();
                if (foundInterpreters.indexOf(selectedFilePath) < 0) {
                    foundInterpreters.add(selectedFilePath);
                    interpretersComboBox.addItem(selectedFilePath);
                }
                interpretersComboBox.setSelectedIndex(foundInterpreters.indexOf(selectedFilePath));
                try {
                    settingsManager.setPythonInterpreter(selectedFilePath);
                } catch (IOException e) {
                    showError(e.getMessage());
                }
            }
        });

        // choose CSV file
        JTextArea chooseFileTextArea = new JTextArea(settingsManager.getCurrentFile(), 1, 35);
        JScrollPane scrollTextAreaPane = new JScrollPane(chooseFileTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        chooseFileTextArea.setEditable(false);

        JFileChooser csvFileChooser = new JFileChooser();
        csvFileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV", "csv");
        csvFileChooser.addChoosableFileFilter(filter);
        JButton chooseCvsButton = new JButton("Choose CSV file");
        chooseCvsButton.addActionListener(actionEvent -> {
            int returnVal = csvFileChooser.showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    chooseFileTextArea.setText("");
                    chooseFileTextArea.getDocument().insertString(0, csvFileChooser.getSelectedFile().getAbsolutePath(), SimpleAttributeSet.EMPTY);
                } catch (BadLocationException e) {
                    showError(e.getMessage());
                }
            }
        });

        // Delimiter field
        JTextArea setDelimiterTextArea = new JTextArea(",", 1, 40);
        JLabel delimiterHint = new JLabel();
        delimiterHint.setSize(new Dimension(70, 30));
        delimiterHint.setText("Delimiter: ");

        // Create "Run" button
        JButton runButton = new JButton("Run");
        runButton.addActionListener(actionEvent -> {
            if (chooseFileTextArea.getText().equals("") || setDelimiterTextArea.getText().equals("") ||
                    interpretersComboBox.getSelectedIndex() < 0) {
                headerLabel.setText("You have to set *.csv file, python3 interpreter and delimiter");
            } else if(!(new File(chooseFileTextArea.getText()).exists())){
                headerLabel.setText("You have to select existing file");
            } else {
                try {
                    settingsManager.setPythonInterpreter(foundInterpreters.get(interpretersComboBox.getSelectedIndex()));
                    settingsManager.setCurrentFile(chooseFileTextArea.getText());
                    settingsManager.setDelimiter(setDelimiterTextArea.getText());

                } catch (IOException e) {
                    showError(e.getMessage());
                }
                prepareShowTableGUI();
                showTable();
            }
        });

        // Exit button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(actionEvent -> {
            System.exit(0);
        });

        controlPanel.add(interpretersListScroll);
        controlPanel.add(showInterpreterDialogButton);
        controlPanel.add(scrollTextAreaPane);
        controlPanel.add(chooseCvsButton);
        controlPanel.add(delimiterHint);
        controlPanel.add(setDelimiterTextArea);
        controlPanel.add(runButton);
        controlPanel.add(exitButton);
        mainFrame.setVisible(true);
    }

    private void prepareShowTableGUI() {
        mainFrame.getContentPane().removeAll();
        mainFrame.repaint();
        mainFrame.setSize(550, 400);
        mainFrame.setResizable(false);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        mainFrame.setVisible(true);
    }

    private void showTable() {
        // Start python
        pythonManager.startPython(settingsManager.getInterpreter(), settingsManager.getScriptPath());
        if (!pythonManager.isStarted()) {
            showError("Process not started! Maybe you select bad interpreter");
        }
        if (pythonManager.getMessage().equals("WRONG_PYTHON_VERSION")) {
            showError("Wrong python version (python 3 is needed)");
        }
        if (pythonManager.getMessage().equals("PANDAS_NOT_FOUND")) {
            showError("Pandas not found");
        }
        // Input necessary information
        pythonManager.inputFilePath(settingsManager.getCurrentFile());
        pythonManager.inputDelimiter(settingsManager.getDelimiter());

        // Get data
        String[] columns = pythonManager.getColumnsNames();
        if (columns == null) {
            showError("Can't get columns names! Maybe you set wrong delimiter");
        }
        ArrayList<String[]> data = pythonManager.getRows(columns.length, DEFAULT_ROWS_LIMIT, offset);
        offset += DEFAULT_ROWS_LIMIT;
        if (data == null) {
            showError("Some IOException occurred");
        }

        // Create table
        DefaultTableModel model1 = new DefaultTableModel(columns, 0);
        table = new JTable(model1);
        for (String[] row : data) {
            model1.addRow(row);
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane tableScrollPane = new JScrollPane(table);
        JScrollBar verticalTableScrollBar = tableScrollPane.getVerticalScrollBar();
        JViewport vp = tableScrollPane.getViewport();
        vp.addChangeListener((l) -> {
            if (verticalTableScrollBar.getValue() + verticalTableScrollBar.getVisibleAmount() == verticalTableScrollBar.getMaximum()) {
                ArrayList<String[]> newData = pythonManager.getRows(columns.length, DEFAULT_ROWS_LIMIT, offset);
                offset += DEFAULT_ROWS_LIMIT;
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                for (String[] row : newData) {
                    model.addRow(row);
                }
            }
        });
        table.setSize(550, 400);
        tableScrollPane.setSize(550, 400);
        mainFrame.add(tableScrollPane);
        mainFrame.setVisible(true);
    }

    private void showError(String errorMessage) {
        mainFrame.getContentPane().removeAll();
        mainFrame.repaint();
        mainFrame.setSize(550, 400);
        mainFrame.setResizable(false);

        JLabel errorLabel = new JLabel(errorMessage, JLabel.CENTER);

        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });
        mainFrame.add(errorLabel);
        mainFrame.setVisible(true);
    }
}