package com.virnect.account.port.outbound;

import java.util.Optional;

import com.virnect.account.domain.model.User;

public interface AuthRepositoryCustom {
	Optional<User> getUserBySignIn(String email);
}
