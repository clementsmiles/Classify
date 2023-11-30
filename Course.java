/**
 * Course Object Class
 * Copyright 2023 Miles Clements
 */

package source;

public class Course {

    private String courseRegNum;
    private String department;
    private String courseNum;
    private String section;
    private String title;
    private String days;
    private String times;
    private String location;
    private Professor professor;
    
    
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
    
    public String getProfName() {
        return professor.getName();
    }
    
    public String getInfo() {
        return this.department + " " + this.courseNum + " "
                + this.section + " " + this.professor.getName() + "\n";
    }
    
    public void print() {
        System.out.println(this.department + " " + this.courseNum + " "
    + this.section + " " + this.professor.getName());
    }
    
    public void printProfessor() {
    	System.out.print(this.professor.getName());
    }
    
    public void profDataPrint() {
        System.out.println(this.department + " " + this.courseNum + " " 
                + this.section);
    }
    
    
}
