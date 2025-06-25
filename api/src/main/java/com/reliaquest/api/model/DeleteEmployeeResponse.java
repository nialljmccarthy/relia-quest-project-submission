package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteEmployeeResponse {

    private String status;
    private boolean data;
    private String name;

    public DeleteEmployeeResponse(String status, boolean data) {
        this.status = status;
        this.data = data;
    }

}
