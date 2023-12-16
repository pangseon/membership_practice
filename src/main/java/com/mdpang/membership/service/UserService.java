package com.mdpang.membership.service;


import com.mdpang.membership.dto.CommonResponseDto;
import com.mdpang.membership.dto.request.UserLoginReq;
import com.mdpang.membership.dto.request.UserSignUpReq;
import com.mdpang.membership.dto.response.UserLoginRes;
import com.mdpang.membership.dto.response.UserSignUpRes;
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

    public UserSignUpRes saveUser(UserSignUpReq req) {
        if (req.getUsername().equals(userRepository.findByUsername(req.getUsername()))){
            throw new IllegalArgumentException("이미 존재하는 회원입니다");
        }
        return UserMapper.INSTANCE.toUserSaveGetRes(
            userRepository.save(UserEntity.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .build()));

    }
    public UserLoginRes LoginUser(UserLoginReq req){
        UserEntity user = userRepository.findByUsername(req.getUsername());
        if (user==null){
            throw new IllegalArgumentException(
                "등록된 유저가 없습니다."
            );
        }
        else if (!passwordEncoder.matches(req.getPassword(), user.getPassword())){
            throw new IllegalArgumentException(
                "비밀번호가 일치하지 않습니다"
            );
        }
        return UserMapper.INSTANCE.toUserLoginGetRes(user);

    }






    @Mapper
    public interface UserMapper {

        UserService.UserMapper INSTANCE = Mappers.getMapper(UserService.UserMapper.class);

        UserSignUpRes toUserSaveGetRes(UserEntity userEntity);

        UserLoginRes toUserLoginGetRes(UserEntity userEntity);

    }

}
