package se.nackademin.backend2.user.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.nackademin.backend2.user.domain.User;
import se.nackademin.backend2.user.domain.UserRepository;

import java.util.List;
import java.util.UUID;

public class SignupService {
    public static final Logger LOG = LoggerFactory.getLogger(SignupService.class);

    private final UserRepository userRepository;

    public SignupService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void signUp(final String username, final String password, final List<String> roles) {
        LOG.info("Signing up user {}", username);
        final User user = new User(username, password, roles);
        userRepository.save(user);
    }
}
