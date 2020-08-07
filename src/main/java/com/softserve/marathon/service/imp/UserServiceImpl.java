package com.softserve.marathon.service.imp;

import com.softserve.marathon.exception.EntityNotFoundException;
import com.softserve.marathon.model.*;
import com.softserve.marathon.repository.MarathonRepository;
import com.softserve.marathon.repository.ProgressRepository;
import com.softserve.marathon.repository.UserRepository;
import com.softserve.marathon.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  MarathonRepository marathonRepository;
    @Autowired
    private  ProgressRepository progressRepository;


//    public UserServiceImpl(UserRepository userRepository, MarathonRepository marathonRepository, ProgressRepository progressRepository) {
//        this.userRepository = userRepository;
//        this.marathonRepository = marathonRepository;
//        this.progressRepository = progressRepository;
//    }

    @Override
    @Transactional
    public void delete(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){
             userRepository.deleteById(id);
        }else {
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
        if (user.isPresent()){
            return user.get();
        } else {
                throw new EntityNotFoundException("User with id " + id + " doesn't exist");
        }
    }

    @Override
    public User getUserByEmail(String email) {
        User user = userRepository.getUserByEmail(email);
        if (user.getId() != null){
            return user;
        } else {
                throw new EntityNotFoundException("User with id " + email + " doesn't exist");
        }
    }

    @Override
    @Transactional
    public boolean createOrUpdateUser(User entity) {
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
    public List<User> getAllByRole(Role role) {
        List<User> roles = userRepository.getAllByRole(role);
        return !roles.isEmpty() ? roles : new ArrayList<>();
    }

    @Override
    public List<User> getAllByMarathon(Long marathonId) {
        List<User> users = userRepository.getAllByMarathon(marathonId);
        return !users.isEmpty() ? users : new ArrayList<>();
    }

    @Override
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

    // the same functionality in progress service
    // com.softserve.marathon.softservemarathon.service.imp.ProgressServiceImpl.addTaskForStudent
    @Override
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(email);
    }
}
