import JSON.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MatchDataUtilTest {

    @Test
    public void testProcessMatchDataFinished() {
        Example example = new Example();
        example.setStatus("finished");
        example.setHomeTeam(new HomeTeam("Home Team"));
        example.setAwayTeam(new AwayTeam("Away Team"));
        example.setLeague(new League("League"));
        example.setName(new HomeScore(2));
        example.setAwayScore(new AwayScore(1));
        String category = "Category";

        String expected = "Выбранная категория:" + category + "\nМатч в лиге: 'League' завершился\nHome Team\t2:1\tAway Team\nМатч был Н/Д";
        String result = MatchDataUtil.processMatchData(example, category);

        assertEquals(expected, result);
    }

    @Test
    public void testProcessMatchDataNotStarted() {
        Example example = new Example();
        example.setStatus("notstarted");
        example.setHomeTeam(new HomeTeam("Home Team"));
        example.setAwayTeam(new AwayTeam("Away Team"));
        example.setLeague(new League("League"));
        String category = "Category";

        String expected = "Выбранная категория:" + category + "\nМатч в лиге: 'League' ещё не начался\nМатч 'Home Team : Away Team' будет в это время: Н/Д\n";
        String result = MatchDataUtil.processMatchData(example, category);

        assertEquals(expected, result);
    }

    @Test
    public void testProcessMatchDataInProgress() {
        Example example = new Example();
        example.setStatus("inprogress");
        example.setHomeTeam(new HomeTeam("Home Team"));
        example.setAwayTeam(new AwayTeam("Away Team"));
        example.setLeague(new League("League"));
        example.setStatusMore("1st half");
        example.setHomeScore(new Score(1));
        example.setAwayScore(new Score(0));
        String category = "Category";

        String expected = "Выбранная категория:" + category + "\nМатч Home Team : Away Team в лиге: 'League' уже начался\nСейчас в матче первый период\nТекущий счёт 1:0";
        String result = MatchDataUtil.processMatchData(example, category);

        assertEquals(expected, result);
    }

    @Test
    public void testProcessMatchDataPostponed() {
        Example example = new Example();
        example.setStatus("postponed");
        example.setHomeTeam(new HomeTeam("Home Team"));
        example.setAwayTeam(new AwayTeam("Away Team"));
        example.setLeague(new League("League"));
        String category = "Category";

        String expected = "Выбранная категория:" + category + "\nМатч между Home Team и Away Team в лиге: 'League' был отложен\n";
        String result = MatchDataUtil.processMatchData(example, category);

        assertEquals(expected, result);
    }

    @Test
    public void testProcessMatchDataUnknownStatus() {
        Example example = new Example();
        example.setStatus("unknown");
        String category = "Category";

        String expected = "Неизвестный статус матча";
        String result = MatchDataUtil.processMatchData(example, category);

        assertEquals(expected, result);
    }
}