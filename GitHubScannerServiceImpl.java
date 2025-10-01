package OSV.scanner.OSVscanner.service;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import OSV.scanner.OSVscanner.client.GitHubApiClient;
import OSV.scanner.OSVscanner.client.OsvApiClient;
import OSV.scanner.OSVscanner.model.CommitVulnerabilityReport;

@Service
public class GitHubScannerServiceImpl  implements GitHubScannerService {
	@Autowired
    private GitHubApiClient gitHubApiClient;

    @Autowired
    private OsvApiClient osvApiClient;

	@Override
	public CommitVulnerabilityReport scanRepository(String repoUrl) {
		   // Step 1: Extract owner and repo name
        String[] parts = repoUrl.replace("https://github.com/", "").split("/");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid GitHub URL");

        String owner = parts[0];
        String repo = parts[1];

        // Step 2: Get last 100 commits
        List<String> commitShas = gitHubApiClient.getLast100CommitShas(owner, repo);

        // Step 3: Check each commit
        Map<String, List<String>> report = new LinkedHashMap<>();

        for (String sha : commitShas) {
            List<String> dependencies = gitHubApiClient.getDependenciesInCommit(owner, repo, sha);

            List<String> vulnerabilities = new ArrayList<>();
            for (String dep : dependencies) {
                String vuln = osvApiClient.checkVulnerability(dep);
                if (vuln != null) {
                    vulnerabilities.add(vuln);
                }
            }

            report.put(sha, vulnerabilities);
        }

        // Step 4: Return report
        CommitVulnerabilityReport result = new CommitVulnerabilityReport();
        result.setCommitVulnerabilities(report);
        return result;
	}

}
