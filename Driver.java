/**
 * Driver Class for Classify Application
 * Copyright 2023 Miles Clements
 */

package source;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Driver {
    
    /**
     * @throws FileNotFoundException 
     * 
     */
    public static void readData() throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader("data.csv"));
    }
    
    /**
     * This method should allow the user to input a course or professor
     * (could potentially be an overloaded method) and show the results
     * for their search query. This menu should also allow the user to
     * go back to the previous menu, and select between course or professor.
     * Copyright 2023 Insert Names Here
     */
    public static void searchMenu() {
        
    }
    
    /**
     * This method should show the first menu allowing the user
     * to select the mode they'd like to search in. (Classes/Professor).
     * Copyright 2023 Insert Names Here
     */
    public static boolean modeMenu() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to Classify! This is a command line version of the planned GUI. \nPlease select an option: \n1. Search for Courses\n 2. Search for Professors");
        int option = 0;
        option = sc.nextInt();
        System.out.println("You have selected: " + option);
        sc.close();
        return true;
    }
    
    public static void main(String[] args) {
        boolean menu = false;
        while (!menu) {
            modeMenu();
        }
        // end program
    }
}
