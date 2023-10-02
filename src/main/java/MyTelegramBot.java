import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MyTelegramBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {//Здесь всё что мы получаем от юзера при общении с ботом
        //System.out.println(update.getMessage().getText());// Получаем сообщения которые вводят боту
        //System.out.println(update.getMessage().getFrom().getFirstName());// Получаем ник того кто вводит

        String command = update.getMessage().getText();
        if(command.equals("/help")){
            String message = "Пока что команд нет";
            SendMessage response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(message);

            try {
                execute(response);
            } catch (TelegramApiException E){
                E.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        // TODO
        return "CurrencyExchangerProjectBot";
    }

    @Override
    public String getBotToken() {
        // TODO
        return "6416187841:AAHmmO5gFJ_UERT5LNrQdxXb6dNJZaAKlts";
    }
}
