package com.softserve.marathon.model;

//import lombok.Getter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "roles")
//@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String role;

    public Role(String role) {
        this.role = role;
    }

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
//    private Set<User> users;

    public String getRole() {
        return role;
    }
}
