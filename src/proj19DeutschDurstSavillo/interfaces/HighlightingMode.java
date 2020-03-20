/*
 * File: HighlightingMode.java
 * F18 CS361 Project 15
 * Names: Martin Deutsch, Rob Durst, Evan Savillo
 * Date: 3/22/2019
 * This file contains the HighlightingMode interface
 * implemented by syntax highlighting objects.
 */

package proj19DeutschDurstSavillo.interfaces;

import org.fxmisc.richtext.model.StyleSpans;

import java.util.Collection;


public interface HighlightingMode
{
    StyleSpans<Collection<String>> computeHighlighting(String text);
}
