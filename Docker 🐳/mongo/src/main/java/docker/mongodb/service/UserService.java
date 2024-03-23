package docker.mongodb.service;

import docker.mongodb.mongo.User;
import docker.mongodb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No user by ID: " + id));
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

}
