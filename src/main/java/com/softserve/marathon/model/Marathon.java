package com.softserve.marathon.model;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Marathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private boolean closed;
    @NotNull
    @Length(min = 3, max = 20)
    private String title;
    @OneToMany(mappedBy = "marathon")
    private List<Sprint> sprints = new ArrayList<>();
    @ManyToMany(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinTable(name = "marathon_user",
            joinColumns = @JoinColumn(name = "id_marathon"),
            inverseJoinColumns = @JoinColumn(name = "id_user"))
    private List<User> users = new ArrayList<>();

    public Marathon() {
    }

    public Marathon(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Marathon(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Sprint> getSprints() {
        return sprints;
    }

    public void setSprints(List<Sprint> sprints) {
        this.sprints = sprints;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Marathon marathon = (Marathon) o;

        if (closed != marathon.closed) return false;
        return title != null ? title.equals(marathon.title) : marathon.title == null;
    }

    @Override
    public int hashCode() {
        int result = (closed ? 1 : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
