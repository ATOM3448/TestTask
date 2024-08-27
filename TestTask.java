import java.io.*;
import java.util.*;

class TestTask 
{
    public static void main(String[] args)
    {
        boolean fullStat = false;
        boolean append = false;
        String outPath = "";
        String prefix = "";
        ArrayList<String> sources = new ArrayList<String>();

        try
        {
            boolean[] argsBlocker = {false, false, false, false, false};

            for (int i = 0; i < args.length; i++)
            {
                switch (args[i])
                {
                    case "-f":
                        if (argsBlocker[0])
                            throw new Exception("Аргумент \"-f\" указан некорректно");

                        fullStat = true;

                        argsBlocker[0] = true;
                        break;
                    case "-a":
                        if (argsBlocker[1])
                            throw new Exception("Аргумент \"-a\" указан некорректно");

                        append = true;

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        break;
                    case "-p":
                        if (argsBlocker[2])
                            throw new Exception("Аргумент \"-p\" указан некорректно");

                        try
                        {
                            prefix = args[++i];
                        }
                        catch (ArrayIndexOutOfBoundsException ex)
                        {
                            throw new Exception("Не удается найти значение аргумента \"-p\"");
                        }

                        if (prefix.replace('\\', '/').contains("/"))
                            throw new Exception("Значение аргумента \"-p\" указано некорректно\n" +
                                                "Если вы хотели указать путь к каталогу результатов - используйте \"-o <path>\"");

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        argsBlocker[2] = true;
                        break;
                    case "-o":
                        if (argsBlocker[3])
                            throw new Exception("Аргумент \"-o\" указан некорректно");

                        try
                        {
                            outPath = args[++i].replace('\\', '/');
                        }
                        catch (ArrayIndexOutOfBoundsException ex)
                        {
                            throw new Exception("Не удается найти значение аргумента \"-o\"");
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
                            throw new Exception("Незивестный аргумент\nЕсли вы указывали файл для чтения - проверьте тип файла");

                        sources.add(args[i].replace('\\', '/'));

                        argsBlocker[0] = true;
                        argsBlocker[1] = true;
                        argsBlocker[2] = true;
                        argsBlocker[3] = true;
                        break;
                }
            }

            if (sources.size() == 0)
                throw new Exception("Не было передано ни одного файла для чтения");
        }
        catch (Exception ex)
        {
            System.out.println("Ошибка при чтении аргументов\nПрограмма завершена во избежание нежелательного результата");
            System.err.println(ex.getMessage());
            return;
        }

        outPath += prefix;

        ArrayList<TypeHandler> handlers = new ArrayList<TypeHandler>();
        try
        {
            handlers.add(new TypeHandler("Integers",
                                         "([-+]?\\d+)|(\\([-+]?\\d+\\))",
                                         fullStat, append,
                                         outPath+"integers.txt", 
                                         new String[][] {{"(", ""}, {")", ""}}));
            handlers.add(new TypeHandler("Floats",
                                         "[-+]?\\d+[.,]\\d+([EeЕе]\\^?(([-+]?\\d+)|(\\([-+]?\\d+\\))))?",
                                         fullStat, append,
                                         outPath+"floats.txt",
                                         new String[][] {{"(", ""}, {")", ""}, {"^", ""}, {",", "."}}));
            handlers.add(new TypeHandler("Strings",
                                         ".*",
                                         fullStat, append,
                                         outPath+"strings.txt",
                                         new String[] {"String", "String"}));
        }
        catch (Exception ex)
        {
            System.err.println(ex.getMessage());
        }

        String strBuf;

        for (String source : sources)
        {
            try (BufferedReader fileIn = new BufferedReader(new FileReader(source)))
            {
                while ((strBuf = fileIn.readLine()) != null)
                {
                    for (int i = 0; i < handlers.size(); i++)
                    {
                        try
                        {
                            if (handlers.get(i).compare(strBuf))
                                break;
                        }
                        catch (Exception ex)
                        {
                            System.out.println(ex.getMessage());
                        }
                    }
                }
            }
            catch (IOException ex) 
            {
                if (java.util.regex.Pattern.compile(outPath+"(integers|floats|strings).txt").matcher(ex.getMessage()).find())
                {
                    System.out.printf("Ошибка работы с потоком записи результатов\n%s\nПрекращение работы\n", ex.getMessage());
                    break;
                }

                System.out.printf("Ошибка работы с потоком чтения файла %s\n%s\nФайл игнорируется\n", source,
                                                                                                             ex.getMessage());
            }
        }

        try 
        {
            for (int i = 0; i < handlers.size(); i++)
            handlers.get(i).closeWriter();
        }
        catch (IOException ex)
        {
            System.out.printf("Ошибка при закрытии потока записи результатов\n%s\nПрекращение работы\n", ex.getMessage());
            return;
        }

        for (int i = 0; i < handlers.size(); i++)
                handlers.get(i).printStats();
    }
}