package proj19DeutschDurstSavillo.editor;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import proj19DeutschDurstSavillo.interfaces.KeyPressStrategy;


/**
 * A strategist which maintains various strategies to be enacted on keypresses.
 */
public class KeyPressStrategist
{
    private AutoCompletionStrategy autoCompletionStrategy;
    private LParenStrategy         lParenStrategy;
    private LBraceStrategy         lBraceStrategy;
    private LBracketStrategy       lBracketStrategy;
    private CloseElectricStrategy  closeElectricStrategy;

    public KeyPressStrategist()
    {
        this.autoCompletionStrategy = new AutoCompletionStrategy();
        this.lParenStrategy = new LParenStrategy();
        this.lBraceStrategy = new LBraceStrategy();
        this.lBracketStrategy = new LBracketStrategy();
        this.closeElectricStrategy = new CloseElectricStrategy();
    }

    /**
     * Determines if a strategy that auto-finishes a left parenthesis,
     * bracket, or brace should be given in return.
     */
    public KeyPressStrategy getElectricStrategy(KeyEvent event)
    {
        String key = event.getText();

        if ("(".equals(key)) {
            return this.lParenStrategy;
        } else if ("[".equals(key)) {
            return this.lBracketStrategy;
        } else if ("{".equals(key)) {
            return this.lBraceStrategy;
        } else if (")".equals(key) || "]".equals(key) || "}".equals(key)) {
            return this.closeElectricStrategy;
        }

        return null;
    }

    /**
     * Determines if an autocompletion window strategy should be
     * given in return
     */
    public AutoCompletionStrategy getAutoCompletionStrategy(KeyEvent event)
    {
        char[] key = event.getText().toCharArray();

        if ((key.length == 1 && isAlphaNum_(key[0]) || (event.getCode().equals(KeyCode.BACK_SPACE)))) {
            return this.autoCompletionStrategy;
        }

        return null;
    }

    /**
     * Checks if a char is an alphanumeric character or underscore
     */
    private static boolean isAlphaNum_(char c)
    {
        boolean isLowerAlpha = (c >= 'a' && c <= 'z');
        boolean isUpperAlpha = (c >= 'A' && c <= 'Z');
        boolean isDigit      = (c >= '0' && c <= '9');
        boolean isUnderscore = (c == '_');

        return isLowerAlpha || isUpperAlpha || isDigit || isUnderscore;
    }
}
