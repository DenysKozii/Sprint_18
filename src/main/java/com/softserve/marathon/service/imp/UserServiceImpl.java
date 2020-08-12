package com.softserve.marathon.service.imp;

import com.softserve.marathon.dto.UserRequest;
import com.softserve.marathon.dto.UserResponce;
import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.*;
import com.softserve.marathon.repository.MarathonRepository;
import com.softserve.marathon.repository.ProgressRepository;
import com.softserve.marathon.repository.RoleRepository;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MarathonRepository marathonRepository;
    private final ProgressRepository progressRepository;
    private final RoleRepository roleRepository;
//    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, MarathonRepository marathonRepository, ProgressRepository progressRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.marathonRepository = marathonRepository;
        this.progressRepository = progressRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        initDefaultData();

    }

    private void initDefaultData() {
        if ((roleRepository.count() == 0)
                && (userRepository.count() == 0)) {
            roleRepository.save(new Role("ROLE_MENTOR"));
            roleRepository.save(new Role("ROLE_STUDENT"));
            User user = new User();
            user.setId(1L);
            user.setRole(roleRepository.findByRole("ROLE_MENTOR"));
            user.setEmail("user@test.com");
            user.setFirstName("First name");
            user.setLastName("Last name");
            user.setPassword("password");
            user.setActive(true);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            User user2 = new User();
            user2.setId(2L);
            user2.setRole(roleRepository.findByRole("ROLE_STUDENT"));
            user2.setEmail("user2@test.com");
            user2.setFirstName("First name");
            user2.setLastName("Last name");
            user2.setPassword("password");
            user2.setActive(true);
            user2.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user2);
            Marathon marathon = new Marathon();
            marathon.setTitle("marathon 1");
            marathon.setId(1L);
            marathon.setClosed(false);
            marathonRepository.save(marathon);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User doesn't exist");
        }
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new EntityNotFoundException("User with id " + id + " doesn't exist");
        }
    }

    @Override
    @Transactional
    public User getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user.getId() != null) {
            return user;
        } else {
            throw new EntityNotFoundException("User with id " + email + " doesn't exist");
        }
    }

    @Override
    @Transactional
    public boolean createOrUpdateUser(User entity) {
        entity.setActive(true);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        entity.setRole(roleRepository.findByRole("TRAINEE"));
        System.out.println(entity.getRole());
        if (entity.getId() != null) {
            Optional<User> user = userRepository.findById(entity.getId());
            if (user.isPresent()) {
                User updateUser = user.get();
                updateUser.setEmail(entity.getEmail());
                updateUser.setFirstName(entity.getFirstName());
                updateUser.setLastName(entity.getLastName());
                updateUser.setRole(entity.getRole());
                updateUser.setPassword(entity.getPassword());
                userRepository.save(updateUser);
                return false;
            }
        }
        userRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public boolean createOrUpdateUser(UserRequest userRequest) {
        User entity = new User();
        entity.setActive(true);
        entity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        entity.setRole(roleRepository.findByRole("STUDENT"));
        entity.setEmail(userRequest.getLogin());
        entity.setFirstName(userRequest.getLogin().substring(0, userRequest.getLogin().indexOf("@")));
        entity.setLastName(userRequest.getLogin().substring(0, userRequest.getLogin().indexOf("@")));
        if (entity.getId() != null) {
            Optional<User> user = userRepository.findById(entity.getId());
            if (user.isPresent()) {
                User updateUser = user.get();
                updateUser.setEmail(userRequest.getLogin());
                updateUser.setFirstName(entity.getFirstName());
                updateUser.setLastName(entity.getLastName());
                updateUser.setRole(entity.getRole());
                updateUser.setPassword(entity.getPassword());
                userRepository.save(updateUser);
                return false;
            }
        }
        userRepository.save(entity);
        return true;
    }

    @Override
    @Transactional
    public List<User> getAllByRole(Role role) {
        List<User> roles = userRepository.getAllByRole(role);
        return !roles.isEmpty() ? roles : new ArrayList<>();
    }

    @Override
    @Transactional
    public List<User> getAllByMarathon(Long marathonId) {
        List<User> users = userRepository.getAllByMarathon(marathonId);
        return !users.isEmpty() ? users : new ArrayList<>();
    }

    @Override
    @Transactional
    public boolean addUserToMarathon(User user, Marathon marathon) {
        User userInstance = userRepository.getOne(user.getId());
        Marathon marathonInstance = marathonRepository.getOne(marathon.getId());
        if (!marathonInstance.getUsers().contains(userInstance)) {
            marathonInstance.getUsers().add(userInstance);
            marathonRepository.save(marathonInstance);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean deleteUserFromMarathon(Long userId, Long marathonId) {
        User userInstance = userRepository.getOne(userId);
        Marathon marathonInstance = marathonRepository.getOne(marathonId);
        if (marathonInstance.getUsers().contains(userInstance)) {
            marathonInstance.getUsers().remove(userInstance);
            marathonRepository.save(marathonInstance);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean addUserToTask(User user, Task task) {
        if (!progressRepository.checkProgressExist(user.getId(), task.getId())) {
            Progress progress = new Progress();
            progress.setStarted(LocalDate.now());
            progress.setStatus("start");
            progress.setTask(task);
            progress.setUser(user);
            progressRepository.save(progress);
            return true;
        }
        return false;
    }

    public UserResponce findByLoginAndPassword(UserRequest userRequest) {
        UserResponce result = null;
        User user = userRepository.getUserByEmail(userRequest.getLogin());
        if ((user != null)
                && (passwordEncoder.matches(userRequest.getPassword(),
                user.getPassword()))) {
            result = new UserResponce();
            result.setLogin(userRequest.getLogin());
            result.setRolename(user.getRole().getRole());
        }
        return result;
    }

}

