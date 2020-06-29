package graspery.herkyasks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class GUI implements ActionListener{
	JFrame f;    
	JFileChooser chooser;
	JButton create;
	JButton download;
	FirebaseHelper firebaseHelper;
	private ArrayList<NewSurveyModel> surveys;
	private ArrayList<SurveyModel> responses;
	
	GUI(){    
		f=new JFrame("Herky Asks Admin Dashboard");  
		ImageIcon img = new ImageIcon("sacstate.png");
		f.setIconImage(img.getImage());
		
		f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		f.add(mainMenu(true));
		f.setLayout(null);    
		f.setSize(700,600);   
		f.setLocationRelativeTo(null);
		f.setVisible(true);    
	}  
	
	private JPanel mainMenu(final boolean init) {
		final JPanel menu = new JPanel(new BorderLayout());
		menu.setBackground(Color.white);
		menu.setSize(new Dimension(685, 550));
		
		final JPanel loadP = new JPanel(new FlowLayout(FlowLayout.CENTER));
		loadP.setBackground(Color.white);
		JLabel loading = new JLabel("Loading...");
		loading.setFont(new Font("Calibri", Font.BOLD, 20));
		loadP.add(loading);
		menu.add(loadP, BorderLayout.CENTER);
		Thread t = new Thread(new Runnable() {
	        public void run() {
	        	if(init) {
	        		firebaseHelper = FirebaseHelper.getInstance();
	        		surveys = FirebaseHelper.getResults();
	        		responses = FirebaseHelper.getSurveys();
	        	}
	        	menu.add(centerPanel(), BorderLayout.CENTER);
	        	menu.add(bottomPanel(), BorderLayout.SOUTH);
	        	menu.remove(loadP);
	        	f.revalidate();
	        }
	    });
	    t.start();
		
		return menu;
	}
	
	private JScrollPane centerPanel() {
		JPanel centerP = new JPanel();
		centerP.setBorder(new EmptyBorder(16, 16, 16, 16));
		centerP.setBackground(Color.white);
		centerP.setLayout(new BoxLayout(centerP, BoxLayout.Y_AXIS));
		
		for(int i = 0; i < surveys.size(); i++) {
			final String currTitle = surveys.get(i).getTitle();
			JPanel eachSurPanel = new JPanel();
			eachSurPanel.setBackground(Color.white);
			eachSurPanel.setBorder(
					BorderFactory.createTitledBorder(
							null, surveys.get(i).getTitle(), TitledBorder.LEFT, TitledBorder.TOP, new Font(
									"Calibri",Font.BOLD,18), Color.black));
			
			JPanel detailP = new JPanel();
			detailP.setBackground(Color.white);
			detailP.setLayout(new BoxLayout(detailP, BoxLayout.Y_AXIS));
			
			JPanel dateP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			dateP.setBackground(Color.white);
			JLabel surveyDate = new JLabel("Due Date: " + surveys.get(i).getDate());
			surveyDate.setFont(new Font("Calibri", Font.PLAIN, 18));
			dateP.add(surveyDate);
			JPanel avaiP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			avaiP.setBackground(Color.white);
			JLabel surveyAvailability = new JLabel("Availability: " + surveys.get(i).getAvailability());
			surveyAvailability.setFont(new Font("Calibri", Font.PLAIN, 18));
			avaiP.add(surveyAvailability);
			
			JPanel respoP = new JPanel(new FlowLayout(FlowLayout.LEFT));
			respoP.setBackground(Color.white);
			JLabel responseCount = new JLabel("Responses: 0");
			responseCount.setFont(new Font("Calibri", Font.PLAIN, 18));
			respoP.add(responseCount);
			for(SurveyModel model : responses) {
				if(model.getSurveyName().equals(currTitle)) {
					responseCount.setText("Responses: " + model.getUserResponses().size());
				}
			}
		
			detailP.add(dateP);
			detailP.add(avaiP);
			detailP.add(respoP);
			
			JPanel commandP = new JPanel();
			commandP.setBackground(Color.white);
			//commandP.setLayout(new FlowLayout(FlowLayout.CENTER));
			
			
			JButton downloadS = new JButton("Download Results");
			downloadS.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	for(SurveyModel model : responses) {
	    				if(model.getSurveyName().equals(currTitle)) {
	    					chooser = new JFileChooser(); 
	    				    chooser.setCurrentDirectory(new java.io.File("."));
	    				    chooser.setDialogTitle("Save survey results at");
	    				    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    				    chooser.setAcceptAllFileFilterUsed(false);
	    				    //    
	    				    if (chooser.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) { 
	    				      System.out.println("getCurrentDirectory(): " 
	    				         +  chooser.getCurrentDirectory());
	    				      System.out.println("getSelectedFile() : " 
	    				         +  chooser.getSelectedFile());
	    				      
	    				      model.generateCSV(chooser.getSelectedFile().toString());
	    				    } else {
	    				      System.out.println("No Selection ");
	    				    }
	    				}
	    			}
	            }
	        });
			
			final int position = i;
			JButton enableS = new JButton("Enable Survey");
			enableS.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	firebaseHelper.enableSurvey(currTitle);
	            	surveys.get(position).setAvailability("true");
	            	f.setVisible(false);
	            	new GUI();
	            }
	        });
			JButton disableS = new JButton("Disable Survey");
			disableS.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	firebaseHelper.disableSurvey(currTitle);
	            	surveys.get(position).setAvailability("false");
	            	f.setVisible(false);
	            	new GUI();
	            }
	        });
			
			JButton editS = new JButton("Edit Survey");
			
			
			JButton deleteS = new JButton("Delete Survey");
			deleteS.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e) {
	            	String[] options = {"Yes! Please.", "No! Not now."}; 
	                int result = JOptionPane.showOptionDialog(
	                   f,
	                   "Are you sure you want to delete -" + currTitle + "- survey?", 
	                   "Delete Survey",            
	                   JOptionPane.YES_NO_OPTION,
	                   JOptionPane.QUESTION_MESSAGE,
	                   null,     //no custom icon
	                   options,  //button titles
	                   options[0] //default button
	                );
	                if(result == JOptionPane.YES_OPTION){
	                	firebaseHelper.deleteSurvey(currTitle);
	                	f.setVisible(false);
		            	new GUI();
	                }
	            }
	        });
			
			commandP.add(downloadS);
			commandP.add(enableS);
			commandP.add(disableS);
			commandP.add(editS);
			commandP.add(deleteS);
			detailP.add(Box.createRigidArea(new Dimension(0, 16)));
			detailP.add(commandP);
			
			eachSurPanel.add(detailP);
			centerP.add(eachSurPanel);
			centerP.add(Box.createRigidArea(new Dimension(0, 16)));
		}
		
		JScrollPane scrollFrame = new JScrollPane(centerP);
		scrollFrame.setBackground(Color.white);
		scrollFrame.setAutoscrolls(true);
		scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollFrame.setPreferredSize(new Dimension(500, 500));
		return scrollFrame;
	}
	
	private JPanel bottomPanel() {
		JPanel bottomP = new JPanel();
		bottomP.setBackground(Color.white);
		bottomP.setSize(new Dimension(500, 50));
		bottomP.setLayout(new FlowLayout(
                FlowLayout.CENTER, 16, 16)); 
		create = new JButton("Create New Survey");
		create.addActionListener(this);
		
		download = new JButton("Export All Survey Results in CSV Format");
		download.addActionListener(this);
		
		bottomP.add(create);
		bottomP.add(download);
		
		return bottomP;
	}
	
	public void actionPerformed(ActionEvent e) {    
		if(e.getSource() == create) {
			new CreateNewSurveyGUI();
			f.setVisible(false);
		} else if(e.getSource() == download) {
			chooser = new JFileChooser(); 
		    chooser.setCurrentDirectory(new java.io.File("."));
		    chooser.setDialogTitle("Save survey results at");
		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		    chooser.setAcceptAllFileFilterUsed(false);
		    //    
		    if (chooser.showSaveDialog(f) == JFileChooser.APPROVE_OPTION) { 
		      System.out.println("getCurrentDirectory(): " 
		         +  chooser.getCurrentDirectory());
		      System.out.println("getSelectedFile() : " 
		         +  chooser.getSelectedFile());
		      
		    } else {
		      System.out.println("No Selection ");
		    }
			 for(SurveyModel m : responses) { 
				 System.out.println(m.getSurveyName());
				 //m.generateCSV(); 
			     m.generateCSV(chooser.getSelectedFile().toString());
			 }
			 
		}
	}  
}
