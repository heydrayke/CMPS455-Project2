//right now i have it set up where task int at the top determines which thing is ran.
//When command line added it will be fixed.

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.ArrayList;
import java.util.List;
//Random and Concurrent Semaphore added during task1
//Arraylists and lists added during task2


public class Main {

    static Random rand = new Random();
    static int n; // domains
    static int m; // files
    static int task=3;
    //task variable for now until we set up the command line
    static List<List<AccessEntry>> accessLists;
    // Stores a list containing all the object access lists for task2.
    // Each inner list holds AccessEntry objects for one file or domain.
    static class AccessEntry {
        final int domain;
        final String permission;

        //making the domain and the permissions for each domain for task2
        //can be like R,W,R/W,or Allow

        AccessEntry(int domain, String permission) {
            this.domain = domain;
            this.permission = permission;
            //makes the domain and permissions at the beginning of each accessentry
        }
    }
    // Begin Task 3

    static List<List<CapabilityEntry>> capabilityLists;

    // Each entry represents an object that a domain can access
    static class CapabilityEntry {

        final int object;
        final String permission;

        CapabilityEntry(int object, String permission) {
            this.object = object;
            this.permission = permission;
        }
    }

    // End Task 3 code changes
    static String[][] matrix;
    static String[] files;
    static Semaphore[] fileLocks;


    public static void main(String[] args) {

        // had nextInt(6) + 2 before, thats actually 2-7. nextInt(5) is 0-4 so +3 = 3-7
        n = rand.nextInt(5) + 3;
        m = rand.nextInt(5) + 3;
        // int randomNum = m * n;  <- dont need this, table is n rows by m+n cols

        System.out.println("N (domains) = " + n + ", M (files) = " + m);

        files = new String[m];
        fileLocks = new Semaphore[m];
        for (int i = 0; i < m; i++) {
            files[i] = "empty";
            fileLocks[i] = new Semaphore(1);   // 1 permit so only one thread in a file at a time
        }

        if (task == 1) {
            makeMatrix();
            printMatrix();
        }
        else if (task == 2) {
            makeAccessLists();
            printAccessLists();
        }
        else if (task == 3) {
            makeCapabilityLists();
            printCapabilityLists();
        }


        DomainThread[] threads = new DomainThread[n];
        for(int i = 0; i < n; i++) {
            threads[i] = new DomainThread(i);
            threads[i].start();
        }

        //wait for everyone to finish
        for (int i = 0; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                System.out.println("interrupted waiting on thread " + (i+1));
            }
        }
        System.out.println("Done.");
    }

    static void makeAccessLists() {

        accessLists = new ArrayList<>();
        //create the initial empty outer array list

        // Goes through each column (object) (goes from 0 to m+n-1)
        for (int col = 0; col < m + n; col++) {
            //makes an accesslist per object
            accessLists.add(new ArrayList<>());
        }

        // Goes through each domain, 0->d
        for (int d = 0; d < n; d++) {
            for (int col = 0; col < m + n; col++) {
                //the first m objects are the files
                if (col < m) {
                    // File object.
                    int r = rand.nextInt(4);
                    String permission;
                    //uses random in r to make the permission for each file object.
                    if (r == 0) {
                        permission = "R";
                    } else if (r == 1) {
                        permission = "W";
                    } else if (r == 2) {
                        permission = "R/W";
                    } else {
                        // No access: do not store an entry.
                        continue;
                    }
                    //makes an entry for the objects access list with the domain and its permission


                    accessLists.get(col).add(
                            // Adds entry to the selected list.
                            new AccessEntry(d, permission)
                    );

                } else {
                    // Domain object, turn it into a domain number
                    int targetDomain = col - m;

                    if (targetDomain == d) {
                        continue; // Skip switching to itself.
                    }
                    //add the source to the destination list, gives permission for the switch
                    if (rand.nextBoolean()) {
                        accessLists.get(col).add(
                                new AccessEntry(d, "allow")
                                //no entry means switch denied
                        );
                    }
                }
            }
        }
    }
    // Begin Task 3

    static void makeCapabilityLists() {

        capabilityLists = new ArrayList<>();

        // Create one capability list for each domain
        for (int d = 0; d < n; d++) {
            capabilityLists.add(new ArrayList<>());
        }

        // Go through each domain
        for (int d = 0; d < n; d++) {

            // Go through every file and domain object
            for (int col = 0; col < m + n; col++) {

                if (col < m) {

                    // File object permissions
                    int r = rand.nextInt(4);

                    String permission;

                    if (r == 0) {
                        permission = "R";
                    }
                    else if (r == 1) {
                        permission = "W";
                    }
                    else if (r == 2) {
                        permission = "R/W";
                    }
                    else {
                        // No permission
                        continue;
                    }

                    capabilityLists.get(d).add(
                            new CapabilityEntry(col, permission)
                    );

                } else {

                    // Domain object
                    int targetDomain = col - m;

                    // Cannot switch to the same domain
                    if (targetDomain == d) {
                        continue;
                    }

                    // Randomly allow or deny domain switching
                    if (rand.nextBoolean()) {

                        capabilityLists.get(d).add(
                                new CapabilityEntry(col, "allow")
                        );
                    }
                }
            }
        }
    }

// End Task 3

    static void makeMatrix() {
        matrix = new String[n][m + n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m + n; j++) {

                if (j < m) {
                    // file column
                    int r = rand.nextInt(4);
                    if (r == 0) matrix[i][j] = "R";
                    else if (r == 1) matrix[i][j] = "W";
                    else if (r == 2) matrix[i][j] = "R/W";
                    else matrix[i][j] = "-";
                }
                else if (j - m == i) {
                    matrix[i][j] = "N/A";  // its own domain
                }
                else {
                    // domain column
                    if (rand.nextBoolean()) matrix[i][j] = "allow";
                    else matrix[i][j] = "-";
                }

            }
        }
    }
    static void printAccessLists() {
        System.out.println("\nAccess Lists for Objects:");
        //for loop for going through every object and file
        for (int col = 0; col < m + n; col++) {
            //prints the name of the object/file
            System.out.print(colName(col) + ": ");
            //grabs a reference for the list
            List<AccessEntry> list = accessLists.get(col);
            if (list.isEmpty()) {
                //empty condition prints empty list
                System.out.print("(empty)");
            } else
            {
                //else statement prints the domain and the permissions per domain
                for (AccessEntry entry : list) {
                    System.out.print("[D" + (entry.domain + 1) + ": " + entry.permission + "] "
                    );
                }
            }
            System.out.println();
        }
        System.out.println("End of Lists. Below is the lists of requests:" );
        System.out.println();
    }
    // Begin Task 3 code changes

    static void printCapabilityLists() {

        System.out.println("\nCapability Lists for Domains:");

        // Go through each domain
        for (int d = 0; d < n; d++) {

            System.out.print("D" + (d + 1) + ": ");

            List<CapabilityEntry> list = capabilityLists.get(d);

            if (list.isEmpty()) {

                System.out.print("(empty)");

            } else {

                // Print all objects this domain can access
                for (CapabilityEntry entry : list) {

                    System.out.print(
                            "[" + colName(entry.object)
                                    + ": " + entry.permission + "] "
                    );
                }
            }

            System.out.println();
        }

        System.out.println("End of Capability Lists.");
        System.out.println("Below is the list of requests:");
        System.out.println();
    }

    // End Task 3 code changes
    static void printMatrix() {
        System.out.println("\nAccess Matrix:");

        System.out.print("\t");
        for (int j = 0; j < m + n; j++) {
            System.out.print(colName(j) + "\t");
        }
        System.out.println();

        for (int i = 0; i < n; i++) {
            System.out.print("D" + (i + 1) + "\t");
            for (int j = 0; j < m + n; j++)
                System.out.print(matrix[i][j] + "\t");
            System.out.println();
        }
        System.out.println();
    }

    // column number -> F1, F2 ... D1, D2 ...
    static String colName(int col) {
        if (col < m) return "F" + (col + 1);
        return "D" + (col - m + 1);
    }


    static boolean arbitrator(int t, int d, int col, String request) {
        String perm;


        if (task == 1) {

            // Task 1: Access Matrix
            perm = matrix[d][col];

        }
        else if (task == 2) {

            // Task 2: Access Lists for Objects
            perm = "-";

            for (AccessEntry entry : accessLists.get(col)) {

                if (entry.domain == d) {
                    perm = entry.permission;
                    break;
                }
            }

        }
        else if (task == 3) {

            // Task 3: Capability Lists for Domains
            perm = "-";

            // Search the current domain's capability list
            for (CapabilityEntry entry : capabilityLists.get(d)) {

                if (entry.object == col) {
                    perm = entry.permission;
                    break;
                }
            }

        }
        else {
            perm = "-";
        }

        System.out.println("[Arbitrator] checking thread " + t + " (D" + (d+1) + ") " + request + " " + colName(col) + ", has: " + perm);

        boolean ok = false;
        if (request.equals("read") && (perm.equals("R") || perm.equals("R/W")))
            ok = true;
        if (request.equals("write") && (perm.equals("W") || perm.equals("R/W")))
            ok = true;
        if (request.equals("switch to") && perm.equals("allow"))
            ok = true;

        if (ok) {
            System.out.println("[Arbitrator] thread " + t + " " + request + " " + colName(col) + " -> ALLOWED");
        } else {
            System.out.println("[Arbitrator] thread " + t + " " + request + " " + colName(col) + " -> DENIED");
        }
        return ok;
    }

    static void yieldRandom(int t) {
        int times = rand.nextInt(5) + 3;
        System.out.println("Thread " + t + " yielding " + times + " times");
        for (int i = 0; i < times; i++) Thread.yield();
    }

}



class DomainThread extends Thread {

    int num;     // thread number, never changes
    int domain;  // current domain, changes when it switches

    DomainThread(int i) {
        num = i + 1;
        domain = i;
    }

    public void run() {
        try {

            for (int req = 0; req < 5; req++) {
                int x = Main.rand.nextInt(Main.m + Main.n);

                //cant switch to the domain we're already in so reroll
                while (x == Main.m + domain) {
                    x = Main.rand.nextInt(Main.m + Main.n);
                }


                if (x < Main.m) {
                    int rw = Main.rand.nextInt(2);  // 0 = read, 1 = write

                    if (rw == 0) {
                        System.out.println("Thread " + num + " (D" + (domain+1) + ") requests read " + Main.colName(x));

                        if (Main.arbitrator(num, domain, x, "read")) {
                            Main.fileLocks[x].acquire();
                            System.out.println("Thread " + num + " locked " + Main.colName(x));
                            System.out.println("Thread " + num + " read " + Main.colName(x) + ": " + Main.files[x]);

                            Main.yieldRandom(num);  // still holding the lock here on purpose

                            System.out.println("Thread " + num + " unlocked " + Main.colName(x));
                            Main.fileLocks[x].release();
                        } else {
                            Main.yieldRandom(num);
                        }
                    }
                    else {
                        System.out.println("Thread " + num + " (D" + (domain+1) + ") requests write " + Main.colName(x));

                        if (Main.arbitrator(num, domain, x, "write")) {
                            Main.fileLocks[x].acquire();
                            System.out.println("Thread " + num + " locked " + Main.colName(x));
                            Main.files[x] = "thread " + num + " was here";
                            System.out.println("Thread " + num + " wrote to " + Main.colName(x) + ": " + Main.files[x]);

                            Main.yieldRandom(num);

                            System.out.println("Thread " + num + " unlocked " + Main.colName(x));
                            Main.fileLocks[x].release();
                        } else {
                            Main.yieldRandom(num);
                        }
                    }

                } else {
                    int target = x - Main.m;
                    System.out.println("Thread " + num + " (D" + (domain+1) + ") requests switch to D" + (target + 1));

                    if (Main.arbitrator(num, domain, x, "switch to")) {
                        domain = target;
                        System.out.println("Thread " + num + " is now in D" + (domain+1));
                    }
                    Main.yieldRandom(num);
                }
            }

        } catch (InterruptedException e) {
            System.out.println("Thread " + num + " got interrupted");
        } catch (Exception e) {
            System.out.println("Thread " + num + " error: " + e);
        }
    }
}

// End code changes by Drayke.
