package application.security;

import io.jsonwebtoken.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@NoArgsConstructor
@Slf4j
public class JwtTokenProvider {

    private String secret;

    private long validityInMilliseconds;

    private JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.token.secret}") String secret,
                            @Value("${jwt.token.expired}") long validityInMilliseconds,
                            JwtUserDetailsService userDetailsService) {
        this.secret = secret;
        this.validityInMilliseconds = validityInMilliseconds;
        this.jwtUserDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String email) {

        Claims claims = Jwts.claims().setSubject(email);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        log.info("IN createToken - claims: {}", claims);
        log.info("IN createToken - new token: {}", token);
        return token;
    }

    public Authentication getAuthentication(String token) {
        JwtUser userDetails = jwtUserDetailsService.loadUserByUsername(getUsername(token));
        log.info("IN getAuthentication - userDetails: {}", userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        String email = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
        log.info("IN getUsername - got email: {}", email);
        return email;
    }

    public String resolveToken(HttpServletRequest req) {
        log.info("IN resolveToken - token in header: {}", req.getHeader("Authorization"));
        return req.getHeader("Authorization");
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            log.info("IN validateToken - our claims: {}", claimsJws.getBody());
            return claimsJws.getBody().getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            log.error("IN validateToken - Exception {}", e.getMessage());
//            throw new AccessDeniedException("JWT token is expired");
            return false;
        }
    }
}
