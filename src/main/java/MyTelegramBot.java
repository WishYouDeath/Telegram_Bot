import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {
    private Map<String, Command> commands = new HashMap<>();
    String nameBot = System.getenv("Telegram_Name");
    String apiBot = System.getenv("Telegram_API");
    public MyTelegramBot() {
        // Регистрируем команды
        commands.put("/help", new HelpCommand());
        commands.put("/authors", new AuthorsCommand());
        // Добавить другие команды по аналогии
    }

    @Override
    public void onUpdateReceived(Update update) {
        String commandText = update.getMessage().getText();
        Command command = commands.get(commandText);
        if (command != null) {
            command.execute(update);
        } else {
            System.out.println("Неизвестная команда!");
        }
    }
    @Override
    public String getBotUsername() {
        return nameBot;
    }
    @Override
    public String getBotToken() {
        return apiBot;
    }

}

