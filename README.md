# Репозиторий тестового задания курса JAVA ШИФТ.
## Основная информация
    При реализации использовались:

        Oracle JDK 22.0.2, 16.07 2024

        OS: Ubuntu 24.04 LTS

        IDE: VS Code, с использованием "Extension Pack for Java"

    Ограничения:
        
        Файлы для чтения должны быть в формате .txt

        Порядок следования аргументов:
            [-f] [-a] [-p <prefix>] [-o <path>] [file1.txt, path/to/file2.txt, ...] 
                [] - необязательное поле
                <> - обязательное поле

    Расшифровка аргументов:
        -f - Запросить у утилиты полную статистику
        -a - Указать утилите не перезаписывать исходящие файлы, а дополнять их
        -p - Указать префикс к исходящим файлам
        -o - Указать путь к исходящим файлам

Аргумент "-s", указанный в задании, не используется, так как, по заданию, минмальная статистика должна собираться и выводиться всегда, из-за чего этот аргумент утрачивает свой смысл.

Если в значении аргумента есть пробелы, например в пути ```toIn/Hello World/file with name.txt``` - необходимо его заключить в кавычки следующим образом: ```"toIn/Hello World/file with name.txt"```.

Утилита адаптирована под числа превосходящие стандартные типы ```long``` и ```double```, однако при сборе статистики для строк используется метод ```length()``` для получения длины строки, следовательно, если длина обрабатываемой строки будет больше максимального значения ```int``` - это может привести к непредсказуемому результату или ошибке, однако это мало вероятно, так как примерный вес файла с только одной такой строкой будет около 4 Гб.

При работе утилита не сохраняет уже обработанные значения из файлов нигде, кроме как в файлах вывода, что позволяет не занимать много ОЗУ, однако в связи с этим вынуждена деражать открытыми поток чтения для файла, на все время его обработки, и потоки записи результатов, на все время выполнения утилиты.

При выводе статистики, если счетчик определенного типа равен нулю - полная статистика не выводится, независимо от аргумента.

## Запуск

Для запуска можно пойти двумя путями:

1. Открыть файлы в IDE, указать в параметрах запуска аргументы и после этого запустить.
2. Открыть терминал, перейти в каталог с файлами командой ```cd```, после чего:
   1. Запустить файл TestTask утилитой ```java```: ```java -jar TestTask.java [args]```
   2. Скомпилировать код утилитой ```javac```: ```javac TestTask.java```, а потом уже запустить утилитой ```java```: ```java TestTask [args]```

Тестировалось при кодировке UTF-8.

