package OSV.scanner.OSVscanner.service;
import OSV.scanner.OSVscanner.model.CommitVulnerabilityReport;
public interface GitHubScannerService {
	 CommitVulnerabilityReport scanRepository(String repoUrl);
}
