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


    public List<String> loadWordsIntoList(File file) throws IOException {
        List<String> set = new ArrayList<>(approximateMaximumWordCount(file));
        loadWordsIntoCollection(file, set);
        return set;
    }

    public Set<String> loadWordsIntoSet(File file) throws IOException {
        Set<String> set = new HashSet<>(approximateMaximumWordCount(file), 1f);
        loadWordsIntoCollection(file, set);
        return set;
    }

    private void loadWordsIntoCollection(File file, Collection<String> collection) throws IOException {
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

    public void saveWords(Collection<String> words, File file) throws IOException {
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

    public File getFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        if (!file.exists() || !file.isFile()) {
            throw new IOException("File " + fileName + " not found.");
        } else {
            return file;
        }
    }

    public File getOrCreateFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        if (!file.exists()) {
            return createFile(fileName);
        } else if (file.isFile()) {
            return file;
        } else {
            throw new IOException(fileName + " exists and is not a file.");
        }
    }

    public File createFile(String fileName) throws IOException {
        File file = getFileInSameFolder(fileName);
        Path filePath = file.toPath();
        if (file.exists()) {
            Files.delete(filePath);
        }
        return Files.createFile(filePath).toFile();
    }

    public int approximateMaximumWordCount(File file) {
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

    public File getFileInSameFolder(String fileName) {
        return new File(getSurroundingFolder() + '/' + fileName);
    }

    public String getSurroundingFolder() {
        String jarURL = URLDecoder.decode(FileHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath(),
                StandardCharsets.UTF_8);
        return jarURL.substring(0, jarURL.lastIndexOf('/'));
    }
}
