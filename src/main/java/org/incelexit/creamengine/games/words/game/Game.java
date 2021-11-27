package org.incelexit.creamengine.games.words.game;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.incelexit.creamengine.bot.CEBot;
import org.incelexit.creamengine.games.words.common.FileHandler;
import org.incelexit.creamengine.games.words.listeners.WordGameChannelListener;
import org.incelexit.creamengine.util.ChannelMessenger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Game {

    private Set<String> allWords;
    private Set<String> sevenLetterWords;
    private Set<String> alreadyUsedWords;

    private final ChannelMessenger channelMessenger;
    private WordGameChannelListener channelListener;
    private final CEBot bot;

    private String sevenLetterWord;
    private Pattern characterRegex;
    private int matchingWordCount;
    private int foundWords;
    private Character requiredCharacter;
    private List<Character> shuffledCharacters;

    private Map<User, Integer> points;

    public Game(TextChannel channel, CEBot bot) {
        this.points = new HashMap<>();
        FileHandler fileHandler = new FileHandler();
        this.channelMessenger = new ChannelMessenger(channel);

        this.bot = bot;

        try {
            allWords = fileHandler.loadWordsIntoSet(
                    fileHandler.getFile(FileHandler.ALL_WORDS_FILE_NAME));
        } catch (IOException e) {
            channelMessenger.sendMessage("A required word list (" + FileHandler.ALL_WORDS_FILE_NAME + ") was not found. " +
                                         "Please ensure these files are in the same folder as the game!");
            allWords = new HashSet<>();
        }

        try {
            sevenLetterWords = fileHandler.loadWordsIntoSet(
                    fileHandler.getFile(FileHandler.SEVEN_LETTER_WORDS_FILE_NAME));
        } catch (IOException e) {
            channelMessenger.sendMessage("A required word list (" + FileHandler.SEVEN_LETTER_WORDS_FILE_NAME + ") was not found. " +
                                         "Please ensure these files are in the same folder as the game!");
            sevenLetterWords = new HashSet<>();
        }

        this.alreadyUsedWords = new HashSet<>();
        this.sevenLetterWord = "";
    }

    public void start() {
        if (wordListsAreEmpty()) {
            channelMessenger.sendMessage("At least one of the word lists is empty. Please ensure that they actually contain words!");
        }

        pickSevenLetterWord();


        this.shuffledCharacters = getShuffledLetters();
        this.requiredCharacter = shuffledCharacters.get(0);
        this.characterRegex = getCharacterRegex();
        this.allWords = getMatchingWords();
        this.matchingWordCount = allWords.size();

        this.foundWords = 0;

        printGameRules();

        setupChannelListener();
    }

    private void setupChannelListener() {
        this.channelListener = new WordGameChannelListener(this, channelMessenger.getChannel());
        this.bot.registerListener(this.channelListener);
    }

    public void processNextWord(User user, String nextWord) {
        if (nextWord.length() == 0) {
            channelMessenger.sendMessage("Please enter a word.\n");
        } else if (nextWord.length() < 4) {
            channelMessenger.sendMessage("Only words longer than 3 letters are allowed.\n");
        } else if (!nextWord.contains(String.valueOf(requiredCharacter))) {
            channelMessenger.sendMessage("The word must contain the letter " + requiredCharacter + "\n");
        } else if (!characterRegex.matcher(nextWord).matches()) {
            channelMessenger.sendMessage(nextWord + " contains letters that are not allowed.\n");
        } else if (!allWords.contains(nextWord)) {
            channelMessenger.sendMessage("I don't know the word " + nextWord + ". If it's really a word, add it to the word list next time!\n");
        } else if (alreadyUsedWords.contains(nextWord)) {
            channelMessenger.sendMessage("You already said " + nextWord + ".\n");
        } else {
            foundWords++;
            alreadyUsedWords.add(nextWord);

            if (nextWord.equals(sevenLetterWord)) {
                channelMessenger.sendMessage("SUPER NICE, you found the seven letter word!\n");
            } else if (isDistinctSevenLetterWord(nextWord)) {
                channelMessenger.sendMessage("""
                        Huh, interesting, that's not the seven letter word I was thinking of.
                        You are very smart!
                        """);
            }

            int pointValue = getPointValue(nextWord);
            addPoints(user, pointValue);

            channelMessenger.sendMessage("Nice! The word " + nextWord + " counts! You get " + pointValue + " Points!\n" +
                                         "Only " + (matchingWordCount - foundWords) + " to go!\n");

        }

        if (foundWords >= matchingWordCount) {
            channelMessenger.sendMessage("""
                    Congratulations! You found all words!
                    Holy shit that must have taken some time. You deserve a cookie :>""");

            finish();
        }
    }

    public void showAlreadyUsedWords() {
        channelMessenger.sendMessage("Words you found so far: ");
        printWords(alreadyUsedWords);
    }

    public void printGameRules() {
        String gameRules = "Welcome to the game!\n" +
                           "Words give points according to the fibonacci sequence, a word that contains all letters exactly once gives double points.\n" +
                           "The letters this time are " + getLettersFormattedString(shuffledCharacters) + "\n" +
                           "The letter " + requiredCharacter + " must be in every word.\n" +
                           "You have found " + alreadyUsedWords.size() + " out of " + matchingWordCount + " so far.\n" +
                           "Good luck finding all of them!\n" +
                           "If you want to see the rules again, enter /rules.\n" +
                           "If you just want to see the allowed letters, enter /letters.\n" +
                           "If you want to show all words you already found, enter /words.\n";

        channelMessenger.sendMessage(gameRules);
    }

    public void printLetters() {
        String letters = "The letters this time are " + getLettersFormattedString(shuffledCharacters) + "\n" +
                         "The letter " + requiredCharacter + " must be in every word.\n";

        channelMessenger.sendMessage(letters);
    }

    private String getLettersFormattedString(List<Character> characters) {
        StringBuilder formattedLetters = new StringBuilder();
        int characterCount = characters.size();
        for (int i = 0; i < characterCount - 2; i++) {
            formattedLetters.append(characters.get(i)).append(", ");
        }
        formattedLetters.append(characters.get(characterCount - 2))
                .append(" and ")
                .append(characters.get(characterCount - 1));
        return formattedLetters.toString();
    }

    private List<Character> getShuffledLetters() {
        char[] sevenCharArray = this.sevenLetterWord.toCharArray();
        List<Character> sevenCharsList = new ArrayList<>(7);
        for (char c : sevenCharArray) {
            sevenCharsList.add(c);
        }
        while (!isShuffled(sevenCharsList, this.sevenLetterWord)) {
            Collections.shuffle(sevenCharsList);
        }
        return sevenCharsList;
    }

    private boolean wordListsAreEmpty() {
        return allWords.size() == 0 || sevenLetterWords.size() == 0;
    }

    private void pickSevenLetterWord() {

        int index = new Random().nextInt(sevenLetterWords.size());

        for (String sevenLetterWord : sevenLetterWords) {
            if (index == 0) {
                this.sevenLetterWord = sevenLetterWord;
                return;
            }
            index--;
        }
    }

    private boolean isShuffled(List<Character> charList, String word) {
        for (int i = 0; i < charList.size(); i++) {
            if (!charList.get(i).equals(word.charAt(i))) {
                return true;
            }
        }

        return false;
    }

    private Set<String> getMatchingWords() {
        String requiredCharacterString = String.valueOf(this.requiredCharacter);
        Set<String> matchingWords = new HashSet<>();
        for (String word : allWords) {
            if (this.characterRegex.matcher(word).matches() && word.contains(requiredCharacterString)) {
                matchingWords.add(word);
            }
        }
        return matchingWords;
    }

    private Pattern getCharacterRegex() {
        StringBuilder regexBuilder = new StringBuilder("[");
        for (Character character : this.shuffledCharacters) {
            regexBuilder.append(character);
        }
        regexBuilder.append("]*");

        return Pattern.compile(regexBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }

    private boolean isDistinctSevenLetterWord(String word) {
        return word.length() == 7 && word.chars().distinct().count() == 7;
    }

    private int getPointValue(String word) {
        int value = fibonacci(word.length() - 3);
        if (word.equals(sevenLetterWord)) {
            value *= 2;
        }
        return value;
    }

    private int fibonacci(int i) {
        int prev = 0;
        int curr = 1;
        int next;

        while (i > 0) {
            i--;
            next = curr + prev;
            prev = curr;
            curr = next;
        }
        return curr;
    }

    public void addPoints(User user, int newPoints) {
        this.points.merge(user, newPoints, Integer::sum);
    }

    private void printWords(Collection<String> words) {
        List<String> wordList = new ArrayList<>(words);
        wordList.sort(String::compareTo);
        String wordListMessage = String.join(", ", wordList);
        this.channelMessenger.sendMessage(wordListMessage);
    }

    private void printMissingWords() {
        channelMessenger.sendMessage("You missed these words:");
        Set<String> missingWords = new HashSet<>(this.allWords);
        missingWords.removeAll(alreadyUsedWords);
        printWords(missingWords);
    }

    private void printPoints() {
        StringBuilder pointStringBuilder = new StringBuilder();
        for (Map.Entry<User, Integer> pointEntry : this.points.entrySet()) {
            pointStringBuilder.append(pointEntry.getKey().getName())
                    .append(": ")
                    .append(pointEntry.getValue())
                    .append(" points");
        }
        this.channelMessenger.sendMessage(pointStringBuilder.toString());
    }

    public void finish() {
        printMissingWords();
        printPoints();
        this.bot.removeListener(this.channelListener);
    }
}
