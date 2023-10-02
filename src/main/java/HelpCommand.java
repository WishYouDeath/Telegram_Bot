import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

public class HelpCommand extends Command {
    @Override
    public void execute(Update update) {
        String message = "Список команд:\n";
        message += "/help - Вывести список команд\n";
        message += "/authors - Список авторов\n";
        // Добавьте другие команды по аналогии

        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText(message);

        try {
            MyTelegramBot bot = new MyTelegramBot(); // Создайте объект бота
            bot.execute(response); // Вызовите метод execute через объект бота для отправки сообщения
        } catch (TelegramApiException E){
            E.printStackTrace();
        }
    }
}
