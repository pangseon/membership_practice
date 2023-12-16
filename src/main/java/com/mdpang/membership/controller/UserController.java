package com.mdpang.membership.controller;

import com.mdpang.membership.dto.CommonResponseDto;
import com.mdpang.membership.dto.request.UserLoginReq;
import com.mdpang.membership.dto.request.UserSignUpReq;
import com.mdpang.membership.dto.response.UserLoginRes;
import com.mdpang.membership.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<CommonResponseDto> signup(@RequestBody UserSignUpReq req) {
        try {
            userService.saveUser(req);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest()
                .body(new CommonResponseDto("중복된 username 입니다.", HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.status(HttpStatus.CREATED.value())
            .body(new CommonResponseDto("회원가입 성공", HttpStatus.CREATED.value()));

    }

    @PostMapping("/login")
    public UserLoginRes login(@RequestBody UserLoginReq req) {
        return userService.LoginUser(req);
    }
}

