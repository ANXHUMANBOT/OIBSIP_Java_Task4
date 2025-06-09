package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OnlineExamSystem extends JFrame implements ActionListener {
    JPanel mainPanel;
    CardLayout cardLayout;

    JPanel loginPanel, profilePanel, quizPanel, resultPanel;
    JTextField tfUsername = new JTextField(15);
    JPasswordField pfPassword = new JPasswordField(15);
    JTextField tfNewUsername = new JTextField(15);
    JPasswordField pfNewPassword = new JPasswordField(15);

    JLabel label = new JLabel();
    JRadioButton[] radioButton = new JRadioButton[4];
    ButtonGroup bg = new ButtonGroup();
    JButton btnNext = new JButton("Next");
    JButton btnSubmit = new JButton("Submit");
    JLabel timerLabel = new JLabel("Time left: 30");

    Timer timer;
    int timeLeft = 30;
    int score = 0, current = 0;
    String currentUser = "";

    List<Question> questions = new ArrayList<>();
    List<Integer> selectedAnswers = new ArrayList<>();

    // Blue Color Scheme
    Color DARK_BLUE = Color.decode("#0057C4");
    Color MEDIUM_BLUE = Color.decode("#008CC1");
    Color LIGHT_BLUE = Color.decode("#5BD5FF");
    Color VERY_LIGHT_BLUE = Color.decode("#B4ECFF");
    Color ALMOST_WHITE = Color.decode("#EFFDFF");

    public OnlineExamSystem() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Online Examination System");

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        initLoginPanel();
        initProfilePanel();
        initQuizPanel();
        initResultPanel();

        add(mainPanel);
        setVisible(true);
    }

    void initLoginPanel() {
        loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(DARK_BLUE);

        JPanel content = new JPanel(new GridLayout(0, 1, 10, 10));
        content.setBackground(DARK_BLUE);

        JLabel title = new JLabel("Welcome to Online Exam System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(ALMOST_WHITE);

        content.add(title);
        content.add(createLabel("Username:"));
        content.add(tfUsername);
        content.add(createLabel("Password:"));
        content.add(pfPassword);

        JButton btnLogin = createButton("Login");
        JButton btnRegister = createButton("Register");

        content.add(btnLogin);
        content.add(btnRegister);

        loginPanel.add(content);
        mainPanel.add(loginPanel, "login");
    }

    void initProfilePanel() {
        profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setBackground(MEDIUM_BLUE);

        JPanel content = new JPanel(new GridLayout(0, 1, 10, 10));
        content.setBackground(MEDIUM_BLUE);

        JLabel title = new JLabel("Update Profile", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(ALMOST_WHITE);

        content.add(title);
        content.add(createLabel("New Username:"));
        content.add(tfNewUsername);
        content.add(createLabel("New Password:"));
        content.add(pfNewPassword);

        JButton btnUpdate = createButton("Update");
        JButton btnStartExam = createButton("Start Exam");
        JButton btnLogout = createButton("Logout");

        content.add(btnUpdate);
        content.add(btnStartExam);
        content.add(btnLogout);

        profilePanel.add(content);
        mainPanel.add(profilePanel, "profile");
    }

    void initQuizPanel() {
        quizPanel = new JPanel(null);
        quizPanel.setBackground(DARK_BLUE);

        label.setBounds(30, 30, 1000, 30);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(ALMOST_WHITE);
        quizPanel.add(label);

        timerLabel.setBounds(1000, 10, 200, 25);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setForeground(ALMOST_WHITE);
        quizPanel.add(timerLabel);

        for (int i = 0; i < 4; i++) {
            radioButton[i] = new JRadioButton();
            radioButton[i].setBounds(50, 80 + i * 40, 900, 25);
            radioButton[i].setBackground(VERY_LIGHT_BLUE);
            radioButton[i].setForeground(Color.BLACK);
            bg.add(radioButton[i]);
            quizPanel.add(radioButton[i]);
        }

        btnNext.setBounds(300, 300, 100, 30);
        btnSubmit.setBounds(420, 300, 100, 30);

        customizeButton(btnNext);
        customizeButton(btnSubmit);

        btnNext.addActionListener(this);
        btnSubmit.addActionListener(this);

        quizPanel.add(btnNext);
        quizPanel.add(btnSubmit);

        mainPanel.add(quizPanel, "quiz");
    }

    void initResultPanel() {
        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBackground(ALMOST_WHITE);

        JLabel title = new JLabel("Performance Analysis", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(DARK_BLUE);

        resultPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(resultPanel, "result");
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        switch (cmd) {
            case "Login", "Register", "Update" -> {
                currentUser = tfUsername.getText().trim();
                cardLayout.show(mainPanel, "profile");
            }
            case "Start Exam" -> startExam();
            case "Next" -> nextQuestion();
            case "Submit" -> submitExam();
            case "Logout" -> logout();
        }
    }

    void startExam() {
        score = 0;
        current = 0;
        selectedAnswers.clear();
        loadQuestions();
        setQuestion();
        btnNext.setEnabled(true);
        cardLayout.show(mainPanel, "quiz");
        startTimer();
    }

    void nextQuestion() {
        saveAnswer();
        current++;
        if (current < questions.size()) {
            setQuestion();
            resetTimer();
        } else {
            btnNext.setEnabled(false);
            if (timer != null)
                timer.cancel();
            showResults();
            cardLayout.show(mainPanel, "result");
        }
    }

    void submitExam() {
        saveAnswer();
        if (timer != null)
            timer.cancel();
        showResults();
        cardLayout.show(mainPanel, "result");
    }

    void logout() {
        if (timer != null)
            timer.cancel();
        tfUsername.setText("");
        pfPassword.setText("");
        tfNewUsername.setText("");
        pfNewPassword.setText("");
        currentUser = "";
        cardLayout.show(mainPanel, "login");
    }

    void saveAnswer() {
        for (int i = 0; i < 4; i++) {
            if (radioButton[i].isSelected()) {
                selectedAnswers.add(i + 1);
                if (i + 1 == questions.get(current).correctOption) {
                    score++;
                }
                return;
            }
        }
        selectedAnswers.add(0); // not answered
    }

    void setQuestion() {
        bg.clearSelection();
        label.setText("Q" + (current + 1) + ": " + questions.get(current).question);
        radioButton[0].setText(questions.get(current).option1);
        radioButton[1].setText(questions.get(current).option2);
        radioButton[2].setText(questions.get(current).option3);
        radioButton[3].setText(questions.get(current).option4);
    }

    void loadQuestions() {
        questions.clear();
        questions.add(new Question("What is the capital of France?", "Berlin", "London", "Paris", "Madrid", 3));
        questions.add(new Question("Which planet is known as the Red Planet?", "Earth", "Mars", "Jupiter", "Venus", 2));
        questions.add(new Question("Who wrote 'Romeo and Juliet'?", "Shakespeare", "Charles Dickens", "Mark Twain", "Jane Austen", 1));
        questions.add(new Question("Which language runs in a web browser?", "Java", "C", "Python", "JavaScript", 4));
        questions.add(new Question("Which year did India get independence?", "1945", "1947", "1950", "1952", 2));
        // Add more questions as needed
    }

    void startTimer() {
        timeLeft = 30;
        timerLabel.setText("Time left: " + timeLeft);
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    timeLeft--;
                    timerLabel.setText("Time left: " + timeLeft);
                    if (timeLeft <= 0) {
                        nextQuestion();
                    }
                });
            }
        }, 1000, 1000);
    }

    void resetTimer() {
        if (timer != null)
            timer.cancel();
        startTimer();
    }

    void showResults() {
        JPanel content = new JPanel(new GridLayout(0, 1));
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int selected = (i < selectedAnswers.size()) ? selectedAnswers.get(i) : 0;
            boolean correct = selected == q.correctOption;
            JLabel resultLabel = new JLabel(
                    "<html><b>Q" + (i + 1) + ":</b> " + q.question +
                            "<br>Selected: " + (selected > 0 ? q.getOptionText(selected) : "Not Answered") +
                            "<br>Correct: " + q.getOptionText(q.correctOption) +
                            "<br><font color='" + (correct ? "green" : "red") + "'>" + (correct ? "Correct" : "Wrong")
                            + "</font><br><br></html>");
            content.add(resultLabel);
        }

        JScrollPane scrollPane = new JScrollPane(content);
        resultPanel.add(scrollPane, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(this, "Your Score: " + score + "/" + questions.size());
    }

    JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(ALMOST_WHITE);
        return label;
    }

    JButton createButton(String text) {
        JButton button = new JButton(text);
        customizeButton(button);
        button.addActionListener(this);
        return button;
    }

    void customizeButton(JButton button) {
        button.setBackground(LIGHT_BLUE);
        button.setForeground(Color.BLACK);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(OnlineExamSystem::new);
    }

    class Question {
        String question, option1, option2, option3, option4;
        int correctOption;

        public Question(String question, String o1, String o2, String o3, String o4, int correct) {
            this.question = question;
            this.option1 = o1;
            this.option2 = o2;
            this.option3 = o3;
            this.option4 = o4;
            this.correctOption = correct;
        }

        String getOptionText(int optionNumber) {
            return switch (optionNumber) {
                case 1 -> option1;
                case 2 -> option2;
                case 3 -> option3;
                case 4 -> option4;
                default -> "Not answered";
            };
        }
    }
}