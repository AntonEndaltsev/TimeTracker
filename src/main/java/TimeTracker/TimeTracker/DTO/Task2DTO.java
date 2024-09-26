package TimeTracker.TimeTracker.DTO;

public class Task2DTO implements Comparable<Task2DTO> {
    private String name;
    private String time;

    public Task2DTO(String name, String time) {
        this.name = name;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Task2DTO() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int compareTo(Task2DTO o) {
        return o.getTime().compareTo(this.time);

    }
}

