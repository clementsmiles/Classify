/**
 * Driver Class for Classify Application
 * Copyright 2023 Miles Clements
 */

package source;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Driver {
    
    
    /**
     * Test method
     * Reads course data from the supplied .csv file
     * @throws IOException 
     */
    public static void readData(ArrayList<Course> classData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\source\\classData.csv"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] lines = line.split(",");
            Course c = new Course(lines[0], lines[1], lines[2], lines[3]);
            classData.add(c);
        }
    }
    
    public boolean checkEquality(Course currentCourse, String d, String c) {
        if (d.equals(currentCourse.getDepartment())) {
            
        }
        return false;
    }
    
    public static void courseQuery(String department, String courseNum, ArrayList<Course> courseData) {
        for (int i = 0; i < courseData.size(); i++) {
           
            
        }
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
        ArrayList<Course> classData = new ArrayList<>();
        try {
            readData(classData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean menu = false;
        while (!menu) {
            modeMenu();
        }
        // end program
    }
}
