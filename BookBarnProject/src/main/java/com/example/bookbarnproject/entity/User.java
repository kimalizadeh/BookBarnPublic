package com.example.bookbarnproject.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;



@Entity
@Table(name ="users")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20, unique = true)
    @Pattern(regexp="^[a-z0-9]{4,20}$", message="Username can only contains lowercase letters and numbers. And must be between 4-20 characters.")
    private String username;

    @Email
    @Column(nullable = false, length = 50)
    private String email;

    @Column(nullable = false, length = 100)
    @Size(min = 6, max = 100, message = "Password must be between 6-100 characters long")
    private String password;

    @Transient
    private String passwordRepeat;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z]{2,50}$",message="First name can only contains lowercase letters. And must be between 2-20 characters.")
    private String firstName;

    @Column(nullable = false)
    @Pattern(regexp = "^[a-zA-Z]{2,50}$",message="Last name can only contains lowercase letters. And must be between 2-20 characters.")
    private String lastName;

    @Column(nullable = false, length = 100)
    @Size(min = 10, max = 100, message = "Address must be between 10-100 characters long")
    private String address;

    @Max(150)
    @Min(0)
    @NotNull
    private int age;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private boolean deprecated = false;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL) // orphanRemoval = true
    private Set<Rental> rentals;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Role getRole() {
        return role;
    }

    public String getRoleString(){
        return role.toString();
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setRole(String role){
        this.role = Role.valueOf(role);
    }

    public Set<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(Set<Rental> rentals) {
        this.rentals = rentals;
    }

    public enum Sex {Male, Female, Other}
    public enum Role {Admin, Member}

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public User(Long id, String username, String email, String password, String firstName, String lastName, String address, int age, String sex, String role){
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.age = age;
        this.sex = Sex.valueOf(sex);
        this.role = Role.valueOf(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        return list;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", passwordRepeat='" + passwordRepeat + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                ", role=" + role +
                '}';
    }
}
