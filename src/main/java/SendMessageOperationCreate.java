import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.Map;

import static constant.Commands.*;
import static java.util.Arrays.asList;

public class SendMessageOperationCreate {
    private final ButtonService buttonService = new ButtonService();

    public SendMessage createGreetingInformation(Message message) { // start
        return createSimpleMessage(message, "Привет, я - бот, который знает расписание игровых событий!\n" +
                "Введи команду /help для того чтобы узнать доступные команды");
    }

    public SendMessage createAuthorsInformation(Message message) { // authors
        return createSimpleMessage(message, "Список авторов: \nЕгор,\nИван");
    }

    public SendMessage createHelpInformation(Message message) { // help
        SendMessage returnedMessage = createSimpleMessage(message, "Мой функционал заключается в этих командах: \n" +
                "/help - Выводит все существующие команды\n" +
                "/about - Рассказывает об основных возможностях бота\n" +
                "/authors - Показывает разработчиков бота\n" +
                "/start - Приветствует пользователя\n" +
                "/get - Получить расписание спортивного матча\n" +
                "/category - Установить спортивную категорию\n" +
                "/date - Установить дату спортивного события\n" +
                "/setNotification - Установить уведомление. Для корректной работы нужно выбрать спортивную категорию " +
                "и дату того матча, на который собираетесь поставить уведомление ");
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(buttonService.createButtons(asList(HELP, ABOUT, AUTHORS, START, GET, CATEGORY)));
        returnedMessage.setReplyMarkup(keyboardMarkup);
        return returnedMessage;
    }

    public SendMessage createBotInformation(Message message) { // about
        return createSimpleMessage(message, "Я умею показывать расписание спортивных матчей в различных категориях " +
                "а также я умею отправлять уведомления на спортивные матчи!");
    }

    public SendMessage createChooseDateMessage(Message message) {
        SendMessage sendMessage = createSimpleMessage(message, "Установите дату матча, который вы ищете\n" +
                "Установка даты должна быть в формате UTC00, то есть -3 часа от Москвы");
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(buttonService.createButtons(asList(TODAY, TOMORROW, YESTERDAY, OTHER_DATE)));
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }
    public SendMessage setNotificationTime(Message message) {
        SendMessage sendMessage = createSimpleMessage(message, "Выберете, когда вы хотите получить уведомление:\n" +
                "За 5 минут до начала матча, или по окончанию матча?");
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(buttonService.createButtons(asList(BEFORE, AFTER)));
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage createChooseCategoryMessage(Message message) {
        SendMessage sendMessage = createSimpleMessage(message, "Установите категорию");
        ReplyKeyboardMarkup keyboardMarkup =
                buttonService.setButtons(buttonService.createButtons(asList("Футбол", "Теннис", "Баскетбол", "Хоккей", "Волейбол", "Гандбол")));
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage getTimeTable(Message message, String teamName, Map<Long, String> userSelectedCategoryMap, Map<Long, String> userSelectedDateMap) {
        Parser parsing = new Parser();
        String selectedDate = userSelectedDateMap.get(message.getChatId());
        String category = userSelectedCategoryMap.get(message.getChatId());
        String timetable = parsing.receiveData(teamName, category, selectedDate, Parser.getExample());
        return createSimpleMessage(message, timetable);
    }

    public SendMessage wrongCommand(Message message) {
        return createSimpleMessage(message, "Неизвестная команда!\nПопробуйте найти нужную команду используя /help");
    }

    public static SendMessage createSimpleMessage(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(message.getChatId()));
        sendMessage.setText(text);
        return sendMessage;
    }
}
