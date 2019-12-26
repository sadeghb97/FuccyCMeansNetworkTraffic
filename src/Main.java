import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {
    private static ArrayList<Point> data;
    private static ArrayList<ArrayList> domains;
    private static ArrayList<double[]> boundsList;

    public static void main(String[] args) {
        while (mainMenu());
        /*ArrayList listOne = new ArrayList();
        listOne.add(12);
        listOne.add(12.24);
        listOne.add(124.215);
        listOne.add(29.78);
        listOne.add(32.644);
        ArrayList listTwo = (ArrayList) listOne.clone();
        listTwo.remove(2);
        listTwo.remove(3);
        UsefulUtils.printList(listOne);
        UsefulUtils.printList(listTwo);*/

    }

    public static boolean mainMenu(){
        StylishPrinter.println("\nMenu:", StylishPrinter.BOLD_RED);
        System.out.println("1: Prepare Data");
        System.out.println("2: Show Domains");
        System.out.println("3: K-Means Clustering");
        System.out.println("4: Fuzzy C-Means Clustering");
        System.out.println("5: Exit");
        System.out.print("\nEnter Your Choice: ");
        int choice = SBProScanner.inputInt(1, 5);

        if(choice==1) prepareData();
        else if(choice == 2) showDomains();
        else if(choice == 3){ if(data != null) Cluster.kmeansClustering(data);}
        else if(choice == 4){ if(data != null) Cluster.fcmClustering(data);}
        else if(choice == 5) return false;
        return true;
    }

    public static void prepareData(){
        data = new ArrayList();
        domains = new ArrayList();
        boundsList = new ArrayList();
        try {
            System.out.print("Preparing Data: ");
            String contents = UsefulUtils.readAllFile("kddcup.data_10_percent_corrected");
            String[] pieces = contents.split("\n");

            for (String item : pieces){
                String[] itemPieces = item.split(",");
                ArrayList itemValues = new ArrayList();
                int i=0;
                for (String v: itemPieces) {
                    itemValues.add(v);
                    if(domains.size() <= i) domains.add(new ArrayList());
                    if(!domains.get(i).contains(v)) domains.get(i).add(v);
                    i++;
                }
                data.add(new Point(itemValues));
            }
            System.out.print(".");
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        for(int i=0; domains.size()>i; i++){
            if(UsefulUtils.isNumeric((String) domains.get(i).get(0))){
                double[] bounds = Cluster.getMinAndMax(domains.get(i));
                boundsList.add(bounds);
            }
            else {
                double[] bounds = {0, domains.get(i).size() - 1};
                boundsList.add(bounds);
            }
        }
        System.out.print(".");

        for (int i=0; data.size()>i; i++) {
            data.get(i).create();
            for(int j=0; data.get(i).source.size()>j; j++){
                String value = (String) data.get(i).source.get(j);

                double doubleValue;
                if(UsefulUtils.isNumeric(value))
                    doubleValue = UsefulUtils.parseDouble(value);
                else doubleValue = domains.get(j).indexOf(value);

                double[] bounds = boundsList.get(j);
                double scaledValue = UsefulUtils.scaleValue(bounds[0], bounds[1], doubleValue);
                if(j < (data.get(i).size() - 1))
                    data.get(i).add(scaledValue);
                else data.get(i).add(scaledValue == 0 ? 0 : 1);
            }
        }
        System.out.println(".");
        System.out.println(data.size() + " records loaded successfuly!");
    }

    public static void showDomains(){
        for (int i=0; domains.size()>i; i++) {
            System.out.print("Domain " + i + ": ");
            UsefulUtils.printList(domains.get(i));
            System.out.print("Bound " + i + ": ");
            Cluster.printBound(boundsList.get(i));
        }
    }
}
