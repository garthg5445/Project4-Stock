public class Account
{
    //Basic Account class for algo testing
    private double balance = 0;
    
    public Account(double amount)
    {
        balance = amount;
    }
    
    public double getBalance()
    {
        return balance;
    }
    
    public void setBalance(double amount)
    {
        balance = amount;
    }
    
    public void deposit(double amount)
    {
        balance += amount;
    }
    
    public void withdrawal(double amount)
    {
        balance -= amount;
    }
}