package assignment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class GameDictionary implements BoggleDictionary {

    Trie dictTrie;

    //intialize instance variables
    public GameDictionary(){
        dictTrie = new Trie();
    }

    @Override
    public boolean contains(String word) {
        if(word == null) throw new IllegalArgumentException("Input is null");
        return dictTrie.find(word);
    }

    @Override
    public boolean isPrefix(String prefix) {
        if(prefix == null) throw new IllegalArgumentException("Input is null");
        return dictTrie.findPrefix(prefix);
    }

    @Override
    public void loadDictionary(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String temp;
        //add words into dictList and dictTrie
        while((temp = br.readLine()) != null){
            dictTrie.insert(temp);
        }
        br.close();
    }

    @Override
    public Iterator<String> iterator() {
        return dictTrie.getIterator();
    }
}
