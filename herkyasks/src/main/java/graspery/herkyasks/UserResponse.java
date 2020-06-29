package graspery.herkyasks;

import java.util.ArrayList;

public class UserResponse {
	private String userKey;
	private ArrayList<String> responses;
	
	public UserResponse(String userKey, ArrayList<String> responses) {
		this.userKey = userKey;
		this.responses = responses;
	}

	public String getUserKey() {
		return userKey;
	}

	public ArrayList<String> getResponses() {
		return responses;
	}
	
	
}
