package graspery.herkyasks;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHelper {
	
	private static FirebaseHelper single_instance = null; 
	private static DatabaseReference ref;
    private static ArrayList<SurveyModel> surveys;
    private static ArrayList<NewSurveyModel> allSurveys;
  
    
    public static ArrayList<SurveyModel> getSurveys() {
    	return surveys;
    }
    
    public static ArrayList<NewSurveyModel> getResults() {
    	return allSurveys;
    }
    
    // private constructor restricted to this class itself 
    private FirebaseHelper() 
    { 
    	surveys = new ArrayList<SurveyModel>();
    	FileInputStream serviceAccount;
		try {
			serviceAccount = new FileInputStream("credentials.json");
			
			FirebaseOptions options = new FirebaseOptions.Builder()
					  .setCredentials(GoogleCredentials.fromStream(serviceAccount))
					  .setDatabaseUrl("https://herkyasks.firebaseio.com")
					  .build();

			FirebaseApp.initializeApp(options);
		
		} catch (IOException e) {
			e.printStackTrace();
		} 
    } 
  
    // static method to create instance of Singleton class 
    public static FirebaseHelper getInstance() 
    { 
        if (single_instance == null)  {
            single_instance = new FirebaseHelper(); 
            ref = FirebaseDatabase.getInstance().getReference();
            getAllSurveys();
            //readData();
        }
  
        return single_instance; 
    }
    
    public void refreshData() {
    	getAllSurveys();
    }

    public void uploadSurvey(NewSurveyModel model) {
    	DatabaseReference usersRef = ref.child("Surveys");
 
    	
    	usersRef.child(model.getTitle()).child("Available").setValueAsync(model.getAvailability());
    	usersRef.child(model.getTitle()).child("Date").setValueAsync(model.getDate());
    	usersRef.child(model.getTitle()).child("Available").setValueAsync(true);
    	for(int i = 0; i < model.getQuestions().size(); i++) {
    		usersRef.child(model.getTitle()).child("Questions").child(model.getQuestions().get(i).getQuestion()).child("type").setValueAsync(
    				model.getQuestions().get(i).getType());
    		
    		for(int j = 0; j < model.getQuestions().get(i).getAnswers().size(); j++) {
    			usersRef.child(model.getTitle()).child("Questions").child(model.getQuestions().get(i).getQuestion()).child(
    					(j+1) + "").setValueAsync(model.getQuestions().get(i).getAnswers().get(j));	
    		}
    		
    	}
    	
    }
    
    public void deleteSurvey(String surveyTitle) {
    	DatabaseReference usersRef = ref.child("Surveys").child(surveyTitle);
    	usersRef.removeValueAsync();
    }
    
    public void disableSurvey(String surveyTitle) {
    	DatabaseReference usersRef = ref.child("Surveys").child(surveyTitle).child("Available");
    	usersRef.setValueAsync(false);
    }
    public void enableSurvey(String surveyTitle) {
    	DatabaseReference usersRef = ref.child("Surveys").child(surveyTitle).child("Available");
    	usersRef.setValueAsync(true);
    }
    
    public static void getAllSurveys() {
    	allSurveys = new ArrayList<NewSurveyModel>();
    	
    	ref.child("Surveys").addListenerForSingleValueEvent(new ValueEventListener() {
  		  public void onDataChange(DataSnapshot dataSnapshot) {
  		    for(DataSnapshot surveySnap : dataSnapshot.getChildren()) {
  		    	String sName = surveySnap.getKey();
  		    	String sAvai = surveySnap.child("Available").getValue().toString();
  		    	String sDate  = surveySnap.child("Date").getValue().toString();
  		    	ArrayList<NewQuestionModel> questions = new ArrayList<NewQuestionModel>();
  		    	for(DataSnapshot qSnap : surveySnap.child("Questions").getChildren()) {
  		    		ArrayList<String> answrs = new ArrayList<String>();
  		    		String type = "";
  		    		for(DataSnapshot each : qSnap.getChildren()) {
  		    			if(each.getKey().equals("type")) {
  		    				type = each.getValue().toString();
  		    			} else {
  		    				answrs.add(each.getValue().toString());
  		    			}
  		    		}
  		    		questions.add(new NewQuestionModel(qSnap.getKey(), type, answrs));
  		    	}
  		    	allSurveys.add(new NewSurveyModel(sName, sDate, sAvai, questions));
  		    }
  		  }
  		  public void onCancelled(DatabaseError error) {
  		  }
  		});
  		
    	ref.child("Results").addListenerForSingleValueEvent(new ValueEventListener() {
  		  public void onDataChange(DataSnapshot dataSnapshot) {
  		    for(DataSnapshot surveySnap : dataSnapshot.getChildren()) {
  		    	boolean questionsLoaded = false;
  		    	ArrayList<UserResponse> userResponses = new ArrayList<UserResponse>();
  	    		ArrayList<String> questions = new ArrayList<String>();
  	    		
  		    	for(DataSnapshot userSnap : surveySnap.getChildren()) {
  		    		ArrayList<String> responses = new ArrayList<String>();
  		    		for(DataSnapshot questionSnap : userSnap.getChildren()) {
  		    			if(!questionsLoaded) {
  		    				questions.add(questionSnap.getKey());
  		    			}
  		    			responses.add(questionSnap.getValue().toString());
  		    		}
  		    		UserResponse userResponse = new UserResponse(userSnap.getKey(), responses);
  		    		userResponses.add(userResponse);
  		    		questionsLoaded = true;
  		    	}
  		    	
  		    	surveys.add(new SurveyModel(surveySnap.getKey(), questions, userResponses));
  		    }
  		  }
  		  public void onCancelled(DatabaseError error) {
  		  }
  		});
  		
  		try {
  			TimeUnit.SECONDS.sleep(10);
  		} catch (InterruptedException e) {
  			e.printStackTrace();
  		}
  		
  
   }
   
   
}
