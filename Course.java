/**
 * Course Object Class
 * Copyright 2023 Miles Clements
 */

package source;

public class Course {
    
    private String department;
    private String courseNum;
    private Professor professor;
    private String section;
    
    public Course(String department, String courseNum, Professor professor, String section) {
        this.department = department;
        this.courseNum = courseNum;
        this.professor = professor;
        this.section = section;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public String getCourseNum() {
        return courseNum;
    }
    
    public void print() {
        System.out.println(this.department + " " + this.courseNum + " "
    + this.section + " " + this.professor.getName());
    }
}
