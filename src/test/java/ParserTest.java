
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import JSON.*;
public class ParserTest {
    @Test
    public void testIsTeamMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode matchNode = objectMapper.readTree("{\"home_team\": {\"name_translations\": {\"ru\": \"Хозяева\", \"en\": \"Hosts\"}},\"away_team\": {\"name_translations\": {\"ru\": \"Гости\", \"en\": \"Guests\"}}}");
        Example example = objectMapper.treeToValue(matchNode, Example.class);
        assertTrue(Parser.isTeamMatch("Хозяева", example));
        assertTrue(Parser.isTeamMatch("Hosts", example));
        assertTrue(Parser.isTeamMatch("Гости", example));
        assertTrue(Parser.isTeamMatch("Guests", example));
        assertFalse(Parser.isTeamMatch("Ливерпуль", example));
        assertFalse(Parser.isTeamMatch("Liverpool", example));
        assertFalse(Parser.isTeamMatch("Бразилия", example));
        assertFalse(Parser.isTeamMatch("Brazil", example));
    }

    @Test
    public void testIsTeamNameMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode teamNode = objectMapper.readTree("{\"home_team\": {\"name_translations\": {\"ru\": \"Хозяева\", \"en\": \"Hosts\"}},\"away_team\": {\"name_translations\": {\"ru\": \"Гости\", \"en\": \"Guests\"}}}");
        Example match = objectMapper.treeToValue(teamNode, Example.class);
        assertTrue(Parser.isTeamNameMatch("Хозяева", match.getHomeTeam(), match.getAwayTeam(), "ru"));
        assertTrue(Parser.isTeamNameMatch("Hosts", match.getHomeTeam(), match.getAwayTeam(), "en"));
        assertFalse(Parser.isTeamNameMatch("Guests", match.getHomeTeam(), match.getAwayTeam(), "zn"));
        assertFalse(Parser.isTeamNameMatch("Русь", match.getHomeTeam(), match.getAwayTeam(), "ru"));
        //
    }

    //для тестирования метода ресив дата шлепнуть 2 jsona один где data: 2 матча(с поялми джсона как в тесте сверху) а другой где дата пустая подключаться к сайту не надо
    // тести мвложенный трай- кетч скип до 61 строки ,
}