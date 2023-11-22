package JSON;
import java.util.List;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "list",
})
@Generated("jsonschema2pojo")
public class Example{
    @JsonProperty("list")
    private List<Match> list;

    @JsonProperty("list")
    public List<Match> getList() {
        return list;
    }

    @JsonProperty("list")
    public void setList(List<JSON.Match> list) {
        this.list = list;
    }

}
