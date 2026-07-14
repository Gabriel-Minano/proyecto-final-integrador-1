//Clase para interceptar la autenticación 

package com.sistema.botica.config;

// import java.util.List;
import java.util.regex.Pattern;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sistema.botica.Repository.UsuarioRepository;
import com.sistema.botica.entity.Usuario;
import com.sistema.botica.service.CustomUserDetailsService;
// import com.sistema.botica.service.UsuarioService;

@Component
public class FormatoPasswordAuthenticationProvider implements AuthenticationProvider {

    // @Autowired
    // private UsuarioService usuarioService;

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    private final CustomUserDetailsService userDetailsService;

    private static final Pattern PASSWORD_PATTERN = Pattern
            .compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,20}$");

    FormatoPasswordAuthenticationProvider(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, CustomUserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }
                        //(?=.*[a-z]) debe contener al menos una letra minúscula 
                        //(?=.*[A-Z]) debe contener al menos una letra mayúscula
                        //(?=.*\d)	Lookahead: debe contener al menos un dígito (\d = [0-9])
                        //`(?=.[!@#$%^&(),.?":{}<>])`debe contener al menos un símbolo especial
                        //.{8,20}	La cadena debe tener entre 8 y 20 caracteres de cualquier tipo
    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        String username = authentication.getName();
        String passwordIngresada = authentication.getCredentials().toString();

        Usuario usuario = usuarioRepository
                .findByUsernameAndEstadoTrue(username)
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        String passwordBD = usuario.getPassword();

        // Uso de la clase encoder para la autenticacion
        if (passwordBD != null && !passwordBD.isBlank()) {

            if (!passwordEncoder.matches(passwordIngresada, passwordBD)) {
                throw new BadCredentialsException("Contraseña incorrecta");
            }

        }
        // Condicional para las expresiones regulares
        else {

            if (!PASSWORD_PATTERN.matcher(passwordIngresada).matches()) {
                throw new BadCredentialsException(
                        "La contraseña no cumple el formato requerido");
            }

        }

        // return new UsernamePasswordAuthenticationToken(
        // usuario.getUsername(),
        // null,
        // authentication.getAuthorities());

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}