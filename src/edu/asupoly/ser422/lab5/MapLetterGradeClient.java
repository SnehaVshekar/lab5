package edu.asupoly.ser422.lab5;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.StringJoiner;

public class MapLetterGradeClient {

    public static String process(String grade) throws Exception{
        URL url = new URL("http://localhost:" + Lab5Servlet.letterGradePort + "/lab5map/calculateLetterGrade?grade=" + grade);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            System.out.println("Error connecting to Calculate Grade : " + + conn.getResponseCode() + " :: message " + conn.getResponseMessage());
            conn.disconnect();
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

        String output;
        StringJoiner result = new StringJoiner("\n");

        while ((output = br.readLine()) != null) {
            result.add(output);

        }

        Map<String, String> letterGradeResponse = new ObjectMapper().readValue(result.toString(), Map.class);
        conn.disconnect();
        return letterGradeResponse.get("letterGrade");
    }
}
