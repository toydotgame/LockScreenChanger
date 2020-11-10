import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class GUI implements ActionListener {
	JLabel newImagePromptText;
	static JTextField newImageFilenamePrompt;
	JLabel oldImagePromptText;
	JLabel oldImagePromptHintText;
	static JTextField oldImageFilenamePrompt;
	static JLabel successText;
	
	public GUI() {
		JFrame frame = new JFrame("Lock Screen Image Changer");
		frame.setSize(400,245);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		frame.add(panel);
		
		newImagePromptText = new JLabel("What's the name of the file you want to set?");
		newImagePromptText.setBounds(10, 10, 275, 25);
		panel.add(newImagePromptText);
		
		newImageFilenamePrompt = new JTextField();
		newImageFilenamePrompt.setBounds(10, 35, 275, 25);
		panel.add(newImageFilenamePrompt);
		
		oldImagePromptText = new JLabel("What's the name of the default image?");
		oldImagePromptText.setBounds(10, 70, 275, 25);
		panel.add(oldImagePromptText);
		
		oldImagePromptHintText = new JLabel("Usually \"1.jpg\", but sometimes it's different.");
		oldImagePromptHintText.setBounds(10, 95, 275, 25);
		panel.add(oldImagePromptHintText);
		
		oldImageFilenamePrompt = new JTextField();
		oldImageFilenamePrompt.setBounds(10, 130, 275, 25);
		panel.add(oldImageFilenamePrompt);
		
		JButton button = new JButton("Apply");
		button.addActionListener(this);
		button.setBounds(285, 10, 95, 190);
		panel.add(button);
		
		successText = new JLabel();
		successText.setBounds(10, 165, 275, 25);
		panel.add(successText);
		
		frame.setVisible(true);
	}
	
	public static void main(String[] args) {
		new GUI();
	}
	
	public void actionPerformed(ActionEvent e) {
		String newFilename = newImageFilenamePrompt.getText();
		String oldFilename = oldImageFilenamePrompt.getText();
		
		boolean newFilenameCorrectBoolean = newFilename.endsWith(".jpg");
		String newFilenameCorrect = String.valueOf(newFilenameCorrectBoolean);
		if(newFilenameCorrect == "true") {
			System.out.println("The new image is a JPEG!");
		} else {
			System.out.println("The new image is the incorrect file type. Please try again with a JPEG.");
		}
		
		boolean oldFilenameCorrectBoolean = oldFilename.endsWith(".jpg");
		String oldFilenameCorrect = String.valueOf(oldFilenameCorrectBoolean);
		if(oldFilenameCorrect == "true") {
			System.out.println("The old image is a JPEG!");
		} else {
			System.out.println("The old image is the incorrect file type. Please try again with a JPEG.");
		}
		
		if(oldFilenameCorrect == "true" && newFilenameCorrect == "true") {
			createTempBatch();
			writeCommandToBatch();
			try {
				Runtime.getRuntime().exec("cmd /c start \"\" run.bat");
				System.out.println("File ran successfully.");
				successText.setText("File run successfully. You can close the app.");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println("At least one of the files specified are of incorrect syntax. Try again with .jpg files.");
		}
	}
	
	private static void createTempBatch() {	// This is code ripped straight from my batch file generator (Toydotgame/batFileGenerator)
		try {
			File outputFile = new File("run.bat");
			if(outputFile.createNewFile()) {
				System.out.println("File created as \"run.bat\"");
				successText.setText("File successfully created.");
			} else {
				System.out.println("Unable to create file.");
				successText.setText("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("Java could not create the file. It may already exist or the jarfile does not have sufficient priveleges.");
			successText.setText("Java could not create the file");
			e.printStackTrace();
		}
	}
	
	private static void writeCommandToBatch() { // This method is also from Toydotgame/batFileGenerator
		String newFilename = newImageFilenamePrompt.getText();
		String oldFilename = oldImageFilenamePrompt.getText();
		String outputBatchName = "run.bat";
		System.out.println("Successfully accessed \"run.bat\"");
		
		try {
			FileWriter myWriter = new FileWriter(outputBatchName);
			
			myWriter.write("@echo off\n");
			myWriter.write(":: BatchGotAdmin\n");
			myWriter.write("REM --> Check for permissions\n");
			myWriter.write(">nul 2>&1 \"%SYSTEMROOT%\\system32\\cacls.exe\" \"%SYSTEMROOT%\\system32\\config\\system\"\n");
			myWriter.write("REM --> If error flag set, we do not have admin.\n");
			myWriter.write("if '%errorlevel%' NEQ '0' (\n");
			myWriter.write("	echo Requesting administrative privileges...\n");
			myWriter.write("	goto UACPrompt\n");
			myWriter.write(") else (\n");
			myWriter.write("	goto gotAdmin\n");
			myWriter.write(")\n");
			myWriter.write(":UACPrompt\n");
			myWriter.write("echo Set UAC = CreateObject^(\"Shell.Application\"^) > \"%temp%\\getadmin.vbs\"\n");
			myWriter.write("echo UAC.ShellExecute \"%~s0\", \"\", \"\", \"runas\", 1 >> \"%temp%\\getadmin.vbs\"\n");
			myWriter.write("\"%temp%\\getadmin.vbs\"\n");
			myWriter.write("exit /B\n");
			myWriter.write(":gotAdmin\n");
			myWriter.write("if exist \"%temp%\\getadmin.vbs\" (\n");
			myWriter.write("	del \"%temp%\\getadmin.vbs\"\n");
			myWriter.write(")\n");
			myWriter.write("pushd \"%CD%\"\n");
			myWriter.write("CD /D \"%~dp0\"\n");
			myWriter.write(":: BatchGotAdmin (Run as Admin code ends)\n");
			myWriter.write("\n");
			myWriter.write("rename " + newFilename + " " + oldFilename + "\n");
			myWriter.write("xcopy " + oldFilename + " \"C:\\Program Files\\images\\" + oldFilename + "\" /y\n");
			myWriter.write("(goto) 2>nul & del \"%~f0\"");
			
			myWriter.close();
		} catch (IOException e) {
			System.out.println("Could not write to access file. Check that the file exists and that the jarfile has sufficient priveleges.");
			successText.setText("Could not write to output file.");
			e.printStackTrace();
		}
	}
}
