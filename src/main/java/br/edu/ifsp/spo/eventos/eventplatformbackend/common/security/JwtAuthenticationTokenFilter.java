package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            var authorizationHeader = request.getHeader("Authorization");

            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                authenticateAccount(authorizationHeader);
            }

            filterChain.doFilter(request, response);

        } catch (JWTDecodeException ex) {
            log.warn("JWTDecodeException", ex);
            sendErrorResponse(response, "JWTDecodeException");
        } catch (TokenExpiredException ex) {
            log.warn("TokenExpiredException");
            sendErrorResponse(response, "TokenExpiredException");
        } catch (SignatureVerificationException ex) {
            log.warn("SignatureVerificationException", ex);
            sendErrorResponse(response, "SignatureVerificationException");
        } catch (AlgorithmMismatchException ex) {
            log.warn("AlgorithmMismatchException", ex);
            sendErrorResponse(response, "AlgorithmMismatchException");
        }
    }

    public void authenticateAccount(String authorizationHeader) throws JWTDecodeException {
        DecodedJWT decodeToken = jwtService.decodeToken(authorizationHeader.substring(7));

        var accountId = decodeToken.getSubject();
        var accountEmail = decodeToken.getClaim("email").asString();
        var accountRole = decodeToken.getClaim("role").asString();
        //TODO: organizer claims:
        var organizer = decodeToken.getClaim("organizer").asList(String.class);
        var organizerSubevent = decodeToken.getClaim("organizer_subevent").asList(String.class);
//        var organizerCoordinator = decodeToken.getClaim("organizerCoordinator").asList(String.class);
//        var organizerCollaborator = decodeToken.getClaim("organizerCollaborator").asList(String.class);

        if(!StringUtils.hasText(accountId) || !StringUtils.hasText(accountEmail) || !StringUtils.hasText(accountRole)) {
            log.error("Invalid token: id, email or role not defined");
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + accountRole));
        var userDetails = new JwtUserDetails(UUID.fromString(accountId), accountEmail, authorities, organizer, organizerSubevent);
        var authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, "", authorities);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        String payload = "{\"error\":\""+message+"\"}";
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader("content-type", "application/json");
        response.getWriter().write(payload);
        response.getWriter().flush();
        response.getWriter().close();
    }
}
