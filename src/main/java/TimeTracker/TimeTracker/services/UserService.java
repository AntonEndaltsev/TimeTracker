package TimeTracker.TimeTracker.services;

import TimeTracker.TimeTracker.models.Task;
import TimeTracker.TimeTracker.models.User;
import TimeTracker.TimeTracker.repositories.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findOne(int id){
        Optional<User> foundUser = userRepository.findById(id);
        return foundUser.orElse((null));
    }

    public User findByNameEquals(String name){
        Optional<User> foundUser = userRepository.findByNameEquals(name);
        return foundUser.orElse((null));
    }

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    @Transactional
    public void update(int id, User updatedUser){
        updatedUser.setId(id);
        userRepository.save(updatedUser);
    }

    @Transactional
    public void delete(int id){
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByName(String name){
        return userRepository.findByNameEquals(name);
    }

    public List<Task> getTaskByUserId(int id){
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()){

            Hibernate.initialize(user.get().getTasks());

            user.get().getTasks().forEach(task -> {
                //long diffInMillies = Math.abs(book.getTakenAt().getTime() - new Date().getTime());
                //if (diffInMillies>864000000)
                //    book.setExpired(true);
            });
            return user.get().getTasks();
        }
        else
            return Collections.emptyList();
    }
}
