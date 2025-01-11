package com.ptit.zenvite.controller;

import com.ptit.zenvite.dto.TestDto;
import com.ptit.zenvite.exception.DataConstraintException;
import com.ptit.zenvite.exception.constant.ErrorKey;
import com.ptit.zenvite.exception.constant.ErrorTitle;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody @Validated(TestDto.Create.class) TestDto testDto) {
        if(testDto.getTestField().equals("throw")) {
            throw DataConstraintException.builder()
                    .errorKey(ErrorKey.Test.TEST_FIELD)
                    .entityName(ErrorKey.Test.class.getName())
                    .title(ErrorTitle.Test.DUPLICATE_TEST_FIELD)
                    .build();
        }

        return ResponseEntity.ok("test api successfully.");
    }
}
