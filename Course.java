/**
 * Course Object Class
 * Copyright 2023 Miles Clements
 */

package source;

public class Course {
    
    private String department;
    // STRING for testing purposes, should be of type Professor
    private String courseNum;
    private String professor;
    private String section;
    
    public Course(String department, String courseNum, String professor, String section) {
        this.department = department;
        this.courseNum = courseNum;
        this.professor = professor;
        this.section = section;
    }
    
    
}
