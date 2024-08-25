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

        if (outPath != null)
        {
            try
            {
                new File(outPath).mkdirs();
            }
            catch (SecurityException ex)
            {
                System.err.printf("Ошибка прав доступа при создании каталога вывода\n%s\nУстановлен путь по умолчанию\n", ex.getMessage());
                outPath = "";
            }
        }
        

        // Объединим путь и префикс
        outPath += prefix;

        // Переменные постоянной статистики
        BigInteger counterInt = BigInteger.ZERO;
        BigInteger counterFloat = BigInteger.ZERO;
        BigInteger counterStr = BigInteger.ZERO;

        // Переменные полной статистики
        BigInteger minInt = null;
        BigInteger maxInt = null;
        BigInteger sumInt = null;
        BigDecimal minFloat = null;
        BigDecimal maxFloat = null;
        BigDecimal sumFloat = null;
        int minStr = 0;
        int maxStr = 0;

        // Потоки вывода результатов
        BufferedWriter integersOut = null;
        BufferedWriter floatsOut = null;
        BufferedWriter stringsOut = null;

        // Буффер для считываемых строк
        String strBuf;

        // Последовательный проход по источникам
        try
        {
            for (String source : sources)
            {
                try (BufferedReader fileIn = new BufferedReader(new FileReader(source)))
                {
                    // Считываем строку и через регулярки проверяем тип
                    while ((strBuf = fileIn.readLine()) != null)
                    {
                        if (strBuf.matches("([-+]?\\d+)|(\\([-+]?\\d+\\))"))
                        {
                            counterInt = counterInt.add(BigInteger.ONE);

                            if (counterInt.equals(BigInteger.ONE))
                                integersOut = new BufferedWriter(new FileWriter(outPath + "integers.txt", append));

                            integersOut.write(strBuf);
                            integersOut.newLine();

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
                            counterFloat = counterFloat.add(BigInteger.ONE);

                            if (counterFloat.equals(BigInteger.ONE))
                                floatsOut = new BufferedWriter(new FileWriter(outPath + "floats.txt", append));

                            floatsOut.write(strBuf);
                            floatsOut.newLine();

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
                            counterStr = counterStr.add(BigInteger.ONE);
                            if (counterStr.equals(BigInteger.ONE))
                                stringsOut = new BufferedWriter(new FileWriter(outPath + "strings.txt", append));

                            stringsOut.write(strBuf);
                            stringsOut.newLine();

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
                    System.out.printf("Ошибка работы с потоком чтения файла %s\n%s\nФайл игнорируется\n", source,
                                                                                                                ex.getMessage());
                }
            }

        // Сохраняем результат и закрываем потоки
        if (counterInt.compareTo(BigInteger.ZERO) == 1)
        {
            integersOut.flush();
            integersOut.close();
        }

        if (counterFloat.compareTo(BigInteger.ZERO) == 1)
        {
            floatsOut.flush();
            floatsOut.close();
        }

        if (counterStr.compareTo(BigInteger.ZERO) == 1)
        {
            stringsOut.flush();
            stringsOut.close();
        }
    }
    catch(IOException ex) 
    {
        System.out.printf("Ошибка работы с потоками записи результатов\n%s\nЗавершение программы\n", ex.getMessage());
        return;
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