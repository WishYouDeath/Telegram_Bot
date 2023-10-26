import okhttp3.*;
import java.net.URI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
public class APIRequest {
    private static final String API_KEY = System.getenv("SITE_API");
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public static String sendGETRequest(String urlString) throws Exception{
        URI uri = URI.create(urlString);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_2, Protocol.HTTP_1_1))
                .build();
        Request request = new Request.Builder()
                .url(uri.toURL())
                .header("X-RapidAPI-Key", API_KEY)
                .build();
        Call call = client.newCall(request);
        Response response = call.execute();
        logger.info("Sending request to API: {}", uri);
        if (response.code() == 200) {
            return Objects.requireNonNull(response.body()).string();
        } else {
            logger.error("Error in request: {}", response);
            throw new Exception("Error in request: " + response);
        }
    }
}