package com.reliaquest.api.client;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

class ServerApiClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RetryTemplate retryTemplate;

    @InjectMocks
    private ServerApiClient serverApiClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serverApiClient = new ServerApiClient(restTemplate, retryTemplate, "http://localhost/api/employees");
    }

    @Test
    void getAllEmployeesFromServer_returnsEmployeeList() {
        ListEmployeeServerResponse mockResponse = new ListEmployeeServerResponse();
        when(restTemplate.getForEntity(any(), eq(ListEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        ListEmployeeServerResponse result = serverApiClient.getAllEmployeesFromServer();

        assertNotNull(result);
        assertEquals(mockResponse, result);
    }

    @Test
    void getAllEmployeesFromServer_handlesRestTemplateException() {
        when(restTemplate.getForEntity(anyString(), eq(ListEmployeeServerResponse.class)))
                .thenThrow(new RuntimeException("RestTemplate error"));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        assertThrows(RuntimeException.class, () -> serverApiClient.getAllEmployeesFromServer());
    }

    @Test
    void getEmployeeById_returnsEmployee() {
        String id = "123";
        SingleEmployeeServerResponse mockResponse = new SingleEmployeeServerResponse();
        when(restTemplate.getForEntity(contains(id), eq(SingleEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        SingleEmployeeServerResponse result = serverApiClient.getEmployeeById(id);

        assertNotNull(result);
        assertEquals(mockResponse, result);
    }

    @Test
    void getEmployeeById_handlesNotFound() {
        String id = "notfound";
        when(restTemplate.getForEntity(contains(id), eq(SingleEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        assertThrows(IllegalArgumentException.class, () -> serverApiClient.getEmployeeById(id));
    }

    @Test
    void createEmployee_returnsCreatedEmployee() {
        EmployeeInput input = new EmployeeInput();
        SingleEmployeeServerResponse mockResponse = new SingleEmployeeServerResponse();
        when(restTemplate.postForEntity(anyString(), any(), eq(SingleEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        SingleEmployeeServerResponse result = serverApiClient.createEmployee(input);

        assertNotNull(result);
        assertEquals(mockResponse, result);
    }

    @Test
    void createEmployee_handlesNonOkStatus() {
        EmployeeInput input = new EmployeeInput();
        when(restTemplate.postForEntity(anyString(), any(), eq(SingleEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        assertThrows(IllegalStateException.class, () -> serverApiClient.createEmployee(input));
    }

    @Test
    void deleteEmployee_deletesEmployeeSuccessfully() {
        String id = "123";
        SingleEmployeeServerResponse employeeResponse = mock(SingleEmployeeServerResponse.class);
        EmployeeOutput employee = mock(EmployeeOutput.class);
        when(employee.getName()).thenReturn("John Doe");
        when(employeeResponse.getData()).thenReturn(employee);

        DeleteEmployeeResponse deleteResponse = new DeleteEmployeeResponse("", true);
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        when(restTemplate.getForEntity(contains(id), eq(SingleEmployeeServerResponse.class)))
                .thenReturn(new ResponseEntity<>(employeeResponse, HttpStatus.OK));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(), eq(DeleteEmployeeResponse.class)))
                .thenReturn(new ResponseEntity<>(deleteResponse, HttpStatus.OK));

        // Use a spy to call the real method for deleteEmployee
        ServerApiClient spyClient = spy(serverApiClient);
        doReturn(employeeResponse).when(spyClient).getEmployeeById(id);

        DeleteEmployeeResponse result = spyClient.deleteEmployee(id);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void deleteEmployee_handlesEmployeeNotFound() {
        String id = "notfound";
        SingleEmployeeServerResponse employeeResponse = mock(SingleEmployeeServerResponse.class);
        when(employeeResponse.getData()).thenReturn(null);
        when(retryTemplate.execute(any()))
                .thenAnswer(invocation -> {
                    RetryCallback callback = invocation.getArgument(0);
                    return callback.doWithRetry(null);
                });
        ServerApiClient spyClient = spy(serverApiClient);
        doReturn(employeeResponse).when(spyClient).getEmployeeById(id);

        assertThrows(IllegalArgumentException.class, () -> spyClient.deleteEmployee(id));
    }


}
