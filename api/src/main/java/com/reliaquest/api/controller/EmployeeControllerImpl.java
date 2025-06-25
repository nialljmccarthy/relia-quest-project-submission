package com.reliaquest.api.controller;

import com.reliaquest.api.client.ServerApiClient;
import com.reliaquest.api.model.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "${apiPrefix}")
public class EmployeeControllerImpl implements IEmployeeController<EmployeeOutput, EmployeeInput> {

    @Autowired
    private ServerApiClient serverApiClient;

    @Override
    public ResponseEntity<List<EmployeeOutput>> getAllEmployees() {
        log.debug("Fetching all employees from server");
        ListEmployeeServerResponse employeeResponse = serverApiClient.getAllEmployeesFromServer();
        return ResponseEntity.ok(employeeResponse.getData());
    }

    @Override
    public ResponseEntity<List<EmployeeOutput>> getEmployeesByNameSearch(String searchString) {
        ListEmployeeServerResponse employeeResponse = serverApiClient.getAllEmployeesFromServer();
        List<EmployeeOutput> employees = employeeResponse.getData();
        log.debug("Filtering employees by name search: {}", searchString);
        List<EmployeeOutput> filteredEmployees = employees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .toList();
        return ResponseEntity.ok(filteredEmployees);
    }

    @Override
    public ResponseEntity<EmployeeOutput> getEmployeeById(String id) {
        SingleEmployeeServerResponse employeeResponse = serverApiClient.getEmployeeById(id);
        if (employeeResponse != null && employeeResponse.getData() != null) {
            return ResponseEntity.ok(employeeResponse.getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        ListEmployeeServerResponse employeeResponse = serverApiClient.getAllEmployeesFromServer();
        log.debug("Filtering employees to find the highest salary");
        List<EmployeeOutput> employees = employeeResponse.getData();
        int highestSalary =
                employees.stream().mapToInt(EmployeeOutput::getSalary).max().orElse(0);
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        ListEmployeeServerResponse employeeResponse = serverApiClient.getAllEmployeesFromServer();
        log.debug("Filtering employees to find the top 10 highest earning employee names");
        List<EmployeeOutput> employees = employeeResponse.getData();

        List<String> top10SalariedEmployeeNames = employees.stream()
                .sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary()))
                .limit(10)
                .map(EmployeeOutput::getName)
                .toList();
        return ResponseEntity.ok(top10SalariedEmployeeNames);
    }

    @Override
    public ResponseEntity<EmployeeOutput> createEmployee(EmployeeInput employeeInput) {
        if (employeeInput == null) {
            throw new IllegalArgumentException("Invalid input type. Expected Employee object.");
        }
        log.debug("Creating employee object: {}", employeeInput);
        SingleEmployeeServerResponse employeeResponse = serverApiClient.createEmployee(employeeInput);
        if (employeeResponse != null && employeeResponse.getData() != null) {
            log.debug("Created employee object: {}", employeeResponse.getData());
            return ResponseEntity.ok(employeeResponse.getData());
        } else {
            log.debug("Failed to create employee object: {}", employeeResponse);
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        log.debug("Deleting employee object: {}", id);
        DeleteEmployeeResponse deleteEmployeeResponse = serverApiClient.deleteEmployee(id);
        String employeeName = deleteEmployeeResponse.getName();
        if (employeeName != null && !employeeName.isEmpty()) {
            log.debug("Deleted employee with ID [{}] and name [{}]", id, employeeName);
            return ResponseEntity.ok(employeeName);
        } else {
            log.debug("Failed to delete employee with ID [{}]", id);
            return ResponseEntity.status(500).body("Failed to delete employee");
        }
    }
}
