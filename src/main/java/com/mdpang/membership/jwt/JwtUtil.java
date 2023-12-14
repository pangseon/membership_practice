package com.mdpang.membership.jwt;

import com.mdpang.membership.entity.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Slf4j
//@Component
public class JwtUtil {

    // JWT 데이터
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";

    private final long TOKEN_TIME = 60*60*1000L;
    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;

    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 생성

    //토큰 생성
    public String createToken(String username, UserRoleEnum role){
        Date date = new Date();

        return BEARER_PREFIX+
            Jwts.builder()
                .setSubject(username)
                .claim(AUTHORIZATION_KEY,role)
                .setExpiration(new Date(date.getTime()+TOKEN_TIME))
                .setIssuedAt(date)
                .signWith(key,signatureAlgorithm)
                .compact();
    }

    // 생성된 JWT를 Cookie에 저장
    public void addJwtToCookie(String token, HttpServletResponse res){
        try{
            token = URLEncoder.encode(token,"utf-8").replaceAll("\\+","%20");

            Cookie cookie =new Cookie(AUTHORIZATION_HEADER, token);
            cookie.setPath("/");

            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e){
            log.error(e.getMessage());
        }

    }

    //Cookie에 들어있던 JWT 토큰을 Substring
    public String substringToken(String tokenValue){
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)){
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }
    // JWT 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token);
            return true;
        }catch (SecurityException | MalformedJwtException | SignatureException e){
            log.error("유효하지 않는 JWT 서명입니다.");
        }catch (ExpiredJwtException e){
            log.error("만료된 토큰입니다.");
        }catch (UnsupportedJwtException e){
            log.error("지원되지 않는 토큰 입니다.");
        }catch (IllegalArgumentException e){
            log.error("잘못된 JWT 토큰입니다.");
        }
        return false;
    }

    // JWT에서 사용자 정보 가져오기
    public Claims getUserInfo(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJwt(token).getBody();
    }


}
