/*
 * Lab5Servlet.java
 *
 * Copyright:  2008 Kevin A. Gary All Rights Reserved
 *
 */
package edu.asupoly.ser422.lab5;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.StringJoiner;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 * @author Kevin Gary
 *
 */
@SuppressWarnings("serial")
public class Lab5Servlet extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		StringBuffer pageBuf = new StringBuffer();
		double grade;
		String year = req.getParameter("year");
		String subject = req.getParameter("subject");

		try {

			URL url = new URL("http://localhost:8080/lab5calc/calculateGrade");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.addRequestProperty("year", year);
			conn.addRequestProperty("subject", subject);

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			StringJoiner result = new StringJoiner("\n");
			String output;
			System.out.println("Output from Server .... \n");

			while ((output = br.readLine()) != null) {
				result.add(output);
			}

			Map<String, String> gradeResponse = new ObjectMapper().readValue(result.toString(), Map.class);
			conn.disconnect();

			System.out.println("Received grade from first server : " + gradeResponse.get("grade"));

			URL url2 = new URL("http://localhost:8080/lab5map/calculateLetterGrade");
			HttpURLConnection conn2 = (HttpURLConnection) url2.openConnection();
			conn2.setRequestMethod("GET");
			//conn2.addRequestProperty("grade", gradeResponse.get("grade"));
			conn2.addRequestProperty("grade", "99.56");


			if (conn2.getResponseCode() != 200) {
				br = new BufferedReader(new InputStreamReader((conn2.getInputStream())));
				result = new StringJoiner("\n");
				System.out.println("Error Output from second Server .... \n");

				while ((output = br.readLine()) != null) {
					System.out.println(output);

				}

				br = new BufferedReader(new InputStreamReader((conn2.getErrorStream())));
				result = new StringJoiner("\n");
				System.out.println("Error 2 Output from second Server .... \n");

				while ((output = br.readLine()) != null) {
					System.out.println(output);

				}
				return;
			}

			br = new BufferedReader(new InputStreamReader((conn2.getInputStream())));

			result = new StringJoiner("\n");
			System.out.println("Output from second Server .... \n");

			while ((output = br.readLine()) != null) {
				result.add(output);

			}

			Map<String, String> letterGradeResponse = new ObjectMapper().readValue(result.toString(), Map.class);
			letterGradeResponse.put("grade", gradeResponse.get("grade"));
			conn2.disconnect();

			res.getWriter().println(new ObjectMapper().writeValueAsString(letterGradeResponse));

		} catch (MalformedURLException e) {
			e.printStackTrace();
			res.sendError(500, "Server Exception Occured");

		} catch (IOException e) {
			e.printStackTrace();
			res.sendError(500, "Server Exception Occured");
		}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req, res);
	}
}
