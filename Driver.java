/**
 * Driver Class for Classify Application
 * Copyright 2023 Miles Clements, Jake Ross
 */

package source;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Driver {
    
    static ArrayList<Course> classData;
    static ArrayList<Professor> profData;
    
    
    public static Professor getProfByName(String name) {
        for (int i = 0; i < profData.size(); i++) {
            if (name.equals(profData.get(i).getName())) {
                return profData.get(i);
            }
        }
        return null;
    }
    
    /**
     * Checks if a professor exists in the data by name.
     * @param name
     * @return
     */
    public static boolean profExists(String name) {
        if (profData.isEmpty()) {
            return false;
        }
        for (int i = 0; i < profData.size(); i++) {
            if (name.equals(profData.get(i).getName())) {
                return true;
            }
        }
        return false;
    }
    
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
            Professor p;
            if (profExists(lines[2])) {
                p = getProfByName(lines[2]);
            } else {
                p = new Professor(lines[2], new ArrayList<Course>());
            }
            Course c = new Course(lines[0], lines[1], p, lines[3]);
            classData.add(c);
            p.addCourse(c);
            profData.add(p);
        }
    }
    
    public static boolean checkEquality(Course currentCourse, String d, String c) {
        if (d.equals(currentCourse.getDepartment())) {
            if (c.length() == 1) {
                if (c.equals(currentCourse.getCourseNum().substring(0, 1))) {
                    return true;
                }
            } else {
                if (c.equals(currentCourse.getCourseNum())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void courseQuery(String department, String courseNum, ArrayList<Course> courseData) {
        for (int i = 0; i < courseData.size(); i++) {
           if (checkEquality(courseData.get(i), department, courseNum)) {
               courseData.get(i).print();
           }
        }
    }
    

    public static void profQuery(String profName) {
        
        
        
        for (int i = 0; i < profData.size(); i++) {
            if (profName.equalsIgnoreCase(profData.get(i).getName())) {
                profData.get(i).printCourse();
                break;
            }
        }
    }
    
    // overloaded method to do the same with no course num
    public static void courseQuery(String department, ArrayList<Course> courseData) {
        for (int i = 0; i < courseData.size(); i++) {
           if (courseData.get(i).getDepartment().equals(department)) {
        	   courseData.get(i).print();
           }
        }
    }
    
    // overloaded method, intakes a bool to determine what to do
    // true means filter higher stuff out
    // false means filter lower stuff out
    public static void courseQuery(String courseNum, ArrayList<Course> courseData, boolean higherOrLower) {
    	int workNum;
    	int courseInt = Integer.parseInt(courseNum);
    	if (higherOrLower) { // smaller
    		for (int i = 0; i < courseData.size(); i++) {
            	workNum = Integer.parseInt(courseData.get(i).getCourseNum());
            	if (workNum <= courseInt) {
            		courseData.get(i).print();
            	}
            }
    	} else { // larger
    		for (int i = 0; i < courseData.size(); i++) {
            	workNum = Integer.parseInt(courseData.get(i).getCourseNum());
            	if (workNum >= courseInt) {
            		courseData.get(i).print();
            	}
            }
    	}
    }
    
    // will return true if the number is valid, false if its not
    public static boolean numIsValid(String num) {
    	try {
    		Integer.parseInt(num);
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
    
    public static String chooseDepartment(Scanner sc) {
        System.out.println("Select a department: \n1. CSE\n2. STA\n3. ENG\n4. LMS");
        String option = "";
        option = sc.nextLine();
        switch (option) {
        case "1":
            return "CSE";
        case "2":
            return "STA";
        case "3":
            return "ENG";
        case "4":
            return "LMS";
        default:
            return "error";
        }
    }
    
    public static String chooseNumber(Scanner sc) {
        System.out.println("Please enter a course number. Entering one digit will search for all classes of that level.");
        String number = "";
        number = sc.nextLine();
        return number;
    }
    
    /**
     * This method should allow the user to input a course or professor
     * (could potentially be an overloaded method) and show the results
     * for their search query. This menu should also allow the user to
     * go back to the previous menu, and select between course or professor.
     * Copyright 2023 Insert Names Here
     */
    public static void courseMenu(Scanner sc) {
        System.out.println("What would you like to do? \n1. Search by department"
                + " AND course number\n2. Filter by department\n3. Filter courses"
                + " above a threshold (inclusive)\n4. Filter courses below a"
                + " threshold (inclusive)");
        String option = "";
        option = sc.nextLine();
        String department = "";
        String courseNum = "";
        switch (option) {
        // option 1, both department and number
        case "1":
        	department = chooseDepartment(sc);
            courseNum = chooseNumber(sc);
            courseQuery(department, courseNum, classData);
            break;
        // option 2, just department
        case "2":
        	department = chooseDepartment(sc);
        	courseQuery(department, classData);
        	break;
        // option 3, filter out higher course nums
        case "3":
        	courseNum = chooseNumber(sc);
        	if (numIsValid(courseNum)) {
        		courseQuery(courseNum, classData, false);
        	} else {
        		System.out.println("Invalid input, returning to main menu!");
        	}
        	break;
        // option 4, filter out lower course nums
        case "4":
        	courseNum = chooseNumber(sc);
        	if (numIsValid(courseNum)) {
        		courseQuery(courseNum, classData, true);
        	} else {
        		System.out.println("Invalid input, returning to main menu!");
        	}
        	break;
        // catch the user input error
        default:
        	System.out.println("Invalid input, returning to main menu!");
        	break;
        }
    }
    
    /**
     * This method will allow the user to enter the name of a professor
     * and then all the information about that professor will be returned.
     * 
     * INCOMPLETE (we have all the courses that the professors teach, but will
     * later add the RateMyProfessor score and potentially other stuff)
     * @param sc
     */
    public static void profMenu(Scanner sc) {
        System.out.println("What would you like to do?\n1. Search professor to find what"
        		+ " courses they teach\n2. Search professors teaching certain classes");
        String option = "";
        option = sc.nextLine();
        switch (option) {
        // option 1, search professor to find out what classes they teach
        case "1":
        	System.out.println("Please type in the name of a professor");
        	String professor = "";
            professor = sc.nextLine();
        	if(profExists(professor)) {
                System.out.println(professor + " teaches the following class(es)");
                profQuery(professor);
                
            } else {
                System.out.println("No such professor in the registry");
            }
        	break;
        case "2":
        	// add stuff here
        	break;
        default:
        	// do nothing
        	break;
        }
    }
    
    /**
     * This method should show the first menu allowing the user
     * to select the mode they'd like to search in. (Classes/Professor).
     * Copyright 2023 Insert Names Here
     */
    public static boolean modeMenu(Scanner sc) {
        System.out.println("Welcome to Classify! DISCLAIMER: This is a command line version of the planned GUI. The data used in this demo may not be accurate to the actual 2024 Spring schedule. \nPlease select an option: \n1. Search for Courses\n2. Search for Professors");
        String option = "0";
        option = sc.nextLine();
        System.out.println("You have selected: " + option);
        switch (option) {
        case "1":
            courseMenu(sc);
            break;
        case "2":
            profMenu(sc);
            break;
        default:
            System.out.println("Invalid input: " + option);
            break;
        }
        return true;
    }
    
    public static void main(String[] args) {
        classData = new ArrayList<>();
        profData = new ArrayList<>();
        try {
            readData(classData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean menu = false;
        Scanner sc = new Scanner(System.in);
        while (!menu) {
            modeMenu(sc);
        }
        sc.close();
        // end program
    }
}
