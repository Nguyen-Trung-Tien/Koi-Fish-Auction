package com.example.auctionkoi.services;

import com.example.auctionkoi.dto.request.ChangePasswordRequest;
import com.example.auctionkoi.dto.request.UserCreationRequest;
import com.example.auctionkoi.dto.request.UserLoginRequest;
import com.example.auctionkoi.dto.request.UserUpdateRequest;
import com.example.auctionkoi.entities.ActivityLog;
import com.example.auctionkoi.entities.User;
import com.example.auctionkoi.repositories.ActivityLogRepositories;
import com.example.auctionkoi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ActivityLogRepositories activityLogRepositories;

    // lưu nhật kí hoạt đông
    private void activity(User user) {
        ActivityLog activityLog = new ActivityLog();
        activityLog.setUser(user);
        activityLogRepositories.save(activityLog);
    }
    // truy cập vào UserRepository để thực hiện các thao tác với database
    public User createUser(UserCreationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setEmail(request.getEmail());
        user.setWallet(0); // Mặc định tạo tài khoản
        User newUser = userRepository.save(user);

        //Cập nhật đăng nhập gần nhất
        activity(user);
        return newUser;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
    // truy cập vào UserRepository để thực hiện các thao tác với database
    public User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    // trả về một user dựa trên id, nếu không tìm thấy user nào thì sẽ throw ra một exception
    public User updateUser(Long userId, UserUpdateRequest request) {
        User user = getUser(userId);

        if (request.getFirstName() != null && !request.getFirstName().isEmpty()) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null && !request.getLastName().isEmpty()) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        // Kiểm tra và cập nhật wallet
        if (request.getWallet() != null) {
            user.setWallet(request.getWallet());
        }

        user.setRole(request.getRole());

        return userRepository.save(user);
    }
    // xóa một user dựa trên id
    public void deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
    }
    // xóa một user dựa trên id, nếu không tìm thấy user nào thì sẽ throw ra một exception
    public User loginUser(UserLoginRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());

        if (optionalUser.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = optionalUser.get();

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        activity(user);

        return user;
    }

    // trả về một user dựa trên username và password
    public String changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getCurrentPassword())) {
            return "Current password is incorrect";
        }

        if (user.getPassword().equals(request.getNewPassword())) {
            return "duplicate";
        }
        user.setPassword(request.getNewPassword());
        userRepository.save(user);
        return "successful";
    }

}
