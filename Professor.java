public class Scraper {
	

	public  void nanonets() throws IOException {
		String url = "https://www.ratemyprofessors.com/professor/1281160";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// con.setRequestProperty("User-Agent", "Mozilla/5.0");
		int responseCode = con.getResponseCode();
		System.out.println("Response code: " + responseCode);
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
		Elements deez = doc.select("span");
		for (Element dis : deez) {
			// String attribute = dis.attr("div.NameTitle__Name-dowf0z-0 cfjPUG");
			String attribute = dis.html();
			System.out.println(attribute);
		}
	}
}
