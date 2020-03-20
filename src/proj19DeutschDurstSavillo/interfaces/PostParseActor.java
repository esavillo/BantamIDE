/*
 * File: PostParseActor.java
 * Names: Evan Savillo and Robert Durst
 * Class: CS461
 * Project 11
 * Date: February 13
 */

package proj19DeutschDurstSavillo.interfaces;

import proj19DeutschDurstSavillo.bantam.ast.ASTNode;

import java.util.Map;


/**
 * Represents an object with the ability to perform some kind of action
 * on a parsed abstract syntax tree, or potentially some subtree thereof.
 */
public interface PostParseActor
{
    /**
     * Act on a tree with some knowledge.
     *
     * @param root      the root node of some abstract syntax tree.
     * @param knowledge a map detailing some ambiguous provided knowledge.
     *                  The implementing class should know what knowledge it needs
     *                  via string key.
     * @return some kind of text response perhaps detailing the results
     * of having visited the tree.
     */
    Object act(ASTNode root, Map<String, Object> knowledge);
}
