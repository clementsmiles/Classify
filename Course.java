/**
 * Course Object Class
 * Copyright 2023 Miles Clements
 */

package source;

public class Course {
    
    private String department;
    private Professor professor;
    private String section;
    
    public Course(String department, Professor professor, String section) {
        this.department = department;
        this.professor = professor;
        this.section = section;
    }
    
    
}
