import java.io.*;
import java.util.*;

class TestTask
{
    public static void main(String[] args)
    {
        // Определим поля хранения аргументов
        boolean fullStat = false;
        boolean append = false;
        String outPath = "";
        String prefix = "";
        ArrayList<String> sources = new ArrayList<String>();

        // Считываем аргументы без доп. пакетов
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
                    outPath = args[++i];
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                default:
                    sources.add(args[i]);
                    break;
            }
        }

        // Объединим путь и префикс
        outPath += prefix;

        try
        {
            // Потоки вывода результатов
            BufferedWriter integersOut = new BufferedWriter(new FileWriter(outPath+"integers.txt", append));
            BufferedWriter floatsOut = new BufferedWriter(new FileWriter(outPath+"floats.txt", append));
            BufferedWriter stringsOut = new BufferedWriter(new FileWriter(outPath+"strings.txt", append));

            // Буффер для считываемых строк
            String strBuf;

            // Последовательный проход по источникам
            for (String source : sources)
            {
                try (BufferedReader fileIn = new BufferedReader(new FileReader(source)))
                {
                    // Считываем строку и через регулярки проверяем тип
                    while ((strBuf=fileIn.readLine())!=null)
                    {
                        if (strBuf.matches("([-+]?\\d+)|(\\([-+]?\\d+\\))"))
                        {
                            integersOut.write(strBuf);
                            integersOut.newLine();
                        }
                        else if (strBuf.matches("[-+]?\\d+[.,]\\d+([EeЕе]\\^?(([-+]?\\d+)|(\\([-+]?\\d+\\))))?"))
                        {
                            floatsOut.write(strBuf);
                            floatsOut.newLine();
                        }
                        else 
                        {
                            stringsOut.write(strBuf);
                            stringsOut.newLine();
                        }
                    }
                }
                catch (IOException ex)
                {
                    System.out.printf("Ошибка работы с потоком чтения файла %s\n%s\nФайл игнорируется\n", source, ex.getMessage());
                }
            }

            // Сохраняем результат
            integersOut.flush();
            floatsOut.flush();
            stringsOut.flush();

            // Закрываем потоки вывода
            integersOut.close();
            floatsOut.close();
            stringsOut.close();
        }
        catch (IOException ex)
        {
            System.out.printf("Ошибка работы с потоком записи\n%s\n", ex.getMessage());
        }
    }
}