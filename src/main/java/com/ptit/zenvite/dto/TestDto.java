package com.ptit.zenvite.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestDto {

    @NotBlank(groups = TestDto.Create.class, message = "validation.not-blank")
    private String testField;

    public interface Create {};

    public interface Update {};
}
