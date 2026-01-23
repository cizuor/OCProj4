package com.openclassrooms.mddapi.security.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.models.User;
import com.openclassrooms.mddapi.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	 private final UserRepository userRepository;
	 
	 

    public UserDetailsServiceImpl(UserRepository userRepository) {
	super();
	this.userRepository = userRepository;
	}



	@Override
    @Transactional
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPseudo(login,login)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec : " + login));

        // On le transforme en UserDetailsImpl grâce à la méthode build
        return UserDetailsImpl.build(user);
    }
}
