package com.reliaquest.api.client;

import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class ServerApiClient {

    private final RestTemplate restTemplate;
    private String serverBaseUrl;
    private final RetryTemplate retryTemplate;

    @Autowired
    public ServerApiClient(
            RestTemplate restTemplate,
            RetryTemplate retryTemplate,
            @Value("${server.api.base-url}") String serverBaseUrl) {
        this.restTemplate = restTemplate;
        this.retryTemplate = retryTemplate;
        this.serverBaseUrl = serverBaseUrl;
    }

    public void setServerBaseUrl(String serverBaseUrl) {
        if (serverBaseUrl == null || serverBaseUrl.isEmpty()) {
            throw new IllegalArgumentException("Server base URL cannot be null or empty");
        }
        this.serverBaseUrl = serverBaseUrl;
        log.debug("Server base URL set to: {}", this.serverBaseUrl);
    }

    public ListEmployeeServerResponse getAllEmployeesFromServer() {
        return retryTemplate.execute(context -> {
            try {
                ResponseEntity<ListEmployeeServerResponse> response =
                        restTemplate.getForEntity(URI.create(serverBaseUrl), ListEmployeeServerResponse.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    log.debug("Retrieved employees from server: {}", response.getBody());
                    return response.getBody();
                } else {
                    throw new IllegalStateException(
                            "Failed to fetch employees from server. Status code: " + response.getStatusCode());
                }
            } catch (HttpClientErrorException  ex) {
                throw new TooManyRequestsException("Received too many requests from the server. Please try again later.");
            }
        });
    }

    public SingleEmployeeServerResponse getEmployeeById(String id) {
        return retryTemplate.execute(context -> {
            try {
                String url = serverBaseUrl + "/" + id;
                log.debug("Sending get request for [{}] to URL : {}", id, url);
                ResponseEntity<SingleEmployeeServerResponse> response =
                        restTemplate.getForEntity(url, SingleEmployeeServerResponse.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new IllegalStateException(
                            "Failed to fetch employees from server. Status code: " + response.getStatusCode());
                }
                log.debug("Retrieved employee from server: {}", response.getBody());
                if(response.getBody() != null) {
                    return response.getBody();
                } else {
                    throw new IllegalArgumentException("Employee with ID " + id + " does not exist.");
                }
            } catch (HttpClientErrorException.TooManyRequests ex) {
                throw new TooManyRequestsException("Received too many requests from the server. Please try again later.");
            }
        });
    }

    public SingleEmployeeServerResponse createEmployee(EmployeeInput input) {
        return retryTemplate.execute(context -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<EmployeeInput> request = new HttpEntity<>(input, headers);
                log.debug("Sending create request for [{}] to URL : {}", input, serverBaseUrl);
                ResponseEntity<SingleEmployeeServerResponse> response =
                        restTemplate.postForEntity(serverBaseUrl, request, SingleEmployeeServerResponse.class);
                if (response.getStatusCode() != HttpStatus.OK) {
                    throw new IllegalStateException("Failed to create employee. Status code: " + response.getStatusCode());
                }
                log.debug("Created employee on server: {}", response.getBody());
                return response.getBody();
            } catch (HttpClientErrorException.TooManyRequests ex) {
                throw new TooManyRequestsException("Received too many requests from the server. Please try again later.");
            }
        });
    }

    @Retryable
    public DeleteEmployeeResponse deleteEmployee(String id) {
        return retryTemplate.execute(context -> {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                SingleEmployeeServerResponse employeeById = getEmployeeById(id);
                if (employeeById.getData() == null) {
                    throw new IllegalArgumentException("Employee with ID " + id + " does not exist.");
                }

                String name = employeeById.getData().getName();
                log.debug("Deleting employee [{}] with name [{}]", id, name);
                DeleteEmployeePayload payload = new DeleteEmployeePayload(name);
                HttpEntity<DeleteEmployeePayload> request = new HttpEntity<>(payload, headers);
                ResponseEntity<DeleteEmployeeResponse> response =
                        restTemplate.exchange(serverBaseUrl, HttpMethod.DELETE, request, DeleteEmployeeResponse.class);
                if (response.getStatusCode() == HttpStatus.OK && response.hasBody() && response.getBody() != null) {
                    response.getBody().setName(name);
                } else {
                    throw new IllegalStateException("Failed to delete employee with ID " + id);
                }
                log.debug("Deleted employee from server: {}", name);
                return response.getBody();
            } catch (HttpClientErrorException.TooManyRequests ex) {
                throw new TooManyRequestsException("Received too many requests from the server. Please try again later.");
            }
        });
    }
}
