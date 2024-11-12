package istad.co.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final GitHubService gitHubService;

    @Autowired
    public AuthController(UserRepository userRepository, GitHubService gitHubService) {
        this.userRepository = userRepository;
        this.gitHubService = gitHubService;
    }

    @GetMapping("/loginSuccess")
    public String loginSuccess(@AuthenticationPrincipal OAuth2User principal) {
        System.out.println(principal.getAttributes());
        String username = principal.getAttribute("login");
        String email = principal.getAttribute("email");
        String githubId = principal.getAttribute("id").toString();
        String accessToken = principal.getAttribute("access_token");

        // Debug logging
        System.out.println("Access Token: " + accessToken);

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isEmpty()) {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setGithubId(githubId);
            userRepository.save(newUser);
        }

        String repos = gitHubService.listRepositories(accessToken);
        return "Repositories: " + repos;
    }

    @GetMapping("/repositories")
    public ResponseEntity<List<GithubResponse>> getRepositories() {
        List<GithubResponse> repositories = gitHubService.getAllRepositories();
        return ResponseEntity.ok(repositories);
    }
}