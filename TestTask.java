import java.io.*;
import java.nio.charset.StandardCharsets;

class TestTask
{
    public static void main(String[] args)
    {
        String[] sources = {"in1.txt", "in2.txt"};
        try
        {
            OutputStreamWriter integersOut = new OutputStreamWriter(new FileOutputStream("integers.txt"), StandardCharsets.UTF_8);
            OutputStreamWriter floatsOut = new OutputStreamWriter(new FileOutputStream("floats.txt"), StandardCharsets.UTF_8);
            OutputStreamWriter stringsOut = new OutputStreamWriter(new FileOutputStream("strings.txt"), StandardCharsets.UTF_8);

            for (String source : sources)
            {
                try (InputStreamReader fileIn = new InputStreamReader(new FileInputStream(source), StandardCharsets.UTF_8))
                {
                    StringBuilder currentStr = new StringBuilder();
                    String strOut;
                    char[] charBuf = new char[1];
                    while (fileIn.read(charBuf) != -1)
                    {
                        if (charBuf[0] != '\n')
                        {
                            currentStr.append(charBuf[0]);
                            continue;
                        }

                        strOut = currentStr.toString();
                        currentStr.delete(0, currentStr.length());

                        if (strOut.matches("[-+]?\\d+"))
                        {
                            integersOut.write(strOut + '\n');
                            continue;
                        }
                        if (strOut.matches("[-+]?\\d+[.,]\\d+([Ee]\\^?\\[-+]?\\d+)?"))
                        {
                            floatsOut.write(strOut + '\n');
                            continue;
                        }
                        stringsOut.write(strOut + '\n');
                    }
                }
                catch (IOException ex)
                {
                    System.out.println(ex.getMessage());
                }
            }
            integersOut.flush();
            floatsOut.flush();
            stringsOut.flush();

            integersOut.close();
            floatsOut.close();
            stringsOut.close();
        }
        catch (IOException ex)
        {
            System.out.println(ex.getMessage());
        }
    }
}