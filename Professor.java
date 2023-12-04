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
    
    public String getInfo() {
        String s = "";
        for(int i = 0; i < courses.size(); i++) {
            s += (courses.get(i).getDepartment() + " " + courses.get(i).getCourseNum() + 
                     " " + courses.get(i).getDays() + " " + courses.get(i).getTime() + 
                     " " + courses.get(i).getCRN() + " " +courses.get(i).getProfName() + "\n");
            
        }
        return s;
    }
    
    public void addCourse(Course c) {
        courses.add(c);
    }
    
    public String printCourse() {
        
    for(int i = 0; i < courses.size(); i++) {
        
       return this.courses.get(i).profDataPrint();
    }
    return name;
    
        
    }
    

}
