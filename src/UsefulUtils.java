import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class UsefulUtils {
    public static String readAllFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append(System.lineSeparator());
            line = br.readLine();
        }
        String everything = sb.toString();
        br.close();
        return everything;
    }

    public static double generateRandomDoubleNumber(double min, double max){
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }

    public static String getWideNumber(double number){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');

        DecimalFormat df = new DecimalFormat();
        df.setDecimalFormatSymbols(symbols);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(2);
        return df.format(number);
    }

    public static boolean isNumeric(String strNum){
        if (strNum == null) return false;
        try {
            double d = Double.parseDouble(strNum);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static double parseDouble(String strNum){
        if (strNum == null) return 0;
        try {
            double d = Double.parseDouble(strNum);
            return d;
        }
        catch (NumberFormatException nfe) {
            return 0;
        }
    }

    public static String listToString(List list){
        if(list == null) return "";
        boolean first = true;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Object object : list) {
            if(!first) stringBuilder.append(", ");
            first = false;
            stringBuilder.append(object);
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static void printList(Collection collection){
        if(collection == null) System.out.println();
        boolean first = true;
        StylishPrinter.print("[", StylishPrinter.BOLD_CYAN);
        for (Object object : collection) {
            if(!first) StylishPrinter.print(", ", StylishPrinter.BOLD_CYAN);
            first = false;
            System.out.print(object);
        }
        StylishPrinter.println("]", StylishPrinter.BOLD_CYAN);
    }

    public static double scaleValue(double min, double max, double rawValue){
        if(min < 0){
            max += Math.abs(min);
            min = 0;
        }

        if(max == min) return 0;
        return (rawValue - min) / (max - min);
    }

    public static String formatDecimalNumber(double number, int maxFractionDigits){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(maxFractionDigits);
        return df.format(number);
    }

    public static void updateProgress(String title, double done, double all){
        System.out.print("\r");
        System.out.print(title + ": " +
                UsefulUtils.formatDecimalNumber(done / all * 100, 2) + "%");
    }

    public static void announceFinishingTask(String annoncement){
        System.out.print("\r");
        StylishPrinter.print("✔", StylishPrinter.BOLD_GREEN);
        System.out.println(" " + annoncement);
    }
}
