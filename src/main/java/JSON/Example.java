package JSON;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "status",
        "status_more",
        "home_team",
        "away_team",
        "start_at",
        "home_score",
        "away_score",
        "league",
        "sport",
        "name_translations"
})
@Generated("jsonschema2pojo")
public class Example {
    @JsonProperty("name_translations")
    private Map<String, String> nameTranslations;
    @JsonProperty("name")
    private String name;
    @JsonProperty("status")
    private String status;
    @JsonProperty("status_more")
    private String statusMore;
    @JsonProperty("home_team")
    private HomeTeam homeTeam;
    @JsonProperty("away_team")
    private AwayTeam awayTeam;
    @JsonProperty("start_at")
    private String startAt;
    @JsonProperty("home_score")
    private HomeScore homeScore;
    @JsonProperty("away_score")
    private AwayScore awayScore;
    @JsonProperty("league")
    private League league;
    @JsonProperty("sport")
    private Sport sport;
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
    @JsonProperty("status_more")
    public String getStatusMore() {
        return statusMore;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
    @JsonProperty("status_more")
    public void setStatusMore(String statusMore) {
        this.statusMore = statusMore;
    }


    @JsonProperty("home_team")
    public HomeTeam getHomeTeam() {
        return homeTeam;
    }

    @JsonProperty("home_team")
    public void setHomeTeam(HomeTeam homeTeam) {
        this.homeTeam = homeTeam;
    }

    @JsonProperty("away_team")
    public AwayTeam getAwayTeam() {
        return awayTeam;
    }

    @JsonProperty("away_team")
    public void setAwayTeam(AwayTeam awayTeam) {
        this.awayTeam = awayTeam;
    }

    @JsonProperty("start_at")
    public String getStartAt() {
        return startAt;
    }

    @JsonProperty("start_at")
    public void setStartAt(String startAt) {
        this.startAt = startAt;
    }

    @JsonProperty("home_score")
    public HomeScore getHomeScore() {
        return homeScore;
    }

    @JsonProperty("home_score")
    public void setHomeScore(HomeScore homeScore) {
        this.homeScore = homeScore;
    }

    @JsonProperty("away_score")
    public AwayScore getAwayScore() {
        return awayScore;
    }

    @JsonProperty("away_score")
    public void setAwayScore(AwayScore awayScore) {
        this.awayScore = awayScore;
    }

    @JsonProperty("league")
    public League getLeague() {
        return league;
    }

    @JsonProperty("league")
    public void setLeague(League league) {
        this.league = league;
    }
    @JsonProperty("sport")
    public Sport getSport() {
        return sport;
    }
    @JsonProperty("name_translations")
    public Map<String, String> getNameTranslations() {
        return nameTranslations;
    }

    @JsonProperty("name_translations")
    public void setNameTranslations(Map<String, String> nameTranslations) {
        this.nameTranslations = nameTranslations;
    }
    @JsonProperty("sport")
    public void setSport(Sport sport) {
        this.sport = sport;
    }
}