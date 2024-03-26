package org.codered.neolithic.images.processing;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class ConversionRefiner {
    private static final Pattern NON_WORD_PATTERN = Pattern.compile("[^\\w\\s]");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s+");
    private static final Pattern LINE_BREAK_PATTERN = Pattern.compile("\\n");
    private static final JLanguageTool LANGUAGE_TOOL = new JLanguageTool(new AmericanEnglish());

    public String refineConversion(String text) {
        // Text cleanup
        String cleanedText = cleanText(text);

        // Perform spell-checking and correction
        cleanedText = spellCheck(cleanedText);

        // Perform grammar correction
        cleanedText = grammarCorrection(cleanedText);

        return cleanedText.trim();
    }

    private String cleanText(String text) {
        // Remove non-word characters
        String cleanedText = NON_WORD_PATTERN.matcher(text).replaceAll(" ");

        // Replace multiple spaces with a single space
        cleanedText = MULTIPLE_SPACES_PATTERN.matcher(cleanedText).replaceAll(" ");

        // Replace line breaks with spaces
        cleanedText = LINE_BREAK_PATTERN.matcher(cleanedText).replaceAll(" ");

        return cleanedText;
    }

    private String spellCheck(String text) {
        StringBuilder newText = new StringBuilder();
        String[] words = text.split(" "); // Split into individual words

        try {
            for (String word : words) {
                List<RuleMatch> matches = LANGUAGE_TOOL.check(word);

                if (!matches.isEmpty() && matches.get(0).getSuggestedReplacements().size() > 0) {
                    // A potential misspelling with suggestions
                    newText.append(matches.get(0).getSuggestedReplacements().get(0)).append(" ");
                } else {
                    // No misspellings detected
                    newText.append(word).append(" ");
                }
            }
        } catch (IOException e) {
            // Log the error or handle it appropriately
            System.err.println("Error during spell check: " + e.getMessage());
        }

        return newText.toString().trim();
    }


    private String grammarCorrection(String text) {
        try {
            List<RuleMatch> matches = LANGUAGE_TOOL.check(text);
            for (RuleMatch match : matches) {
                if (match.getSuggestedReplacements().size() > 0) {
                    text = text.replaceAll(match.getFromPos() + "(?=" + Pattern.quote(String.valueOf(match.getToPos())) + ")", match.getSuggestedReplacements().get(0));
                }
            }
        } catch (IOException e) {
            System.err.println("Error during grammar check: " + e.getMessage());
        }
        return text;
    }
}
