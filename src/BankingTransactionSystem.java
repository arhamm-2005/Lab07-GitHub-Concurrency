class BankAccount {
    public double balance;
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public synchronized void deposit(double amount) {
        System.out.println(Thread.currentThread().getName() + " is depositing " + amount);
        balance += amount;
        System.out.println("New balance after deposit: " + balance);
        notify();
    }
    
    public synchronized void withdraw(double amount) {
        while (balance < amount) {
            try {
                System.out.println(Thread.currentThread().getName() + " is waiting to withdraw " + amount);
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(Thread.currentThread().getName() + " is withdrawing " + amount);
        balance -= amount;
        System.out.println("New balance after withdrawal: " + balance);
    }
    
    public synchronized void checkBalance() {
        System.out.println(Thread.currentThread().getName() + " checked balance: " + balance);
    }
}

class DepositTask implements Runnable {
    private final BankAccount account;
    private final double amount;
    
    public DepositTask(BankAccount account, double amount) {
        this.account = account;
        this.amount = amount;
    }
    
    @Override
    public void run() {
        account.deposit(amount);
    }
}

class WithdrawTask implements Runnable {
    private final BankAccount account;
    private final double amount;
    
    public WithdrawTask(BankAccount account, double amount) {
        this.account = account;
        this.amount = amount;
    }
    
    @Override
    public void run() {
        account.withdraw(amount);
    }
}

public class BankingTransactionSystem {
    public static void main(String[] args) {
        BankAccount account = new BankAccount(500);
        System.out.println("Initial Balance: " + account.balance);
        
        Thread deposit1 = new Thread(new DepositTask(account, 300), "Thread-Deposit1");
        Thread withdraw1 = new Thread(new WithdrawTask(account, 200), "Thread-Withdraw1");
        Thread withdraw2 = new Thread(new WithdrawTask(account, 200), "Thread-Withdraw2");
        Thread deposit2 = new Thread(new DepositTask(account, 500), "Thread-Deposit2");
        
        deposit1.start();
        withdraw1.start();
        withdraw2.start();
        deposit2.start();
        
        try {
            deposit1.join();
            withdraw1.join();
            withdraw2.join();
            deposit2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        account.checkBalance();
        System.out.println("Final Balance: " + account.balance);
    }
}
