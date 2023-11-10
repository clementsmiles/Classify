/**
 * Professor Object Class
 * Copyright 2023 Miles Clements
 */

package source;
import java.util.ArrayList;

public class Professor {
    
    // Name of professor
    private String name;
    // ArrayList of all courses professor is planning on teaching.
    private ArrayList<Course> courses;
    
    // Constructor for a Professor object.
    public Professor(String name, ArrayList<Course> courses) {
        this.name = name;
        this.courses = courses;
    }
}
