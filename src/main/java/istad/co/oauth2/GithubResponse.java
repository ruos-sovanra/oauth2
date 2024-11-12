package istad.co.oauth2;

import lombok.Builder;

@Builder
public record GithubResponse(
        String url,
        String branch
) {
}
