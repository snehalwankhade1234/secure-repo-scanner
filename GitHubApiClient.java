package OSV.scanner.OSVscanner.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class GitHubApiClient {
	 private final WebClient webClient;

	    public GitHubApiClient(@Value("${github.token}") String githubToken) {
	        this.webClient = WebClient.builder()
	                .baseUrl("https://api.github.com")
	                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + githubToken)
	                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
	                .build();
	    }

	    // ✅ Get last 100 commit SHAs from the repo
	    public List<String> getLast100CommitShas(String owner, String repo) {
	        List<String> commitShas = new ArrayList<>();

	        webClient.get()
	                .uri("/repos/{owner}/{repo}/commits?per_page=100", owner, repo)
	                .retrieve()
	                .bodyToFlux(Object.class)
	                .toStream()
	                .forEach(commitObj -> {
	                    // commitObj is a LinkedHashMap, extract SHA
	                    var map = (java.util.LinkedHashMap<?, ?>) commitObj;
	                    commitShas.add((String) map.get("sha"));
	                });

	        return commitShas;
	    }

	    // ✅ Simulated: extract dependencies from a commit
	    // Later you can read the pom.xml or package.json files
	    public List<String> getDependenciesInCommit(String owner, String repo, String sha) {
	        // For now, return fake dependencies
	        List<String> deps = new ArrayList<>();
	        deps.add("log4j:2.14.1"); // just to simulate
	        return deps;
	    }
}
