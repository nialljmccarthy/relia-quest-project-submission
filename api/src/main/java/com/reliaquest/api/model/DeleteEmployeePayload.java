package com.reliaquest.api.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeleteEmployeePayload {
    String name;

    public DeleteEmployeePayload(String name) {
        this.name = name;
    }

}
