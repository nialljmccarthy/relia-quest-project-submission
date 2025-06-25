package com.reliaquest.api.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.lang.IllegalArgumentException;

import com.reliaquest.api.client.ServerApiClient;
import com.reliaquest.api.model.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

class EmployeeControllerImplTest {

    @Mock
    private ServerApiClient serverApiClient;

    @InjectMocks
    private EmployeeControllerImpl controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEmployees_returnsEmployeeList() {
        List<EmployeeOutput> employees = Arrays.asList(new EmployeeOutput(), new EmployeeOutput());
        ListEmployeeServerResponse response = mock(ListEmployeeServerResponse.class);
        when(response.getData()).thenReturn(employees);
        when(serverApiClient.getAllEmployeesFromServer()).thenReturn(response);

        ResponseEntity<List<EmployeeOutput>> result = controller.getAllEmployees();
        assertEquals(employees, result.getBody());
    }

    @Test
    void getEmployeesByNameSearch_filtersByName() {
        EmployeeOutput e1 = new EmployeeOutput();
        e1.setName("Alice");
        EmployeeOutput e2 = new EmployeeOutput();
        e2.setName("Bob");
        List<EmployeeOutput> employees = Arrays.asList(e1, e2);
        ListEmployeeServerResponse response = mock(ListEmployeeServerResponse.class);
        when(response.getData()).thenReturn(employees);
        when(serverApiClient.getAllEmployeesFromServer()).thenReturn(response);

        ResponseEntity<List<EmployeeOutput>> result = controller.getEmployeesByNameSearch("ali");
        assertEquals(Collections.singletonList(e1), result.getBody());
    }

    @Test
    void getEmployeeById_found() {
        EmployeeOutput employee = new EmployeeOutput();
        SingleEmployeeServerResponse response = mock(SingleEmployeeServerResponse.class);
        when(response.getData()).thenReturn(employee);
        when(serverApiClient.getEmployeeById("1")).thenReturn(response);

        ResponseEntity<EmployeeOutput> result = controller.getEmployeeById("1");
        assertEquals(employee, result.getBody());
    }

    @Test
    void getEmployeeById_notFound() {
        SingleEmployeeServerResponse response = mock(SingleEmployeeServerResponse.class);
        when(response.getData()).thenReturn(null);
        when(serverApiClient.getEmployeeById("1")).thenReturn(response);

        ResponseEntity<EmployeeOutput> result = controller.getEmployeeById("1");
        assertEquals(404, result.getStatusCode().value());
    }

    @Test
    void getHighestSalaryOfEmployees_returnsMaxSalary() {
        EmployeeOutput e1 = new EmployeeOutput();
        e1.setSalary(100);
        EmployeeOutput e2 = new EmployeeOutput();
        e2.setSalary(200);
        List<EmployeeOutput> employees = Arrays.asList(e1, e2);
        ListEmployeeServerResponse response = mock(ListEmployeeServerResponse.class);
        when(response.getData()).thenReturn(employees);
        when(serverApiClient.getAllEmployeesFromServer()).thenReturn(response);

        ResponseEntity<Integer> result = controller.getHighestSalaryOfEmployees();
        assertEquals(200, result.getBody());
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_returnsTop10Names() {
        EmployeeOutput e1 = new EmployeeOutput();
        e1.setName("A");
        e1.setSalary(100);
        EmployeeOutput e2 = new EmployeeOutput();
        e2.setName("B");
        e2.setSalary(200);
        List<EmployeeOutput> employees = Arrays.asList(e1, e2);
        ListEmployeeServerResponse response = mock(ListEmployeeServerResponse.class);
        when(response.getData()).thenReturn(employees);
        when(serverApiClient.getAllEmployeesFromServer()).thenReturn(response);

        ResponseEntity<List<String>> result = controller.getTopTenHighestEarningEmployeeNames();
        assertEquals(Arrays.asList("B", "A"), result.getBody());
    }

    @Test
    void createEmployee_validInput() {
        EmployeeInput employee = new EmployeeInput();
        SingleEmployeeServerResponse response = mock(SingleEmployeeServerResponse.class);
        EmployeeOutput employeeOutput = new EmployeeOutput();
        when(response.getData()).thenReturn(employeeOutput);
        when(serverApiClient.createEmployee(any())).thenReturn(response);

        ResponseEntity<EmployeeOutput> result = controller.createEmployee(employee);
        assertEquals(employeeOutput, result.getBody());
    }

    @Test
    void createEmployee_invalidInput() {
        assertThrows(IllegalArgumentException.class, () -> controller.createEmployee(null));
    }

    @Test
    void deleteEmployeeById_success() {
        DeleteEmployeeResponse response = mock(DeleteEmployeeResponse.class);
        when(response.getName()).thenReturn("John");
        when(serverApiClient.deleteEmployee("1")).thenReturn(response);

        ResponseEntity<String> result = controller.deleteEmployeeById("1");
        assertEquals(200, result.getStatusCode().value());
        assertEquals("John", result.getBody());
    }

    @Test
    void deleteEmployeeById_failure() {
        DeleteEmployeeResponse response = mock(DeleteEmployeeResponse.class);
        when(response.getName()).thenReturn("");
        when(serverApiClient.deleteEmployee("1")).thenReturn(response);

        ResponseEntity<String> result = controller.deleteEmployeeById("1");
        assertEquals(500, result.getStatusCodeValue());
    }
}
