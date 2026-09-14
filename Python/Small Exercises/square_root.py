"""
Description: The program ask the user after a float for a and a float for 
epsilon At the end it prints the square root of a with a accuracy of epsilon.
"""

# Function for the square root.
def square_root(a, epsilon):
    # At first the formular for x_i and the definition of variable i for the iterations of the while loop.
    x_i = a / 2.0  
    i = 0  
    
    # Calculation of the formular and the break of while loop after success.
    while True:
        x_next_number = (x_i + a / x_i) / 2.0
        
        # The if-clause for the ending of the while loop.
        if abs(x_next_number - x_i) < epsilon:
            break
        
        # Updating of i and x_i
        x_i = x_next_number
        i += 1  
    # The return of the result from the function
    return x_next_number 

def main():
    # The two variables for the calculation that need user input and the result as output.
    a = float(input("Enter a float for a: "))
    epsilon = float(input("Enter a float for epsilon: "))
    print(f"This is the square root of {a} is {square_root(a, epsilon)}.")

main ()