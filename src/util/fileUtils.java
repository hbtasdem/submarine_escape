package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class fileUtils {

    // private constructor below, can't instantiate this class so purely static, so
    // no need for instances
    private fileUtils() {

    }

    // write a method to convert a file to a string
    public static String load_as_string(String file) {
        String result = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String buffer = ""; // we are reading the file line by line so a var to hold a temp line is
                                // necessary

            while ((buffer = reader.readLine()) != null) {
                result += buffer + "\n";
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } // cmd + shift + o to import needed packages
        return result;
    }
}
