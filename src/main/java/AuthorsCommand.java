import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AuthorsCommand extends Command {
    @Override
    public void execute(Update update) {
        String message = "Список авторов:\n";
        message += "Егор\n";
        message += "Иван\n";


        SendMessage response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText(message);

        try {
            MyTelegramBot bot = new MyTelegramBot(); // Создать объект бота
            bot.execute(response); // Вызвать метод execute через объект бота для отправки сообщения
        } catch (TelegramApiException E){
            E.printStackTrace();
        }
    }
}

