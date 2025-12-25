
package com.virtualinterviewer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String targetRole;
    
    @Enumerated(EnumType.STRING)
    private UserRole role;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum UserRole {
        USER, ADMIN
    }

    public User() {}

    public User(Long id, String email, String password, String firstName, String lastName, String phoneNumber, String targetRole, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.targetRole = targetRole;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(String targetRole) {
        this.targetRole = targetRole;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
// import com.fasterxml.jackson.annotation.JsonIgnore;
// import jakarta.persistence.*;
// import java.time.LocalDateTime;
// import java.util.List;

// @Entity
// @Table(name = "users")
// public class User {
//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     private Long id;

//     @Column(unique = true, nullable = false)
//     private String email;

//     @Column(nullable = false)
//     private String password;

//     @Column(nullable = false)
//     private String firstName;

//     @Column(nullable = false)
//     private String lastName;

//     @Column(length = 10)
//     private String phoneNumber;

//     @Column(length = 500)
//     private String targetRole;

//     @Column(columnDefinition = "TEXT")
//     private String resumePath;

//     @Column(columnDefinition = "TEXT")
//     private String bio;

//     @Enumerated(EnumType.STRING)
//     private UserRole role = UserRole.USER;

//     @Column(nullable = false)
//     private LocalDateTime createdAt = LocalDateTime.now();

//     private LocalDateTime updatedAt;

//     @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//     @JsonIgnore
//     private List<Interview> interviews;

//     public enum UserRole {
//         USER, ADMIN
//     }

//     public User() {}

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }

//     public String getPassword() { return password; }
//     public void setPassword(String password) { this.password = password; }

//     public String getFirstName() { return firstName; }
//     public void setFirstName(String firstName) { this.firstName = firstName; }

//     public String getLastName() { return lastName; }
//     public void setLastName(String lastName) { this.lastName = lastName; }

//     public String getPhoneNumber() { return phoneNumber; }
//     public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

//     public String getTargetRole() { return targetRole; }
//     public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

//     public String getResumePath() { return resumePath; }
//     public void setResumePath(String resumePath) { this.resumePath = resumePath; }

//     public String getBio() { return bio; }
//     public void setBio(String bio) { this.bio = bio; }

//     public UserRole getRole() { return role; }
//     public void setRole(UserRole role) { this.role = role; }

//     public LocalDateTime getCreatedAt() { return createdAt; }
//     public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

//     public LocalDateTime getUpdatedAt() { return updatedAt; }
//     public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

//     public List<Interview> getInterviews() { return interviews; }
//     public void setInterviews(List<Interview> interviews) { this.interviews = interviews; }
// }
