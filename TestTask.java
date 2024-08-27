import java.io.*;
import java.util.*;
import MyExceptions.*;

class TestTask {
    public static void main(String[] args) {
        // Переменные для сохранения аргументов
        boolean fullStat = false;
        boolean append = false;
        String outPath = "";
        String prefix = "";
        ArrayList<String> sources = new ArrayList<String>();

        // Обработчик аргументов
        try {
            // Блокировщик аргументов
            boolean[] argsBlocker = { false, false, false, false, false };

            // Ищем среди аргументов интересующие совпадения
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                case "-f":
                    // Если аргумент заблокирован - выдаем ошибку
                    if (argsBlocker[0])
                        throw new ArgumentsHandlerException("Аргумент \"-f\" указан некорректно");

                    // Сохраняем значение аргумента
                    fullStat = true;

                    // Блокируем этот аргумент во избежание повтора
                    argsBlocker[0] = true;
                    break;
                case "-a":
                    if (argsBlocker[1])
                        throw new ArgumentsHandlerException("Аргумент \"-a\" указан некорректно");

                    append = true;

                    // Блокируем этот аргумент во избежание повтора
                    // А также аргументы, что должны быть перед ним
                    argsBlocker[0] = true;
                    argsBlocker[1] = true;
                    break;
                case "-p":
                    if (argsBlocker[2])
                        throw new ArgumentsHandlerException("Аргумент \"-p\" указан некорректно");

                    // Если выходим за пределы аргументов - ошибка
                    try {
                        prefix = args[++i];
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        throw new ArgumentsHandlerException("Не удается найти значение аргумента \"-p\"");
                    }

                    if (prefix.replace('\\', '/').contains("/"))
                        throw new ArgumentsHandlerException("Значение аргумента \"-p\" указано некорректно\n"+
                                                            "Если вы хотели указать путь к каталогу результатов - используйте \"-o <path>\"");

                    // Блокируем этот аргумент во избежание повтора
                    // А также аргументы, что должны быть перед ним
                    argsBlocker[0] = true;
                    argsBlocker[1] = true;
                    argsBlocker[2] = true;
                    break;
                case "-o":
                    if (argsBlocker[3])
                        throw new ArgumentsHandlerException("Аргумент \"-o\" указан некорректно");

                    try {
                        // Стандартизируем смену каталога
                        outPath = args[++i].replace('\\', '/');
                    }
                    catch (ArrayIndexOutOfBoundsException ex) {
                        throw new ArgumentsHandlerException("Не удается найти значение аргумента \"-o\"");
                    }

                    // Так как outPath трактуется дальше как префикс
                    // он должен заканчиваться на '/'
                    if (!outPath.endsWith("/"))
                        outPath += "/";

                    // Блокируем этот аргумент во избежание повтора
                    // А также аргументы, что должны быть перед ним
                    argsBlocker[0] = true;
                    argsBlocker[1] = true;
                    argsBlocker[2] = true;
                    argsBlocker[3] = true;
                    break;
                default:
                    // Поддерживаем файлы для чтения только .txt
                    if (!args[i].endsWith(".txt"))
                        throw new ArgumentsHandlerException("Незивестный аргумент\n"+
                                                            "Если вы указывали файл для чтения - проверьте тип файла, должен быть \"*.txt\"\n"+
                                                            "Если вы указывали значение аргумента, которое имеет символ пробела - оберните значение целиком с помощью \"");

                    sources.add(args[i].replace('\\', '/'));

                    // Блокируем этот аргумент во избежание повтора
                    // А также аргументы, что должны быть перед ним
                    argsBlocker[0] = true;
                    argsBlocker[1] = true;
                    argsBlocker[2] = true;
                    argsBlocker[3] = true;
                    break;
                }
            }

            // Источники обязательно должны быть переданы
            if (sources.size() == 0)
                throw new ArgumentsHandlerException("Не было передано ни одного файла для чтения");

            // Путь к файлу - по сути и есть префикс
            // Соединяем два префикса
            outPath += prefix;
        }
        catch (ArgumentsHandlerException ex) {
            System.out.println("Ошибка при обработке аргументов\nРабота завершена во избежание нежелательного результата");
            System.err.println(ex.getMessage());
            return;
        }

        // Объявляем необходимые обработчики
        ArrayList<TypeHandler> handlers = new ArrayList<TypeHandler>();
        try {
            handlers.add(new TypeHandler("Integers", "([-+]?\\d+)|(\\([-+]?\\d+\\))",
                                         fullStat, append,
                                         outPath + "integers.txt",
                                         new String[][] { { "(", "" }, { ")", "" } }));
            handlers.add(new TypeHandler("Floats", "[-+]?\\d+[.,]\\d+([EeЕе]\\^?(([-+]?\\d+)|(\\([-+]?\\d+\\))))?",
                                         fullStat, append,
                                         outPath + "floats.txt",
                                         new String[][] { { "(", "" }, { ")", "" }, { "^", "" }, { ",", "." } }));
            handlers.add(new TypeHandler("Strings", ".*",
                                         fullStat, append,
                                         outPath + "strings.txt",
                                         new String[] { "String", "String" }));
        }
        catch (ModsException ex) {
            System.out.println("Ошибка инициализации обработчиков\nПеркращение работы");
            System.err.println(ex.getMessage());
            return;
        }

        // Строка для построчного чтения файлов
        String strBuf;

        // По очереди проходим источники
        for (String source : sources) {
            try (BufferedReader fileIn = new BufferedReader(new FileReader(source))) {
                while ((strBuf = fileIn.readLine()) != null) {
                    for (int i = 0; i < handlers.size(); i++) {
                        if (handlers.get(i).compare(strBuf))
                            break;
                    }
                }
            }
            // Более конкретная ошибка для потока вывода
            catch (FileCreationException ex) {
                System.out.println("Ошибка создания каталогов/файлов для сохранения результатов\nПрекращение работы");
                System.err.println(ex.getMessage());
                return;
            }
            // Специально описанное исключение для различия
            // В каком из потоков возникла ошибка
            catch (WriterException ex) {
                System.out.println("Ошибка записи результатов\nПрекращение работы");
                System.err.println(ex.getMessage());
                return;
            }
            catch (Exception ex) {
                System.out.printf("Ошибка работы с потоком чтения файла %s\nФайл игнорируется\n", source);
                System.err.println(ex.getMessage());
            }
        }

        // Закрываем потоки вывода в обработчиках
        for (int i = 0; i < handlers.size(); i++) {
            try {
                handlers.get(i).closeWriter();
            }
            catch (IOException ex) {
                System.out.printf("Ошибка при закрытии потока записи результатов\nПрекращение работы\n");
                System.err.println(ex.getMessage());
                return;
            }
        }

        // Выводим статистику из обработчиков
        for (int i = 0; i < handlers.size(); i++)
            handlers.get(i).printStats();
    }
}