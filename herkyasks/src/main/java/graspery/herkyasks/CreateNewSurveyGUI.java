package graspery.herkyasks;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

public class CreateNewSurveyGUI implements ActionListener{
	private GridBagConstraints gbc;
	
	private JFrame frame;
	private JPanel parentPanel;
	
	private JTextField surveyNameTF;
	private int questionCount;
	
	JTextField answerField;
	JTextField qTextField;
	
	private ArrayList<NewQuestionModel> questionObjects;
	private String questionType;
	private ArrayList<String> answers;
	private String available;
	private String dueDate;
	
	private NewSurveyModel survey;
	private FirebaseHelper firebaseHelper;
	private JDatePickerImpl datePicker;
	
	public CreateNewSurveyGUI() {
		firebaseHelper = FirebaseHelper.getInstance();
		questionCount = 0;
		available = "true";
		dueDate = "18-Sep-2020";
		answers = new ArrayList<String>();
		questionObjects = new ArrayList<NewQuestionModel>();
		
		frame = new JFrame("Create New Survey");
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(800, 800));
		parentPanel = new JPanel(new BorderLayout());
		
		buildNorthPanel();
		buildWestPanel();
		buildCentralPanel();
		buildSouthPanel();
		
		frame.add(parentPanel);
		frame.setVisible(true);
	}
	
	private void buildSouthPanel() {
		JPanel southPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 5, 5));
		southPanel.setBackground(Color.white);
		
		JButton discard = new JButton("Discard");
		discard.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				new GUI();
				frame.setVisible(false);
			 }
		});

		JButton submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				answers.add(answerField.getText().toString());
	            questionObjects.add(new NewQuestionModel(qTextField.getText().toString(), questionType, answers));
	            	
	            if(!datePicker.getJFormattedTextField().getText().isEmpty()) {
	            	dueDate = datePicker.getJFormattedTextField().getText(); 
	            }
	            survey = new NewSurveyModel(surveyNameTF.getText().toString(),
	            		dueDate, available, questionObjects);
	            firebaseHelper.uploadSurvey(survey);
	            firebaseHelper.refreshData();
	            
	            new GUI();
				frame.setVisible(false);
			 }
		});
		
		southPanel.add(discard);
		southPanel.add(submit);
		
		parentPanel.add(southPanel, BorderLayout.SOUTH);
	}
	
	JPanel centralPanel;
	private void buildCentralPanel() {
		
		centralPanel = new JPanel();
		centralPanel.setBackground(Color.white);
		centralPanel.setBorder(
	            BorderFactory.createTitledBorder("Create Survey Questions"));
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.Y_AXIS)); 
		
	
		final JButton addButton = new JButton("Add another question");
		addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	answers.add(answerField.getText().toString());
            	questionObjects.add(new NewQuestionModel(qTextField.getText().toString(), questionType, answers));
            	
            	System.out.println(questionObjects.get(questionCount).getString());
            	answers = new ArrayList<String>();
            	questionCount++;
            	centralPanel.remove(addButton);
                centralPanel.add(questionBuilder());
                centralPanel.add(addButton);
                
                frame.revalidate();
            }
        });
	
		centralPanel.add(questionBuilder());
		centralPanel.add(addButton);
		
		JScrollPane scrollFrame = new JScrollPane(centralPanel);
		scrollFrame.setAutoscrolls(true);
		scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		parentPanel.add(scrollFrame, BorderLayout.CENTER);
	}
	
	
	int answersCount;
	private JScrollPane questionBuilder() {
		answersCount = 0;
		
		final JPanel questionBuilder = new JPanel();
		questionBuilder.setBackground(Color.white);
		questionBuilder.setLayout(new BoxLayout(questionBuilder, BoxLayout.Y_AXIS));
		questionBuilder.setBorder(
	            BorderFactory.createTitledBorder("Question " + (questionCount + 1)));
		
		JPanel enterQuestionPanel = new JPanel();
		enterQuestionPanel.setBackground(Color.white);
		enterQuestionPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
		enterQuestionPanel.setLayout(new GridLayout(1,1));
		JLabel qLabel = new JLabel("Enter question: ");
		qTextField = new JTextField();	
		enterQuestionPanel.add(qLabel);
		enterQuestionPanel.add(qTextField);
		questionBuilder.add(enterQuestionPanel);
		
		JPanel typePanel = new JPanel();
		typePanel.setBackground(Color.white);
		typePanel.setBorder(new EmptyBorder(16, 16, 16, 16));
		typePanel.setLayout(new GridLayout(1, 1));
		JLabel typeLabel = new JLabel("Select Question Type");
		String types[] = {"single", "checkbox", "slider", "free response"};
		JComboBox type = new JComboBox(types);
		type.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) 
		    { 
				questionType = e.getItem().toString();
		    } 
		});
		questionType = "single";
		typePanel.add(typeLabel);
		typePanel.add(type);
		questionBuilder.add(typePanel);
		
		questionBuilder.add(answersPanel(answersCount));
		
		final JButton addAnswer = new JButton("Add another option");
		addAnswer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	if(answerField.getText().toString().isEmpty()) {
            		JOptionPane.showMessageDialog(frame, "Can't leave answer field empty before creating another one.");
            	} else {
	            	answers.add(answerField.getText().toString());
	            	
	            	answersCount++;
	            	questionBuilder.remove(addAnswer);
	                questionBuilder.add(answersPanel(answersCount));
	                questionBuilder.add(addAnswer);
	                
	                frame.revalidate();
	            }
            }
        });
		questionBuilder.add(addAnswer);
		
		JScrollPane scrollFrame = new JScrollPane(questionBuilder);
		scrollFrame.setBorder(new EmptyBorder(16, 16, 16, 16));
		scrollFrame.setBackground(Color.white);
		scrollFrame.setAutoscrolls(true);
		scrollFrame.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollFrame.setPreferredSize(new Dimension(200, 300));
		return scrollFrame;
	}
	
	private JPanel answersPanel(int ansCount) {
		JPanel answerPanel = new JPanel();
		answerPanel.setBackground(Color.white);
		answerPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
		answerPanel.setLayout(new GridBagLayout());
		JLabel count = new JLabel("Answer #" + (ansCount+1) + ": ");
		answerField = new JTextField();
		answerField.setPreferredSize(new Dimension(150, 20));
		
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        
		answerPanel.add(count, gbc);

		gbc.gridx = 1;
        gbc.weightx = 0.7;
		answerPanel.add(answerField, gbc);
		
		return answerPanel;
	}
	
	
	private void buildNorthPanel() {
		JLabel createNewSurvey = new JLabel("Create New Survey");
		createNewSurvey.setFont(new Font("Calibri", Font.BOLD, 20));

		JLabel surveyNameLabel = new JLabel("Enter Survey Title           "); 
 
		surveyNameTF = new JTextField(16); 
		
		JPanel northPanel = new JPanel();
		northPanel.setBackground(Color.white);
		northPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS)); 
		northPanel.add(createNewSurvey);
		
		JPanel flowPanel = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 5, 5));
		flowPanel.setBackground(Color.white);
		flowPanel.add(surveyNameLabel);
		flowPanel.add(surveyNameTF);
		
		northPanel.add(flowPanel);
		
		parentPanel.add(northPanel, BorderLayout.NORTH);
	}
	
	private void buildWestPanel() {
		JLabel startLabel = new JLabel("Survey Start Date");
		JLabel endLabel = new JLabel("Survey End Date");
		
		JCheckBox availableCheckBox = new JCheckBox("Available Immediately");
		availableCheckBox.setBackground(Color.white);
		availableCheckBox.setSelected(true);
		
		UtilDateModel model = new UtilDateModel();
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
		
		UtilDateModel model2 = new UtilDateModel();
		JDatePanelImpl datePanel2 = new JDatePanelImpl(model2, p);
		JDatePickerImpl datePicker2 = new JDatePickerImpl(datePanel2, new DateLabelFormatter());
		
		
		JPanel westPanel = new JPanel();
		westPanel.setBackground(Color.white);
		westPanel.setBorder(
	            BorderFactory.createTitledBorder("Date and Availability"));
		westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS)); 
		westPanel.add(startLabel);
		westPanel.add(datePicker);
		westPanel.add(endLabel);
		westPanel.add(datePicker2);
		westPanel.add(availableCheckBox);
		
		
		parentPanel.add(westPanel, BorderLayout.WEST);
	}
	
	public void actionPerformed(ActionEvent e) {
		
	}
	
	public class DateLabelFormatter extends AbstractFormatter {

	    private String datePattern = "yyyy-MM-dd";
	    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

	    @Override
	    public Object stringToValue(String text) throws ParseException {
	        return dateFormatter.parseObject(text);
	    }

	    @Override
	    public String valueToString(Object value) throws ParseException {
	        if (value != null) {
	            Calendar cal = (Calendar) value;
	            return dateFormatter.format(cal.getTime());
	        }

	        return "";
	    }

	}
	
}
