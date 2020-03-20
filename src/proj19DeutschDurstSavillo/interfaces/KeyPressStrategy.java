package proj19DeutschDurstSavillo.interfaces;

import javafx.scene.input.KeyEvent;
import proj19DeutschDurstSavillo.editor.CustomCodeArea;


/**
 * Some strategy to execute on a javaCodeArea given a keypress
 */
public interface KeyPressStrategy
{
    /**
     * Describes what must be performed on a given customCodeArea given a keypress
     *
     * @param customCodeArea - the area on which the strategy is executed
     * @param event - the key press event
     */
    void execute(CustomCodeArea customCodeArea, KeyEvent event);
}
