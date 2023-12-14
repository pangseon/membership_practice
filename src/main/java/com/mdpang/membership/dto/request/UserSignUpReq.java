package com.mdpang.membership.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserSignUpReq {
    @Pattern(regexp = "[a-z0-9]{4,10}$")
    private String username;
    @Pattern(regexp = "[a-z0-9]{8,20}$")
    private String password;

}
