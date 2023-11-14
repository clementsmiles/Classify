/**
 * Professor Object Class
 * Copyright 2023 Miles Clements
 */

package source;
import java.util.ArrayList;

public class Professor {
    
    private String name;
    private ArrayList<Course> courses;
    
    public Professor(String name, ArrayList<Course> courses) {
        this.name = name;
        this.courses = courses;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addCourse(Course c) {
        courses.add(c);
    }
}
