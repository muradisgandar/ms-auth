package az.gdg.msauth.security.filter;

import az.gdg.msauth.security.bean.CustomUserDetail;
import az.gdg.msauth.security.service.impl.UserDetailServiceImpl;
import az.gdg.msauth.security.util.TokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@AllArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

    private final TokenUtil tokenUtil;
    private final UserDetailServiceImpl userDetailService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String jwtFromHeader = extractJwtFromHeader(request);

        if (jwtFromHeader != null && tokenUtil.isTokenValid(jwtFromHeader)) {
            String email = tokenUtil.getMailFromToken(jwtFromHeader);

            CustomUserDetail userDetails = (CustomUserDetail) userDetailService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtFromHeader(HttpServletRequest req) {
        // if token generated and sended with Bearer prefix, then check this in filter
//         && authHeader.startsWith("Bearer ");
        String authHeader = req.getHeader("X-Auth-Token");
        if (StringUtils.hasText(authHeader)) {
//          return authHeader.substring(7);
            return authHeader;
        }

        return null;
    }

    // if occurs any exception, then return error information to client
//    private void httpResponseWithNotAcceptableStatus(HttpServletResponse response) throws IOException {
//        ResponseDto responseDto = new ResponseDto(HttpStatus.NOT_ACCEPTABLE.value(), "Token is not valid!");
//        byte[] responseAsBytes = (new ObjectMapper()).writeValueAsString(responseDto).getBytes();
//        response.setHeader("Content-Type", "application/json");
//        response.setStatus(HttpStatus.NOT_ACCEPTABLE.value());
//        response.getOutputStream().write(responseAsBytes);
//
//    }


}
