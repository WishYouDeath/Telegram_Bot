package JSON;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "home_team",
        "away_team",
        "start_at",
        "sport",
        "league",
        "away_score",
        "home_score",
        "status",
        "data",
})
@Generated("jsonschema2pojo")
public class Match {

    @JsonProperty("home_team")
    private Home_team homeTeam;
    @JsonProperty("away_team")
    private Away_team awayTeam;
    @JsonProperty("start_at")
    private String startAt;
    @JsonProperty("sport")
    private Sport sport;
    @JsonProperty("league")
    private League league;
    @JsonProperty("away_score")
    private Away_score awayScore;
    @JsonProperty("home_score")
    private Home_score homeScore;
    @JsonProperty("status")
    private String status;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("home_team")
    public Home_team getHomeTeam() {
        return homeTeam;
    }

    @JsonProperty("home_team")
    public void setHomeTeam(Home_team homeTeam) {
        this.homeTeam = homeTeam;
    }

    @JsonProperty("away_team")
    public Away_team getAwayTeam() {
        return awayTeam;
    }

    @JsonProperty("away_team")
    public void setAwayTeam(Away_team awayTeam) {
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

    @JsonProperty("sport")
    public Sport getSport() {
        return sport;
    }

    @JsonProperty("sport")
    public void setSport(Sport sport) {
        this.sport = sport;
    }

    @JsonProperty("league")
    public League getLeague() {
        return league;
    }

    @JsonProperty("league")
    public void setLeague(League league) {
        this.league = league;
    }

    @JsonProperty("away_score")
    public Away_score getAwayScore() {
        return awayScore;
    }

    @JsonProperty("away_score")
    public void setAwayScore(Away_score awayScore) {
        this.awayScore = awayScore;
    }

    @JsonProperty("home_score")
    public Home_score getHomeScore() {
        return homeScore;
    }

    @JsonProperty("home_score")
    public void setHomeScore(Home_score homeScore) {
        this.homeScore = homeScore;
    }
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
}

