package proj19DeutschDurstSavillo.editor;

import javafx.scene.input.KeyEvent;
import org.reactfx.util.FxTimer;
import proj19DeutschDurstSavillo.interfaces.KeyPressStrategy;

import java.time.Duration;


/**
 * The strategy for when a right parenthesis, bracket, or brace is typed.
 */
public class CloseElectricStrategy implements KeyPressStrategy
{
    /**
     * If a right parenthesis, bracket, or brace has been typed, and
     * the next character on the screen is also one of the aforementioned,
     * then we effectively skip past it.
     */
    @Override
    public void execute(CustomCodeArea customCodeArea, KeyEvent event)
    {
        // wait for original key to type & appear before auto-closing
        FxTimer.runLater(Duration.ofMillis(100), () ->
        {
            int    caretPosition = customCodeArea.getCaretPosition();
            String nextChar      = customCodeArea.getText(caretPosition, caretPosition + 1);

            if (")".equals(nextChar) || "]".equals(nextChar) || "}".equals(nextChar)) {
                customCodeArea.deleteText(caretPosition, caretPosition + 1);
            }
        });
    }
}
