package com.outreach.datasynchronizer.service;

import com.outreach.datasynchronizer.dto.UserRequest;
import com.outreach.datasynchronizer.dto.UserResponse;
import com.outreach.datasynchronizer.entity.User;
import com.outreach.datasynchronizer.event.UserChangeEvent;
import com.outreach.datasynchronizer.event.UserChangeEventPublisher;
import com.outreach.datasynchronizer.exception.UserNotFoundException;
import com.outreach.datasynchronizer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserChangeEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return toResponse(user);
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        User user = toEntity(request);
        User saved = userRepository.save(user);
        eventPublisher.publish(buildEvent(saved, UserChangeEvent.ChangeType.CREATED));
        return toResponse(saved);
    }

    @Transactional
    public UserResponse updateUser(UUID userId, UserRequest request) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        existing.setName(request.getName());
        existing.setFirstName(request.getFirstName());
        existing.setMiddleName(request.getMiddleName());
        existing.setLastName(request.getLastName());
        existing.setEmailId(request.getEmailId());
        existing.setPhoneNumber(request.getPhoneNumber());
        existing.setAttributes(request.getAttributes());

        User updated = userRepository.save(existing);
        eventPublisher.publish(buildEvent(updated, UserChangeEvent.ChangeType.UPDATED));
        return toResponse(updated);
    }

    @Transactional
    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        userRepository.deleteById(userId);
        eventPublisher.publish(UserChangeEvent.builder()
                .userId(userId)
                .changeType(UserChangeEvent.ChangeType.DELETED)
                .timestamp(Instant.now())
                .build());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByEmail(String emailId) {
        return userRepository.findByEmailId(emailId).stream()
                .map(this::toResponse)
                .toList();
    }

    // --- Mapping helpers ---

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .phoneNumber(user.getPhoneNumber())
                .attributes(user.getAttributes())
                .build();
    }

    private User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .lastName(request.getLastName())
                .emailId(request.getEmailId())
                .phoneNumber(request.getPhoneNumber())
                .attributes(request.getAttributes())
                .build();
    }

    private UserChangeEvent buildEvent(User user, UserChangeEvent.ChangeType type) {
        return UserChangeEvent.builder()
                .userId(user.getUserId())
                .changeType(type)
                .timestamp(Instant.now())
                .name(user.getName())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .emailId(user.getEmailId())
                .phoneNumber(user.getPhoneNumber())
                .attributes(user.getAttributes())
                .build();
    }
}

