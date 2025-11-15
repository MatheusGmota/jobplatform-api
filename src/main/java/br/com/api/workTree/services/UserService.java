package br.com.api.workTree.services;

import br.com.api.workTree.domain.dtos.LoginRequestDTO;
import br.com.api.workTree.domain.dtos.LoginResponseDTO;
import br.com.api.workTree.domain.dtos.RegisterRequestDTO;
import br.com.api.workTree.domain.dtos.RegisterResponseDTO;
import br.com.api.workTree.domain.entities.User;
import br.com.api.workTree.domain.errors.BusinessException;
import br.com.api.workTree.infra.security.TokenService;
import br.com.api.workTree.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;
    public LoginResponseDTO login(LoginRequestDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        User user = (User) auth.getPrincipal();
        var token = tokenService.generateToken((User) auth.getPrincipal());

        return new LoginResponseDTO(user.getId(), user.getUsername(), token);
    }

    public RegisterResponseDTO register(@Valid RegisterRequestDTO data) {
        if (this.repository.findByEmail(data.email()).isPresent())
            throw new BusinessException("Usuário já cadastrado");

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User();
        newUser.setName(data.name());
        newUser.setEmail(data.email());
        newUser.setPassword(encryptedPassword);
        newUser.setRole(data.type());
        newUser.setSkills(data.skills());
        newUser.setDescription(data.description());

        this.repository.save(newUser);

        return RegisterResponseDTO.from(newUser);
    }
}
