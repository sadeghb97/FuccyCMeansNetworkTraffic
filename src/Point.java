import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Point implements Serializable {
    public ArrayList source;
    public ArrayList cordinates;
    double firstClusterMembership = 0;
    double secondClusterMembership = 0;

    Point(ArrayList list){
        source = list;
    }

    public void create(){
        cordinates = new ArrayList();
    }

    public double get(int index){
        return ((double) cordinates.get(index));
    }

    public int size(){
        return cordinates.size();
    }

    public boolean add(double value){
        return cordinates.add(value);
    }

    public double set(int index, double value){
        return ((double) cordinates.set(index, value));
    }

    public boolean isNormal(){
        return ((double) cordinates.get(41)) == 0;
    }

    public static Point createRandomPoint() {
        Random random = new Random();
        Point point = new Point(null);
        point.create();
        for(int i=0; 42>i; i++){
            point.add(random.nextDouble());
        }
        return point;
    }

    public static Point createRandomPoint(ArrayList<Point> data) {
        Random random = new Random();
        Point point = new Point(null);
        point.create();
        for(int i=0; 42>i; i++){
            int randIndex = random.nextInt(data.size());
            point.add(data.get(randIndex).get(i));
        }
        return point;
    }

    public static Point createZeroPoint(){
        Point point = new Point(null);
        point.create();
        for(int i=0; 42>i; i++){
            point.add(0);
        }
        return point;
    }
}
