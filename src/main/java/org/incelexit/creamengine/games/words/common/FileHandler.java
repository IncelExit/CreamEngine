package org.incelexit.creamengine.games.words.common;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileHandler {

    private static final float WORD_COUNT_MAXIMUM_FACTOR = 1.2f;
    private static final int AVERAGE_NUMBER_OF_CHARACTERS_PER_WORD = 5;

    public static final String ALL_WORDS_FILE_NAME = "data/all_words.txt";
    public static final String SEVEN_LETTER_WORDS_FILE_NAME = "data/seven_letter_words.txt";


    public static List<String> loadWordsIntoList(File file) throws IOException {
        List<String> set = new ArrayList<>(approximateMaximumWordCount(file));
        loadWordsIntoCollection(file, set);
        return set;
    }

    public static Set<String> loadWordsIntoSet(File file) throws IOException {
        Set<String> set = new HashSet<>(approximateMaximumWordCount(file), 1f);
        loadWordsIntoCollection(file, set);
        return set;
    }

    private static void loadWordsIntoCollection(File file, Collection<String> collection) throws IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();

        while (line != null) {
            collection.add(line);
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        fileReader.close();
    }

    public static void saveWords(Collection<String> words, File file) throws IOException {
        ArrayList<String> sortedWords = new ArrayList<>(words);
        sortedWords.sort(String::compareTo);
        FileWriter fileWriter = new FileWriter(file);
        for (String word : sortedWords) {
            fileWriter.write(word);
            fileWriter.write('\n');
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public static File getFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File " + fileName + " not found.");
        } else {
            return file;
        }
    }

    public static File getOrCreateFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        if (!file.exists()) {
            return createFile(fileName);
        } else if (file.isFile()) {
            return file;
        } else {
            throw new IOException(fileName + " exists and is not a file.");
        }
    }

    public static File createFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        Path filePath = file.toPath();
        if (file.exists()) {
            Files.delete(filePath);
        }
        return Files.createFile(filePath).toFile();
    }

    public static int approximateMaximumWordCount(File file) {
        if (file == null) {
            return 0;
        }

        //expecting a maximum of 2 billion words
        int byteCount = (int) file.length();

        //expecting UTF-8 encoding
        int averageBytesPerWord = AVERAGE_NUMBER_OF_CHARACTERS_PER_WORD * 4;

        //multiply by factor to avoid unnecessary hash set memory
        return (int) (byteCount * WORD_COUNT_MAXIMUM_FACTOR) / averageBytesPerWord;
    }

    public static File getFileInSameFolder(String fileName) {
        return new File(getSurroundingFolder() + '/' + fileName);
    }

    public static String getSurroundingFolder() {
        String jarURL = URLDecoder.decode(FileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                StandardCharsets.UTF_8);
        return jarURL.substring(0, jarURL.lastIndexOf('/'));
    }

    public static boolean addWord(String word) {
        try {
            boolean addedToAllWords = addWord(getFile(ALL_WORDS_FILE_NAME), word);
            boolean addedToSevenLetterWords = addWord(getFile(SEVEN_LETTER_WORDS_FILE_NAME), word);
            return addedToAllWords || addedToSevenLetterWords;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean addWord(File file, String word) {
        try {
            List<String> words = loadWordsIntoList(file);
            if (words.contains(word)) {
                return false;
            }

            words.add(word);
            saveWords(words, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeWord(String word) {
        try {
            boolean removedFromAllWords = removeWord(getFile(ALL_WORDS_FILE_NAME), word);
            boolean removedFromSevenLetterWords = removeWord(getFile(SEVEN_LETTER_WORDS_FILE_NAME), word);
            return removedFromAllWords || removedFromSevenLetterWords;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean removeWord(File file, String word) {
        try {
            List<String> words = loadWordsIntoList(file);
            if (!words.contains(word)) {
                return false;
            }

            words.remove(word);
            saveWords(words, file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
