package graspery.herkyasks;

import java.util.ArrayList;

public class NewSurveyModel {
	private String title;
	private String date;
	private String availability;
	private ArrayList<NewQuestionModel> questions;
	
	public NewSurveyModel(String title, String date, String availability, ArrayList<NewQuestionModel> questions) {
		this.title = title;
		this.date = date;
		this.availability = availability;
		this.questions = questions;
	}
	
	
	
	public void setTitle(String title) {
		this.title = title;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public void setAvailability(String availability) {
		this.availability = availability;
	}



	public void setQuestions(ArrayList<NewQuestionModel> questions) {
		this.questions = questions;
	}



	public String getAvailability() {
		return availability;
	}
	public String getDate() {
		return date;
	}
	public ArrayList<NewQuestionModel> getQuestions() {
		return questions;
	}
	public String getTitle() {
		return title;
	}
}
