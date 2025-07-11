package pt.gov.chavemoveldigital.AutoVoter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OAuthVotingService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private String evotingBaseUrl = "http://localhost:8080";
    private String cmdBaseUrl = "http://localhost:9090";
    private String currentToken;
    private Long currentVoterId;
    private boolean isAuthenticated = false;

    public OAuthVotingService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public OAuthVotingService(String evotingBaseUrl, String cmdBaseUrl) {
        this();
        this.evotingBaseUrl = evotingBaseUrl;
        this.cmdBaseUrl = cmdBaseUrl;
    }

    public static class UserCredentials {
        private String telephoneNumber;
        private Integer pin;
        private Long nif;
        private String firstName;
        private String lastName;
        private String municipality;
        private String district;
        private String parish;

        public UserCredentials() {}

        public UserCredentials(String telephoneNumber, Integer pin, Long nif, String municipality) {
            this.telephoneNumber = telephoneNumber;
            this.pin = pin;
            this.nif = nif;
            this.municipality = municipality;
        }

        public String getTelephoneNumber() { return telephoneNumber; }
        public void setTelephoneNumber(String telephoneNumber) { this.telephoneNumber = telephoneNumber; }

        public Integer getPin() { return pin; }
        public void setPin(Integer pin) { this.pin = pin; }

        public Long getNif() { return nif; }
        public void setNif(Long nif) { this.nif = nif; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getMunicipality() { return municipality; }
        public void setMunicipality(String municipality) { this.municipality = municipality; }

        public String getDistrict() { return district; }
        public void setDistrict(String district) { this.district = district; }

        public String getParish() { return parish; }
        public void setParish(String parish) { this.parish = parish; }
    }

    public static class VotingResults {
        private int processed;
        private int successful;
        private int failed;
        private int totalVotes;
        private List<String> errors;
        private LocalDateTime startTime;
        private LocalDateTime endTime;

        public VotingResults() {
            this.errors = new ArrayList<>();
            this.startTime = LocalDateTime.now();
        }

        public int getProcessed() { return processed; }
        public void setProcessed(int processed) { this.processed = processed; }

        public int getSuccessful() { return successful; }
        public void setSuccessful(int successful) { this.successful = successful; }

        public int getFailed() { return failed; }
        public void setFailed(int failed) { this.failed = failed; }

        public int getTotalVotes() { return totalVotes; }
        public void setTotalVotes(int totalVotes) { this.totalVotes = totalVotes; }

        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }

        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

        public void addError(String error) {
            this.errors.add(error);
        }

        public double getSuccessRate() {
            return processed > 0 ? (double) successful / processed * 100 : 0;
        }

        public long getDurationSeconds() {
            if (startTime != null && endTime != null) {
                return java.time.Duration.between(startTime, endTime).getSeconds();
            }
            return 0;
        }
    }

    private <T> T makeJsonRequest(String url, Object requestBody, Class<T> responseType, HttpMethod method) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, method, entity, responseType);

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in request to " + url + ": " + e.getMessage());
            throw new RuntimeException("Error communicating with " + url, e);
        }
    }

    private <T> T makePostParamsRequest(String url, Class<T> responseType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, null, responseType);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in request to " + url + ": " + e.getMessage());
            throw new RuntimeException("Error communicating with " + url, e);
        }
    }

    private <T> T makeFormRequest(String url, MultiValueMap<String, String> formData, Class<T> responseType) {
        try {
            if (restTemplate.getMessageConverters().stream().noneMatch(c -> c instanceof org.springframework.http.converter.FormHttpMessageConverter)) {
                restTemplate.getMessageConverters().add(new org.springframework.http.converter.FormHttpMessageConverter());
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            System.out.println("[DEBUG] Form data sent (MultiValueMap): " + formData);
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error in request to " + url + ": " + e.getMessage());
            throw new RuntimeException("Error communicating with " + url, e);
        }
    }

    public Map<String, Object> initializeOAuthLogin() {
        System.out.println("Starting OAuth login...");

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = makeJsonRequest(
                    evotingBaseUrl + "/oauth/login",
                    null,
                    Map.class,
                    HttpMethod.GET
            );

            this.currentToken = (String) response.get("token");
            System.out.println("OAuth initialized: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("Error initializing OAuth: " + e.getMessage());
            throw e;
        }
    }

    public void registerTokenInCMD(Map<String, Object> tokenData) {
        System.out.println("Registering token in Digital Mobile Key...");

        try {
            makeJsonRequest(
                    cmdBaseUrl + "/oauth/token",
                    tokenData,
                    String.class,
                    HttpMethod.POST
            );
            System.out.println("Token registered in DMK");
        } catch (Exception e) {
            System.err.println("Error registering token in DMK: " + e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> authenticateWithCredentials(String telephoneNumber, Integer pin) {
        System.out.println("Authenticating with credentials...");

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("telephoneNumber", telephoneNumber);
            formData.add("pin", pin.toString());
            formData.add("token", currentToken);

            Map<String, Object> response = makeFormRequest(
                    cmdBaseUrl + "/users/authenticate",
                    formData,
                    Map.class
            );

            System.out.println("Credentials authenticated: " + response);
            return response;
        } catch (Exception e) {
            System.err.println("Error in authentication: " + e.getMessage());
            throw e;
        }
    }

    public Long verifySMSCode(Integer smsCode) {
        System.out.println("Verifying SMS code...");
        try {
            if (smsCode == null) {
                throw new IllegalArgumentException("SMS code is null!");
            }
            if (currentToken == null) {
                throw new IllegalStateException("OAuth token not set!");
            }
            System.out.println("Sending to /users/verify-smscode: SMSCode=" + smsCode + ", token=" + currentToken);
            try {
                Long voterId = makePostParamsRequest(
                        cmdBaseUrl + "/users/verify-smscode?SMSCode=" + smsCode, Long.class
                );
                this.currentVoterId = voterId;
                System.out.println("SMS verified, voter ID: " + voterId);
                return voterId;
            } catch (RuntimeException ex) {
                System.err.println("Error in server response: " + ex.getMessage());
                if (ex.getCause() instanceof org.springframework.web.client.HttpClientErrorException) {
                    org.springframework.web.client.HttpClientErrorException httpEx =
                            (org.springframework.web.client.HttpClientErrorException) ex.getCause();
                    System.err.println("Error response body: " + httpEx.getResponseBodyAsString());
                }
                throw ex;
            }
        } catch (Exception e) {
            System.err.println("Error verifying SMS: " + e.getMessage());
            throw e;
        }
    }
    public void completeAuthentication() {
        System.out.println("Completing authentication in voting system...");

        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("token", currentToken);
            formData.add("id", currentVoterId.toString());

            makeFormRequest(
                    evotingBaseUrl + "/oauth/auth-with-token",
                    formData,
                    String.class
            );

            this.isAuthenticated = true;
            System.out.println("Authentication complete!");
        } catch (Exception e) {
            System.err.println("Error completing authentication: " + e.getMessage());
            throw e;
        }
    }
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAvailableElections() {
        System.out.println("Getting available elections...");

        try {
            List<Map<String, Object>> elections = makeJsonRequest(
                    evotingBaseUrl + "/elections?isActive=true",
                    null,
                    List.class,
                    HttpMethod.GET
            );

            System.out.println("Elections found: " + elections.size());
            return elections;
        } catch (Exception e) {
            System.err.println("Error getting elections: " + e.getMessage());
            throw e;
        }
    }
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getElectionBallot(Long electionId) {
        System.out.println("Getting candidates for election " + electionId + "...");

        try {
            List<Map<String, Object>> organisations = makeJsonRequest(
                    evotingBaseUrl + "/elections/" + electionId + "/ballot",
                    null,
                    List.class,
                    HttpMethod.GET
            );

            System.out.println("Candidates found: " + organisations.size());
            return organisations;
        } catch (Exception e) {
            System.err.println("Error getting candidates: " + e.getMessage());
            throw e;
        }
    }
    @SuppressWarnings("unchecked")
    public List<Long> checkIfAlreadyVoted(Long nif) {
        try {
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("nif", nif.toString());

            List<Long> elections = makeFormRequest(
                    evotingBaseUrl + "/voters/has-voted",
                    formData,
                    List.class
            );

            return elections != null ? elections : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error checking if already voted: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    public Map<String, Object> castRandomVote(Long electionId, List<Map<String, Object>> organisations,
                                              Long voterNif, String municipalityName) {
        System.out.println("Casting random vote in election " + electionId + "...");

        try {
            Random random = new Random();
            Map<String, Object> randomOrg = organisations.get(random.nextInt(organisations.size()));

            Map<String, Object> voteData = new HashMap<>();
            voteData.put("organisationId", randomOrg.get("id"));
            voteData.put("voterNif", voterNif.toString());
            voteData.put("municipalityName", municipalityName);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = makeJsonRequest(
                    evotingBaseUrl + "/elections/" + electionId + "/castVote",
                    voteData,
                    Map.class,
                    HttpMethod.POST
            );

            String orgName = (String) randomOrg.getOrDefault("organisationName",
                    randomOrg.getOrDefault("name", "Organization"));
            System.out.println("Vote registered for " + orgName);
            return response;
        } catch (Exception e) {
            System.err.println("Error voting: " + e.getMessage());
            throw e;
        }
    }
    @SuppressWarnings("unchecked")
    public Map<String, Object> performCompleteLogin(UserCredentials userCredentials) {
        System.out.println("Starting complete login process...");
        try {
            Map<String, Object> oauthData = initializeOAuthLogin();

            registerTokenInCMD(oauthData);
            Map<String, Object> authResponse = authenticateWithCredentials(
                    userCredentials.getTelephoneNumber(),
                    userCredentials.getPin()
            );
            Map<String, Object> params = (Map<String, Object>) authResponse.get("params");
            Integer smsCode = (Integer) params.get("SMScode");
            Long voterId = verifySMSCode(smsCode);
            completeAuthentication();
            System.out.println("Login completed successfully!");
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("voterId", voterId);
            return result;
        } catch (Exception e) {
            System.err.println("Login process failed: " + e.getMessage());
            throw e;
        }
    }
    public Map<String, Object> performRandomVoting(Long voterNif, String municipalityName) {
        if (!isAuthenticated) {
            throw new RuntimeException("Not authenticated! Please login first.");
        }

        System.out.println("Starting random voting...");

        try {
            List<Long> alreadyVoted = checkIfAlreadyVoted(voterNif);
            System.out.println("Already voted in " + alreadyVoted.size() + " elections");

            List<Map<String, Object>> elections = getAvailableElections();

            int votesCount = 0;
            for (Map<String, Object> election : elections) {
                Long electionId = ((Number) election.get("id")).longValue();
                String electionName = (String) election.get("name");

                if (alreadyVoted.contains(electionId)) {
                    System.out.println("Skipping election " + electionName + " - already voted");
                    continue;
                }

                Boolean isStarted = makeJsonRequest(
                        evotingBaseUrl + "/elections/" + electionId + "/isStarted",
                        null,
                        Boolean.class,
                        HttpMethod.GET
                );

                if (!isStarted) {
                    System.out.println("Skipping election " + electionName + " - not started");
                    continue;
                }

                try {
                    List<Map<String, Object>> organisations = getElectionBallot(electionId);

                    if (organisations.isEmpty()) {
                        System.out.println("Skipping election " + electionName + " - no candidates");
                        continue;
                    }

                    castRandomVote(electionId, organisations, voterNif, municipalityName);
                    votesCount++;

                    Thread.sleep(1000);

                } catch (Exception voteError) {
                    System.out.println("Error voting in election " + electionName + ": " + voteError.getMessage());
                }
            }

            System.out.println("Voting finished! Total votes: " + votesCount);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("votesCount", votesCount);
            return result;

        } catch (Exception e) {
            System.err.println("Random voting error: " + e.getMessage());
            throw e;
        }
    }
    public Map<String, Object> executeFullProcess(UserCredentials userCredentials) {
        try {
            System.out.println("Executing full process...");
            String municipality = userCredentials.getMunicipality() != null ?
                    userCredentials.getMunicipality() : "Lisbon";

            Map<String, Object> loginResult = performCompleteLogin(userCredentials);

            Map<String, Object> votingResult = performRandomVoting(userCredentials.getNif(), municipality);

            System.out.println("Full process executed successfully!");

            Map<String, Object> result = new HashMap<>();
            result.put("login", loginResult);
            result.put("voting", votingResult);
            return result;

        } catch (Exception e) {
            System.err.println("Full process failed: " + e.getMessage());
            throw e;
        }
    }
    public List<UserCredentials> readUsersFromFile(String filePath) {
        InputStream inputStream = null;
        try {
            try {
                ClassPathResource resource = new ClassPathResource(filePath);
                inputStream = resource.getInputStream();
            } catch (Exception e) {
                System.out.println("[INFO] File not found in classpath, trying filesystem: " + filePath);
                java.io.File file = new java.io.File(filePath);
                if (file.exists()) {
                    inputStream = new java.io.FileInputStream(file);
                } else {
                    throw new IOException("File not found: " + filePath);
                }
            }
            JsonNode jsonNode = objectMapper.readTree(inputStream);
            List<UserCredentials> users = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode userNode : jsonNode) {
                    UserCredentials user = objectMapper.treeToValue(userNode, UserCredentials.class);
                    users.add(user);
                }
            }
            inputStream.close();
            System.out.println("Loaded " + users.size() + " users from file " + filePath);
            return users;
        } catch (IOException e) {
            System.err.println("Error reading file " + filePath + ": " + e.getMessage());
            System.out.println("Using sample users as fallback...");
            return createSampleUsers();
        }
    }
    public List<UserCredentials> readUsersFromFileFromResources(String filePath) {
        InputStream inputStream = null;
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            inputStream = resource.getInputStream();

            JsonNode jsonNode = objectMapper.readTree(inputStream);
            List<UserCredentials> users = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode userNode : jsonNode) {
                    UserCredentials user = objectMapper.treeToValue(userNode, UserCredentials.class);
                    users.add(user);
                }
            }
            inputStream.close();
            System.out.println("Loaded " + users.size() + " users from resource " + filePath);
            return users;
        } catch (IOException e) {
            System.err.println("Error reading resource " + filePath + ": " + e.getMessage());
            System.out.println("Using sample users as fallback...");
            return createSampleUsers();
        }
    }
    private List<UserCredentials> createSampleUsers() {
        List<UserCredentials> users = new ArrayList<>();

        UserCredentials user1 = new UserCredentials("+351910123456", 1234, 323456789L, "Cascais");
        user1.setFirstName("João");
        user1.setLastName("Silva");
        user1.setDistrict("Lisbon");
        user1.setParish("São Domingos de Rana");
        users.add(user1);

        UserCredentials user2 = new UserCredentials("+351920234567", 5678, 334567890L, "Matosinhos");
        user2.setFirstName("Maria");
        user2.setLastName("Santos");
        user2.setDistrict("Porto");
        user2.setParish("União das freguesias de Matosinhos e Leça da Palmeira");
        users.add(user2);

        return users;
    }
    public List<UserCredentials> validateAndProcessUsers(List<UserCredentials> users) {
        List<UserCredentials> validUsers = new ArrayList<>();

        for (UserCredentials user : users) {
            if (user.getTelephoneNumber() == null || user.getPin() == null || user.getNif() == null) {
                System.out.println("Invalid user ignored: missing required fields");
                continue;
            }

            if (user.getMunicipality() == null) user.setMunicipality("Lisbon");
            if (user.getDistrict() == null) user.setDistrict("Lisbon");
            if (user.getFirstName() == null) user.setFirstName("");
            if (user.getLastName() == null) user.setLastName("");
            if (user.getParish() == null) user.setParish("");

            validUsers.add(user);
        }

        System.out.println(validUsers.size() + " valid users processed");
        return validUsers;
    }
    public VotingResults performVotingFromFile(int numberOfUsers, String filePath, boolean shuffleUsers,
                                               int delayBetweenUsers, boolean continueOnError) {
        try {
            System.out.println("Loading users from JSON file...");

            List<UserCredentials> allUsers = readUsersFromFile(filePath);
            List<UserCredentials> validUsers = validateAndProcessUsers(allUsers);

            if (validUsers.isEmpty()) {
                throw new RuntimeException("No valid user found in file");
            }

            if (shuffleUsers) {
                Collections.shuffle(validUsers);
                System.out.println("Users shuffled randomly");
            }

            List<UserCredentials> usersToProcess = validUsers.subList(0,
                    Math.min(numberOfUsers, validUsers.size()));

            System.out.println("Starting processing of " + usersToProcess.size() + " users...");

            VotingResults results = new VotingResults();

            for (int i = 0; i < usersToProcess.size(); i++) {
                UserCredentials user = usersToProcess.get(i);
                results.setProcessed(results.getProcessed() + 1);

                try {
                    System.out.println("\nUser [" + (i + 1) + "/" + usersToProcess.size() + "] " +
                            user.getFirstName() + " " + user.getLastName());
                    System.out.println("Phone: " + user.getTelephoneNumber() + " | ID: " + user.getNif());
                    System.out.println("Location: " + user.getParish() + ", " + user.getMunicipality() + ", " + user.getDistrict());

                    this.isAuthenticated = false;
                    this.currentToken = null;
                    this.currentVoterId = null;

                    Map<String, Object> result = executeFullProcess(user);

                    results.setSuccessful(results.getSuccessful() + 1);

                    @SuppressWarnings("unchecked")
                    Map<String, Object> voting = (Map<String, Object>) result.get("voting");
                    Integer votesCount = (Integer) voting.getOrDefault("votesCount", 0);
                    results.setTotalVotes(results.getTotalVotes() + votesCount);

                    System.out.println("Success! Votes: " + votesCount);

                } catch (Exception error) {
                    results.setFailed(results.getFailed() + 1);
                    String errorMsg = user.getFirstName() + " " + user.getLastName() +
                            " (" + user.getTelephoneNumber() + "): " + error.getMessage();
                    results.addError(errorMsg);

                    System.err.println("Error: " + error.getMessage());

                    if (!continueOnError) {
                        System.out.println("Stopping execution due to error");
                        break;
                    }
                }

                if (i < usersToProcess.size() - 1) {
                    System.out.println("Waiting " + delayBetweenUsers + "ms...");
                    try {
                        Thread.sleep(delayBetweenUsers);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }

            results.setEndTime(LocalDateTime.now());
            printDetailedReport(results);

            return results;

        } catch (Exception e) {
            System.err.println("Fatal error in processing: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    private void printDetailedReport(VotingResults results) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("FINAL DETAILED REPORT");
        System.out.println("=".repeat(50));
        System.out.println("Duration: " + results.getDurationSeconds() + " seconds");
        System.out.println("Processed: " + results.getProcessed());
        System.out.println("Successes: " + results.getSuccessful());
        System.out.println("Failures: " + results.getFailed());
        System.out.println("Total votes: " + results.getTotalVotes());
        System.out.println("Success rate: " + String.format("%.1f%%", results.getSuccessRate()));

        if (!results.getErrors().isEmpty()) {
            System.out.println("\nDETAILED ERRORS:");
            for (int i = 0; i < results.getErrors().size(); i++) {
                System.out.println((i + 1) + ". " + results.getErrors().get(i));
            }
        }

        System.out.println("Processing finished!");
    }
    public VotingResults executeVotingFromFile(int numberOfUsers, String filePath) {
        return performVotingFromFile(numberOfUsers, filePath, true, 3000, true);
    }
    public VotingResults executeVotingFromFile(int numberOfUsers) {
        return executeVotingFromFile(numberOfUsers, "users.json");
    }
    public static void main(String[] args) {
        OAuthVotingService service = new OAuthVotingService();

        List<UserCredentials> users = service.readUsersFromFileFromResources("json/users.json");

        for (UserCredentials user : users) {
            try {
                System.out.println("\n==============================");
                System.out.println("Simulating login and vote for: " + user.getTelephoneNumber());
                service.isAuthenticated = false;
                Map<String, Object> result = service.executeFullProcess(user);
                System.out.println("Result: " + result);
            } catch (Exception e) {
                System.err.println("Error processing user " + user.getTelephoneNumber() + ": " + e.getMessage());
            }
        }
        System.out.println("\nSimulation finished.");
    }
}
