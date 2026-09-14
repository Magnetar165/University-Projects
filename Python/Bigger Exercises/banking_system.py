"""
Description: This programme simulates a banking system with the creation of a savings account or checking account, 
the login to the account with the performance of withdrawing, depositing, account balance display, interest crediting 
for savings accounts as well as the logout and closing of the programme.
"""

# import for the abstract class BankAccount
from abc import ABC, abstractmethod

# The BankAccount class has the methods authentification for checking name and password, and get_balance as 
# well as the abstract methods deposit and withdraw. It serves as a template for different types of bank accounts.
class BankAccount(ABC):
    
    # The constructor of the class initializes the account information account_holder and password as a string 
    # and balance as a float.
    def __init__(self, account_holder: str, password: str, balance: float):
        self.account_holder = account_holder    # Name of the account holder
        self.password = password    # Password for authentication
        self.balance = balance  # Current balance

    # Method for authentication to check whether the name and password are correct.
    def authentification (self, name: str, password:str) -> bool:
        return self.account_holder == name and self.password == password
    
    # Abstract method for deposit (It must be implemented by the subclass).
    @abstractmethod
    def deposit (self, amount: float):
        pass
    
    # Abstract method for withdraw (must be implemented by the subclass).
    @abstractmethod
    def withdraw (self, amount: float):
        pass
    
    # Method for requesting the current account balance.
    def get_balance (self) -> float:
        return self.balance
    
# The SavingAccount class inherits from the BankAccount class.
class SavingAccount (BankAccount):
    min_balance = 100   # Minimum balance that must always remain in the account
    interest_rate = 0.02    #Interest rate for the savings account

    # Method for depositing money into the account.
    def deposit(self, amount: float):
        if amount > 0:
            self.balance += amount
            print ("The deposit was successful.")
        else:
            print ("The amount is invalid.")

    # Method for withdrawal with consideration of the minimum balance.
    def withdraw(self, amount: float):
        if 0 < amount <= (self.balance -self.min_balance):
            self.balance -= amount
            print ("The withdraw was successful.")
        else:
            print("The amount is invalid.")

    # Method for crediting the interest rate to the account.
    def add_interest(self):
        self.balance += self.interest_rate * self.balance
        print("The interest rate has been added.")

# The CheckingAccount class inherits from the BankAccount class.
class CheckingAccount (BankAccount):
    overdraft_limit = 500

    # Method for paying into the current account.
    def deposit(self, amount: float):
        if amount > 0:
            self.balance += amount
            print ("The deposit was successful.")
        else:
            print ("The amount is invalid.")

    #Method for withdraw under consideration of the overdraft limit.
    def withdraw(self, amount: float):
        if 0 < amount <= (self.balance + self.overdraft_limit):
            self.balance -= amount
            print ("The withdraw was successful.")
        else:
            print("The amount is invalid.")

# Main function for interacting with the banking system
def main():
    # List for saving all created accounts
    accounts = []

    while True:
        # Show the main menu
        print("\n-- Banking System ---")
        print("1. Create new bank account")
        print("2. Login")
        print("3. Exit")

        choice = input("Chose your option: ")

        # Create new account
        if choice == "1":
            name = input("Name: ")
            password = input("Password: ")
            initial_balance = float(input("Initial balance: "))

            # Select the account type
            print("Please select an account type:")
            print("1. Saving account")
            print("2. Checking account")
            account_type = input("Select 1 or 2: ")

            if account_type == "1":
                if initial_balance < SavingAccount.min_balance:
                    print("Error! Your minimum balance for saving account must be: $100.")
                    continue
                accounts.append(SavingAccount(name, password, initial_balance))
                print("Your saving account has been successfully created.")
            elif account_type == "2":
                accounts.append(CheckingAccount(name, password, initial_balance))
                print("Your checking account has been successfully created.")
            else:
                print("Invalid Input.")

        # Login to an existing account
        elif choice == "2":
            name = input("Name: ")
            password = input("Password: ")
            account = None  # Variable for saving the logged-in account

            # Search for the right account in the list
            for acc in accounts:
                if acc.authentification(name, password):
                    account = acc
                    break
                    
            
            if account is None:
                print ("Login failed.")
                continue

            while True:
                # Show menu for logged in user
                print(f"\n Wellkom {name}!")
                print("1. Deposit")
                print("2. Withdraw")
                print("3. Check account balance")
                if isinstance(account, SavingAccount):
                    print("4. Add interest rate")
                print("5. Logout")
                
                action = input("Please select an option: ")

                # Make a deposit
                if action == "1":
                    amount = float(input("Amount: "))
                    account.deposit(amount)
                
                # Perform withdraw
                elif action == "2":
                    amount = float(input("Amount: "))
                    account.withdraw(amount)
                
                # Display account balance
                elif action == "3":
                    print(f"Current account balance: ${account.get_balance():.2f}")
                
                # Credit interest
                elif action == "4" and isinstance(account, SavingAccount):
                    account.add_interest()
                
                # Log out
                elif action == "5":
                    print("Logout successful.")
                    break

                else:
                    print("Invalid Input.")
        
        # Exit programme
        elif choice == "3":
            print("The programme will be closed. Thank you.")
            break

        else:
            print("Invalid Input.")

# Starting the banking system
main()