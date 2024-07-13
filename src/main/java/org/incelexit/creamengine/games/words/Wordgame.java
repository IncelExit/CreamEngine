package org.incelexit.creamengine.games.words;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import org.incelexit.creamengine.bot.CEBot;
import org.incelexit.creamengine.games.words.callbacks.LetterMessageSetterCallback;
import org.incelexit.creamengine.games.words.callbacks.WordMessageSetterCallback;
import org.incelexit.creamengine.games.words.common.FileHandler;
import org.incelexit.creamengine.games.words.listeners.WordGameChannelListener;
import org.incelexit.creamengine.util.CEApp;
import org.incelexit.creamengine.util.ChannelMessenger;
import org.incelexit.creamengine.util.MessagePinCallback;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class Wordgame implements CEApp {

    private Set<String> allWords;
    private Set<String> sevenLetterWords;
    private Set<String> alreadyUsedWords;

    private final ChannelMessenger channelMessenger;
    private WordGameChannelListener channelListener;

    private String sevenLetterWord;
    private Pattern characterRegex;
    private int matchingWordCount;
    private int foundWords;
    private Character requiredCharacter;
    private List<Character> shuffledCharacters;

    private Map<User, Integer> points;

    private String letterMessageId;
    private String wordMessageId;

    public Wordgame(MessageChannel channel) {
        this.points = new HashMap<>();
        this.channelMessenger = new ChannelMessenger(channel);

        try {
            allWords = FileHandler.loadWordsIntoSet(
                    FileHandler.getFile(FileHandler.ALL_WORDS_FILE_NAME));
        } catch (IOException e) {
            channelMessenger.sendMessage("A required word list (" + FileHandler.ALL_WORDS_FILE_NAME + ") was not found. " +
                                         "Please ensure these files are in the same folder as the game!");
            allWords = new HashSet<>();
        }

        try {
            sevenLetterWords = FileHandler.loadWordsIntoSet(
                    FileHandler.getFile(FileHandler.SEVEN_LETTER_WORDS_FILE_NAME));
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

        channelMessenger.unpinAllMessages();
    }

    private void setupChannelListener() {
        this.channelListener = new WordGameChannelListener(this, channelMessenger.getChannel());
        CEBot.getBot().registerListener(this.channelListener);
    }

    public void processNextWord(User user, Message message) {
        String nextWord = message.getContentDisplay();
        nextWord = nextWord.toLowerCase(Locale.ROOT);
        if (nextWord.length() == 0) {
            addCrossReaction(message);
            channelMessenger.sendMessage("Please enter a word.\n");
        } else if (nextWord.length() < 4) {
            addCrossReaction(message);
            channelMessenger.sendMessage("Only words longer than 3 letters are allowed.\n");
        } else if (!nextWord.contains(String.valueOf(requiredCharacter))) {
            addCrossReaction(message);
            channelMessenger.sendMessage("The word must contain the letter " + requiredCharacter + "\n");
        } else if (!characterRegex.matcher(nextWord).matches()) {
            addCrossReaction(message);
            channelMessenger.sendMessage(nextWord + " contains letters that are not allowed.\n");
        } else if (!allWords.contains(nextWord)) {
            addCrossReaction(message);
            channelMessenger.sendMessage("I don't know the word " + nextWord + ".");
        } else if (alreadyUsedWords.contains(nextWord)) {
            addCrossReaction(message);
            channelMessenger.sendMessage("You already said " + nextWord + ".\n");
        } else {
            foundWords++;
            alreadyUsedWords.add(nextWord);
            addCheckmarkReaction(message);

            if (nextWord.equals(sevenLetterWord)) {
                addStarReaction(message);
                message.addReaction(Emoji.fromUnicode("U+2B50")).queue();
//                channelMessenger.sendMessage("SUPER NICE, you found the seven letter word!\n");
            } else if (isDistinctSevenLetterWord(nextWord)) {
//                channelMessenger.sendMessage("""
//                        Huh, interesting, that's not the seven letter word I was thinking of.
//                        You are very smart!
//                        """);
            }

            int pointValue = getPointValue(nextWord);
            addPoints(user, pointValue);

            addPointReactions(message, pointValue);
//            channelMessenger.sendMessage("Nice! The word " + nextWord + " counts! You get " + pointValue + " Points!\n" +
//                                         "Only " + (matchingWordCount - foundWords) + " to go!\n");


        }

        if (foundWords >= matchingWordCount) {
            channelMessenger.sendMessage("""
                    Congratulations! You found all words!
                    Holy shit that must have taken some time. You deserve a cookie :>""");

            finish();
        }
    }

    public void printFoundWords() {
        channelMessenger.sendMessage("You have found " + foundWords + " out of " + matchingWordCount + " words. ");
        String formattedWords = formatWords(alreadyUsedWords);

        channelMessenger.unpinMessage(wordMessageId);
        channelMessenger.sendMessage(formattedWords, new WordMessageSetterCallback(this), new MessagePinCallback());
    }

    public void printGameRules() {
        channelMessenger.sendMessage("""
                Welcome to the game!
                Words give points according to the fibonacci sequence, a word that contains all letters exactly once gives double points.
                If you want to see the rules again, enter /rules.
                If you just want to see the allowed letters, enter /letters.
                If you want to show all words you already found, enter /words.
                And if you want to know how many points everyone has, enter /score.
                
                :white_check_mark: the word counts
                :cross_mark: I don't know that word
                :star: you found the 7 letter word
                :one: the amount of points the word was worth
                
                """);

        printLetters();
        printFoundWords();

        channelMessenger.sendMessage("Good luck finding all of them!");
    }

    public void printLetters() {
        String letters = "The letters this time are " + getLettersFormattedString(shuffledCharacters) + "\n";
        channelMessenger.unpinMessage(letterMessageId);
        channelMessenger.sendMessage(letters, new LetterMessageSetterCallback(this), new MessagePinCallback());
    }

    private String getLettersFormattedString(List<Character> characters) {
        StringBuilder formattedLetters = new StringBuilder();
        formattedLetters.append("**").append(requiredCharacter).append("**");
        for (char c : characters) {
            if (c != requiredCharacter) {
                formattedLetters.append(", ").append(c);
            }
        }
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

    private String formatWords(Collection<String> words) {
        List<String> wordList = new ArrayList<>(words);
        wordList.sort(String::compareTo);
        return String.join(", ", wordList);
    }

    private void printMissingWords() {
        channelMessenger.sendMessage("You missed these words:");
        Set<String> missingWords = new HashSet<>(this.allWords);
        missingWords.removeAll(alreadyUsedWords);
        String formattedWords = formatWords(missingWords);
        this.channelMessenger.sendMessage(formattedWords);
    }

    public void printPoints() {
        StringBuilder pointStringBuilder = new StringBuilder();
        for (Map.Entry<User, Integer> pointEntry : this.points.entrySet()) {
            pointStringBuilder.append(pointEntry.getKey().getAsMention())
                    .append(": ")
                    .append(pointEntry.getValue())
                    .append(" points")
                    .append(System.lineSeparator());
        }
        this.channelMessenger.sendMessage(pointStringBuilder.toString());
    }

    public void finish() {
        channelMessenger.unpinAllMessages();
        printMissingWords();
        printPoints();
        CEBot.getBot().removeListener(this.channelListener);
    }

    public void setLetterMessageId(String letterMessageId) {
        this.letterMessageId = letterMessageId;
    }

    public void setWordMessageId(String wordMessageId) {
        this.wordMessageId = wordMessageId;
    }

    private void addPointReactions(Message message, int points) {
        String pointString = Integer.toString(points);
        for(char c : pointString.toCharArray()) {
            message.addReaction(Emoji.fromUnicode("U+003" + c + "U+FE0F U+20E3")).queue();
        }
    }

    private void addCheckmarkReaction(Message message) {
        message.addReaction(Emoji.fromUnicode("U+2705")).queue();
    }

    private void addCrossReaction(Message message) {
        message.addReaction(Emoji.fromUnicode("U+274C")).queue();
    }

    private void addStarReaction(Message message) {
        message.addReaction(Emoji.fromUnicode("U+2B50")).queue();
    }
}
