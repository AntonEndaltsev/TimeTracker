package TimeTracker.TimeTracker.DTO;

import java.time.LocalDateTime;

public class TaskDTO {

    private int id;


    private String name;


    private int owner_id;


    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public TaskDTO(int id, String name, int owner_id, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.name = name;
        this.owner_id = owner_id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public TaskDTO() {
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

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}
