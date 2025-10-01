package OSV.scanner.OSVscanner.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class OsvApiClient {
	 private final WebClient webClient;

	    public OsvApiClient(@Value("${osv.api.url}") String osvApiUrl) {
	        this.webClient = WebClient.builder()
	                .baseUrl(osvApiUrl)
	                .build();
	    }

	    // ✅ Check one dependency for vulnerabilities
	    public String checkVulnerability(String dependency) {
	        // Split "log4j:2.14.1" into name and version
	        String[] parts = dependency.split(":");
	        if (parts.length != 2) return null;

	        String packageName = parts[0];
	        String version = parts[1];

	        // You may enhance this by mapping ecosystem based on package (for now assume Maven)
	        String ecosystem = "Maven";

	        Map<String, Object> requestBody = Map.of(
	                "package", Map.of(
	                        "name", packageName,
	                        "ecosystem", ecosystem
	                ),
	                "version", version
	        );

	        try {
	            Map response = webClient.post()
	                    .contentType(MediaType.APPLICATION_JSON)
	                    .bodyValue(requestBody)
	                    .retrieve()
	                    .bodyToMono(Map.class)
	                    .block();

	            if (response != null && response.containsKey("vulns")) {
	                List vulns = (List) response.get("vulns");
	                if (!vulns.isEmpty()) {
	                    return "Vulnerable: " + packageName + ":" + version;
	                }
	            }
	        } catch (Exception e) {
	            System.out.println("Error while calling OSV API: " + e.getMessage());
	        }

	        return null;
	    }
}
