import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import java.util.HashMap;
import java.util.Map;

public class MyTelegramBot extends TelegramLongPollingBot {
    private Map<String, Command> commands = new HashMap<>();

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
        // Укажите имя вашего бота
        return "SportsTimetableBot";
    }

    @Override
    public String getBotToken() {
        // Укажите токен вашего бота
        return "6521790062:AAHBfHm6N_EZFjrZbkBTKejJZnPMmlwPsyk";
    }

}

