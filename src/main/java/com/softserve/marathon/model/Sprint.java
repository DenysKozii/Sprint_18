package com.softserve.marathon.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

@Entity
public class Sprint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate finish;
    private LocalDate startDay;
    @NotNull
    @Length(min = 2, max = 20)
    private String title;
    @OneToMany(mappedBy = "sprint", fetch = FetchType.LAZY)
    private List<Task> tasks = new ArrayList<>();
    @ManyToOne
    private Marathon marathon;

    public Sprint() {
    }

    public Sprint(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFinish() {
        return finish;
    }

    public void setFinish(LocalDate finish) {
        this.finish = finish;
    }

    public LocalDate getStartDay() {
        return startDay;
    }

    public void setStartDay(LocalDate startDay) {
        this.startDay = startDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Marathon getMarathon() {
        return marathon;
    }

    public void setMarathon(Marathon marathon) {
        this.marathon = marathon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sprint sprint = (Sprint) o;
        return id == sprint.id &&
                finish.equals(sprint.finish) &&
                startDay.equals(sprint.startDay) &&
                title.equals(sprint.title) &&
                marathon.equals(sprint.marathon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, finish, startDay, title, marathon);
    }
}

