package com.reliaquest.api.client;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import com.reliaquest.api.exception.TooManyRequestsException;
import com.reliaquest.api.model.ListEmployeeServerResponse;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class TooManyRequestsTest {

    @Mock
    private RetryTemplate retryTemplate;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ServerApiClient serverApiClient;

    public TooManyRequestsTest() {
        MockitoAnnotations.openMocks(this);
        serverApiClient.setServerBaseUrl("http://localhost/api/employees");
    }

    @Test
    void getAllEmployeesFromServer_tooManyRequests_throwsException() {
        when(restTemplate.getForEntity(any(), eq(ListEmployeeServerResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.TOO_MANY_REQUESTS));
        when(retryTemplate.execute(any())).thenAnswer(invocation -> {
            RetryCallback callback = invocation.getArgument(0);
            return callback.doWithRetry(null);
        });

        assertThrows(TooManyRequestsException.class, () -> serverApiClient.getAllEmployeesFromServer());
    }
}
