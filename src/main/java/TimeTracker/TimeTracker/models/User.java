package TimeTracker.TimeTracker.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "userstimetracker")
public class User{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;


    @OneToMany(mappedBy = "owner")
    private List<Task> tasks;

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

    public List<Task> getTasks() {
        return tasks;
    }

    public User(String name) {

        this.name = name;
    }

    public User() {
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }
}
