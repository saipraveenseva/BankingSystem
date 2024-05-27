public class Customer {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String username; //u
    private String password;
    private String accountNumber;
    private double accountBalance;

    public Customer(String firstName, String lastName, String mobileNumber, String email, String username, String password, String accountNumber, double accountBalance) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.username = username;
        this.password = password;
        this.accountNumber = accountNumber;
        this.accountBalance = accountBalance;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getAccountBalance() {
        return accountBalance;
    }
}
