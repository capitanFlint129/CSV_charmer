import main.PythonManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class CsvLoaderTest {
    Properties properties = new Properties();
    PythonManager pythonManager = new PythonManager();
    ArrayList<String[]> testData = new ArrayList<>();
    String testDataPath = "test-data/test_data.csv";
    String testDelimiter = ",";
    final Integer LIMIT = 2;

    @Before
    public void setUp() throws IOException {

        FileInputStream fileIn = new FileInputStream(testDataPath);
        Scanner sc = new Scanner(fileIn);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] row = line.split(",");
            for (int i = 0; i < row.length; i++) {
                row[i] = row[i].replace("\"", "");
                row[i] = row[i].replaceAll("\\P{Print}","");
            }
            testData.add(row);
        }
        sc.close();

        InputStream input = new FileInputStream(TestRunner.testConfigFile);
        properties = new Properties();
        properties.load(input);
    }



    @Test
    public void testCsvLoader() {
        pythonManager.startPython(properties.getProperty("goodInterpreter"), "python-files/csv_loader.py");
        pythonManager.getMessage();
        pythonManager.inputFilePath(testDataPath);
        pythonManager.inputDelimiter(testDelimiter);
        String[] columns = pythonManager.getColumnsNames();
        Assert.assertEquals(columns.length, testData.get(0).length);
        for (int i = 0; i < columns.length; i++) {
            Assert.assertEquals(columns[i], testData.get(0)[i]);
        }
        Integer limit = LIMIT;
        Integer offest = 0;
        for (int n = 0; n < (testData.size() + LIMIT - 1) / LIMIT; n++) {
            ArrayList<String[]> rows = pythonManager.getRows(columns.length, limit, offest);
            for (int i = 0; i < rows.size(); i++) {
                String[] row = rows.get(i);
                for (int j = 0; j < row.length; j++) {
                    row[j] = row[j].replaceAll("\\P{Print}","");
                    Assert.assertEquals(row[j], testData.get(offest + i + 1)[j]);
                }
            }
            offest += limit;
        }
    }

    @Test
    public void testCsvLoaderWrongVersion() {
        if (properties.getProperty("pythonWrongVersionInterpreter") != null){
            pythonManager.startPython(properties.getProperty("pythonWrongVersionInterpreter"), "python-files/csv_loader.py");
            Assert.assertEquals(pythonManager.getMessage(), "WRONG_PYTHON_VERSION");
        }
    }

    @Test
    public void testCsvLoaderNoPandas() {
        if (properties.getProperty("noPandasInterpreter") != null){
            pythonManager.startPython(properties.getProperty("noPandasInterpreter"), "python-files/csv_loader.py");
            Assert.assertEquals(pythonManager.getMessage(), "PANDAS_NOT_FOUND");
        }
    }

    @Test
    public void testCsvLoaderBadInterpreter() {
        if (properties.getProperty("badInterpreter") != null) {
            pythonManager.startPython(properties.getProperty("badInterpreter"), "python-files/csv_loader.py");
            Assert.assertFalse(pythonManager.isStarted());
        }
    }
}