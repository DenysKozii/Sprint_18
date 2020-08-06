package com.softserve.marathon.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "DATE")
    private LocalDate created;

    @NotNull
    @Length(min = 2, max = 20)
    private String title;

    @Column(columnDefinition = "DATE")
    private LocalDate updated;

    @ManyToOne
    private Sprint sprint;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<Progress> progresses = new ArrayList<>();

    public Task() {
    }

    public Task(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDate updated) {
        this.updated = updated;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    public List<Progress> getProgresses() {
        return progresses;
    }

    public void setProgresses(List<Progress> progresses) {
        this.progresses = progresses;
    }
}
