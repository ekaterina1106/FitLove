package com.example.fitlove.models;

import com.example.fitlove.models.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Clients")
public class Clients implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 60, message = "Имя не должно превышать 60 символов")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Роль обязательна")
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    private Set<Role> role = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "Enrollments",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "class_id", referencedColumnName="id"))
    private Set<GroupClasses> classes = new HashSet<>();

    public Clients() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }




    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public @NotNull(message = "Роль обязательна") Set<Role> getRole() {
        return role;
    }

    public boolean isAdmin(){
        return role.contains(Role.ROLE_ADMIN);
    }


}
