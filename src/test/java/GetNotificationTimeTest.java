import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import JSON.Example;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.HashMap;
import java.util.Map;

public class GetNotificationTimeTest {

    @Test
    public void testParseDateFinished() {
        Example example = mock(Example.class);
        when(example.getStatus()).thenReturn("finished");
        when(example.getChatId()).thenReturn(123L);
        when(example.getStatus()).thenReturn("finished");
        when(example.getStatusMore()).thenReturn("2nd half");
        when(DateTimeUtil.getDate(example)).thenReturn("2023-01-01 12:00:00");

        Map<Long, String> userSelectedCategoryMap = new HashMap<>();
        userSelectedCategoryMap.put(123L, "TestCategory");
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);

        String result = GetNotificationTime.ParseDate(example, userSelectedCategoryMap, message);

        assertEquals("Выбранная категория:TestCategory\nМатч в лиге: 'League' завершился\nHome Team\t2:1\tAway Team\nМатч был второй период", result);
    }

    @Test
    public void testParseDateNotStarted() {
        Example example = mock(Example.class);
        when(example.getStatus()).thenReturn("notstarted");
        when(example.getStartAt()).thenReturn("2023-01-01 12:00:00");
        when(example.getChatId()).thenReturn(123L);
        when(example.getStatusMore()).thenReturn("1st half");
        when(DateTimeUtil.getDate(example)).thenReturn("2023-01-01 12:00:00");

        Map<Long, String> userSelectedCategoryMap = new HashMap<>();
        userSelectedCategoryMap.put(123L, "TestCategory");
        Message message = mock(Message.class);
        when(message.getChatId()).thenReturn(123L);

        String result = GetNotificationTime.ParseDate(example, userSelectedCategoryMap, message);

        assertEquals("Выбранная категория:TestCategory\nМатч в лиге: 'League' ещё не начался\nМатч 'Home Team : Away Team' будет в это время: 2023-01-01 12:00:00\n", result);
    }

}
