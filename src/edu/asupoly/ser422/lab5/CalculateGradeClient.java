package edu.asupoly.ser422.lab5;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.StringJoiner;

public class CalculateGradeClient {

    public static String process(String year, String subject) throws Exception {

        String urlString = "http://localhost:" + Lab5Servlet.calculateGradePort + "/lab5calc/calculateGrade?year=" + (year == null ? "" : year) + "&subject=" + (subject == null ? "" : subject);
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            System.out.println("Error connecting to Calculate Grade : " + +conn.getResponseCode() + " :: message " + conn.getResponseMessage());
            conn.disconnect();
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        StringJoiner result = new StringJoiner("\n");
        String output;

        while ((output = br.readLine()) != null) {
            result.add(output);
        }

        Map<String, String> gradeResponse = new ObjectMapper().readValue(result.toString(), Map.class);
        conn.disconnect();
        return gradeResponse.get("grade");
    }
}
