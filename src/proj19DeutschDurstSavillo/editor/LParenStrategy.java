package proj19DeutschDurstSavillo.editor;

import javafx.scene.input.KeyEvent;
import org.reactfx.util.FxTimer;
import proj19DeutschDurstSavillo.interfaces.KeyPressStrategy;

import java.time.Duration;


public class LParenStrategy implements KeyPressStrategy
{
    /**
     * Writes out a matching right parenthesis
     */
    @Override
    public void execute(CustomCodeArea customCodeArea, KeyEvent event)
    {
        // wait for original key to type & appear before auto-closing
        FxTimer.runLater(Duration.ofMillis(100), () ->
        {
            int caretPosition = customCodeArea.getCaretPosition();
            customCodeArea.insertText(caretPosition, ")");
            customCodeArea.moveTo(caretPosition);
        });
    }
}
