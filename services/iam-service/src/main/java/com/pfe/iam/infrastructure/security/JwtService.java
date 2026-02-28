package com.pfe.iam.infrastructure.security;

import com.pfe.iam.domain.model.User;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class JwtService {

    // A placeholder for actual JWT implementation using jjwt or similar
    // We provide a dummy token for now to allow compiling and startup verification
    public String generateToken(User user) {
        return "dummy-jwt-token-" + UUID.randomUUID().toString();
    }
}
