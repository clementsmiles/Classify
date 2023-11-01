package source.Classify;

public class Course {
    
    private String department;
    private Professor professor;
    private Course[] prerequisites;
    
    
    // Constructor for Course object.
    public Course(String department, Professor professor) {
        this.department = department;
        this.professor = professor;
    }
    
    
}
