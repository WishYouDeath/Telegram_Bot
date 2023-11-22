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
        "current",
})
@Generated("jsonschema2pojo")
public class Home_score {
    @JsonProperty("current")
    private Integer current;
    @JsonIgnore
    private Map<Integer, Object> additionalProperties = new LinkedHashMap<Integer, Object>();

    @JsonProperty("current")
    public Integer getScore() {
        return current;
    }
    @JsonProperty("current")
    public void setScore(Integer score) {
        this.current = score;
    }


}
