import java.io.*;
import java.util.*;
import java.math.*;

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

        // Переменные постоянной статистики
        BigInteger counterInt = BigInteger.ZERO;
        BigInteger counterFloat = BigInteger.ZERO;
        BigInteger counterStr = BigInteger.ZERO;


        // Переменные полной статистики
        BigInteger minInt = BigInteger.ZERO;
        BigInteger maxInt = BigInteger.ZERO;
        BigInteger sumInt = BigInteger.ZERO;
        BigDecimal minFloat = BigDecimal.ZERO;
        BigDecimal maxFloat = BigDecimal.ZERO;
        BigDecimal sumFloat = BigDecimal.ZERO;
        int minStr = 0;
        int maxStr = 0;

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
                    while ((strBuf=fileIn.readLine()) != null)
                    {
                        if (strBuf.matches("([-+]?\\d+)|(\\([-+]?\\d+\\))"))
                        {
                            integersOut.write(strBuf);
                            integersOut.newLine();

                            counterInt = counterInt.add(BigInteger.ONE);

                            if (!fullStat)
                                continue;

                            BigInteger bufInt = new BigInteger(strBuf.replace("(", "")
                                                                     .replace(")", ""));

                            if (counterInt.equals(BigInteger.ONE))
                            {
                                minInt = bufInt;
                                maxInt = bufInt;
                                sumInt = bufInt;
                                continue;
                            }

                            sumInt = sumInt.add(bufInt);

                            if (maxInt.compareTo(bufInt) == -1)
                            {
                                maxInt = bufInt;
                            }
                            else if (minInt.compareTo(bufInt) == 1)
                            {
                                minInt = bufInt;
                            }
                        }
                        else if (strBuf.matches("[-+]?\\d+[.,]\\d+([EeЕе]\\^?(([-+]?\\d+)|(\\([-+]?\\d+\\))))?"))
                        {
                            floatsOut.write(strBuf);
                            floatsOut.newLine();

                            counterFloat = counterFloat.add(BigInteger.ONE);

                            if (!fullStat)
                                continue;

                            BigDecimal bufFloat = new BigDecimal(strBuf.replace("^", "")
                                                                       .replace("(", "")
                                                                       .replace(")", "")
                                                                       .replace(",", "."));

                            if (counterFloat.equals(BigInteger.ONE))
                            {
                                minFloat = bufFloat;
                                maxFloat = bufFloat;
                                sumFloat = bufFloat;
                                continue;
                            }

                            sumFloat = sumFloat.add(bufFloat);

                            if (maxFloat.compareTo(bufFloat) == -1)
                            {
                                maxFloat = bufFloat;
                            }
                            else if (minFloat.compareTo(bufFloat) == 1)
                            {
                                minFloat = bufFloat;
                            }
                        }
                        else 
                        {
                            stringsOut.write(strBuf);
                            stringsOut.newLine();

                            counterStr = counterStr.add(BigInteger.ONE);

                            if (!fullStat)
                                continue;

                            int strLen = strBuf.length();

                            if (counterStr.equals(BigInteger.ONE))
                            {
                                minStr = strLen;
                                maxStr = strLen;
                                continue;
                            }

                            if (strLen > maxStr)
                            {
                                maxStr = strLen;
                            }
                            else if (strLen < minStr)
                            {
                                minStr = strLen;
                            }
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

        System.out.printf("Integer stats:\n\tCount: %s\n", counterInt);
        if (fullStat && (counterInt.compareTo(BigInteger.ZERO) == 1))
            System.out.printf("\tMax: %s\n\tMin: %s\n\tSum: %s\n\tAverange: %s\n", maxInt,
                                                                                          minInt,
                                                                                          sumInt,
                                                                                          sumInt.divide(counterInt));
        System.out.printf("Float stats:\n\tCount: %s\n", counterFloat);
        if (fullStat && (counterFloat.compareTo(BigInteger.ZERO) == 1))
            System.out.printf("\tMax: %s\n\tMin: %s\n\tSum: %s\n\tAverange: %s\n", maxFloat,
                                                                                          minFloat,
                                                                                          sumFloat,
                                                                                          sumFloat.divide(new BigDecimal(counterFloat),
                                                                                                    6,
                                                                                                          RoundingMode.HALF_DOWN));
        System.out.printf("String stats:\n\tCount: %s\n", counterStr);
        if (fullStat && (counterStr.compareTo(BigInteger.ZERO) == 1))
            System.out.printf("\tMax len: %s\n\tMin len: %s\n", maxStr,
                                                                       minStr);
    }
}