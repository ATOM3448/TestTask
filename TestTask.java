import java.io.*;
import java.util.*;

class TestTask
{
    // Функция считывания значения аргумента с пробелами
    private String readArgs(String[] args, int startIndex)
    {
        if (!args[startIndex].startsWith("\""))
        {
            if (args[startIndex].startsWith("-"))
                return "";
            return args[startIndex];
        }

        return;
    }

    public static void main(String[] args)
    
        // Определим флаги наличия аргументов
        boolean lastArgSkiped = false;

        // Определим поля хранения аргументов
        boolean fullStat = false;
        boolean append = false;
        String outPath = "";
        String prefix = "";
        ArrayList<String> sources = new ArrayList<String>();

        // Считываем аргументы без доп. пакетов
        for (int i = 0; i < args.length; i++)
        {
            /*
            Любой многократный ввод одинаковых аргументов никак не влияет на ход выполенния,
            кроме времени на микроуровне.
            */
            switch (args[i])
            {
                case "-f":
                    fullStat = true;
                    break;
                case "-s":
                    /*
                    Если установлено, что нужна полная статистика,
                    значит уже был использован противоположный аргумент,
                    значит выполнение данного блока кода ошибка и игнорируется.
                    В целом данный блок кода ничего и не делает
                    но данный аргумент нужен по ТЗ
                    */
                    break;
                case "-a":
                    append = true;
                    break;
                case "-o":
                    if (outPath != "")
                        break;
                    
                    break;
                case "-p":

                    break;
                default:
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
                        if (strBuf.matches("[-+]?\\d+"))
                        {
                            integersOut.write(strBuf);
                            integersOut.newLine();
                        }
                        else if (strBuf.matches("[-+]?\\d+[.,]\\d+([EeЕе]\\^?[-+]?\\d+)?"))
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
                    System.out.println("Ошибка работы с потоком чтения файла " + source);
                    System.out.println(ex.getMessage());
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
            System.out.println("Ошибка работы с потоком записи");
            System.out.println(ex.getMessage());
        }
    }
}