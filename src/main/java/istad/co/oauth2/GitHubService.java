package istad.co.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitHubService {

    @Autowired
    private RestTemplate restTemplate;
    private final RepositoryRepository repositoryRepository;

    public String listRepositories(String accessToken) {
        String url = "https://api.github.com/user/repos";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        List<Repo> repositories = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(responseBody);
            for (JsonNode node : root) {
                String repoUrl = node.get("html_url").asText();
                String defaultBranch = node.get("default_branch").asText();
                Repo repo = new Repo();
                repo.setUrl(repoUrl);
                repo.setBranch(defaultBranch);
                repositories.add(repo);
                repositoryRepository.save(repo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return repositories.toString();
    }

    public List<GithubResponse> getAllRepositories() {

        List<Repo> repositories = repositoryRepository.findAll();

        return repositories.stream()
        .map(repo -> new GithubResponse(repo.getUrl(), repo.getBranch()))
        .collect(Collectors.toList());
    }
}