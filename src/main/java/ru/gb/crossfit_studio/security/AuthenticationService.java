package ru.gb.crossfit_studio.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gb.crossfit_studio.model.DTO.UserDTO;
import ru.gb.crossfit_studio.model.Role;
//import ru.gb.crossfit_studio.model.RoleName;
import ru.gb.crossfit_studio.service.UserService;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthResponse signUp(UserDTO request) {
        request.setPassword(passwordEncoder.encode(request.getPassword()));

        var user = User.builder()
                .username(request.getLogin())
                .password(request.getPassword())
                .roles(Role.valueOf(request.getRole()).getName())
                .build();

        userService.create(request);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */

    public JwtAuthResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getLogin(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getLogin());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthResponse(jwt);
    }

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }
}
