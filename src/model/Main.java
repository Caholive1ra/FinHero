package model;

public class Main {
    public static void main(String[] args) {

        Account conta1 = new Account();

        conta1.createAccount();
        conta1.deleteAccount();
        conta1.uptadeAccount();

        Transaction transaction1 = new Transaction();

        transaction1.createTransaction();
        transaction1.deleteTransaction();
        transaction1.updateTransaction();

        User user1 = new User();

        user1.createUser();
        user1.uptadeUser();
        user1.deleteUser();

        Category categoria1 = new Category();

        System.out.println("-----------------------------");
        categoria1.createCategory();
        System.out.println("-----------------------------");
        categoria1.deleteCategory();
        System.out.println("-----------------------------");
        categoria1.updateCategory();
        System.out.println("-----------------------------");



    }
}
