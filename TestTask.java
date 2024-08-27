// ПЕРЕВЕДИ В ООП ПАРСИНГ АРГУМЕНТОВ
// РЕАЛИЗУЙ ПЕРЕХВАТ ИСКЛЮЧЕНИЙ

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

        for (int i = 0; i < args.length; i++)
        {
            switch (args[i])
            {
                case "-f":
                    fullStat = true;
                    break;
                case "-s":
                    break;
                case "-a":
                    append = true;
                    break;
                case "-o":
                    outPath = args[++i].replace('\\', '/');
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                default:
                    sources.add(args[i]);
                    break;
            }
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
            System.out.println(ex.getMessage());
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