package gym.domain;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClientTest {
    @Test
    void gettersAndSettersAndToString() {
        Client client = new Client(1,"Alex","a@gmail.com","000");
        assertEquals(1,client.getId());
        assertEquals("Alex",client.getName());
        assertEquals("a@gmail.com",client.getEmail());
        assertEquals("000",client.getPhone());
        client.setId(2);
        assertEquals(2,client.getId());
        client.setName("Ana");
        assertEquals("Ana",client.getName());
        client.setEmail("b@gmail.com");
        assertEquals("b@gmail.com",client.getEmail());
        client.setPhone("001");
        assertEquals("001",client.getPhone());

        String clientString = client.toString();
        assertTrue(clientString.contains("2"));
        assertTrue(clientString.contains("Ana"));


    }

}
