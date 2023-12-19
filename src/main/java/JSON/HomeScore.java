package JSON;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "current",
})
@Generated("jsonschema2pojo")
public class HomeScore{
    @JsonProperty("current")
    private Integer current;

    @JsonProperty("current")
    public Integer getCurrent() {
        return current;
    }

    @JsonProperty("current")
    public void setName(Integer current) {
        this.current = current;
    }
}