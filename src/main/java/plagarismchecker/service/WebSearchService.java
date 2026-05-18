package plagarismchecker.service;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@Service
public class WebSearchService {

    private final String API_KEY =
            "YOUR API";

    // SEARCH WEB

    public List<String> searchWeb(
            String query)
            throws Exception {

        query =java.net.URLEncoder.encode(query,java.nio.charset.StandardCharsets.UTF_8);

        String searchURL =
                "https://serpapi.com/search.json?q="
                + query
                + "&api_key="
                + API_KEY;

        URL url = new URL(searchURL);

        HttpURLConnection connection =
                (HttpURLConnection)
                        url.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );

        StringBuilder response =
                new StringBuilder();

        String line;

        while((line = reader.readLine())
                != null){

            response.append(line);
        }

        reader.close();

        String json =
                response.toString();

        ObjectMapper mapper =
                new ObjectMapper();

        JsonNode root =
                mapper.readTree(json);

        JsonNode organicResults =
                root.get("organic_results");

        List<String> links =
                new ArrayList<>();

        if(organicResults != null){

            for(JsonNode result
                    : organicResults){

                JsonNode linkNode =
                        result.get("link");

                if(linkNode != null){

                    links.add(
                            linkNode.asText()
                    );
                }

                if(links.size() >= 5){
                    break;
                }
            }
        }

        return links;
    }

    // GET WEBPAGE TEXT

    public String getWebpageText(
            String url)
            throws Exception {

        Document document =
                Jsoup.connect(url)
                        .userAgent("Mozilla")
                        .get();

        return document.body().text();
    }
}