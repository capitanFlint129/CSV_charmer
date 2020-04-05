# CSV Charmer

Приложение для чтения *.csv файлов с примитивным графическим интерфейсом.

Пользователь должен:
1. Выбрать интерперетатор Python из найденных на его устройстве или добавить его самостоятельно
2. Выбрать *.csv файл
3. Указать, какой разделитель используется в выбранном файле

По нажатию на кнопку "Run" открывается таблица. Данные прогружаются по мере того, как пользователь прокручивает таблицу вниз.

## Требования
Для корректной работы приложения необходимо:
* Linux
* Интерпертатор Python 3
* Pandas

## Запуcк
Для запуска проекта необходимо скомпилировать его и запустить класс Menu.

## Тестирование
Для запуска тестов необходимо скомпилировать код и запустить класс TestRunner. Для более полного тестирования необходимо указать разлиные интерпретаторы Python, которые потребует программа или самостоятельно прописать их в файле testConfig.properties. Если их не указать, то не все случаи будут протестированы.

## Структура приложения
Структура приложения состоит из нескольких классов:
* Menu - основной класс, отвечает за графический интерфейс и основную логику приложения.
* SettingsManager - используется для настройки приложения.
* PythonManager - отвечает за запуск кода на Python и взаимдействие с запущенным процессом.
* OSManager - весь код, зависящий от операционной системы вынесен в этот класс. С помощью него можно добавить поддержку других операционных систем.


 