package gym.ui;

import gym.domain.Client;
import gym.service.ClientService;
import static gym.Constants.*;
import java.util.List;
import java.util.Scanner;
import gym.common.ValidationException;
import gym.common.RepositoryException;
public class ConsoleUI {
    private final ClientService clientService;
    private final Scanner scanner = new Scanner(System.in);
    public ConsoleUI(ClientService clientService) {
        this.clientService = clientService;
    }
    public void run() {
        while (true) {
            menu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case ADD: addClient(); break;
                    case LIST: listClients(); break;
                    case VIEW: viewClientByID(); break;
                    case UPDATE: updateClient(); break;
                    case DELETE: deleteClient(); break;
                    case FILTER_BY_NAME: filterName(); break;
                    case FILTER_BY_EMAIL: filterEmail(); break;
                    case EXIT:  return;
                    default: System.out.println("Invalid choice!");
                }
            } catch(ValidationException | RepositoryException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (NumberFormatException nfe) {
                System.out.println("Error: invalid number format");
            }
            System.out.println();
        }

    }
    private void menu() {
        System.out.println("CLIENTS MENU");
        System.out.println(ADD+". Create Client");
        System.out.println(LIST+". List Clients");
        System.out.println(VIEW+". View Client By ID");
        System.out.println(UPDATE+". Update Client");
        System.out.println(DELETE+". Delete Client");
        System.out.println(FILTER_BY_NAME+". Filter By Name");
        System.out.println(FILTER_BY_EMAIL+". Filter By Email");
        System.out.println(EXIT+". Back");
        System.out.println("Select an option: ");
    }
    private int askID() {
        System.out.println("ID: ");
        return Integer.parseInt(scanner.nextLine().trim());
    }
    private void listClients() {

        clientService.getAllClients().forEach(System.out::println);

    }

    private void addClient() {
        int id =  askID();
        System.out.println("Name: ");
        String name = scanner.nextLine().trim();
        System.out.println("Email: ");
        String email = scanner.nextLine().trim();
        System.out.println("Phone: ");
        String phone = scanner.nextLine().trim();
        clientService.addClient(id, name, email, phone);
    }
    private void viewClientByID() {
        int id = askID();
        System.out.println(clientService.getOneClient(id));
    }
    private void updateClient() {
        int id = askID();
        System.out.print("New name: ");
        String name = scanner.nextLine().trim();
        System.out.print("New email: ");
        String email = scanner.nextLine().trim();
        System.out.print("New phone: ");
        String phone = scanner.nextLine().trim();
        clientService.updateClient(id, name, email, phone);
    }
    private void deleteClient() {
        int id = askID();
        clientService.removeClient(id);
    }
    private void filterName() {
        System.out.println("Enter Client's name: ");
        String name = scanner.nextLine().trim();

        clientService.filterByNameContains(name).forEach(System.out::println);
    }
    private void filterEmail() {
        System.out.println("Enter Client's email: ");
        String email = scanner.nextLine().trim();
        clientService.filterByEmailContains(email).forEach(System.out::println);
    }


}
