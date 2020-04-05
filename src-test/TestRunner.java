import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

public class TestRunner {
    static Properties properties = new Properties();
    static String testConfigFile = "src-test/testConfig.properties";

    public static void main(String[] args) throws IOException {
        InputStream input = new FileInputStream(testConfigFile);
        properties = new Properties();
        properties.load(input);
        requestTestProperty("goodInterpreter", "Set python 3 interpreter");
        requestTestProperty("badInterpreter", "Set bad python interpreter");
        requestTestProperty("pythonWrongVersionInterpreter", "Set python 2 interpreter");
        requestTestProperty("noPandasInterpreter", "Set python 3 interpreter without pandas");
        FileWriter writer = new FileWriter(testConfigFile);
        properties.store(writer, "test settings");
        writer.close();
        Result result = JUnitCore.runClasses(CsvLoaderTest.class);
        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
            System.out.println(result.getRunCount());
            System.out.println(result.getFailureCount());
        }
    }

    public static void requestTestProperty(String propertyName, String message){
        Scanner scanner = new Scanner(System.in);
        if (properties.getProperty(propertyName) == null) {
            System.out.println(message);
            properties.setProperty(propertyName, scanner.next());
        }
    }
}