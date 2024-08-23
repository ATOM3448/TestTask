import java.io.*;
import java.nio.charset.StandardCharsets;

class TestTask
{
    public static void main(String[] args)
    {
        String[] sources = {"in1.txt", "in2.txt"};

        String strOut;

        try
        {
            BufferedWriter integersOut = new BufferedWriter(new FileWriter("integers.txt"));
            BufferedWriter floatsOut = new BufferedWriter(new FileWriter("floats.txt"));
            BufferedWriter stringsOut = new BufferedWriter(new FileWriter("strings.txt"));

            for (String source : sources)
            {
                
                try (BufferedReader fileIn = new BufferedReader(new FileReader(source)))
                {
                    while ((strOut=fileIn.readLine())!=null)
                    {
                        if (strOut.matches("[-+]?\\d+"))
                        {
                            integersOut.write(strOut);
                            integersOut.newLine();
                        }
                        else if (strOut.matches("[-+]?\\d+[.,]\\d+([EeЕе]^?[-+]?\\d+)?"))
                        {
                            floatsOut.write(strOut);
                            floatsOut.newLine();
                        }
                        else 
                        {
                            stringsOut.write(strOut);
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

            integersOut.flush();
            floatsOut.flush();
            stringsOut.flush();

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