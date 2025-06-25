package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SingleEmployeeServerResponse {

    private EmployeeOutput data;
    private String status;
}
