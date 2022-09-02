# CSV Charmer

An application for reading *.csv files with a primitive graphical interface.

The user must:

1. Choose a Python interpreter from those found on his device or add it yourself
2. Select *.csv file
3. Specify which delimiter is used in the selected file

By clicking on the "Run" button, the table opens. The data is loaded as the user scrolls down the table.

## Requirements
* Linux
* Python 3
* Pandas

## Launch
To run the project, you must compile it and run the Menu class.

## Testing
To run tests, you must compile the code and run the TestRunner class. For more complete testing, you need to specify different Python interpreters that the program will require or write them yourself in the testConfig.properties file. If they are not specified, then not all cases will be tested.

## Architecture of application
Most important application classes:
* Menu - the main class responsible for the graphical interface and the main logic of the application
* SettingsManager - used to configure the application
* PythonManager - responsible for running Python code and interacting with the running process
* OSManager - all code that depends on the operating system is placed in this class. It can be used to add support for other operating systems


 
