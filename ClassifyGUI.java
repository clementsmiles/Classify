package source;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

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

    static ArrayList<Course> courseData;
    static ArrayList<Professor> profData;
    static ArrayList<Course> backup;
    
    // Start backend methods, may require separate classes
    
    public static void main(String[] args) {
        courseData = new ArrayList<>();
        profData = new ArrayList<>();
        try {
            readData(courseData);
            backup = deepCopy(backup, courseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        courseData.remove(0);
        launch(args);
    }
    
    private static ArrayList<Course> deepCopy(ArrayList<Course> copyTo, ArrayList<Course> toCopy) {
        if (copyTo == null) {
            copyTo = new ArrayList<Course>();
        } else {
            copyTo.clear();
        }
        copyTo.clear();
        for (int i = 0; i < toCopy.size(); i++) {
            copyTo.add(toCopy.get(i));
        }
        return copyTo;
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
    
    public static void exportToPDF(String text) {
        String[] lines = text.split("\n");
        try {
            int lineCount = 0;
            PDDocument output = new PDDocument();
            PDPage page = new PDPage();
            output.addPage(page);
            PDPageContentStream stream = new PDPageContentStream(output, page);
            PDFont currentFont = new PDType1Font(Standard14Fonts.FontName.TIMES_BOLD);
            stream.setFont(currentFont, 25);
            stream.beginText();
            stream.newLineAtOffset(25, 750);
            stream.showText("                                      Classify");
            stream.newLineAtOffset(0, -20);
            currentFont = new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN);
            stream.setFont(currentFont, 10);
            for (int i = 0; i < lines.length; i++) {
                stream.newLineAtOffset(0, -20);
                stream.showText(lines[i]);
                lineCount++;
                if (lineCount % 34 == 0) {
                    PDPage newPage = new PDPage();
                    output.addPage(newPage);
                    stream.endText();
                    stream.close();
                    stream = new PDPageContentStream(output, newPage);
                    stream.beginText();
                    stream.setFont(currentFont, 10);
                    stream.newLineAtOffset(25, 750);
                }
            }
            stream.endText();
            stream.close();
            int fileNum = 0;
            boolean fileCreated = false;
            do {
                File checkFile = new File("output(" + fileNum + ").pdf");
                if (checkFile.exists()) {
                    fileNum++;
                } else {
                    output.save("output(" + fileNum + ").pdf");
                    fileCreated = true;
                }
            } while (!fileCreated);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        String[] titles = {"Mr. ", "Dr. ", "Mrs. ", "Ms. "};
        for (int i = 0; i < titles.length; i++) {
            if (name.contains(titles[i])) {
                name = name.replaceAll(titles[i], "");
            }
        }
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
    
    public static String[] fixName(String[] values) {
        values[8] += values[9];
        String[] fixedArray = new String[9];
        for (int i = 0; i < 9; i++) {
            fixedArray[i] = values[i];
        }
        return fixedArray;
    }
    
    
    public static String[] fixCommasInName(String[] values) {
        boolean foundQuote = false;
        int iterator = 0;
        while (!foundQuote) {
            if (values[5 + iterator].contains("\"")) {
                for (int i = 5; i <= 5 + iterator; i++) {
                    values[4] += values[i];
                    values[i] = "";
                }
                foundQuote = true;
            } else {
                iterator++;
            }
        }
        values[4] = values[4].substring(1, values[4].length() - 1);
        ArrayList<String> fixedVals = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            if (!values[i].isEmpty()) {
                fixedVals.add(values[i]);
            }
        }
        String[] correctVals = fixedVals.toArray(new String[fixedVals.size()]);
        return correctVals;
    }
    
    public static String[] correctLine(String[] values) {
        if (values[4].substring(0, 1).equals("\"")) {
            values = fixCommasInName(values);
        }
        if (values.length == 10) {
            values = fixName(values);
        }
        return values;
    }
    
    public static String[] checkForNulls(String[] values) {
        for (int i = 0; i < 8; i++) {
            if (values[i].isBlank() || values[i].equals("|")) {
                values[i] = "n/a";
            }
        }
        if (values[8].equals("Staff")) {
            values[8] = "n/a";
        }
        return values;
    }
    
    public static String[] removeRedundantSpaces(String[] values) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].substring(0, 1).equals(" ")) {
                values[i] = values[i].substring(1);
            }
            if (values[i].substring(values[i].length() - 1,
                    values[i].length()).equals(" ")) {
                values[i] = values[i].substring(0, values[i].length() - 1);
            }
        }
        return values;
    }
    
    public static void readData(ArrayList<Course> courseData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\source\\courseData.csv"));
        String line;
        while ((line = br.readLine()) != null) {
            String[] lines = line.split(",");
            if (lines.length > 9) {
                lines = correctLine(lines);
            }
            lines = checkForNulls(lines);
            Professor p;
            lines[8] = parseProfessorName(lines[8]);
            lines = removeRedundantSpaces(lines);
            if (profExists(lines[8])) {
                p = getProfByName(lines[8]);
            } else {
                p = new Professor(lines[8], new ArrayList<Course>());
            }
            Course c = new Course(lines[0], lines[1], lines[2], lines[3],
                    lines[4], lines[5], lines[6], lines[7], p);
            courseData.add(c);
            p.addCourse(c);
            profData.add(p);
        }
    }
    
    public void removeTimes(String time) {
        
    }
    
    /**
     * Gets an ObservableList of strings containing all departments.
     * @return List of all departments
     */
    public ObservableList<String> getAllDepartments() {
        HashSet<String> depList = new HashSet<String>();
        for (int i = 0; i < courseData.size(); i++) {
            depList.add(courseData.get(i).getDepartment());
        }
        return FXCollections.observableArrayList(depList);
    }
    
    /**
     * Gets an ObservalbleList of strings containing all professors
     * @return List of all professors
     */
    public ObservableList<String> getAllProfessors() {
        HashSet<String> profList = new HashSet<String>();
        for (int i = 0; i  < profData.size(); i++) {
            profList.add(profData.get(i).getName());
        }
        return FXCollections.observableArrayList(profList);
    }
    
    public ObservableList<String> getAllTimes() {
        ObservableList<String> list = FXCollections.observableArrayList(
                "8:00am",
                "8:30am",
                "10:05am",
                "11:40am",
                "12:10pm",
                "12:15pm",
                "1:05pm",
                "1:15pm",
                "2:15pm",
                "2:50pm",
                "4:25pm",
                "5:00pm",
                "5:30pm",
                "6:00pm",
                "6:30pm",
                "6:45pm",
                "7:30pm"
        );
        return list;
    }
    
    public ObservableList<String> getAllDays() {
        ObservableList<String> list = FXCollections.observableArrayList(
                "M",
                "T",
                "W",
                "R",
                "F"
        );
        return list;
    }
    
    public void addBlackListGuide(String category, String value) {
        switch (category) {
        case "Professor":
            courseData = blackListProfessor(value);
            break;
        case "Time":
            courseData = blackListTime(value);
            break;
        case "Days":
            courseData = blackListDays(value);
            break;
        }
    }
    
    public ArrayList<Course> blackListProfessor(String professor) {
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getProfName().equals(professor)) {
                courseData.remove(i);
                i--;
            }
        }
        return courseData;
    }
    
    public ArrayList<Course> blackListTime(String time) {
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getTime().contains(time)) {
                courseData.remove(i);
                i--;
            }
        }
        return courseData;
    }
    
    public ArrayList<Course> blackListDays(String day) {
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getDays().contains(day)) {
                courseData.remove(i);
                i--;
            }
        }
        return courseData;
    }
    
     /** 
     * Makes an Observable list of strings that contain </> signs.
     * @return List of Signs
     */
    public ObservableList<String> makeSignList() {
        HashSet<String> signList = new HashSet<String>();
        signList.add("Greater Than");
        signList.add("Less Than");
        return FXCollections.observableArrayList(signList);
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
        for (int i = 0; i < courseData.size(); i++) {
            if (checkEquality(courseData.get(i), department, courseNum)) {
                result += courseData.get(i).getInfo();
            }
         }
        return result;
    }
    
    public static String courseQuery(String department) {
        String result = "";
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getDepartment().equals(department)) {
                result += courseData.get(i).getInfo();
            }
         }
        return result;
    }
    
    public static String signCourseQuery(String sign, String courseNum) {
    	// make variables / accounting for 1/2/3/4
        String result = "";
        if (courseNum.equals("1")) {
        	courseNum = "100";
        }
        if (courseNum.equals("2")) {
        	courseNum = "200";
        }
        if (courseNum.equals("3")) {
        	courseNum = "300";
        }
        if (courseNum.equals("4")) {
        	courseNum = "400";
        }
        int num = Integer.parseInt(courseNum);
        boolean lessOrGreater = false; // false = greater than
        // figure out the sign
        if (sign.equals("Less Than")) {
        	lessOrGreater = true; // true = less than
        }
        // loop through
        for (int i = 1; i < courseData.size(); i++) {
        	String current = courseData.get(i).getCourseNum();
        	int currentNum;
        	try {
        		currentNum = Integer.parseInt(current);
        	} catch (Exception e) {
        		current = courseData.get(i).getCourseNum();
        		current = current.substring(0, current.length() - 1);
        		currentNum = Integer.parseInt(current);
        	}
        	
        	if (lessOrGreater == true) { // less than
        		if (currentNum <= num) {
        			result += courseData.get(i).getInfo();
        		}
        	} else { // more than
        		if (currentNum > num) {
        			result += courseData.get(i).getInfo();
        		}
        	}
        }
        return result;
    }
    
    
    public String searchProfByClass(String dep, String num) {
        String result = "";
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getDepartment().equals(dep)) {
                if (courseData.get(i).getCourseNum().equals(num)) {
                    result += courseData.get(i).getProfessor();
                    result += "\n";
                }
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
        Button pdfButton = new Button("Save to PDF");
        pdfButton.setDisable(true);
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.textProperty().addListener((observable, oldValue, newValue) -> {
            pdfButton.setDisable(newValue.trim().isEmpty());
        });
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, numLabel, numField, resultsArea, resultButton, pdfButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue(), numField.getText())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    //incomplete as of 11/30/2023
    private void showProfessorNameSearchWindow() {
        Stage profSearchStage = new Stage();
        profSearchStage.setTitle("Search a professor by their name");
        ObservableList<String> professors = getAllProfessors();
        ComboBox<String> professorBox = new ComboBox<>(professors);
        Label professorLabel = new Label("Professor");
        Button resultButton = new Button("Search");
        TextField resultsArea = new TextField();
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(professorLabel, professorBox, resultButton, resultsArea);
        resultButton.setOnAction(e -> resultsArea.setText("Hi"));
        Scene scene = new Scene(root, 800, 500);
        profSearchStage.setScene(scene);
        profSearchStage.show();
    }
    
    private void showSearchProfByClass() {
        Stage profSearchStage = new Stage();
        profSearchStage.setTitle("Search for professors by course");
        Label depLabel = new Label("Department");
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        Label courseLabel = new Label("Course");
        Button resultButton = new Button("Search");
        TextField courseField = new TextField();
        TextArea resultsArea = new TextArea();
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, courseLabel, courseField, resultButton, resultsArea);
        resultButton.setOnAction(e -> resultsArea.setText(searchProfByClass(departmentBox.getValue(), courseField.getText())));
        Scene scene = new Scene(root, 800, 500);
        profSearchStage.setScene(scene);
        profSearchStage.show();
    }
    
  
  
    private void filterByDepartmentWindow() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Filter by Department");
        Label depLabel = new Label("Department");
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        Button resultButton = new Button("Search!");
        Button pdfButton = new Button("Save to PDF");
        pdfButton.setDisable(true);
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.textProperty().addListener((observable, oldValue, newValue) -> {
            pdfButton.setDisable(newValue.trim().isEmpty());
        });
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, resultsArea, resultButton, pdfButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();
    }

        
        
    
    private void filterWithThreshold() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Filter with a class level threshold");
        ObservableList<String> signs = makeSignList();
        ComboBox<String> signBox = new ComboBox<>(signs);
        TextField numField = new TextField();
        Label numLabel = new Label("Course Level");
        Label signLabel = new Label("Greater/Less Than");
        Button resultButton = new Button("Search!");
        Button pdfButton = new Button("Save to PDF");
        pdfButton.setDisable(true);
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        resultsArea.setWrapText(true);
        resultsArea.textProperty().addListener((observable, oldValue, newValue) -> {
            pdfButton.setDisable(newValue.trim().isEmpty());
        });
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(signLabel, signBox, numLabel, numField, resultsArea, resultButton, pdfButton);
        resultButton.setOnAction(e -> resultsArea.setText(signCourseQuery(signBox.getValue(), numField.getText())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();

    }
    
    
    private void addtoBlacklistMenu() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Add to Blacklist");
        ObservableList<String> choices = FXCollections.observableArrayList(
                "Professor",
                "Time",
                "Days"
        );
        ComboBox<String> choiceBox = new ComboBox<>(choices);
        ComboBox<String> optionBox = new ComboBox<>();
        optionBox.setDisable(true);
        Button addButton = new Button("Add to blacklist");
        Button resetButton = new Button("Reset Blacklist");
        Label result = new Label(" ");
        addButton.setDisable(true);
        choiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
        optionBox.getItems().clear();
        if (newValue != null) {
            switch (newValue) {
            case "Professor":
                optionBox.setItems(getAllProfessors());
                break;
            case "Time":
                optionBox.setItems(getAllTimes());
                break;
            case "Days":
                optionBox.setItems(getAllDays());
                break;
            }
            optionBox.setDisable(false);
            addButton.setDisable(false);
        } else {
            optionBox.setDisable(true); 
            addButton.setDisable(true);
        }
        });
        
        addButton.setOnAction(e -> {
            addBlackListGuide(choiceBox.getValue(), optionBox.getValue());
            result.setText("Added " + optionBox.getValue() + " to the blacklist");
        });
        resetButton.setOnAction(e -> {
            courseData = deepCopy(courseData, backup);
            result.setText("Blacklist reset.");
        });
        result.setText("");
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(choiceBox, optionBox, addButton, result, resetButton);
        Scene scene = new Scene(root, 500, 300);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    @Override
    public void start(Stage mainStage) {
        mainStage.setTitle("Classify");
        mainStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        Label titleLabel = new Label("Classify");
        Button coursesButton = new Button("Courses");
        Button professorsButton = new Button("Professors");
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.add (titleLabel, 0, 0);
        gridPane.add(coursesButton, 0, 1);
        gridPane.add(professorsButton, 1, 1);
        gridPane.setColumnSpan(titleLabel, 2);
        coursesButton.setOnAction(e -> showCoursesWindow());
        professorsButton.setOnAction(e -> showProfessorsWindow());
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10,10, 10, 10));
        vbox.getChildren().add(gridPane);
        Scene scene = new Scene(vbox, 200, 100);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
    private void showCoursesWindow() {
        Stage coursesStage = new Stage();
        coursesStage.setTitle("Courses Menu");
        Button searchButton = new Button("Search by department and course number");
        Button filterByDepartmentButton = new Button("Filter by department");
        Button filterWithButton = new Button("Filter courses with a class level threshold");
        Button addToBlacklistButton = new Button("Add to blacklist");
        GridPane coursesGridPane = new GridPane();
        coursesGridPane.setPadding(new Insets(10, 10, 10, 10));
        coursesGridPane.setVgap(10);
        coursesGridPane.setHgap(10);
        coursesGridPane.add(searchButton, 0, 0);
        coursesGridPane.add(filterByDepartmentButton, 0, 1);
        coursesGridPane.add(filterWithButton, 0, 2);
        coursesGridPane.add(addToBlacklistButton, 0, 3);
        searchButton.setOnAction(e -> showSearchDandNumWindow());
        filterByDepartmentButton.setOnAction(e -> filterByDepartmentWindow());
        filterWithButton.setOnAction(e -> filterWithThreshold());
        addToBlacklistButton.setOnAction(e -> addtoBlacklistMenu());
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
        searchByNameButton.setOnAction(e -> showProfessorNameSearchWindow());
        searchByClassButton.setOnAction(e -> showSearchProfByClass());
        professorsStage.setScene(scene);
        professorsStage.show();
    }
    
}
