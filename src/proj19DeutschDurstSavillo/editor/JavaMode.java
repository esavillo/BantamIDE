/*
 * File: JavaMode.java
 * F18 CS361 Project 15
 * Names: Martin Deutsch, Rob Durst, Evan Savillo
 * Date: 3/22/2019
 * This file contains the MipsMode class, which implements the
 * HighlightingMode interface to handle syntax highlighting for Bantam Java.
 */

package proj19DeutschDurstSavillo.editor;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import proj19DeutschDurstSavillo.interfaces.HighlightingMode;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JavaMode handles syntax highlighting for Bantam Java
 *
 * @author Evan Savillo
 * @author Rob Durst
 * @author Martin Deutsch
 */
public final class JavaMode implements HighlightingMode
{
    /**
     * a list of key words to be highlighted
     */
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "alias", "boolean", "break", 
            "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "var"
    };

    /**
     * regular expressions of characters to be highlighted
     */
    private static final String KEYWORD_PATTERN   = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN     = "\\(|\\)";
    private static final String BRACE_PATTERN     = "\\{|\\}";
    private static final String BRACKET_PATTERN   = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN    = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN   = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
    private static final String INTEGER_PATTERN   = "(?<![\\w])(?<![\\d.])[0-9]+(?![\\d.])(?![\\w])";

    /**
     * patterns to be highlighted
     */
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<INTEGER>" + INTEGER_PATTERN + ")"
    );

    /**
     * Computes the highlighting of substrings of text to return the style of each substring.
     *
     * @param text string to compute highlighting of
     * @return StyleSpans Collection Object
     */
    @Override
    public StyleSpans<Collection<String>> computeHighlighting(String text)
    {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        Matcher matcher   = PATTERN.matcher(text);
        int     lastKwEnd = 0;

        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            matcher.group("INTEGER") != null ? "integer" :
                                                                                    null; /* never happens */
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }
}
