package com.softserve.marathon.model;

//import lombok.Getter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "roles")
//@Getter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String role;

    public Role() {
    }

    public Role(String role) {
        this.role = role;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "role")
    private Set<User> users;

    public String getRole() {
        return role;
    }
}
