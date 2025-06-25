package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ListEmployeeServerResponse {
    private List<EmployeeOutput> data;
    private String status;

}
