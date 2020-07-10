package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        String fileName;
        if (args.length == 0) {
            System.out.println("U need to pass existing file name as argument. As default program reads file: in.txt\n\n\n");
            fileName = "in.txt";
        } else {
            fileName = args[0];
        }
        File file = new File(fileName);
        StringBuilder sb = new StringBuilder();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                sb.append(scanner.nextLine()).append(" ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String text = sb.toString();
        int sentenceCount = text.trim().split("[.!?]").length;
        int wordsCount = text.split("\\s+").length;
        int charCount = text.replace(" ", "").length();
        int countSylls = 0;
        int countPolySylls = 0;
        String[] words = text.trim().split("\\s+");
        for (String w : words) {
            int syllsInWord = countSyllables(w);
            countSylls += syllsInWord;
            if (syllsInWord > 2) countPolySylls++;
        }
        double scoreARI = calcARI(charCount, wordsCount, sentenceCount);
        double scoreColLiau = calcColemanLiau(charCount, wordsCount, sentenceCount);
        double scoreSMOG = calcSMOG(sentenceCount, countPolySylls);
        double scoreFleshKin = calcFleschKincaid(wordsCount, sentenceCount, countSylls);
        System.out.printf("Words: %d\n", wordsCount);
        System.out.printf("Sentences: %d\n", sentenceCount);
        System.out.printf("Characters: %d\n", charCount);
        System.out.printf("Syllables: %d\n", countSylls);
        System.out.printf("Polysyllables: %d\n", countPolySylls);
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        String method = scanner.nextLine().trim();
        switch (method) {
            case "ARI":
                System.out.printf("Automated Readability Index: " + Math.round(scoreARI * 100) / 100.0
                        + " (about %s year olds).", upperBoundByScore(scoreARI));
                break;
            case "FK":
                System.out.printf("Flesch–Kincaid readability tests: " + Math.round(scoreFleshKin * 100) / 100.0
                        + " (about %s year olds).", upperBoundByScore(scoreFleshKin));
                break;
            case "SMOG":
                System.out.printf("Simple Measure of Gobbledygook: " + Math.round(scoreSMOG * 100) / 100.0
                        + " (about %s year olds).", upperBoundByScore(scoreSMOG));
                break;
            case "CL":
                System.out.printf("Coleman–Liau index: " + Math.round(scoreColLiau * 100) / 100.0
                        + " (about %s year olds).", upperBoundByScore(scoreColLiau));
                break;
            case "all":
                System.out.printf("\nAutomated Readability Index : " + Math.round(scoreARI * 100) / 100.0
                        + " (about %s year olds).\n", upperBoundByScore(scoreARI));
                System.out.printf("Flesch–Kincaid readability tests: " + Math.round(scoreFleshKin * 100) / 100.0
                        + " (about %s year olds).\n", upperBoundByScore(scoreFleshKin));
                System.out.printf("Simple Measure of Gobbledygook: " + Math.round(scoreSMOG * 100) / 100.0
                        + " (about %s year olds).\n", upperBoundByScore(scoreSMOG));
                System.out.printf("Coleman–Liau index: " + Math.round(scoreColLiau * 100) / 100.0
                        + " (about %s year olds).\n", upperBoundByScore(scoreColLiau));
                break;
        }
    }
    private static int countSyllables(String word) {
        word = word.trim().replaceAll("[.!?:,;'\"]", "");
        if (word.indexOf(' ') > -1) throw new IllegalArgumentException();
        if (word.charAt(word.length() - 1) == 'e') word = word.substring(0, word.length() - 1);
        int count = 0;
        Pattern pattern = Pattern.compile("[aeiouyAEIOUY]([^aeiouyAEIOUY]|\\b)");
        Matcher matcher = pattern.matcher(word);
        while (matcher.find()){
            count++;
        }

        return Math.max(1, count);
    }

    private static double calcColemanLiau  (int charCount, int wordsCount, int sentenceCount) {
        double l = 100.0 * charCount / wordsCount;
        double s = 100.0 * sentenceCount / wordsCount;
        return 0.0588 * l - 0.296 * s - 15.8;
    }
    private static double calcSMOG (int sentenceCount, int polySyllablesCount) {
        return 1.043 * Math.sqrt(polySyllablesCount * 30.0 / sentenceCount) + 3.1291;
    }
    private static double calcFleschKincaid (int wordsCount, int sentenceCount, int syllablesCount) {
        return 0.39 * wordsCount / sentenceCount + 11.8 * syllablesCount / wordsCount - 15.59;
    }

    private static double calcARI (int charCount, int wordsCount, int sentenceCount) {
        return 4.71 * charCount / wordsCount + 0.5 * wordsCount / sentenceCount - 21.43;
    }

    private static String ageRangeByScore (double score) {
        switch ((int) Math.round(Math.ceil(score))) {
            case 1:
                return "5-6";
            case 2:
                return "6-7";
            case 3:
                return "7-9";
            case 4:
                return "9-10";
            case 5:
                return "10-11";
            case 6:
                return "11-12";
            case 7:
                return "12-13";
            case 8:
                return "13-14";
            case 9:
                return "14-15";
            case 10:
                return "15-16";
            case 11:
                return "16-17";
            case 12:
                return "17-18";
            case 13:
                return "18-24";
            case 14:
                return "24+";
            default:
                return "<wrong data>";
        }
    }
    private static String upperBoundByScore (double score) {
        switch ((int) Math.round(Math.ceil(score))) {
            case 1:
                return "6";
            case 2:
                return "7";
            case 3:
                return "9";
            case 4:
                return "10";
            case 5:
                return "11";
            case 6:
                return "12";
            case 7:
                return "13";
            case 8:
                return "14";
            case 9:
                return "15";
            case 10:
                return "16";
            case 11:
                return "17";
            case 12:
                return "18";
            case 13:
                return "24";
            case 0:
                return "<wrong data>";
            default:
                return "24+";
        }
    }
}
