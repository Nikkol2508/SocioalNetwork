package application.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@AllArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken(request);
//        if (token != null) {
//            Authentication authentication = jwtTokenProvider.getAuthentication(token);
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            log.info("IN doFilterInternal - authentication: {}", authentication);
            if (authentication != null) {
                log.info("IN doFilterInternal - principal of authentication: {}", authentication.getPrincipal());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("IN doFilterInternal - JwtTokenFilter in using, authentication: {}", SecurityContextHolder.getContext().getAuthentication());
            }
        }  else {
//            SecurityContextHolder.getContext().setAuthentication(null);
//            log.info("IN doFilterInternal - authentication in ContextHolder: {}", SecurityContextHolder.getContext().getAuthentication());
        }
        chain.doFilter(request, response);
    }
}
