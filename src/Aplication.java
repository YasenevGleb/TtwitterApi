import org.apache.log4j.Logger;

import java.io.*;

public class Aplication {
    private static final Logger logger = Logger.getLogger(Aplication.class);
    public static String[] getList() {
        StringBuilder stringOfNames= new StringBuilder();
        try(FileInputStream fin=new FileInputStream("InputLogins.txt"))
        {
            int i=-1;
            while((i=fin.read())!=-1){
                stringOfNames.append((char) i);
            }
        }
        catch(IOException ex){
            logger.debug("You must check that you have InputLogins.txt in main folder: "+ex);
        }
        String [] array=getNames(stringOfNames.toString());
        return array;
    }
    public static String[] getNames(String str){
        String arrayForNames[];
        arrayForNames=str.split(" ");
        return arrayForNames;
    }
}
