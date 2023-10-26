import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    @Test
    public void testIsTeamMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode matchNode = objectMapper.readTree("{\"home_team\": {\"name_translations\": {\"ru\": \"Хозяева\", \"en\": \"Hosts\"}},\"away_team\": {\"name_translations\": {\"ru\": \"Гости\", \"en\": \"Guests\"}}}");

        assertTrue(Parser.isTeamMatch("Хозяева", matchNode));
        assertTrue(Parser.isTeamMatch("Hosts", matchNode));
        assertTrue(Parser.isTeamMatch("Гости", matchNode));
        assertTrue(Parser.isTeamMatch("Guests", matchNode));
        assertFalse(Parser.isTeamMatch("Ливерпуль", matchNode));
        assertFalse(Parser.isTeamMatch("Liverpool", matchNode));
        assertFalse(Parser.isTeamMatch("Бразилия", matchNode));
        assertFalse(Parser.isTeamMatch("Brazil", matchNode));
    }

    @Test
    public void testIsTeamNameMatch() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode teamNode = objectMapper.readTree("{\"name_translations\": {\"ru\": \"Хозяева\", \"en\": \"Hosts\"}}");

        assertTrue(Parser.isTeamNameMatch("Хозяева", teamNode, "ru"));
        assertTrue(Parser.isTeamNameMatch("Hosts", teamNode, "en"));
        assertFalse(Parser.isTeamNameMatch("Гости", teamNode, "ru"));
        assertFalse(Parser.isTeamNameMatch("Guests", teamNode, "en"));
    }
}