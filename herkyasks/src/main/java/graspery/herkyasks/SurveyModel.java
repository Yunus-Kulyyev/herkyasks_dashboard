package graspery.herkyasks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class SurveyModel {
	private String surveyName;
	private ArrayList<String> questions;
	private ArrayList<UserResponse> userResponses;
	
	public SurveyModel(String surveyName, ArrayList<String> questions, ArrayList<UserResponse> userResponses) {
		this.surveyName = surveyName;
		this.userResponses = userResponses;
		this.questions = questions;
	}

	public String getSurveyName() {
		return surveyName;
	}
	
	public ArrayList<String> getQuestions() {
		return questions;
	}

	public ArrayList<UserResponse> getUserResponses() {
		return userResponses;
	}	
	
	public void generateCSV(String dir) {
		String csv = ",";
		
		for(int i = 0; i < questions.size(); i++) {
			String question = questions.get(i).replace(',', ';');
			csv = csv + question + ",";
		}
		csv = csv.substring(0, csv.length()-1);
		csv = csv + "\n";
		
		for(int i = 0; i < userResponses.size(); i++) {
			csv = csv + userResponses.get(i).getUserKey() + ",";
			for(int j = 0; j < userResponses.get(i).getResponses().size(); j++) {
				String response = userResponses.get(i).getResponses().get(j).replace(',', ';');
				response = response.replace(")", "");
				response = response.replace("(", "");
				
				csv = csv + response + ",";
			}
			csv = csv.substring(0, csv.length()-1);
			csv = csv + "\n";
		}
		
		try {
			File file = new File(dir + "\\" + surveyName + ".csv");
			PrintWriter out = new PrintWriter(file);
			out.println(csv);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

	public void setSurveyName(String surveyName) {
		this.surveyName = surveyName;
	}

	public void setQuestions(ArrayList<String> questions) {
		this.questions = questions;
	}

	public void setUserResponses(ArrayList<UserResponse> userResponses) {
		this.userResponses = userResponses;
	}
	
	
}
