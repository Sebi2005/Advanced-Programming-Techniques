package gym.repository.core.client;

import gym.domain.Client;


import gym.repository.core.FilteredRepository;
import gym.repository.core.InMemoryRepository;
public class ClientRepository extends FilteredRepository<Integer,Client> {

    public ClientRepository() {
        create(new Client(1,"Pop Andreas","andreas@gmail.com","0722331990"));
        create(new Client(2, "Dragos Ion", "xdelta@yahoo.com", "0744332991"));
        create(new Client(3,"Cocan Mario","marioc@gamil.com", "0789567432"));
        create(new Client(4,"Badulescu Marian","marian@yahoo.com","0756789987"));
        create(new Client(5,"Coman Florin","florinel@gmail.com","0755663220"));

    }


}
