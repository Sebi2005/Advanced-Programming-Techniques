package gym.domain;

public class Client implements Identifiable<Integer>, java.io.Serializable{
    private Integer id;
    private String name;
    private String email;
    private String phone;
    public Client(Integer id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
    @Override public Integer getId() {return id;}
    @Override public void setId(Integer id) {this.id = id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPhone() {return phone;}
    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPhone(String phone) {this.phone = phone;}
    @Override
    public String toString() {
        return id + " , " + name + " , " + email+ " , " + phone;
    }
}
