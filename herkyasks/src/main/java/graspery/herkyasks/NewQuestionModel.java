package graspery.herkyasks;

import java.util.ArrayList;

public class NewQuestionModel {
	private String question;
	private String type;
	private ArrayList<String> answers;
	
	public NewQuestionModel(String question, String type, ArrayList<String> answers) {
		this.question = question;
		this.type = type;
		this.answers = answers;
	}

	public String getString() {
		return question + " -- " + type + " -- " + answers.toString() + "  HASHCODE:  " + this.hashCode();
	}
	
	public ArrayList<String> getAnswers() {
		return answers;
	}
	public String getType() {
		return type;
	}
	public String getQuestion() {
		return question;
	}
}
