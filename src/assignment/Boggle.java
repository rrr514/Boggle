package assignment;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Arrays;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Scanner;


import assignment.BoggleGame.SearchTactic;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

// implement your UI here! we should be able to run it with 
//      java assignment.Boggle

public class Boggle {
    static char[][] board;
    static ArrayList<String> cubes = new ArrayList<String>();
    ArrayList<Character> chars = new ArrayList<Character>();
    Random random = new Random();
    String inputWord = " ";
    int playerPoints = 0;
    JTextArea scoreText = new JTextArea(1, 10);
    JTextArea feedbackText = new JTextArea(1, 10);
    JTextArea wordsGuessed = new JTextArea(20, 10);
    JTextArea allWords = new JTextArea(50, 10);
    JLabel label = new JLabel();
    ArrayList<String> wordsGuessedList = new ArrayList<>();


    JFrame frame = new JFrame("BOGGLY BOGG BOGG");
    JTextField text;

    int whichPlayer = 0;

    JTextArea pointsCountPlayer = new JTextArea();
    JTextArea pointsCountComp = new JTextArea();

    boolean wordValid = false;
    boolean wordDone = false;
    boolean wordShort = false;
    boolean wordOnBoard = false;

    static int sizeInput = 4;
    static int playersInput = 1;

    GameDictionary dict = new GameDictionary();
    GameManager manager;// = new GameManager(sizeInput, playersInput, inputWord, dict);

    JPanel gridPanel;
    JPanel feedbackPanel = new JPanel();

    static boolean inputYes = false;

    static boolean ranBefore = false;



    public static void main(String[] args) throws IOException {
        Boggle boggle = new Boggle();

        System.out.println("boggly bogg bogg");

        try {
            boggle.getCubes("cubes.txt");
        }

        catch (IOException e) {
            System.err.println("Exception");
        }

        if(ranBefore == false){
            boggle.inputAsk();
            ranBefore = true;
        }
        else{boggle.setManager(sizeInput, playersInput);}
        


        

    }




    public void setManager(int sizeInput, int playersInput) throws IOException{
        manager = new GameManager(sizeInput, playersInput, "cubes.txt", dict);
        if(inputYes){
            getChars(cubes);
            generateBoard();

            gui(board);
        }
    }

    public char[][] generateBoard() {

        if(manager.size>0){
        board = new char[manager.size][manager.size];
        }
        else{board = new char[manager.size][manager.size];}
        // fill board w cubes by randomly assigning chars
        int whichChar;

        boolean enoughChars = false;
        if(chars.size() >= sizeInput*sizeInput){
            enoughChars = true;
        }

        for (int i = 0; i < manager.size; i++) {
            for (int j = 0; j < manager.size; j++) {
                whichChar = random.nextInt(chars.size());
                board[i][j] = chars.get(whichChar);
                if(enoughChars){
                    chars.remove(whichChar);
                }
            }
        }
        manager.setGame(board);

        return board;
    }

    public ArrayList<String> getCubes(String cubesFileName) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(cubesFileName));
        String cube;

        while ((cube = reader.readLine()) != null) {
            cubes.add(cube);
        }

        reader.close();
        return cubes;
    }

    public ArrayList<Character> getChars(ArrayList<String> cubes) {
        char cubeChar;

        for (String cube : cubes) {
            int charAtInt = random.nextInt(cube.length());
            cubeChar = cube.charAt(charAtInt);
            chars.add(cubeChar);
        }

        return chars;
    }

    public void gui(char[][] board) throws IOException {
        gridPanel = new JPanel(new GridLayout(manager.size, manager.size));
        dict.loadDictionary("words.txt");
        manager.setSearchTactic(SearchTactic.SEARCH_BOARD);

        gridPanel.setLayout(null);
        // making grid and putting board into grid
        for (int i = 0; i < manager.size; i++) {
            for (int j = 0; j < manager.size; j++) {
                label = new JLabel(Character.toString(board[i][j]), SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                label.setBounds(j * 50, i * 50, 50, 50);
                gridPanel.add(label);
            }
        }
        if(sizeInput > 5){
            gridPanel.setPreferredSize(new Dimension(300, 200));
        }
        else{
            gridPanel.setPreferredSize(new Dimension(10000, 10000));
        }

        // feedback panel info
        frame.add(gridPanel);
        gridPanel.revalidate();

        feedbackPanel.setPreferredSize(new Dimension(200, 200));
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        frame.add(feedbackPanel, BorderLayout.EAST);

        // text input area

        JPanel inputPanel = new JPanel();
        inputPanel.setPreferredSize(new Dimension(200, 100));

        // making enter button
        text = new JTextField(30);
        JButton enter = new JButton("Enter");
        inputPanel.add(text);
        inputPanel.add(enter);
        inputPanel.revalidate();

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.revalidate();

        text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputWord = text.getText();
                wordEntered();
            }
        });

        // what happens when you put words into the enter button
        enter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputWord = text.getText();

                wordEntered();

                frame.revalidate();
            }

        });

        // making score tracker
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new BorderLayout());
        scorePanel.setPreferredSize(new Dimension(100, 200));
        scorePanel.add(scoreText);
        feedbackPanel.add(scorePanel);

        scoreText.setEditable(false);
        scoreText.setText("Score for player " + (whichPlayer + 1) + " = " + Integer.toString(playerPoints));
        frame.revalidate();

        // words guessed tracker
        wordsGuessed.setLineWrap(true);
        wordsGuessed.setWrapStyleWord(true);
        wordsGuessed.setPreferredSize(new Dimension(100, 100));

        JPanel wordsGuessedPanel = new JPanel();
        wordsGuessedPanel.setLayout(new BorderLayout());
        wordsGuessedPanel.add(wordsGuessed);

        feedbackPanel.add(wordsGuessedPanel);

        wordsGuessed.setEditable(false);
        wordsGuessed.setText("Valid words guessed by all players: ");

        JScrollPane scrollPaneWordsGuessed = new JScrollPane(wordsGuessed);
        scrollPaneWordsGuessed.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPaneWordsGuessed.setPreferredSize(new Dimension(200, 200));
        wordsGuessedPanel.add(scrollPaneWordsGuessed);

        frame.revalidate();

        // switchPlayer
        JButton switchPlayerButton = new JButton("Switch player");
        inputPanel.add(switchPlayerButton);
        inputPanel.revalidate();

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.revalidate();

        switchPlayerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePlayer();
            }
        });

        // end game
        JButton endGame = new JButton("Show all words");
        inputPanel.add(endGame);
        inputPanel.revalidate();

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.revalidate();

        JScrollPane allWordsPane = new JScrollPane(allWords);
        allWordsPane.setPreferredSize(new Dimension(200, 200));
        allWordsPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JTextArea allWords = new JTextArea();
        allWords.setEditable(false);
        allWords.setLineWrap(true);
        allWords.setWrapStyleWord(true);
        allWords.setPreferredSize(new Dimension(100, 400));

        allWordsPane.add(allWords);

        pointsCountPlayer.setEditable(false);
        pointsCountComp.setEditable(false);
        pointsCountPlayer.setLineWrap(true);
        pointsCountComp.setLineWrap(true);
        pointsCountPlayer.setWrapStyleWord(true);
        pointsCountComp.setWrapStyleWord(true);
        pointsCountPlayer.setPreferredSize(new Dimension(100, 400));
        pointsCountComp.setPreferredSize(new Dimension(100, 400));

        allWordsPane.add(pointsCountPlayer);
        allWordsPane.add(pointsCountComp);

        allWords.setEditable(false);
        allWords.setText("Valid words guessed: ");

        endGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllWords();
                allWordsPane.add(allWords);
                frame.revalidate();

            }
        });

        feedbackPanel.add(allWordsPane);

        frame.revalidate();

        // new game
        JButton newGame = new JButton("New game");
        inputPanel.add(newGame);
        inputPanel.revalidate();

        frame.add(inputPanel, BorderLayout.SOUTH);
        frame.revalidate();

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    newGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void wordEntered() {
        feedbackText.setText("");
        feedbackText.setLineWrap(true);
        text.setText("");
        if (inputWord == null) {
            feedbackText.setText("Please enter a word.");
        }
        if (manager.findWord(board, inputWord)) {
            manager.addWord(inputWord, whichPlayer);
            playerPoints = manager.getScores()[whichPlayer];
            scoreText.setText("Score for player " + (whichPlayer + 1) + " = " + Integer.toString(playerPoints));
            // turnGridNullColor();
            guiWordFound();
            frame.revalidate();
        }
    }

    public void guiWordFound() {

        ArrayList<Point> coords = new ArrayList<>(manager.getLastAddedWord());

        JLabel[][] labels = new JLabel[manager.size][manager.size];
        Component[] components = gridPanel.getComponents();

        int in = 0;
        for (int x = 0; x < manager.size; x++) {
            for (int y = 0; y < manager.size; y++) {
                labels[x][y] = (JLabel) components[in];
                labels[x][y].setBackground(null);
                labels[x][y].setOpaque(false);
                in++;
                gridPanel.repaint();
                frame.repaint();
                frame.revalidate();
            }
        }

        for (Point p : coords) {

            int x = p.x;
            int y = p.y;
            labels[x][y].setOpaque(true);
            labels[x][y].setBackground(Color.cyan);
        }

        boolean isUnique = true;
        for (String word : wordsGuessedList) {
            if (word.equalsIgnoreCase(inputWord)) {
                isUnique = false;
                break;
            }
        }

        boolean yesWord = false;

        for (String word : manager.getAllWords()) {
            if (word.equalsIgnoreCase(inputWord)) {
                yesWord = true;
                break;
            }
        }

        if (isUnique && manager.findWord(board, inputWord) && inputWord.length() > 3 && yesWord) {
            wordsGuessedList.add(inputWord);
            wordsGuessed.setText("Valid words guessed by all players: " + wordsGuessedList);
            gridPanel.revalidate();
        }

    

        else {
            if (isUnique == false) {
                feedbackText.setText("You've already entered that word. (Players may not repeat a word that another player has guessed.)");
            }


            if (inputWord.length() <= 3) {
                feedbackText.setText("Word too short.");

            }

            if (manager.findWord(board, inputWord) == false) {
                feedbackText.setText("That word is not on the board.");
            }

            if (yesWord == false) {
                feedbackText.setText("That word is not a valid word.");
            }

            else{
                feedbackText.setText("Something is wrong with that word. Make sure there are no extra spaces.");
            }
        }

        feedbackPanel.add(feedbackText);
        frame.revalidate();
    }

    public void showAllWords() {
        String allWordsList = "";
        for (String word : manager.getAllWords()) {
            if (allWordsList == "") {
                allWordsList = word;
            } else {
                allWordsList = allWordsList + ", " + word;
            }

            allWords.setText("All words: " + allWordsList);
        }

        int[] pointsList = manager.getScores();
        if (pointsList == null) {
            pointsCountPlayer.setText("Player points: " + 0);
        } else {
            pointsCountPlayer.setText("Player points: " + Arrays.toString(pointsList));
        }

        int maxPoints = manager.maxPoints(manager.getAllWords());
        pointsCountComp.setText("Max possible score: " + Integer.toString(maxPoints));

        feedbackPanel.add(pointsCountPlayer);
        feedbackPanel.add(pointsCountComp);

        frame.revalidate();
    }

    public void newGame() throws IOException {
    
        frame.dispose();
        main(null);

    }

    public void changePlayer() {
        int numPlayers = manager.numPlayers;
        if (whichPlayer == numPlayers - 1) {
            whichPlayer = 0;
        } else {
            whichPlayer++;
        }
        playerPoints = manager.getScores()[whichPlayer];
        scoreText.setText("Score for player " + (whichPlayer + 1) + " = " + Integer.toString(playerPoints));
        frame.revalidate();
    }

    public void inputAsk() throws IOException{
            Scanner scanner = new Scanner(System.in);
    
            System.out.println("Please enter board size as a positive integer greater than 0: ");
            String sizeInputString = scanner.nextLine();

            System.out.println("Please enter number of players as a positive integer greater than 0: ");
            String playersInputString = scanner.nextLine();
             try {
                if(Integer.parseInt(sizeInputString) >= 1 && Integer.parseInt(playersInputString) >= 1){
                    sizeInput = Integer.parseInt(sizeInputString);
                    playersInput = Integer.parseInt(playersInputString);
                  
                    inputYes = true;
                    }
  
                    else{
                        System.out.println("Please make sure both integers are positive and greater than 0.");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Please enter valid integers.");
                }

            scanner.close();

            if(inputYes){
                try{
                    setManager(sizeInput, playersInput);
                }
                catch(IOException e){
                    e.printStackTrace();
                }

            }
        }
    }






