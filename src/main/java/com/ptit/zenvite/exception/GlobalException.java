package com.ptit.zenvite.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.zalando.problem.ThrowableProblem;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Builder
public class GlobalException extends ThrowableProblem {

    private String errorKey;

    private String entityName;

    private String title;
}
