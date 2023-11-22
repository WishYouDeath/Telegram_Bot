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
        "name",
        "name_translations"
})
@Generated("jsonschema2pojo")
public class Away_team {
    @JsonProperty("name")
    private String name;
    @JsonProperty("name_translations")
    private Map<String, String> nameTranslations;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

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

