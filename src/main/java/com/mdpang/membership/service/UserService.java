package com.mdpang.membership.service;


import com.mdpang.membership.dto.CommonResponseDto;
import com.mdpang.membership.dto.request.UserSignUpReq;
import com.mdpang.membership.repository.UserRepository;
import com.mdpang.membership.entity.UserEntity;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CommonResponseDto saveUser(UserSignUpReq req) {
        if (req.getUsername().equals(userRepository.findByUsername(req.getUsername()))){
            throw new IllegalArgumentException("이미 존재하는 회원입니다");
        }
        return UserMapper.INSTANCE.toUserSaveGetRes(
            userRepository.save(UserEntity.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .build()));

    }






    @Mapper
    public interface UserMapper {

        UserService.UserMapper INSTANCE = Mappers.getMapper(UserService.UserMapper.class);

        CommonResponseDto toUserSaveGetRes(UserEntity userEntity);
    }

}
