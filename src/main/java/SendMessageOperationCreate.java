import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import static constant.Commands.*;
import static java.util.Arrays.asList;

public class SendMessageOperationCreate {
    private final ButtonService buttonService = new ButtonService();
    public SendMessage createGreetingInformation(Message message){ // start
        return createSimpleMessage(message, "Привет, я - бот, который знает расписание игровых событий!\n" +
                        "Введи команду /help для того чтобы узнать доступные команды");
    }
    public SendMessage createAuthorsInformation(Message message){ // authors
        return createSimpleMessage(message, "Список авторов: \nЕгор,\nИван");
    }
    public SendMessage createHelpInformation(Message message){ // help
        SendMessage returnedMessage = createSimpleMessage(message, "Мой функционал заключается в этих командах: \n" +
                "/help - Выводит все существующие команды\n" +
                "/about - Рассказывает об основных возможностях бота\n" +
                "/authors - Показывает разработчиков бота\n" +
                "/start - Приветствует пользователя\n" +
                "/get - Получить расписание спортивного матча");
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(buttonService.createButtons(asList(HELP,ABOUT,AUTHORS,START,GET)));
        returnedMessage.setReplyMarkup(keyboardMarkup);
        return returnedMessage;
    }
    public SendMessage createBotInformation(Message message){ // about
        return createSimpleMessage(message, "Я умею показывать расписание игр и даже знаю результаты некоторых из них!");
    }
    public SendMessage getTimeTable(Message message, String teamName) {
        Parser parsing = new Parser();
        String timetable = parsing.receiveData(teamName);
        return createSimpleMessage(message, timetable);
    }
    public SendMessage wrongCommand(Message message){
        return createSimpleMessage(message, "Неизвестная команда!\nПопробуйте найти нужную команду используя /help");
    }
    public SendMessage createSimpleMessage(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        return sendMessage;
    }
}
