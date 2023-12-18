import JSON.AwayTeam;
import JSON.HomeTeam;
import JSON.League;
import JSON.HomeScore;
import JSON.AwayScore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import JSON.Example;
public class MatchDataUtilTest {
    @Test
    public void testProcessMatchDataFinished() {
        Example example = mock(Example.class);
        when(example.getHomeTeam()).thenReturn(new HomeTeam("Home Team", null));
        when(example.getAwayTeam()).thenReturn(new AwayTeam("Away Team", null));
        when(example.getLeague()).thenReturn(new League("League", null));
        when(example.getHomeScore()).thenReturn(new HomeScore(2, null));
        when(example.getAwayScore()).thenReturn(new AwayScore(1, null));
        when(example.getStatus()).thenReturn("finished");
        when(example.getStatusMore()).thenReturn("2nd half");
        when(DateTimeUtil.getDate(example)).thenReturn("2023-01-01 12:00:00");

        String result = MatchDataUtil.processMatchData(example, "TestCategory");

        assertEquals("Выбранная категория:TestCategory\nМатч в лиге: 'League' завершился\nHome Team\t2:1\tAway Team\nМатч был второй период", result);
    }

    @Test
    public void testProcessMatchDataNotStarted() {
        // Mocking the Example object
        Example example = mock(Example.class);
        when(example.getHomeTeam()).thenReturn(new HomeTeam("Home Team", null));
        when(example.getAwayTeam()).thenReturn(new AwayTeam("Away Team", null));
        when(example.getLeague()).thenReturn(new League("League", null));
        when(example.getStatus()).thenReturn("notstarted");
        when(DateTimeUtil.getDate(example)).thenReturn("2023-01-01 12:00:00");

        String result = MatchDataUtil.processMatchData(example, "TestCategory");

        assertEquals("Выбранная категория:TestCategory\nМатч в лиге: 'League' ещё не начался\nМатч 'Home Team : Away Team' будет в это время: 2023-01-01 12:00:00\n", result);
    }
}
