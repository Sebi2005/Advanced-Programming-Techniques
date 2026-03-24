package gym.domain;
import java.time.LocalDateTime;
public class Session implements Identifiable<Integer>, java.io.Serializable {
    private Integer id;
    private Integer clientId;
    private LocalDateTime dateTime;
    private String description;
    public Session(Integer id, Integer clientId, LocalDateTime dateTime, String description) {
        this.id = id;
        this.clientId = clientId;
        this.dateTime = dateTime;
        this.description = description;
    }
    @Override
    public Integer getId() {
        return id;
    }
    @Override
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    public String getDescription() {
        return description;

    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return id + " , Client ID: " + clientId + " , " + dateTime+ " , " + description;
    }

}
