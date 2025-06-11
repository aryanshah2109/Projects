// Exception handling: 
// 1) IllegalStartException while start
// 2) IllegalArgumentException if image not found

// Thread:
// Prints "User is typing....." every half a second that user is typing whenever the user is in test

package myPackage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class TypingSpeedTest2 extends Application {

    TextArea userTextArea;
    Label timerLabel, wpmLabel, accuracyLabel, paragraphLabel;
    ProgressBar timeBar;
    Timeline timer;
    int timeLeft = 60;
    TypingThread typingThread;

    final String[] paragraphs = {
    	    "Practice makes perfect. Keep improving your typing skills every day! With consistency, you'll see great results in no time. Stay committed and you’ll achieve your goal. Never give up on your practice.",
    	    "JavaFX makes building GUIs in Java simple and fun to use. It provides powerful tools to design modern interfaces. With JavaFX, creating applications becomes much more enjoyable. You can add interactivity and style effortlessly.",
    	    "Multithreading allows programs to do multiple things at once. It improves performance and responsiveness in applications. By using multiple threads, tasks can be processed in parallel. This allows for a better user experience.",
    	    "Coding is not just a skill, it's a superpower in the digital world. With it, you can build anything from games to websites. It unlocks the potential to create technology that can change the world. Keep coding and discover endless possibilities.",
    	    "Always handle exceptions to make your app crash-proof and smooth. Good error handling improves user experience a lot. It prevents the app from crashing and keeps things running smoothly. Users appreciate when apps don’t break unexpectedly.",
    	    "Learn data structures to write efficient and fast programs. They form the foundation of smart problem solving. Data structures help organize and store data effectively. Mastering them will make you a better programmer.",
    	    "Debugging is a part of coding, not a sign of failure. Fixing bugs teaches you how your code truly works. It’s an opportunity to understand your program better. Every bug fixed makes you a more experienced developer.",
    	    "Reading code is as important as writing it. Understanding others' logic builds strong developer skills. It gives you insights into different coding styles. It also improves your ability to debug and collaborate.",
    	    "Clean code is easier to read and maintain over time. Naming variables clearly helps future developers too. Write code that others can easily understand. It makes team projects more efficient and less error-prone.",
    	    "Practice daily even if it's for a short time. Consistency matters more than long irregular hours. Even small efforts add up over time. A little bit each day will improve your skills significantly.",
    	    "Comments in code help explain the logic to others. They make collaboration and debugging much easier. Clear comments provide context for complex sections of code. They also help when you revisit the code after some time.",
    	    "Object-oriented programming promotes modular and reusable code. It helps manage complexity in large projects. By breaking down tasks into smaller chunks, OOP makes coding more manageable. Reusable code saves time and reduces errors.",
    	    "Version control tools like Git keep your code safe and track changes. Always commit your progress regularly. Git helps you keep a history of your code and easily revert changes. It’s an essential tool for modern development.",
    	    "Typing speed improves naturally with time and patience. Focus more on accuracy before aiming for speed. With regular practice, your speed will follow. Don’t rush; mastery comes with steady improvement.",
    	    "Software testing ensures your application works as expected. It helps catch bugs before they reach the user. Testing is crucial to ensure the app functions correctly. It improves the quality and reliability of your product."
    	};

    String currentParagraph;

    public void start(Stage stage) {
        Label paraLabel = new Label("Type this:");
        paraLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d2d2d;");
        
        paragraphLabel = new Label();
        paragraphLabel.setWrapText(true);
        paragraphLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        paragraphLabel.setMaxWidth(450);


        userTextArea = new TextArea();
        userTextArea.setPromptText("Start typing here...");
        userTextArea.setWrapText(true);	// lets text go to next line if space is less
        userTextArea.setPrefRowCount(5);
        userTextArea.setDisable(true);	// initially disabled but enabled after clicking start button
        userTextArea.setStyle("-fx-font-size: 14px; -fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        

        timerLabel = new Label("Time Left: 60s");
        timerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d9534f;");

        wpmLabel = new Label("WPM: ");
        wpmLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #5bc0de;");

        accuracyLabel = new Label("Accuracy: ");
        accuracyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #5bc0de;");

        timeBar = new ProgressBar(1.0);	
        timeBar.setStyle("-fx-accent: #5bc0de;");

        Button startBtn = new Button("Start");
        Button endBtn = new Button("End Test");
        endBtn.setDisable(true); // End button disabled initially

        startBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");
        endBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10px 20px; -fx-border-radius: 5px;");

        startBtn.setOnAction(e -> {
            startBtn.setDisable(true); // when clicked start button it will disable start and enable end
            endBtn.setDisable(false);  // when clicked end button it will enable end and disable start	
            try {
            	startTest();
            }
            catch(IllegalStateException exception) {
            	System.out.print(exception.getMessage());
            }
        });

        endBtn.setOnAction(e -> {
            startBtn.setDisable(false);	
            endBtn.setDisable(true);
            endTest();
        });

        VBox layout = new VBox(10, paraLabel, paragraphLabel, userTextArea, timeBar, timerLabel, wpmLabel, accuracyLabel, new HBox(10, startBtn, endBtn));
        layout.setStyle("-fx-padding: 20px; -fx-background-color: #f8f9fa; -fx-alignment: center;");       
        try {
            stage.getIcons().add(new Image("iconTyping.png"));
        } catch (IllegalArgumentException e) {		// thrown exception if image not found  
            System.out.println("Icon image not found: " + e.getMessage());
        }

        stage.setScene(new Scene(layout, 800, 600));
        stage.setTitle("Typing Speed Test");
        stage.show();
    }

    void startTest() throws IllegalStateException {
        timeLeft = 60;
        timerLabel.setText("Time Left: 60s");
        userTextArea.clear();
        wpmLabel.setText("WPM: ");
        accuracyLabel.setText("Accuracy: ");
        userTextArea.setDisable(false);	// enables user area
        userTextArea.requestFocus();	// makes cursor go to user area when clicked start

        // get random paragraph
        if (paragraphs.length == 0) {
            throw new IllegalStateException("No paragraphs available for the test.");
        }       
        currentParagraph = paragraphs[new Random().nextInt(paragraphs.length)];
        paragraphLabel.setText(currentParagraph);

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {	// means after 1 second execute the below code
            timeLeft--;
            timerLabel.setText("Time Left: " + timeLeft + "s");
            timeBar.setProgress(timeLeft / 60.0);
            if (timeLeft <= 0) {
                timer.stop();
                endTest();
            }
        }));
        timer.setCycleCount(60);	// commands the keyframe to loop for 60 counts i.e. seconds. 
        							// as we have one keyframe it will loop it for 60 seconds
        timer.play();

        typingThread = new TypingThread(userTextArea);
        typingThread.setDaemon(true);	// jvm automatically ends when daemon thread used even if thread execution is left
        								// in normal threads we need to let thread to complete its tasks and only then jvm can exit
        typingThread.start();
    }

    void endTest() {
        if (timer != null) timer.stop();
        if (typingThread != null) typingThread.stopTyping();	// called as when test ends the thread still has a object so it will make 
        														// running variable to false and stop execution of thread 
        userTextArea.setDisable(true);

        String typedText = userTextArea.getText();
        int typedWords = typedText.trim().isEmpty() ? 0 : typedText.trim().split("\\s+").length;	// "\\s+" is regex
        																							// it means remove all whitespaces and any excess
        																							// spaces and \n and \t. aim is to find number
        																							// of words entered by user
        int correctWords = countCorrectWords(currentParagraph, typedText);

        double wpm = (typedWords / (60.0 - timeLeft)) * 60;	// we use *60 as first part gives words per second
        double accuracy = (typedWords == 0) ? 0 : (correctWords * 100.0 / typedWords);	

        wpmLabel.setText(String.format("WPM: %.2f", wpm));
        accuracyLabel.setText(String.format("Accuracy: %.2f%%", accuracy));
    }

    int countCorrectWords(String original, String typed) {
        String[] originalWords = original.trim().split("\\s+");
        String[] typedWords = typed.trim().split("\\s+");
        int count = 0;
        for (int i = 0; i < Math.min(originalWords.length, typedWords.length); i++) {
            if (originalWords[i].equals(typedWords[i])) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Custom thread to provide background "User is typing.. " message in console while user is giving the test
    class TypingThread extends Thread {
        final TextArea inputArea;
        boolean running = true;

        public TypingThread(TextArea inputArea) {
            this.inputArea = inputArea;
        }

        @Override
        public void run() {
            try {
                while (running) {
                    if (!inputArea.getText().trim().isEmpty()) {
                        System.out.println("User is typing...");
                    }
                    Thread.sleep(500); 
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void stopTyping() {
            running = false;
        }
    }
}
