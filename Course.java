/**
 * Course Object Class
 * Copyright 2023 Miles Clements
 */

package source.Classify;

public class Course {
    
    // Identifier for the department the class is hosted by.
    private String department;
    // Professor teaching this class.
    private Professor professor;
    
    // Constructor for Course object.
    public Course(String department, Professor professor) {
        this.department = department;
        this.professor = professor;
    }
    
    
}
