import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class ClosestPoints {
    public static void main(String[] args) throws Exception {
        Set<Point> points = generatePoints(25, 100, 100);
        System.out.println("\nInitial set of randomly generated points:");
        for (Point point : points) {
            System.out.print(point.get_x() + ", " + point.get_y() + "  |  ");}
        System.out.println("\n\nSorted by x-coordinate:");
        ArrayList<Point> X = generateSortedArray(points, 0); // array of points sorted by x coordinate
        printPoints(X);
        System.out.println("\n\nSorted by y-coordinate:");
        ArrayList<Point> Y = generateSortedArray(points, 1); // array of points sorted by y coordinate
        printPoints(Y);
        ArrayList<Point> closest_points = findClosestPoints(X, Y);
        float d = calculateDistance(closest_points.get(0), closest_points.get(1));
        System.out.println("\n\nTwo closest points:");
        printPoints(closest_points);
        System.out.println(String.format("\nDistance: %f \n", d));
    }

    // Generate a set of random points
    public static Set<Point> generatePoints(int size, int hrange, int vrange) throws Exception { 
        /*
        * size = number of points; 
        * hrange = range where points` x-coord. are generated;
        * vrange = range where points` y-coord. are generated;
        */                                 
        if (size <= 0 || hrange <= 0 || vrange <= 0) {
            throw new Exception("Invalid input in ClosestPoints.generatePoints(). The size, as well as the x- and y- ranges must be greater than zero");
        }
        Set<Point> points = new HashSet<Point>();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int x = random.nextInt(hrange);
            int y = random.nextInt(vrange);
            points.add(new Point(x, y));
        }
    return points;
    }

    // Generate ArrayLists of type Point, sorted by x- or y- coordinate
    public static ArrayList<Point> generateSortedArray(Set<Point> points, int coord) throws Exception { 
        /*
        * coord = 0 --> x-axis
        * coord = 1 --> y-axis
        */
        int size = points.size() - 1;
        ArrayList<Point> points_list = new ArrayList<>();
        for (Point point : points) {
            points_list.add(point);}
        sort(points_list, 0, size, coord);
        return points_list;
    }

    // Use Divide & Conquer to find the two closest points 
    public static ArrayList<Point> findClosestPoints(ArrayList<Point> X, ArrayList<Point> Y) {
        int length = X.size();
       // elementary cases:
        if (length == 2) { // the only two points are the closest
            return X;} 
        else if (length == 3) { // if the length is 3 --> use brute force to find out the minimum distance
            ArrayList<Point> closest = new ArrayList<>();
            float d1 = calculateDistance(X.get(0), X.get(1));
            float d2 = calculateDistance(X.get(1), X.get(2));
            float d3 = calculateDistance(X.get(0), X.get(2));
            if (d1 <= d2 && d1 <= d3) {
                closest.add(X.get(0));
                closest.add(X.get(1));
            } else if (d2 <= d1 && d2 <= d3) {
                closest.add(X.get(1));
                closest.add(X.get(2)); 
            } else {
                closest.add(X.get(0));
                closest.add(X.get(2)); 
            }
            return closest;
        }
        int middle = (int)Math.floor(X.size() / 2); // round down the division for the middle element of the array
        float l_x = (X.get(middle).get_x() + X.get(middle + 1).get_x()) / 2; 
        /* 
        * l_x is used for defining the middle region where 
        * points` distances are checked across the diving line
        */
        ArrayList<Point> X_l = new ArrayList<Point>(X.subList(0, middle)); // left half of the points
        ArrayList<Point> X_r = new ArrayList<Point>(X.subList(middle, length)); // right half of the points
        ArrayList<Point> closest_l = findClosestPoints(X_l, Y); // two closest points in the left half
        ArrayList<Point> closest_r = findClosestPoints(X_r, Y); // two closest points in the right half
        ArrayList<Point> combined = combine(Y, l_x, closest_l, closest_r); // two closest points overall
        float d1 = calculateDistance(closest_l.get(0), closest_l.get(1)); // distance between the two closest points in the left half
        float d2 = calculateDistance(closest_r.get(0), closest_r.get(1)); // distance between the two closest points in the right half
        float d3 = calculateDistance(combined.get(0), combined.get(1)); // distance between the two closest points across the separation
        if (d1 <= d2 && d1 <= d3) { // the two closest points lay in the left part
            return closest_l;
        } else if (d2 <= d1 && d2 <= d3) { // the two closest points lay in the right part
            return closest_r; 
        } else { // the two closest points lay on different sides of the dividing line
            return combined; 
        }
    }

    // check if there are closest points located in different halves
    public static ArrayList<Point> combine(ArrayList<Point> Y, float l_x, ArrayList<Point> closest_l, ArrayList<Point> closest_r) {
        float d; // distance between two points
        ArrayList<Point> combined = new ArrayList<>();
        float d1 = calculateDistance(closest_l.get(0), closest_l.get(1)); // distance between two closest points in the left half
        float d2 = calculateDistance(closest_r.get(0), closest_r.get(1)); // distance between two closest points in the right half
        if (d1 < d2) { // the two closest points in the left half are the closest points
            combined.add(closest_l.get(0));
            combined.add(closest_l.get(1));
            d = d1;
        } else { // the two closest points in the right half are the closest points
            combined.add(closest_r.get(0));
            combined.add(closest_r.get(1));
            d = d2;
        }
        // check if the two closest points actually lay on different sides from the middle line
        ArrayList<Point> Y1 = new ArrayList<>(); 
        for (Point point : Y) { // scan the area within +- d from the middle line. Y1 contains all points in that area
            if ((l_x - d) <= point.get_x() && point.get_x() <= (l_x + d)) {
                Y1.add(point);
            }
        }
        // check the distance from each point in the +-d region to 7 neighboring points. Return the two closest points if such found
        for (int i = 0; i <= Y1.size(); i++) {
            int j = 1;
            while (j <= 7 && (i + j) < Y1.size()) {
                float d3 = calculateDistance(Y1.get(i), Y1.get(i + j));
                if (d3 < d) {
                    combined.set(0, Y1.get(i));
                    combined.set(1, Y1.get(i + j));
                    d = d3;
                }
                j++;
            }  
        }
        return combined;
    }

    // Calculate the Euclidean distance between two points
    public static float calculateDistance(Point a, Point b) {
        return (float)Math.sqrt(Math.pow((b.get_x() - a.get_x()), 2) + Math.pow((b.get_y() - a.get_y()), 2));
    }

    // Merge Sort
    public static void sort(ArrayList<Point> array, int p, int r, int coord) throws Exception {
        if (p < r) { // if there are at least two points..
            int q = (int)Math.floor((p + r) / 2); // round down for the middle element
            // recursively divide the array until reaching the elementary case, and sort by merging
            sort(array, p, q, coord);
            sort(array, q+1, r, coord);
            merge(array, p, r, q, coord);
        }
    }

    // sort by merging the elementary arrays 
    public static void merge(ArrayList<Point> array, int p, int r, int q, int coord) throws Exception { // coord for differentiation between x- and y- axis sorting
        int inf = Integer.MAX_VALUE;
        int n1 = q - p + 2;
        int n2 = r - q + 1;
        // initialize the left and right half arrays
        ArrayList<Point> L = new ArrayList<>();
        for (int a = 0; a < n1; a++) {
            L.add(new Point(0, 0));}
        ArrayList<Point> R = new ArrayList<>();
        for (int b = 0; b < n2; b++) {
            R.add(new Point(0, 0));}
        // set the end element as infinity and fill the rest with (sub)array values
        for (int i = 0; i <= n1 - 2; i++) {
            L.set(i, array.get(p + i)); // adding the left element to L
            L.set(n1 - 1, new Point(inf, inf)); // and the right element
        }
        for (int j = 0; j <= n2 - 2; j++) {
            R.set(j, array.get(q + j + 1)); // adding the left element to R
            R.set(n2 - 1, new Point(inf, inf)); // and the right element
        }
        // sort by merging
        int i = 0;
        int j = 0;
        if (coord == 0) { // sort by x coordinate
            for (int k = p; k <= r; k++) { // compare two subarrays 
                if (L.get(i).get_x() <= R.get(j).get_x()) { 
                    array.set(k, L.get(i)); // reset with min value
                    i++;
                } else {
                    array.set(k, R.get(j));
                    j++;
                }
            }
        } else if (coord == 1) { // sort by y coordinate
            for (int k = p; k <= r; k++) {
                if (L.get(i).get_y() <= R.get(j).get_y()) {
                    array.set(k, L.get(i));
                    i++;
                } else {
                    array.set(k, R.get(j));
                    j++;
                }
            }
        } else {
            throw new Exception("Invalid axis input in ClosestPoints.generateSortedArray(). Specify 0 for x-axis, 1 for y-axis");
        }
    }
    

    // print the points in the output
    public static void printPoints(ArrayList<Point> points) {
        for (Point point : points) {
            System.out.print(point.get_x() + ", " + point.get_y() + "  |  ");
        }
    }
}