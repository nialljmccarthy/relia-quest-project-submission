package com.reliaquest.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Please <b>do not</b> modify this interface. If you believe there's a bug or the API contract does not align with our
 * mock web server... that is intentional. Good luck!
 *
 * @implNote It's uncommon to have a web controller implement an interface; We include such design pattern to
 * ensure users are following the desired input/output for our API contract, as outlined in the code assessment's README.
 *
 * @param <Entity> object representation of an Employee
 * @param <Input> object representation of a request body for creating Employee(s)
 */
public interface IEmployeeController<Entity, Input> {

    @GetMapping()
    @Operation(summary = "Get all employees", description = "Returns a list of all employees")
    ResponseEntity<List<Entity>> getAllEmployees();

    @GetMapping("/search/{searchString}")
    @Operation(
            summary = "Get employees by name search",
            description = "Returns a list of employees filtered by name search string")
    ResponseEntity<List<Entity>> getEmployeesByNameSearch(@PathVariable String searchString);

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID", description = "Returns a single employee by their ID")
    ResponseEntity<Entity> getEmployeeById(@PathVariable String id);

    @GetMapping("/highestSalary")
    @Operation(
            summary = "Get highest salary of all employees",
            description = "Returns the highest salary value among all employees")
    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    @GetMapping("/topTenHighestEarningEmployeeNames")
    @Operation(
            summary = "Get top ten highest earning employee names",
            description = "Returns a list of names of the top ten highest earning employees")
    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    @PostMapping()
    @Operation(summary = "Post a new employee", description = "Returns the created employee object")
    ResponseEntity<Entity> createEmployee(@RequestBody Input employeeInput);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete employee by ID",
            description = "Deletes an employee by their ID and returns the employee name")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id);
}
