/**
 * Professor Object Class
 * Copyright 2023 Miles Clements, Sam Clark
 */

package source;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;


public class Professor {
    
    private String name;
    private String rating;
    private String[] comments;
    private ArrayList<Course> courses;
    
    public Professor(String name, ArrayList<Course> courses) {
        this.name = name;
        this.courses = courses;
        rateMyProfessor();
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addCourse(Course c) {
        courses.add(c);
    }
    
    public void printCourse() {
        
    for(int i = 0; i < courses.size(); i++) {
       this.courses.get(i).profDataPrint();
    }
    
        
    }

    public void rateMyProfessor() throws IOException {
        Scanner fileReader = new Scanner(new File("rateMyProfID.csv"));
		String id = "";
		fileReader.useDelimiter(",");
		
		while(fileReader.hasNext()) {
			String content = fileReader.next();
			if (content.indexOf(this.name) != -1) {
				id = fileReader.nextLine();
				id = id.substring(1, id.length());
			}
		}
        if (id.equals("")) {
            this.rating = "NA";
        } else {
            
		    String url = "https://www.ratemyprofessors.com/professor/"
                    + profToRating.get(this.name);
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

		    String rating = "NA";
		    Elements deez = doc.select("div.RatingValue__Numerator-qw8sqy-2");
            String rating = "NA";
		    for (Element dis : deez) {
			    attribute = dis.html();
		    }
		    this.rating = rating;
            
		    Elements deez2 = doc.select("div.Comments__StyledComments-dzzyvm-0");
            String[] comments = new String[deez2.size()];
            int index = 0;
		    for (Element dis : deez2) {
			    comments[i] = dis;
                index++;
		    }

            
        }

	}
    
}
