import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            MyTelegramBot bot = new MyTelegramBot();
            botsApi.registerBot(bot);
            bot.startNotificationThread(); //Запускаем 2ой поток
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}