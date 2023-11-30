package source;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClassifyGUI extends Application {

    static ArrayList<Course> classData;
    static ArrayList<Professor> profData;
    
    // Start backend methods, may require separate classes
    
    public static void main(String[] args) {
        classData = new ArrayList<>();
        profData = new ArrayList<>();
        try {
            readData(classData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }
    
    public static boolean profExists(String name) {
        if (profData.isEmpty()) {
            return false;
        }
        for (int i = 0; i < profData.size(); i++) {
            if (name.equals(profData.get(i).getName())) {
                return true;
            }
        }
        return false;
    }
    
    public static Professor getProfByName(String name) {
        for (int i = 0; i < profData.size(); i++) {
            if (name.equals(profData.get(i).getName())) {
                return profData.get(i);
            }
        }
        return null;
    }
    
    public static String parseProfessorName(String name) {
        String parsedName = "";
        if (name.contains(";")) {
            name = name.substring(0, name.indexOf(";"));
        }
        if (name.substring(0, 1).equals("\"")) {
            name = name.substring(1, name.length());
            for (int i = name.indexOf(" ") + 1; true; i++) {
                if (i == name.length() || name.substring(i, i+1).equals("\"")) {
                    i = 0;
                    parsedName += " ";
                }
                parsedName += name.charAt(i);
                if (i == name.indexOf(" ")) {
                    break;
                }
            }
            return parsedName;
        }
        return name;
    }
    
    public static String[] correctLine(String[] values) {
        values[8] += values[9];
        String[] fixedArray = new String[9];
        for (int i = 0; i < 9; i++) {
            fixedArray[i] = values[i];
        }
        return fixedArray;
    }
    public static void readData(ArrayList<Course> classData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\source\\newClassData.csv"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] lines = line.split(",");
            if (lines.length == 10) {
                lines = correctLine(lines);
            }
            Professor p;
            String profName = parseProfessorName(lines[8]);
            if (profExists(profName)) {
                p = getProfByName(profName);
            } else {
                p = new Professor(profName, new ArrayList<Course>());
            }
            Course c = new Course(lines[0], lines[1], lines[2], lines[3],
                    lines[4], lines[5], lines[6], lines[7], p);
            classData.add(c);
            p.addCourse(c);
            profData.add(p);
        }
    }
    
    /**
     * Gets an ObservableList of strings containing all departments.
     * @return List of all departments
     */
    public ObservableList<String> getAllDepartments() {
        HashSet<String> depList = new HashSet<String>();
        for (int i = 0; i < classData.size(); i++) {
            depList.add(classData.get(i).getDepartment());
        }
        return FXCollections.observableArrayList(depList);
    }
    
    // End backend methods
    
    
    // Start query methods
    
    public static boolean checkEquality(Course currentCourse, String d, String c) {
        if (d.equals(currentCourse.getDepartment())) {
            if (c.length() == 1) {
                if (c.equals(currentCourse.getCourseNum().substring(0, 1))) {
                    return true;
                }
            } else {
                if (c.equals(currentCourse.getCourseNum())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static String courseQuery(String department, String courseNum) {
        String result = "";
        for (int i = 0; i < classData.size(); i++) {
            if (checkEquality(classData.get(i), department, courseNum)) {
                result += classData.get(i).getInfo();
            }
         }
        return result;
    }
    
    

    // Start GUI/FX methods
    
    private void showSearchDandNumWindow() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search by department and number");
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        TextField numField = new TextField();
        Label depLabel = new Label("Department");
        Label numLabel = new Label("Course Number");
        Button resultButton = new Button("Search!");
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, numLabel, numField, resultsArea, resultButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue(), numField.getText())));
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    private void jake() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search by department and number");
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        TextField numField = new TextField();
        Label depLabel = new Label("Department");
        Label numLabel = new Label("Course Number");
        Button resultButton = new Button("Search!");
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, numLabel, numField, resultsArea, resultButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue(), numField.getText())));
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    @Override
    public void start(Stage mainStage) {
        mainStage.setTitle("Classify");
        Button coursesButton = new Button("Courses");
        Button professorsButton = new Button("Professors");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add(coursesButton, 0, 0);
        gridPane.add(professorsButton, 1, 0);
        coursesButton.setOnAction(e -> showCoursesWindow());
        professorsButton.setOnAction(e -> showProfessorsWindow());
        Scene scene = new Scene(gridPane, 200, 100);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
    private void showCoursesWindow() {
        Stage coursesStage = new Stage();
        coursesStage.setTitle("Courses Menu");
        Button searchButton = new Button("Search by department and course number");
        Button filterByDepartmentButton = new Button("Filter by department");
        Button filterAboveThresholdButton = new Button("Filter courses above a threshold");
        Button filterBelowThresholdButton = new Button("Filter courses below a threshold");
        Button addToBlacklistButton = new Button("Add to blacklist");
        GridPane coursesGridPane = new GridPane();
        coursesGridPane.setPadding(new Insets(10, 10, 10, 10));
        coursesGridPane.setVgap(10);
        coursesGridPane.setHgap(10);
        coursesGridPane.add(searchButton, 0, 0);
        coursesGridPane.add(filterByDepartmentButton, 0, 1);
        coursesGridPane.add(filterAboveThresholdButton, 0, 2);
        coursesGridPane.add(filterBelowThresholdButton, 0, 3);
        coursesGridPane.add(addToBlacklistButton, 0, 4);
        searchButton.setOnAction(e -> showSearchDandNumWindow());
        filterByDepartmentButton.setOnAction(e -> showSearchDandNumWindow());
        Scene scene = new Scene(coursesGridPane, 400, 300);
        coursesStage.setScene(scene);
        coursesStage.show();
    }

    private void showProfessorsWindow() {
        Stage professorsStage = new Stage();
        professorsStage.setTitle("Professors Menu");
        Button searchByNameButton = new Button("Search professor by name");
        Button searchByClassButton = new Button("Search professors teaching a class");
        GridPane professorsGridPane = new GridPane();
        professorsGridPane.setPadding(new Insets(10, 10, 10, 10));
        professorsGridPane.setVgap(10);
        professorsGridPane.setHgap(10);
        professorsGridPane.add(searchByNameButton, 0, 0);
        professorsGridPane.add(searchByClassButton, 0, 1);
        Scene scene = new Scene(professorsGridPane, 400, 200);
        professorsStage.setScene(scene);
        professorsStage.show();
    }

}
