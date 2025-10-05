package br.com.fiap.dailyreminder.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.fiap.dailyreminder.services.TokenJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthorizationFilter extends OncePerRequestFilter {

    @Autowired
    TokenJwtService tokenJwtService;

    @Override
    protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain
    ) throws ServletException, IOException {

      var token = getTokenFromHeader(request);

      System.out.println(token);

      if (token != null) {
        var decodedToken = tokenJwtService.validate(token);

        System.out.println(decodedToken.getSubject());


        if (decodedToken == null) {
          response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }

        request.setAttribute("user_id", decodedToken.getSubject());

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(decodedToken.getSubject(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println(auth);


//        if (token != null) {
//            var usuario = tokenJwtService.validate(token);
//            Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
      }

      filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request){

        var header = request.getHeader("Authorization");

        if (header == null) {
            return null;
        }

        return header.replace("Bearer ", "");
    }
    
}
