package se.nackademin.backend2.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import se.nackademin.backend2.user.domain.User;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOG = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private AuthenticationManager authenticationManager;
    private JWTIssuer jwtIssuer;
    private ObjectMapper objectMapper;

    public JWTAuthenticationFilter(final AuthenticationManager authenticationManager, final JWTIssuer jwtIssuer, final ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.jwtIssuer = jwtIssuer;
        this.objectMapper = objectMapper;
    }


    @Override
    public Authentication attemptAuthentication(final HttpServletRequest req,
                                                final HttpServletResponse res) throws AuthenticationException {
        return getPrincipal(req)
                .map(user -> authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword(),
                                new ArrayList<>()))
                )
                .orElse(null);
    }

    private Optional<UserDto> getPrincipal(HttpServletRequest req) {
        try {
            return Optional.of(objectMapper.readValue(req.getInputStream().readAllBytes(), UserDto.class));
        } catch (IOException e) {
            LOG.info("Unable to fetch user from request");
            return Optional.empty();
        }
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest req,
                                            final HttpServletResponse res,
                                            final FilterChain chain,
                                            final Authentication auth) throws IOException {
        User user = (User) auth.getPrincipal();
        String token = jwtIssuer.generateToken(user);

        /*
            TODO: Uppgift 3
            Vi ??r nu i ett authentication filter. Dvs n??got d??r vi kan f??ra saker med requestet innan det kommer till applikationen.
            Det vi vill g??ra i detta filter ??r att vi vill ist??llet f??r att returera "Du ??r inloggad!" s?? vill vi returnera
            en JWT-token.

            Vi har registrerat detta filter s?? att det kommer att k??ras efter det att en anv??ndare har authentiserats och med oss
            har vi en "Authentication auth" med anv??ndarinformationen. Du kan komma ??t anv??ndaren genom
            User user = (User) auth.getPrincipal()

            Kolla p?? JWTIssuer f??r att se hur man genererar tokens.
            Returnera en JWT-token fr??n denna klass.

            Testa i swagger att logga in igen, och d?? borde ni f?? en token tillbaka

            ??ppna https://jwt.io/ och pasta in er token
            Kolla p?? resultatet.
         */
//Alternativ res.getWriter().write(jwt.Issuer.generateToken()user);
        res.getWriter().write(token);
        res.getWriter().flush();



    }


}