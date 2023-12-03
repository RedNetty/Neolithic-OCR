package org.codered.neolithic.images.processing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConversionRefiner {


    public ConversionRefiner() {

    }

    public String refineConversion(String text) {
        return correctOcrOutput(text);
    }

    private String correctOcrOutput(String ocrOutput) {
        String complexPattern = buildComplexPattern();
        Pattern pattern = Pattern.compile(complexPattern);
        Matcher matcher = pattern.matcher(ocrOutput);

        StringBuffer correctedOutput = new StringBuffer();
        while (matcher.find()) {
            String match = matcher.group();
            String correctedMatch = analyzeAndCorrectExpression(match);
            matcher.appendReplacement(correctedOutput, correctedMatch);
        }
        matcher.appendTail(correctedOutput);

        return correctedOutput.toString();
    }

    private String buildComplexPattern() {
        // Patterns for various mathematical elements
        String numberPattern = "([0-9]+\\.?[0-9]*|\\([0-9]+\\.?[0-9]*\\))";
        String operatorPattern = "([-+*/^:])";
        String functionPattern = "(sin|cos|tan|log|ln|sqrt)";
        String complexElementPattern = String.format("(%s|%s)", numberPattern, functionPattern);

        // Complex patterns for combinations of elements
        String binaryOperationPattern = complexElementPattern + "\\s*" + operatorPattern + "\\s*" + complexElementPattern;
        String functionWithArgumentPattern = functionPattern + "\\s*\\(" + complexElementPattern + "\\)";

        // Combine all patterns
        return String.join("|", binaryOperationPattern, functionWithArgumentPattern);
    }

    private String analyzeAndCorrectExpression(String expression) {
        // Analyze the structure of the expression and apply corrections based on its components
        // This is a simplified version of what a parser might do

        // Example: Replace ":" with "/" in division-like patterns
        String correctedExpression = expression.replaceAll("(?<=\\d)\\s*:\\s*(?=\\d)", "/");

        // Handle other specific cases, such as functions, exponents, and so on
        // Additional complex logic can be added here

        // Normalize spaces and other formatting issues
        correctedExpression = correctedExpression.replaceAll("\\s+", "");

        return correctedExpression;
    }


}
