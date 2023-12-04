/**
 * Classify Application
 * Copyright 2023 Miles Clements, Jake Ross, Justin Darling, Harrison Frisk, Samuel Clark
 */

package source;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassifyGUI extends Application {

    static ArrayList<Course> courseData;
    static ArrayList<Professor> profData;
    static ArrayList<Course> backup;
    
    // Start backend methods
    
    public static void main(String[] args) {
        courseData = new ArrayList<>();
        profData = new ArrayList<>();
        try {
            readData(courseData);
            backup = deepCopy(backup, courseData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        launch(args);
    }
    /**
     * Copies the contents of one list to another.
     * @param copyTo List to copy elements to (data will be cleared!)
     * @param toCopy List to copy elements from
     * @return
     */
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
    
    /**
     * Checks if a professor exists by their name.
     * @param name Name to search for
     * @return True if professor exists, false otherwise
     */
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
    
    /**
     * Exports results (text) to a .PDF file in the Classify directory.
     * @param text Results to export to the .PDF file
     */
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
    
    /**
     * Given a name, returns the corresponding Professor object.
     * @param name Name to search
     * @return Professor object if exists, null otherwise
     */
    public static Professor getProfByName(String name) {
        for (int i = 0; i < profData.size(); i++) {
            if (name.equals(profData.get(i).getName())) {
                return profData.get(i);
            }
        }
        return null;
    }
    
    /**
     * Parse a professor name for use in the professor object.
     * Parsing includes removing titles (Mr. Dr. etc), swapping
     * first and last names if they are flipped, removes additional
     * appended names, and removing quotation marks.
     * @param name Professor name to parse
     * @return Parsed professor name
     */
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
    
    /**
     * Method used to fix a name that was read wrong due to commas.
     * @param values Values read for Course object
     * @return Fixed String Array of values with a correct name
     */
    public static String[] fixName(String[] values) {
        values[8] += values[9];
        String[] fixedArray = new String[9];
        for (int i = 0; i < 9; i++) {
            fixedArray[i] = values[i];
        }
        return fixedArray;
    }
    
    /**
     * Fixes a course title that was read wrong due to commas in name.
     * @param values Values read for a Course object
     * @return Corrected String array with correct course title
     */
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
    
    /**
     * Helper method to find out any potential issues with a given
     * String array containing values for a Course object
     * @param values String array of values for Course object
     * @return Corrected String Array
     */
    public static String[] correctLine(String[] values) {
        if (values[4].substring(0, 1).equals("\"")) {
            values = fixCommasInName(values);
        }
        if (values.length == 10) {
            values = fixName(values);
        }
        return values;
    }
    
    /**
     * Checks all values in String array for null or placeholder values.
     * Replaces them with n/a for readability.
     * @param values Values with potential nulls
     * @return String array with corrected nulls
     */
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
    
    /**
     * Removes any redundant spaces in any given value for readability.
     * @param values Values with potential redundant spaces
     * @return String array with corrected values
     */
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
    
    /**
     * Reads data from the given .csv file.
     * @param courseData List containing all course data
     * @throws IOException thrown if .csv file not found
     */
    public static void readData(ArrayList<Course> courseData) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("src\\source\\courseData.csv"));
        String line;
        br.readLine();
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
     * Gets an ObservableList of strings containing all professors
     * @return List of all professors
     */
    public ObservableList<String> getAllProfessors() {
        HashSet<String> profList = new HashSet<String>();
        for (int i = 0; i  < profData.size(); i++) {
            if (!(profData.get(i).getName().equals("n/a"))) {
                profList.add(profData.get(i).getName());
            }

        } 
        return FXCollections.observableArrayList(profList);
    }
    
    /**
     * Gets an ObservableList of strings containing a curated list of times.
     * @return List of curated times
     */
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
    
    /**
     * Returns an ObservableList of strings containing days of the week.
     * @return List of days of week
     */
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
    
    /**
     * Helper method used to guide blacklist values to their proper removal.
     * @param category Category to remove
     * @param value Value to remove
     */
    public void addBlackListGuide(String category, String value) {
    	if (value == null) {
    		category = "NULL_VALUE";
    	}
        switch (category) {
        case "NULL_VALUE": // don't access/add anything
        	break;
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
    
    
    /**
     * Adds a professor to the blacklist.
     * @param professor Professor name to add
     * @return New courseList minus Professor that was blacklisted
     */
    public ArrayList<Course> blackListProfessor(String professor) {
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getProfName().equals(professor)) {
                courseData.remove(i);
                i--;
            }
        }
        return courseData;
    }
    
    /**
     * Adds a time to the blacklist.
     * @param time Time to blacklist
     * @return Course List minus time blacklisted
     */
    public ArrayList<Course> blackListTime(String time) {
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getTime().contains(time)) {
                courseData.remove(i);
                i--;
            }
        }
        return courseData;
    }
    
    /**
     * Adds a day to the blacklist.
     * @param day Day of week to blacklist
     * @return Course list minus day to blacklist
     */
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
    
    /**
     * Gets a professor's RateMyProfessor score and reviews.
     * @param name Name to search
     * @return RateMyProfessor score and reviews.
     * @throws IOException
     */
    public static String rateMyProfessor(String name) throws IOException {     
        BufferedReader br = new BufferedReader
                (new FileReader("src\\source\\rateMyProfID.csv"));
        String line;
        String id = "";
        while ((line = br.readLine()) != null) {
            String[] lines = line.split(",");
            if (lines[0].equals(name)) {
                id = lines[1];
            }
        }
       
        if (id.equals("")) {
            return "NA";
        }
        
        String url = "https://www.ratemyprofessors.com/professor/"
                    + id;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            BufferedReader in = new BufferedReader
                    (new InputStreamReader(con.getInputStream()));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);

            }
            in.close();
            String html = response.toString();
            Document doc = Jsoup.parse(html);

            String returnVal = "RateMyProfessor:\n\n";
            Elements deez1 = doc.select("div.RatingValue__Numerator-qw8sqy-2");
            for (Element dis : deez1) {
                returnVal += "Rating: " + dis.html() + " / 5\n\n";
            }
            returnVal += "Comments:\n";
            Elements deez2 = doc.select("div.Comments__StyledComments-dzzyvm-0");
            for (Element dis : deez2) {
                returnVal += dis.html() + "\n\n";
            }
            return returnVal;
    }

    
    
    // End backend methods
    
    
    // Start query methods
    
    /**
     * Checks if a course is equal to a given department
     * and course number combo.
     * @param currentCourse Course to compare against
     * @param d Department
     * @param c Course number
     * @return true if equal, false otherwise
     */
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
    
    /**
     * Searches for courses with the given criteria.
     * @param department Department
     * @param courseNum Course Number
     * @return String containing all matching courses information
     */
    public static String courseQuery(String department, String courseNum) {
        String result = "";
        if (courseNum.equals("") && department == null) {
        	result = "Error: Pick a department!\nError: Enter a course number!";
        	return result;
        }
        if (courseNum.equals("")) {
        	result = "Error: Enter a course number!";
        	return result;
        }
        if (department == null) {
        	result = "Error: Pick a department!";
        	return result;
        }
        for (int i = 0; i < courseData.size(); i++) {
            if (checkEquality(courseData.get(i), department, courseNum)) {
                result += courseData.get(i).getInfo();
            }
        }
        if (result.equals("")) {
        	result = "No data matches the search condition.";
        }
        return result;
    }
    
    /**
     * Searches for courses with the given criteria.
     * @param department Department
     * @return String containing all matching courses information
     */
    public static String courseQuery(String department) {
        String result = "";
        if (department == null) {
        	result = "Error: Pick a department";
        	return result;
        }
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getDepartment().equals(department)) {
                result += courseData.get(i).getInfo();
            }
        }
        if (result.equals("")) {
        	result = "No data matches the search condition.";
        }
        return result;
    }
    
    /**
     * Searches for courses that are above or below a certain
     * level threshold
     * @param sign Greater than, less than
     * @param courseNum Number to compare to
     * @return String of matching courses info
     */
    public static String signCourseQuery(String sign, String courseNum) {
    	String result = "";
    	// account for null
    	if (sign == null && courseNum.equals("")) {
    		result = "Error: Pick a sign!\nerror: Enter a course number!";
    		return result;
    	}
    	if (sign == null) {
    		result = "Error: Pick a sign!";
    		return result;
    	}
    	if (courseNum.equals("")) {
    		result = "Error: Enter a course number!";
    		return result;
    	}
    	// make variables / accounting for 1/2/3/4
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
        if (result.equals("")) {
        	result = "No data matches the search conditions.";
        }
        return result;
    }
    
    /**
     * Searches for and returns all the info about a given professor
     * @param professor
     * @return String containing all matching courses information
     */
    public static String professorQuery(String professor) {
        String result = "";
        for (int i = 0; i < profData.size(); i++) {
            if ((profData.get(i).getName().equals(professor))) {
               
                result += profData.get(i).getInfo();
                break;
            }
         }
        return result;
    }
    
    /**
     * Searches for a professor given a course they teach
     * @param dep Department of course
     * @param num Number of course
     * @return String of matching professor's info
     */
    public String searchProfByClass(String dep, String num) {
        String result = "";
        HashSet<String> containsMap = new HashSet<>();
        //error handling 
        if (dep == null && num.equals("")) {
            result = "Error: Please choose a department \nError: Please enter a course number"
                    + " (e.g. \"101\", \"274\")";
        return result;
        }
        if (dep == null) {
            result = "Error: Please choose a department";  
            return result;
        }
        if (num.equals("")) {
            result = "Error: Please enter a course number (e.g. \"101\", \"274\")";
            return result;
        }
        
        for (int i = 0; i < courseData.size(); i++) {
            if (courseData.get(i).getDepartment().equals(dep)) {
                if (courseData.get(i).getCourseNum().equals(num)) {
                    containsMap.add(courseData.get(i).getProfessor());
                }
            }
        }
        
        if(containsMap.isEmpty()) {
            result = "No data matches the search conditions.";
            return result;
        }
        
        
        for (String element : containsMap) {
            result += element;
            result += "\n";
        }
        return result;
    }
    
    

    // Start GUI/FX methods
    
    /**
     * Shows menu for searching by department and course number.
     */
    private void showSearchDandNumWindow() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search by department and number");
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        searchStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        TextField numField = new TextField();
        Label depLabel = new Label("Department");
        depLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Label numLabel = new Label("Course Number");
        numLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
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
        root.getChildren().addAll(depLabel, departmentBox, numLabel, numField, resultsArea, resultButton, pdfButton, backButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue(), numField.getText())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 410);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    
    private void showProfessorNameSearchWindow() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search a professor by their name");
        ObservableList<String> professors = getAllProfessors();
        ComboBox<String> professorBox = new ComboBox<>(professors);
        
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        
        
        Label professorLabel = new Label("Professor");
        professorLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Button resultButton = new Button("Search");
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        TextArea rateMyProfessorOutput = new TextArea();
        rateMyProfessorOutput.setEditable(false);
        
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(professorLabel, professorBox, resultButton, resultsArea,
                rateMyProfessorOutput, backButton);
        resultButton.setOnAction(e -> {
            String professorName = professorBox.getValue();
            String result = professorQuery(professorName);
            String rateMyProfessorInfo;
            try {
                rateMyProfessorInfo = rateMyProfessor(professorName);
            } catch (Exception E) {
                rateMyProfessorInfo = "N/A";
            }
            if(result == "") {
                resultsArea.setText("Error: Choose a professor!");
            } else {
            resultsArea.setText(result);
            rateMyProfessorOutput.setText(rateMyProfessorInfo);
            }
        });
        
        Scene scene = new Scene(root, 800, 500);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
    /**
     * Shows the menu for searching a professor by course.
     */
    private void showSearchProfByClass() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Search for professors by course");
        Label depLabel = new Label("Department");
        depLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        searchStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        
        
        Label courseLabel = new Label("Course Number");
        courseLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Button resultButton = new Button("Search");
        TextField courseField = new TextField();
        TextArea resultsArea = new TextArea();
        resultsArea.setEditable(false);
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(depLabel, departmentBox, courseLabel, courseField, resultButton, resultsArea, backButton);
        resultButton.setOnAction(e -> resultsArea.setText(searchProfByClass(departmentBox.getValue(),
                courseField.getText())));
        Scene scene = new Scene(root, 800, 390);
        searchStage.setScene(scene);
        searchStage.show();

    }
  
    /**
     * Shows the menu for searching for courses by department.
     */
    private void filterByDepartmentWindow() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Filter by Department");
        Label depLabel = new Label("Department");
        depLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        ObservableList<String> departments = getAllDepartments();
        ComboBox<String> departmentBox = new ComboBox<>(departments);
        searchStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        
        
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
        root.getChildren().addAll(depLabel, departmentBox, resultsArea, resultButton, pdfButton, backButton);
        resultButton.setOnAction(e -> resultsArea.setText(courseQuery(departmentBox.getValue())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 350);
        searchStage.setScene(scene);
        searchStage.show();
    }

    /**
     * Shows the menu for searching by a course number threshold.
     */
    private void filterWithThreshold() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Filter with a class level threshold");
        ObservableList<String> signs = makeSignList();
        ComboBox<String> signBox = new ComboBox<>(signs);
        searchStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        
        TextField numField = new TextField();
        Label numLabel = new Label("Course Level");
        numLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Label signLabel = new Label("Greater/Less Than");
        signLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
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
        root.getChildren().addAll(signLabel, signBox, numLabel, numField, resultsArea, resultButton, pdfButton, backButton);
        resultButton.setOnAction(e -> resultsArea.setText(signCourseQuery(signBox.getValue(), numField.getText())));
        pdfButton.setOnAction(e -> exportToPDF(resultsArea.getText()));
        Scene scene = new Scene(root, 800, 400);
        searchStage.setScene(scene);
        searchStage.show();

    }
    
    /**
     * Shows the menu for adding attributes to the blacklist.
     */
    private void addtoBlacklistMenu() {
        Stage searchStage = new Stage();
        searchStage.setTitle("Add to Blacklist");
        searchStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            searchStage.close();
        });
        
        
        ObservableList<String> choices = FXCollections.observableArrayList(
                "Professor",
                "Time",
                "Days"
        );
        ComboBox<String> choiceBox = new ComboBox<>(choices);
        ComboBox<String> optionBox = new ComboBox<>();
        optionBox.setDisable(true);
        Label categoryLabel = new Label("Category");
        categoryLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Label optionLabel = new Label("Option");
        optionLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
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
        if (optionBox.getValue() == null) {
        	result.setText("Pick an option to blacklist");
        } else {
        	result.setText("Added " + optionBox.getValue() + " to the blacklist");
        }
        });
        resetButton.setOnAction(e -> {
            courseData = deepCopy(courseData, backup);
            result.setText("Blacklist reset.");
        });
        result.setText("");
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(categoryLabel, choiceBox, optionLabel, optionBox, addButton, result, resetButton, backButton);
        Scene scene = new Scene(root, 250, 300);
        searchStage.setScene(scene);
        searchStage.show();
    }
    
     /**
      * Shows the Classify main menu.
      */
    public void start(Stage mainStage) {
        mainStage.setTitle("Classify");
        mainStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        mainStage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
            @Override
            public void handle(javafx.stage.WindowEvent event) {
                Platform.exit();
            }
        });

        Label titleLabel = new Label("Classify");
        titleLabel.setFont(Font.font("Ariel", FontWeight.BOLD, 20));
        Button coursesButton = new Button("Courses");
        Button professorsButton = new Button("Professors");
        Button addToBlacklistButton = new Button("Add to blacklist");
        coursesButton.setOnAction(e -> showCoursesWindow());
        professorsButton.setOnAction(e -> showProfessorsWindow());
        addToBlacklistButton.setOnAction(e -> addtoBlacklistMenu());
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(titleLabel, coursesButton, professorsButton, addToBlacklistButton, imageView);
        Scene scene = new Scene(vbox, 250, 215);
        mainStage.setScene(scene);
        mainStage.show();
    }
    
    /**
     * Shows the menu for Course operations.
     */
    private void showCoursesWindow() {
        Stage coursesStage = new Stage();
        coursesStage.setTitle("Courses Menu");
        Button searchButton = new Button("Search by department and course number");
        Button filterByDepartmentButton = new Button("Filter by department");
        Button filterWithButton = new Button("Filter courses with a class level threshold");
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        coursesStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            coursesStage.close();
        });
        GridPane coursesGridPane = new GridPane();
        coursesGridPane.setPadding(new Insets(10, 10, 10, 10));
        coursesGridPane.setVgap(10);
        coursesGridPane.setHgap(10);
        coursesGridPane.add(searchButton, 0, 0);
        coursesGridPane.add(filterByDepartmentButton, 0, 1);
        coursesGridPane.add(filterWithButton, 0, 2);
        GridPane.setHalignment(backButton, javafx.geometry.HPos.CENTER);
        coursesGridPane.add(backButton, 0, 3);
        searchButton.setOnAction(e -> showSearchDandNumWindow());
        filterByDepartmentButton.setOnAction(e -> filterByDepartmentWindow());
        filterWithButton.setOnAction(e -> filterWithThreshold());
        Scene scene = new Scene(coursesGridPane, 260, 165);
        coursesStage.setScene(scene);
        coursesStage.show();
    }

    /**
     * Shows the menu for Professor operations.
     */
    private void showProfessorsWindow() {
        Stage professorsStage = new Stage();
        professorsStage.setTitle("Professors Menu");
        Button searchByNameButton = new Button("Search professor by name");
        Button searchByClassButton = new Button("Search professors teaching a class");
        professorsStage.getIcons().add(new Image(ClassifyGUI.class.getResourceAsStream("classify.png")));
        InputStream stream = ClassifyGUI.class.getResourceAsStream("classify.png");
        Image image = new Image(stream);
        ImageView imageView = new ImageView(image);
        Button backButton = new Button("Back", imageView);
        backButton.setOnAction(event -> {
            professorsStage.close();
        });
        
        GridPane professorsGridPane = new GridPane();
        professorsGridPane.setPadding(new Insets(10, 10, 10, 10));
        professorsGridPane.setVgap(10);
        professorsGridPane.setHgap(10);
        professorsGridPane.add(searchByNameButton, 0, 0);
        professorsGridPane.add(searchByClassButton, 0, 1);
        GridPane.setHalignment(backButton, javafx.geometry.HPos.CENTER);
        professorsGridPane.add(backButton,  0, 3);
        Scene scene = new Scene(professorsGridPane, 225, 140);
        searchByNameButton.setOnAction(e -> showProfessorNameSearchWindow());
        searchByClassButton.setOnAction(e -> showSearchProfByClass());
        professorsStage.setScene(scene);
        professorsStage.show();
    }
    
}
