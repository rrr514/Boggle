package assignment;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

public class GameManager implements BoggleGame {

    private char[][] board;
    public int size;
    public int[] scores;
    private TreeSet<String>[] playerWords;
    private TreeSet<String> allWords;
    public int numPlayers;
    private GameDictionary dict;
    private SearchTactic searchTactic;
    private int[] dr = {-1, -1, -1, 0, 0, 1, 1, 1};
    private int[] dc = {1, 0, -1, 1, -1, 1, 0, -1};
    private ArrayDeque<Point> lastWordCoords;
    private ArrayList<Point> lastWordCoordsStored = new ArrayList();

    //parameter constructor
    public GameManager(int size, int numPlayers, String cubeFile, BoggleDictionary dict){
        try{
            newGame(size, numPlayers, cubeFile, dict);
        }
        catch (IOException e){
            System.err.println("Error with cube file.");
        }
    }

    @Override
    public int addWord(String word, int player) {
        if(word == null) throw new IllegalArgumentException("Input word is null");
        word = word.toUpperCase();
        if(allWords == null){
            allWords = (TreeSet<String>) getAllWords();
        }
        if(allWords.contains(word) && !playerWords[player].contains(word)){
            playerWords[player].add(word);
            findWordCoords(board, word);
            int points = word.length() - 3;
            scores[player] += points;
            return points;
        }
        else return 0;
    }


    @Override
    //returns all possible words on the board as a Collection of Strings
    public Collection<String> getAllWords() {
        TreeSet<String> ret = new TreeSet<>();
        //searching by board
        if(searchTactic == SearchTactic.SEARCH_BOARD){
            boolean[][] visited;
            for(int i = 0;i < size;i++){
                for(int j = 0;j < size;j++){
                    String word = "" + board[i][j];
                    visited = new boolean[size][size];
                    visited[i][j] = true;
                    searchWholeBoard(board, i, j, word, visited, ret);
                }
            }
            return ret;
        }
        //searching by dictionary
        else{
            Iterator<String> iter = dict.dictTrie.getIterator();
            String word;
            String wordUppercase;
            while(iter.hasNext()){
                word = iter.next();
                if(word.length() < 4) continue;
                wordUppercase = word.toUpperCase();
                if(findWord(board, wordUppercase)){
                    ret.add(wordUppercase);
                }
            }
            return ret;
        }
    }

    //recurses the entire board for valid words and adds them to a TreeSet of Strings
    private void searchWholeBoard(char[][] board, int r, int c, String word, boolean[][] visited, TreeSet<String> words){
        if(!dict.isPrefix(word)){
            return;
        }
        if(dict.contains(word) && word.length() >= 4){
            words.add(word);
        }
        for(int i = 0;i < dr.length;i++){
            int newR = r + dr[i];
            int newC = c + dc[i];
            if(newR >= 0 && newR < size && newC >= 0 && newC < size && !visited[newR][newC]){
                visited[newR][newC] = true;
                searchWholeBoard(board, newR, newC, word + board[newR][newC], visited, words);
                visited[newR][newC] = false;
            }
        }
    }

    //iterates through the board to find a matching first letter as a starting point
    public boolean findWord(char[][] board, String wordToFind){
        boolean[][] visited = new boolean[size][size];
        wordToFind = wordToFind.toUpperCase();
        String word;
        for(int i = 0;i < size;i++){
            for(int j = 0;j < size;j++){
                if(board[i][j] == wordToFind.charAt(0)){
                    visited[i][j] = true;
                    word = "" + board[i][j];
                    if(findInBoard(board, i, j, word, wordToFind, visited)){
                        return true;
                    }
                    visited[i][j] = false;
                }
            }
        }
        return false;
    }

    //same as findWord() except calls the findInBoard method that stores the coordinates
    private boolean findWordCoords(char[][] board, String wordToFind){
        boolean[][] visited = new boolean[size][size];
        wordToFind = wordToFind.toUpperCase();
        String word;
        ArrayDeque<Point> coords;
        for(int i = 0;i < size;i++){
            for(int j = 0;j < size;j++){
                if(board[i][j] == wordToFind.charAt(0)){
                    visited[i][j] = true;
                    word = "" + board[i][j];
                    coords = new ArrayDeque<>();
                    coords.push(new Point(i, j));
                    if(findInBoard(board, i, j, word, wordToFind, coords, visited)){
                        return true;
                    }
                    visited[i][j] = false;
                }
            }
        }
        return false;
    }

    //recurses through the board looking specifically for param wordToFind
    private boolean findInBoard(char[][] board, int r, int c, String word, String wordToFind, boolean[][] visited){
        if(word.equals(wordToFind)){
            return true;
        }
        boolean b = false;
        for(int i = 0;i < dr.length;i++){
            if(b) break;
            int newR = r + dr[i];
            int newC = c + dc[i];
            if(newR >= 0 && newR < size && newC >= 0 && newC < size && !visited[newR][newC] && board[newR][newC] == wordToFind.charAt(word.length())){
                visited[newR][newC] = true;
                b = findInBoard(board, newR, newC, word + board[newR][newC], wordToFind, visited);
                visited[newR][newC] = false;
            }
        }
        return b;
    }

    //overloaded method where the coordinates of the letters of the word are stored
    private boolean findInBoard(char[][] board, int r, int c, String word, String wordToFind, ArrayDeque<Point> coords, boolean[][] visited){
        if(word.equals(wordToFind)){
            //store the coords in an instance variable
            lastWordCoords.addAll(coords);
            return true;
        }
        boolean b = false;
        for(int i = 0;i < dr.length;i++){
            if(b) break;
            int newR = r + dr[i];
            int newC = c + dc[i];
            if(newR >= 0 && newR < size && newC >= 0 && newC < size && !visited[newR][newC] && board[newR][newC] == wordToFind.charAt(word.length())){
                visited[newR][newC] = true;
                coords.push(new Point(newR, newC));
                b = findInBoard(board, newR, newC, word + board[newR][newC], wordToFind, coords, visited);
                visited[newR][newC] = false;
                coords.pop();
            }
        }
        return b;
    }

    @Override
    public char[][] getBoard() {
        return board;
    }

    @Override
    public List<Point> getLastAddedWord() {
        if(lastWordCoords.isEmpty() && lastWordCoordsStored == null) return null;
        if(lastWordCoords.isEmpty()) return lastWordCoordsStored;
        lastWordCoordsStored = new ArrayList<>();
        while(!lastWordCoords.isEmpty()){
            lastWordCoordsStored.add(lastWordCoords.pollLast());
        }
        
        return lastWordCoordsStored;
    }

    @Override
    public int[] getScores() {
        return scores;
    }

    @Override
    public void newGame(int size, int numPlayers, String cubeFile, BoggleDictionary dict) throws IOException {
        //checking for illegal arguments
        if(size <= 0) throw new IllegalArgumentException("Invalid size");
        if(numPlayers <= 0) throw new IllegalArgumentException("Invalid number of players");
        if(dict == null) throw new IllegalArgumentException("Invalid dictionary");
        //intializing instance variables
        this.size = size;
        this.numPlayers = numPlayers;
        scores = new int[numPlayers];
        instantiate_playerWords();
        this.dict = (GameDictionary) dict;
        searchTactic = SEARCH_DEFAULT;
        lastWordCoords = new ArrayDeque<>();

        //creating the board
        ArrayList<String> cubes = getCubes(cubeFile);
        ArrayList<Character> chars = getChars(cubes);
        board = generateBoard(chars);
    }

    @Override
    public void setGame(char[][] board) {
        this.board = board;
        scores = new int[numPlayers];
        instantiate_playerWords();
    }

    @Override
    public void setSearchTactic(SearchTactic tactic) {
        searchTactic = tactic;
    }

    private ArrayList<String> getCubes(String cubesFileName) throws IOException{
        ArrayList<String> cubes = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(cubesFileName));
        String cube;

        while((cube = reader.readLine()) != null){
            cubes.add(cube);
        }

        reader.close();
        return cubes;
    }

    private ArrayList<Character> getChars(ArrayList<String> cubes){
        ArrayList<Character> chars = new ArrayList<>();
        Random random = new Random();
        char cubeChar;

        for(String cube : cubes){
            int charAtInt = random.nextInt(cube.length());
            cubeChar = cube.charAt(charAtInt);
            chars.add(cubeChar);
        }

        return chars;
    }

    private char[][] generateBoard(ArrayList<Character> chars){
        //fill board w cubes by randomly assigning chars from arraylist into char[size][size]
        Random random = new Random();
        char[][] genBoard = new char[size][size];
        int whichChar;

        boolean enoughChars = false;
        if(chars.size() >= size*size){
            enoughChars = true;
        }

        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                whichChar = random.nextInt(chars.size());
                genBoard[i][j] = chars.get(whichChar);
                //
                
                if(enoughChars){
                    chars.remove(whichChar);
                }
            }
        }

        return genBoard;
    }

    private void instantiate_playerWords(){
        playerWords = new TreeSet[numPlayers];
        for(int i = 0;i < numPlayers;i++){
            playerWords[i] = new TreeSet<>();
        }
    }

    public int maxPoints(Collection<String> wordsList){
        int maxPoints = 0;
        for(String word : wordsList){
            if(word.length() > 3){
                maxPoints = maxPoints + word.length() - 3;
            }
        }
        return maxPoints;
    }


}

