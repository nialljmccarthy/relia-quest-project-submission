package com.reliaquest.api.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ListEmployeeServerResponse {
    private List<EmployeeOutput> data;
    private String status;
}
