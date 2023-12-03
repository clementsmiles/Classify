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
    
    
    public Course(String courseRegNum, String department, String courseNum, String section, String title, String days, String times, String location, Professor professor) {
        this.courseRegNum = courseRegNum;
        this.department = department;
        this.courseNum = courseNum;
        this.section = section;
        this.title = title;
        this.days = days;
        this.times = times;
        this.location = location;
        this.professor = professor;
    }
    
    public String getCRN() {
        return courseRegNum;
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
        return this.courseRegNum + " " + this.department + " "
                + this.courseNum + " " + this.section + " "
                + this.title + " " + this.days + " "
                + this.times + " " + this.location + " "
                + this.professor.getName() + "\n";
    }
    
    public void print() {
        System.out.println(this.department + " " + this.courseNum + " "
    + this.section + " " + this.professor.getName());
    }
    
    public void printProfessor() {
    	System.out.print(this.professor.getName());
    }
    
    public String getProfessor() {
        return this.professor.getName();
    }
    
    public String getTime() {
        return this.times;
    }
    
    public String getDays() {
        return this.days;
    }
    
    public String profDataPrint() {
        return (this.department + " " + this.courseNum + " " 
                + this.section + "\n");
    }
    
    
}
