package mate.academy.bookstore.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import mate.academy.bookstore.dto.user.UserRegistrationRequestDto;
import mate.academy.bookstore.dto.user.UserResponseDto;
import mate.academy.bookstore.exception.RegistrationException;
import mate.academy.bookstore.mapper.UserMapper;
import mate.academy.bookstore.model.Role;
import mate.academy.bookstore.model.RoleName;
import mate.academy.bookstore.model.User;
import mate.academy.bookstore.repository.role.RoleRepository;
import mate.academy.bookstore.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final RoleName DEFAULT_ROLE_NAME = RoleName.ROLE_USER;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RegistrationException("Can't register user with email "
                    + userDto.getEmail());
        }
        User user = userMapper.toModel(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Set<Role> userRoles = new HashSet<>();
        Role userRole = roleRepository.findByName(DEFAULT_ROLE_NAME)
                .orElseThrow(() -> new RegistrationException("Default user role not found"));
        userRoles.add(userRole);
        user.setRoles(userRoles);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.map(userMapper::toDto).orElseThrow(
                () -> new RuntimeException("Can't find user by email " + email));
    }
}
