package br.com.api.workTree.services;

import br.com.api.workTree.domain.dtos.*;
import br.com.api.workTree.domain.entities.User;
import br.com.api.workTree.domain.errors.BusinessException;
import br.com.api.workTree.domain.errors.NotFoundException;
import br.com.api.workTree.infra.security.TokenService;
import br.com.api.workTree.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public UserResponseDTO obterPorId(Long id) {
        return repository.findById(id).map(UserResponseDTO::from).orElseThrow(() -> new NotFoundException("Nenhum usuário encontrado para o ID: " + id));
    }

    public UserResponseDTO editar(UserRequestDTO user, Long id) {
        User userToUpdate = repository.findById(id).orElseThrow(() -> new NotFoundException("Nenhum usuário encontrado para o ID: " + id));

        String encryptedPassword = new BCryptPasswordEncoder().encode(user.password());

        userToUpdate.setName(user.name());
        userToUpdate.setPassword(encryptedPassword);
        userToUpdate.setEmail(user.email());
        userToUpdate.setDescription(user.description());
        userToUpdate.setSkills(user.skills());

        User updatedUser = repository.save(userToUpdate);
        return UserResponseDTO.from(updatedUser);
    }

    public LoginResponseDTO login(LoginRequestDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            User user = (User) auth.getPrincipal();
            var token = tokenService.generateToken((User) auth.getPrincipal());

            return new LoginResponseDTO(user.getId(), user.getUsername(), token);
        } catch (AuthenticationException e) {
            throw new BusinessException("Usuário ou senha incorretos");
        }
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

    public void delete(Long id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Nenhum usuário encontrado para o ID: " + id));
        repository.delete(user);
    }
}
