package task;

import java.io.*;
import java.math.*;
import task.MyExceptions.*;

/**
 * <p>
 * Класс - обработчик типа.
 * <p>
 * Решает относить указанное значение к его типу или нет, записывает прошедшие
 * значения, ведет статистику,
 **/

class TypeHandler {
    private String name;

    private String pattern;
    private String[][] mods;

    private File outFile;
    private BufferedWriter out;

    private boolean fullStat;

    private boolean append;

    private BigInteger counter;
    private BigDecimal max;
    private BigDecimal min;
    private BigDecimal sum;

    {
        out = null;
        mods = null;
        counter = BigInteger.ZERO;
        max = null;
        min = null;
        sum = null;
    }

    /**
     * @param name     Название типа
     * @param pattern  Регулярное выражение, основываясь на октором решается
     *                 относить ли значение к этому типу
     * @param fullStat Решение о ведении полной статистики
     * @param append   Решение о расрении существующих файлов
     * @param outPath  Путь к файлу, где будут записываться результаты отбора
     * @param mods     Модификаторы строки, для преобразования к {@code BigDecimal},
     *                 Если передано {@code {"String", "String"}} то используется
     *                 {@code BigDecimal(value.length())}
     * @throws ModsException Если переданный модификатор имеет размерность != 2
     */
    public TypeHandler(final String name, final String pattern, final boolean fullStat, final boolean append,
                       final String outPath, final String[]... mods) throws ModsException {
        this.name = name;
        this.pattern = pattern;
        this.outFile = new File(outPath);
        this.fullStat = fullStat;
        this.append = append;

        if ((mods != null))
            for (String[] i : mods)
                if (i.length != 2)
                    throw new ModsException("Некорректно указаны модификаторы строки");

        this.mods = mods;
    }

    /**
     * Создание необходимых каталогов, файлов и инициализация {@code BufferWriter}
     * 
     * @throws IOException       Ошибка ввода/вывода
     * @throws SecurityException Ошибка прав доступа
     */
    private void initWriter() throws IOException, SecurityException {
        File root = outFile.getParentFile();

        if ((root != null) && !root.exists())
        {
            root.mkdirs();
            if (!root.exists())
                throw new SecurityException("Не удалось создать каталог для вывода");
        }
        if (!outFile.exists())
        {
            outFile.createNewFile();
            if (!outFile.exists())
                throw new SecurityException("Не удалось создать файл для вывода");
        }

        out = new BufferedWriter(new FileWriter(outFile, append));
    }

    /**
     * Инициализирует переменные для полной статистики
     * 
     * @param value Начальное значение
     */
    private void initStats(BigDecimal value) {
        max = value;
        min = value;
        sum = value;
    }

    /**
     * Преобразует {@code String value} в {@code BigDecimal} с помощью указанных
     * {@code mods} для возможности вести полную статистику
     * 
     * @param value Значение которое должно быть преобразовано
     * @return {@code BigDecimal}
     */
    private BigDecimal applyMods(final String value) {
        String out = value;
        if (mods == null)
            return new BigDecimal(out);

        if ((mods[0][0] == "String") && (mods[0][1] == "String"))
            return new BigDecimal(out.length());

        for (String[] i : mods)
            out = out.replace(i[0], i[1]);

        return new BigDecimal(out);
    }

    /**
     * <p>
     * Основываясь на регулярном выражении, переданном в конструктор метож
     * {@code compare(value)} решает относить-ли переданное значение {@code value} к
     * определенном типу или нет.
     * <p>
     * Основываясь на рещении, {@code value} сохраняется в файл, указанный в
     * конструкторе
     *
     * @return {@code true} если файл сохранен; {@code false} иначе.
     * @throws WriterException       Ошибка при записи значения
     * @throws FileCreationException Ошибка при создании выходных каталогов/файлов
     */
    public boolean compare(final String value) throws FileCreationException, WriterException {
        if (!value.matches(pattern))
            return false;

        if (counter.equals(BigInteger.ZERO)) {
            try {
                initWriter();
            }
            catch (IOException ex) {
                throw new FileCreationException(
                        "Ошибка ввода/вывода при попытке создать файл/каталог результата\n" + ex.getMessage(),
                        ex.getCause());
            }
            catch (SecurityException ex) {
                throw new FileCreationException("Нет прав чтобы создать файл/каталог результата\n" + ex.getMessage(),
                        ex.getCause());
            }
        }

        try {
            out.write(value);
            out.newLine();
        }
        catch (IOException ex) {
            throw new WriterException("Ошибка ввода/вывода при попытке сохранить результат\n" + ex.getMessage(),
                    ex.getCause());
        }

        counter = counter.add(BigInteger.ONE);

        if (!fullStat)
            return true;

        BigDecimal bufValue = applyMods(value);

        if (counter.equals(BigInteger.ONE)) {
            initStats(bufValue);
            return true;
        }

        sum = sum.add(bufValue);

        if (max.compareTo(bufValue) == -1)
            max = bufValue;
        else if (min.compareTo(bufValue) == 1)
            min = bufValue;

        return true;
    }

    /**
     * Закрывает потоки записи
     * <p>
     * Предполагается, что вызывается, когда новые значения перестают поступать
     * 
     * @throws IOException Ошибка ввода/вывода
     */
    public void closeWriter() throws IOException {
        if (out == null)
            return;

        out.flush();
        out.close();
    }

    /**
     * Выводить собранную статистику в стандартный поток вывода
     */
    public void printStats() {
        System.out.printf("%s stats:\n\tCounter: %s\n", name, counter);
        if (!fullStat || (counter.compareTo(BigInteger.ZERO) != 1))
            return;

        System.out.printf("\tMax: %s\n\tMin: %s\n", max, min);

        if ((mods[0][0] != "String") && mods[0][1] != "String")
            System.out.printf("\tSum: %s\n\tAverange: %s\n", sum,
                                                                    sum.divide(new BigDecimal(counter), 3, RoundingMode.HALF_DOWN));
    }
}