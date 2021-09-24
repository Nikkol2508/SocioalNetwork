package application.security;

import application.exceptions.Error;
import application.exceptions.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.MimeTypeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        ErrorResponse error = new ErrorResponse(Error.UNAUTHORIZED.getErrorName(), e.getMessage(),
                httpServletRequest.getPathInfo());
        httpServletResponse.setStatus(401);
        httpServletResponse.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(error));
        httpServletResponse.getWriter().flush();
    }
}
