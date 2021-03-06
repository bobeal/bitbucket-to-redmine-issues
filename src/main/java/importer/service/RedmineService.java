package importer.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class RedmineService {

    private Log log = LogFactory.getLog(RedmineService.class);

    private class RedmineAuthInterceptor implements ClientHttpRequestInterceptor {

        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            HttpHeaders headers = request.getHeaders();
            headers.add("X-Redmine-API-Key", apikey);
            return execution.execute(request, body);
        }
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
        interceptors.add(new RedmineAuthInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    @Value("${redmine.apikey}")
    private String apikey;

    @Value("${redmine.baseUrl}")
    private String baseUrl;

    public Map<String, String> listUsers() throws JsonProcessingException, IOException {
        JsonNode result = restTemplate().getForObject(baseUrl + "/users.json", JsonNode.class);
        Map<String, String> users = new HashMap<String, String>();
        result.findValue("users").forEach(user -> users.put(user.get("id").asText(), user.get("login").asText()));
        return users;
    }

    public Map<String, String> listIssuesStatuses() throws JsonProcessingException, IOException {
        JsonNode result = restTemplate().getForObject(baseUrl + "/issue_statuses.json", JsonNode.class);
        Map<String, String> issuesStatuses = new HashMap<String, String>();
        result.findValue("issue_statuses").forEach(status -> issuesStatuses.put(status.get("id").asText(), status.get("name").asText()));
        return issuesStatuses;
    }
}
