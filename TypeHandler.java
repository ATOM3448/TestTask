import java.io.*;
import java.math.*;

class TypeHandler
    {
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

        public TypeHandler(final String name, final String pattern, final boolean fullStat, final boolean append, final String outPath, final String[] ...mods)throws Exception
        {
            this.name = name;
            this.pattern = pattern;
            this.outFile = new File(outPath);
            this.fullStat = fullStat;
            this.append = append;

            if ((mods != null))
                for (String[] i:mods)
                    if (i.length != 2)
                        throw new Exception("Некорректно указаны модификаторы строки");

            this.mods = mods;
        }

        private void initWriter() throws Exception
        {
            File root = outFile.getParentFile();

            if (!root.exists())
                root.mkdirs();
            if (!outFile.exists())
                outFile.createNewFile();

            out = new BufferedWriter(new FileWriter(outFile, append));
        }

        private void initStats(BigDecimal value)
        {
            max = value;
            min = value;
            sum = value;
        }

        private BigDecimal applyMods(final String value)
        {
            String out = value;
            if (mods == null)
                return new BigDecimal(out);

            if ((mods[0][0] == "String") && (mods[0][1] == "String"))
                return new BigDecimal(out.length());

            for (String[] i:mods)
                out = out.replace(i[0], i[1]);

            return new BigDecimal(out);
        }

        public boolean compare(final String value) throws Exception
        {
            if (!value.matches(pattern))
                return false;

            if (counter.equals(BigInteger.ZERO))
                initWriter();

            out.write(value);
            out.newLine();

            counter = counter.add(BigInteger.ONE);

            if (!fullStat)
                return true;

            BigDecimal bufValue = applyMods(value);

            if (counter.equals(BigInteger.ONE))
            {
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

        public void closeWriter()throws IOException
        {
            if (out == null)
                return;

            out.flush();
            out.close();
        }

        public void printStats()
        {
            System.out.printf("%s stats:\n\tCounter: %s\n", name, counter);
            if (!fullStat || (counter.compareTo(BigInteger.ZERO) != 1))
                return;

            System.out.printf("\tMax: %s\n\tMin: %s\n", max, min);

            if ((mods[0][0] != "String") && mods[0][1] != "String")
                System.out.printf("\tSum: %s\n\tAverange: %s\n", sum, sum.divide(new BigDecimal(counter), 3, RoundingMode.HALF_DOWN));
        }
    }