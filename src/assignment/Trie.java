package assignment;

import java.util.ArrayDeque;
import java.util.TreeMap;
import java.util.Iterator;
import java.io.*;

public class Trie {

    private TrieNode root;
    private TrieIterator iterator;

    class TrieNode {
        private String val;
        private TrieNode parent;
        private TreeMap<Character, TrieNode> children;
        private boolean isWord;

        public TrieNode(String s) {
            val = s;
            parent = null;
            children = new TreeMap<>();
            isWord = false;
        }

        // constructor that allows parent of the node to be defined
        public TrieNode(String s, TrieNode parent) {
            val = s;
            this.parent = parent;
            children = new TreeMap<>();
            isWord = false;
        }
    }

    // iterator that iterates through the Trie
    class TrieIterator implements Iterator<String> {
        private ArrayDeque<TrieNode> searchStack;
        private StringBuilder wordBuilder;
        private boolean doneSearching;

        public TrieIterator() {
            searchStack = new ArrayDeque<>();
            searchStack.push(root);
            wordBuilder = new StringBuilder();
            doneSearching = false;
        }

        public boolean hasNext() {
            return !searchStack.isEmpty() && !doneSearching;
        }

        public String next() {
            TrieNode curNode;
            String ret;
            while (!searchStack.isEmpty() && !doneSearching) {
                curNode = searchStack.peek();
                ret = wordBuilder.toString();
                // leaf node
                if (curNode.children == null || curNode.children.isEmpty()) {
                    boolean flag = hasSiblingToExplore(curNode);
                    //if next sibling exists
                    if(flag){
                        //remove current node
                        TrieNode toAdd = getSiblingToExplore(curNode);
                        searchStack.pop();
                        wordBuilder.deleteCharAt(wordBuilder.length() - 1);
                        searchStack.push(toAdd);
                        wordBuilder.append(searchStack.peek().val);
                    }
                    //remove ancestors with no siblings left
                    else {
                        removeDeadAncestors();
                    }
                } 
                else { 
                    // push first child
                    searchStack.push(curNode.children.get(curNode.children.firstKey()));
                    wordBuilder.append(searchStack.peek().val);
                }

                //return if current node is a valid word
                if(curNode.isWord) return ret;
            }
            return null;
        }

        //removes all ancestors that have had all their siblings searched already
        private void removeDeadAncestors(){
            TrieNode curNode;
            while(!searchStack.isEmpty()){
                curNode = searchStack.peek();
                if(curNode.val.equals("")){
                    doneSearching = true;
                    break;
                }
                if(!hasSiblingToExplore(curNode)){
                    searchStack.pop();
                    wordBuilder.deleteCharAt(wordBuilder.length() - 1);
                }
                else {
                    //add the sibling, delete curNode
                    TrieNode toAdd = getSiblingToExplore(curNode);
                    searchStack.pop();
                    wordBuilder.deleteCharAt(wordBuilder.length() - 1);
                    searchStack.push(toAdd);
                    wordBuilder.append(searchStack.peek().val);
                    break;
                }
            }
        }

        private boolean hasSiblingToExplore(TrieNode curNode){
            TreeMap<Character, TrieNode> siblings = curNode.parent.children;
            char prevChar = searchStack.peek().val.charAt(0);
            return siblings.higherKey(prevChar) != null;
        }

        private TrieNode getSiblingToExplore(TrieNode curNode){
            TreeMap<Character, TrieNode> siblings = curNode.parent.children;
            char prevChar = searchStack.peek().val.charAt(0);
            return siblings.get(siblings.higherKey(prevChar));
        }
    }

    public Trie() {
        root = new TrieNode("");
        iterator = new TrieIterator();
    }

    public String insert(String in) {
        return insert(root, in, 0);
    }

    private String insert(TrieNode node, String in, int index) {
        if (index >= in.length()) {
            node.isWord = true;
            return in;
        } else {
            if (node.children.containsKey(in.charAt(index))) {
                return insert(node.children.get(in.charAt(index)), in, index + 1);
            } else {
                node.children.put(in.charAt(index), new TrieNode("" + in.charAt(index), node));
                return insert(node.children.get(in.charAt(index)), in, index + 1);
            }
        }
    }

    public boolean find(String in) {
        // if(in.isAlphabetic())
        return find(root, in.toLowerCase(), 0);
    }

    private boolean find(TrieNode node, String in, int index) {
        if (index == in.length() && node.isWord) {
            return true;
        } else if (index >= in.length()) {
            return false;
        } else {
            if (node.children.containsKey(in.charAt(index))) {
                return find(node.children.get(in.charAt(index)), in, index + 1);
            } else {
                return false;
            }
        }
    }

    public boolean findPrefix(String prefix) {
        return findPrefix(root, prefix.toLowerCase(), 0);
    }

    // the same thing as find except remove the isWord check
    private boolean findPrefix(TrieNode node, String prefix, int index) {
        if (index == prefix.length()) {
            return true;
        } else if (index > prefix.length()) {
            return false;
        } else {
            if (node.children.containsKey(prefix.charAt(index))) {
                return findPrefix(node.children.get(prefix.charAt(index)), prefix, index + 1);
            } else {
                return false;
            }
        }
    }

    public void printTrie() {
        printTrie(root, "");
    }

    private void printTrie(TrieNode node, String prefix) {
        if (node.isWord) {
            System.out.println(prefix);
        }
        if (node.children.size() == 0) {
            return;
        } else {
            for (TrieNode n : node.children.values()) {
                printTrie(n, prefix + n.val);
            }
        }
    }

    public TrieIterator getIterator() {
        return iterator;
    }
}
