/*
 * Lab5Servlet.java
 *
 * Copyright:  2008 Kevin A. Gary All Rights Reserved
 *
 */
package edu.asupoly.ser422.lab5;


import java.io.*;
import java.util.Properties;

import javax.servlet.*;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class Lab5Servlet extends HttpServlet {

	static String calculateGradePort;
	static String letterGradePort;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		Properties props = new Properties();
		try {
			InputStream propFile = this.getClass().getClassLoader().getResourceAsStream("application.properties");
			props.load(propFile);
			propFile.close();
			calculateGradePort = props.getProperty("lab5calcport");
			letterGradePort = props.getProperty("lab5mapport");
		}
		catch (IOException ie) {
			ie.printStackTrace();
			throw new ServletException("Could not open property file");
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		StringBuffer pageBuf = new StringBuffer();
		String year = req.getParameter("year");
		String subject = req.getParameter("subject");
		System.out.println("year received : " + year);
		System.out.println("subject received : " + subject);

		try {

			String grade = CalculateGradeClient.process(year, subject);

			System.out.println("Received grade from first server : " + grade);

			String letterGrade = MapLetterGradeClient.process(grade);


			pageBuf.append("\n\t<br/>Grade: " + grade);
			pageBuf.append("\n\t<br/>Letter: " + letterGrade);

			res.setContentType("text/html");

			PrintWriter out = res.getWriter();
			out.println(pageBuf.toString());

		} catch (Exception e){
			e.printStackTrace();
			res.sendError(500, "Server Exception Occured");

		}

	}
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		doGet(req, res);
	}
}
