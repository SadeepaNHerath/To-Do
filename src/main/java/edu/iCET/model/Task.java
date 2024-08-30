package edu.iCET.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Task {
    private Integer id;
    private String title;
    private String status;
    private LocalDate completionDate;

}