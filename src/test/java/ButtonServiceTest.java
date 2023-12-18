import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ButtonServiceTest {
    private final ButtonService buttonService = new ButtonService();

    @Test
    public void testSetButtons() {
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("Button1"));
        keyboardRow.add(new KeyboardButton("Button2"));
        keyboardRowList.add(keyboardRow);

        ReplyKeyboardMarkup replyKeyboardMarkup = buttonService.setButtons(keyboardRowList);

        assertEquals(1, replyKeyboardMarkup.getKeyboard().size());
        assertEquals(2, replyKeyboardMarkup.getKeyboard().get(0).size());
        assertEquals("Button1", replyKeyboardMarkup.getKeyboard().get(0).get(0).getText());
        assertEquals("Button2", replyKeyboardMarkup.getKeyboard().get(0).get(1).getText());
    }

    @Test
    public void testCreateButtons() {
        List<String> buttonsName = new ArrayList<>();
        buttonsName.add("Button1");
        buttonsName.add("Button2");

        List<KeyboardRow> keyboardRows = buttonService.createButtons(buttonsName);

        assertEquals(1, keyboardRows.size());
        assertEquals(2, keyboardRows.get(0).size());
        assertEquals("Button1", keyboardRows.get(0).get(0).getText());
        assertEquals("Button2", keyboardRows.get(0).get(1).getText());
    }
}