import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
public class SendMessageOperationCreate {
    public SendMessage createGreetingInformation(Message message){ // start
        Parser parsing = new Parser();
        parsing.receiveData();
        return createSimpleMessage(message, "Привет, я бот, который знает расписание игровых событий!");

    }
    /*public SendMessage createParseTime(Message message){ // start
        String timetable =
        return createSimpleMessage(message, timetable);
    }*/
    public SendMessage createAuthorsInformation(Message message){ // authors
        return createSimpleMessage(message, "Список авторов: \nЕгор,\nИван");
    }
    public SendMessage createHelpInformation(Message message){ // help
        return createSimpleMessage(message, "Мой функционал заключается в этих командах: \n" +
                "/help - Выводит все существующие команды\n" +
                "/about - Рассказывает об основных возможностях бота\n" +
                "/authors - Показывает разработчиков бота\n" +
                "/start - Приветствует пользователя\n");
    }
    public SendMessage createBotInformation(Message message){ // about
        return createSimpleMessage(message, "Я умею показывать расписание игр и даже знаю результаты некоторых из них!");
    }
    public SendMessage wrongCommand(Message message){
        return createSimpleMessage(message, "Неизвестная команда!\nПопробуйте найти нужную команду в /help");
    }
    private SendMessage createSimpleMessage(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        return sendMessage;
    }
}
