package JSON;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "nameTranslations",
})
@Generated("jsonschema2pojo")
public class AwayTeam{
    @JsonProperty("name")
    private String name;
    @JsonProperty("name_translations")
    private Map<String, String> nameTranslations;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("name_translations")
    public Map<String, String> getNameTranslations() {
        return nameTranslations;
    }

    @JsonProperty("name_translations")
    public void setNameTranslations(Map<String, String> nameTranslations) {
        this.nameTranslations = nameTranslations;
    }
}
