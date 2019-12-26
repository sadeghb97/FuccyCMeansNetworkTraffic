import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class Cluster {
    public final static double M = 1.001;
    public ArrayList<Point> points;
    public Point center;
    public int normalRecords;
    public int attackRecords;
    boolean isAttacksCluster = false;

    Cluster(){
        create();
        center = Point.createRandomPoint();
        normalRecords = 0;
        attackRecords = 0;
    }

    public void create(){
        points = new ArrayList();
    }

    public void calculateNormalRecords(){
        normalRecords = 0;
        attackRecords = 0;
        for(int i=0; points.size()>i; i++){
            if(points.get(i).get(0) > 0) attackRecords++;
            else normalRecords++;
        }
    }

    public Point calculateKMeansCenter(){
        Point sumPoint = Point.createZeroPoint();
        for(int i=0; points.size()>i; i++){
            for(int j=0; points.get(i).size()>j; j++){
                sumPoint.set(j, sumPoint.get(j) + points.get(i).get(j));
            }
        }

        int clusterSize = points.size();
        for (int i=0; sumPoint.size()>i; i++){
            sumPoint.set(i, sumPoint.get(i) / clusterSize);
        }
        center = sumPoint;
        return center;
    }

    public static void calculateFCMCenter(Cluster firstCluster, Cluster secondCluster,
                                   ArrayList<Point> dataSet){
        Point sumPointOne = Point.createZeroPoint();
        double dividerOne = 0;
        Point sumPointTwo = Point.createZeroPoint();
        double dividerTwo = 0;

        for(int i=0; dataSet.size()>i; i++){
            double membOne = dataSet.get(i).firstClusterMembership;
            double membTwo = dataSet.get(i).secondClusterMembership;
            for(int j=0; dataSet.get(i).size()>j; j++){
                sumPointOne.set(j, sumPointOne.get(j) + membOne * dataSet.get(i).get(j));
                dividerOne += membOne;
                sumPointTwo.set(j, sumPointTwo.get(j) + membTwo * dataSet.get(i).get(j));
                dividerTwo += membTwo;
            }
        }

        for(int j=0; sumPointOne.size()>j; j++){
            sumPointOne.set(j, sumPointOne.get(j) / dividerOne);
            sumPointTwo.set(j, sumPointTwo.get(j) / dividerTwo);
        }
        firstCluster.center = sumPointOne;
        secondCluster.center = sumPointTwo;
    }

    public static void kmeansClustering(ArrayList<Point> data){
        Cluster firstCluster = new Cluster();
        Cluster secondCluster = new Cluster();
        ArrayList<Point> testSet = (ArrayList<Point>) data.clone();
        ArrayList<Point> learningSet = new ArrayList<>();
        int targetLearningSetSize = (int) (data.size() * 0.3);
        initLearningAndTestSet(testSet, learningSet, targetLearningSetSize);

        System.out.println();
        System.out.print("K-Means Clustering: 0%");
        int maxExecution = 300;
        double conv = 0;
        int exeNum = 0;
        for(; maxExecution>exeNum && conv < 0.96; exeNum++){
            firstCluster.create();
            secondCluster.create();
            for(int j=0; learningSet.size()>j; j++){
                Point point = learningSet.get(j);
                double firstClusterDistance = Cluster.getDistance(point, firstCluster.center);
                double secondClusterDistance = Cluster.getDistance(point, secondCluster.center);
                if(firstClusterDistance < secondClusterDistance) firstCluster.points.add(point);
                else secondCluster.points.add(point);
            }

            firstCluster.calculateKMeansCenter();
            secondCluster.calculateKMeansCenter();

            firstCluster.calculateNormalRecords();
            secondCluster.calculateNormalRecords();
            double fcAttacksRate = ((double) firstCluster.attackRecords) / firstCluster.points.size();
            double scAttacksRate = ((double) secondCluster.attackRecords) / secondCluster.points.size();
            conv = Math.max(fcAttacksRate, scAttacksRate) /
                    (fcAttacksRate + scAttacksRate);

            System.out.print("\r");
            System.out.print("K-Means Clustering: " +
                    UsefulUtils.formatDecimalNumber(
                            ((double) exeNum) / maxExecution * 100, 2) +
                    "% | Convergence: " +
                    UsefulUtils.formatDecimalNumber(conv * 100, 2) + "%"
                    );
        }
        UsefulUtils.announceFinishingTask("K-Means Clustering finished successfuly");
        StylishPrinter.print("◉", StylishPrinter.BOLD_GREEN);
        System.out.println(" Convergence: " +
                UsefulUtils.formatDecimalNumber(conv * 100, 2) + "%");
        StylishPrinter.print("◉", StylishPrinter.BOLD_GREEN);
        System.out.println(" Execution number: " + exeNum);

        System.out.println("\nFCNormal: " + UsefulUtils.getWideNumber(firstCluster.normalRecords));
        System.out.println("FCAttack: " + UsefulUtils.getWideNumber(firstCluster.attackRecords));
        System.out.println("SCNormal: " + UsefulUtils.getWideNumber(secondCluster.normalRecords));
        System.out.println("SCAttack: " + UsefulUtils.getWideNumber(secondCluster.attackRecords));

        double fcAttacksRate = ((double) firstCluster.attackRecords) / firstCluster.points.size();
        double scAttacksRate = ((double) secondCluster.attackRecords) / secondCluster.points.size();

        if(fcAttacksRate > scAttacksRate) {
            firstCluster.isAttacksCluster = true;
            secondCluster.isAttacksCluster = false;
        }
        else {
            firstCluster.isAttacksCluster = false;
            secondCluster.isAttacksCluster = true;
        }
        System.out.println("FCIAC: " + firstCluster.isAttacksCluster);
        System.out.println("SCIAC: " + secondCluster.isAttacksCluster);

        printAccuracy(testSet, firstCluster, secondCluster);
    }

    public static void fcmClustering(ArrayList<Point> data){
        Cluster firstCluster = new Cluster();
        Cluster secondCluster = new Cluster();
        ArrayList<Point> testSet = (ArrayList<Point>) data.clone();
        ArrayList<Point> learningSet = new ArrayList<>();
        int targetLearningSetSize = (int) (data.size() * 0.3);
        initLearningAndTestSet(testSet, learningSet, targetLearningSetSize);

        System.out.println();
        System.out.print("FCM Clustering: 0%");
        int maxExecution = 25;
        for(int i=0; maxExecution>i; i++){
            firstCluster.create();
            secondCluster.create();
            for(int j=0; learningSet.size()>j; j++){
                Point point = learningSet.get(j);
                double firstClusterDistance = Cluster.getDistance(point, firstCluster.center);
                double secondClusterDistance = Cluster.getDistance(point, secondCluster.center);

                double makhraj = Math.pow(firstClusterDistance/firstClusterDistance, 2/(M - 1)) +
                        Math.pow(firstClusterDistance/secondClusterDistance, 2/(M - 1));
                double tempFirstMem = 1 / makhraj;

                makhraj = Math.pow(secondClusterDistance/firstClusterDistance, 2/(M - 1)) +
                        Math.pow(secondClusterDistance/secondClusterDistance, 2/(M - 1));
                double tempSecondMem = 1 / makhraj;

                point.firstClusterMembership = tempFirstMem / (tempFirstMem + tempSecondMem);
                point.secondClusterMembership = tempSecondMem / (tempFirstMem + tempSecondMem);
            }
            calculateFCMCenter(firstCluster, secondCluster, learningSet);
            UsefulUtils.updateProgress("FCM Clustering", i, maxExecution);
        }
        UsefulUtils.announceFinishingTask("FCM Clustering finished successfuly");

        double firstAttacksSum = 0;
        double firstNormalSum = 0;
        double firstAttackRate;
        double secondAttacksSum = 0;
        double secondNormalSum = 0;
        double secondAttackRate;
        for(int i=0; learningSet.size()>i; i++){
            if(learningSet.get(i).isNormal()) {
                firstNormalSum += learningSet.get(i).firstClusterMembership;
                secondNormalSum += learningSet.get(i).secondClusterMembership;
            }
            else {
                firstAttacksSum += learningSet.get(i).firstClusterMembership;
                secondAttacksSum += learningSet.get(i).secondClusterMembership;
            }
        }
        firstAttackRate = firstAttacksSum / (firstAttacksSum + firstNormalSum);
        secondAttackRate = secondAttacksSum / (secondAttacksSum + secondNormalSum);

        System.out.println("\nFCNormal: " + UsefulUtils.getWideNumber(firstNormalSum));
        System.out.println("FCAttack: " + UsefulUtils.getWideNumber(firstAttacksSum));
        System.out.println("SCNormal: " + UsefulUtils.getWideNumber(secondNormalSum));
        System.out.println("SCAttack: " + UsefulUtils.getWideNumber(secondAttacksSum));
        if(firstAttackRate > secondAttackRate)
            firstCluster.isAttacksCluster = true;
        else secondCluster.isAttacksCluster = true;
        System.out.println("FCIAC: " + firstCluster.isAttacksCluster);
        System.out.println("SCIAC: " + secondCluster.isAttacksCluster);

        printAccuracy(testSet, firstCluster, secondCluster);
    }

    public static void printAccuracy(ArrayList<Point> testSet, Cluster firstCluster,
                                 Cluster secondCluster){

        int successTests = 0;
        int failedTest = 0;
        for(int i=0; testSet.size()>i; i++){
            Point point = testSet.get(i);
            double firstClusterDistance = Cluster.getDistance(point, firstCluster.center);
            double secondClusterDistance = Cluster.getDistance(point, secondCluster.center);
            if(firstClusterDistance < secondClusterDistance){
                if((firstCluster.isAttacksCluster && !point.isNormal()) ||
                        (!firstCluster.isAttacksCluster && point.isNormal())) successTests++;
                else failedTest++;
            }
            else {
                if((secondCluster.isAttacksCluster && !point.isNormal()) ||
                        (!secondCluster.isAttacksCluster && point.isNormal())) successTests++;
                else failedTest++;
            }
        }
        System.out.println("\nSuccessful Tests: " + UsefulUtils.getWideNumber(successTests));
        System.out.println("Failed Tests: " + UsefulUtils.getWideNumber(failedTest));
        String accuracy = UsefulUtils.formatDecimalNumber(
                (((double) successTests) / (successTests + failedTest)) * 100,
                2);
        System.out.println("Accuracy: " + accuracy + "%");
    }

    public static void initLearningAndTestSet(ArrayList<Point> testSet,
            ArrayList<Point> learningSet, int targetLearningSetSize){

        Random random = new Random();
        System.out.print("Init learning set and test set: 0%");
        while(targetLearningSetSize > learningSet.size()){
            learningSet.add(testSet.remove(random.nextInt(testSet.size())));
            UsefulUtils.updateProgress("Init learning and test set", learningSet.size(),
                    targetLearningSetSize);
        }
        UsefulUtils.announceFinishingTask(
                "Init learning and test set finished successfuly");

        StylishPrinter.print("◉", StylishPrinter.BOLD_GREEN);
        System.out.println(" AllSize: " + learningSet.size() + testSet.size());
        StylishPrinter.print("◉", StylishPrinter.BOLD_GREEN);
        System.out.println(" TestSetSize: " + testSet.size());
        StylishPrinter.print("◉", StylishPrinter.BOLD_GREEN);
        System.out.println(" LearninSetSize: " + learningSet.size());
    }

    public static double getDistance(Point firstPoint, Point secondPoint){
        double sum = 0;
        for(int i=0; firstPoint.size()>i; i++){
            double minus = firstPoint.get(i) - secondPoint.get(i);
            double pow = Math.pow(minus, 2);
            sum += pow;
        }

        return Math.sqrt(sum);
    }

    public static double[] getMinAndMax(ArrayList list){
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int i=0; list.size()>i; i++) {
            double value = UsefulUtils.parseDouble((String) list.get(i));
            if(value < min) min = value;
            if(value > max) max = value;
        }

        double[] out = {min, max};
        return out;
    }

    public static void printBound(double[] array){
        if(array == null) System.out.println();
        boolean first = true;
        StylishPrinter.print("[", StylishPrinter.BOLD_CYAN);
        for (double value : array) {
            if(!first) StylishPrinter.print(", ", StylishPrinter.BOLD_CYAN);
            first = false;
            System.out.print(value);
        }
        StylishPrinter.println("]", StylishPrinter.BOLD_CYAN);
    }
}
