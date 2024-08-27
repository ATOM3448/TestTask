import java.io.*;
import java.util.*;
import MyExceptions.*;

class TestTask {
    public static void main(String[] args) {
        boolean fullStat = false;
        boolean append = false;
        String outPath = "";
        String prefix = "";
        ArrayList<String> sources = new ArrayList<String>();

        try {
            boolean[] argsBlocker = { false, false, false, false, false };

            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-f":
                        if (argsBlocker[0])
                            throw new ArgumentsHandlerException("Аргумент \"-f\" указан некорректно");

                        fullStat = true;

                        argsBlocker[0] = true;
                        break;
                    case "-a":
                        if (argsBlocker[1])
                            throw new ArgumentsHandlerException("Аргумент \"-a\" указан некорректно");

                        append = true;

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        break;
                    case "-p":
                        if (argsBlocker[2])
                            throw new ArgumentsHandlerException("Аргумент \"-p\" указан некорректно");

                        try {
                            prefix = args[++i];
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new ArgumentsHandlerException("Не удается найти значение аргумента \"-p\"");
                        }

                        if (prefix.replace('\\', '/').contains("/"))
                            throw new ArgumentsHandlerException("Значение аргумента \"-p\" указано некорректно\n" +
                                    "Если вы хотели указать путь к каталогу результатов - используйте \"-o <path>\"");

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        argsBlocker[2] = true;
                        break;
                    case "-o":
                        if (argsBlocker[3])
                            throw new ArgumentsHandlerException("Аргумент \"-o\" указан некорректно");

                        try {
                            outPath = args[++i].replace('\\', '/');
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            throw new ArgumentsHandlerException("Не удается найти значение аргумента \"-o\"");
                        }

                        if (!outPath.endsWith("/"))
                            outPath += "/";

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        argsBlocker[2] = true;
                        argsBlocker[3] = true;
                        break;
                    default:
                        if (!args[i].endsWith(".txt"))
                            throw new ArgumentsHandlerException("Незивестный аргумент\n" +
                                    "Если вы указывали файл для чтения - проверьте тип файла\n" +
                                    "Если вы указывали значение аргумента, которое имеет символ пробела - оберните значение целиком с помощью \"");

                        sources.add(args[i].replace('\\', '/'));

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        argsBlocker[2] = true;
                        argsBlocker[3] = true;
                        break;
                }
            }

            if (sources.size() == 0)
                throw new ArgumentsHandlerException("Не было передано ни одного файла для чтения");
        } catch (ArgumentsHandlerException ex) {
            System.out.println(
                    "Ошибка при обработке аргументов\nРабота завершена во избежание нежелательного результата");
            System.err.println(ex.getMessage());
            return;
        }

        outPath += prefix;

        ArrayList<TypeHandler> handlers = new ArrayList<TypeHandler>();
        try {
            handlers.add(new TypeHandler("Integers",
                    "([-+]?\\d+)|(\\([-+]?\\d+\\))",
                    fullStat, append,
                    outPath + "integers.txt",
                    new String[][] { { "(", "" }, { ")", "" } }));
            handlers.add(new TypeHandler("Floats",
                    "[-+]?\\d+[.,]\\d+([EeЕе]\\^?(([-+]?\\d+)|(\\([-+]?\\d+\\))))?",
                    fullStat, append,
                    outPath + "floats.txt",
                    new String[][] { { "(", "" }, { ")", "" }, { "^", "" }, { ",", "." } }));
            handlers.add(new TypeHandler("Strings",
                    ".*",
                    fullStat, append,
                    outPath + "strings.txt",
                    new String[] { "String", "String" }));
        } catch (ModsException ex) {
            System.out.println("Ошибка инициализации обработчиков\nПеркращение работы");
            System.err.println(ex.getMessage());
            return;
        }

        String strBuf;

        for (String source : sources) {
            try (BufferedReader fileIn = new BufferedReader(new FileReader(source))) {
                while ((strBuf = fileIn.readLine()) != null) {
                    for (int i = 0; i < handlers.size(); i++) {
                        if (handlers.get(i).compare(strBuf))
                            break;
                    }
                }
            } catch (WriterException ex) {
                System.out.println("Ошибка записи результатов\nПрекращение работы");
                System.err.println(ex.getMessage());
                return;
            } catch (FileCreationException ex) {
                System.out.println("Ошибка создания каталогов/файлов для сохранения результатов\nПрекращение работы");
                System.err.println(ex.getMessage());
                return;
            } catch (Exception ex) {
                System.out.printf("Ошибка работы с потоком чтения файла %s\nФайл игнорируется\n", source);
                System.err.println(ex.getMessage());
            }
        }

        for (int i = 0; i < handlers.size(); i++) {
            try {
                handlers.get(i).closeWriter();
            } catch (IOException ex) {
                System.out.printf("Ошибка при закрытии потока записи результатов\nПрекращение работы\n");
                System.err.println(ex.getMessage());
                return;
            }
        }

        for (int i = 0; i < handlers.size(); i++)
            handlers.get(i).printStats();
    }
}