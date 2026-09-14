"""
Description: The programme asks the user for a positive integer number. This represents the number 
of digits of all numbers that are to be checked to see whether they are digit sum numbers. At the 
end, the programme returns the sum of the possible sum digit numbers with exactly this number of digits.
"""

# This function checks how many n-digit numbers are digit sum numbers.
def sum_of_digit_sum_numbers(n):
    # The Variable for the digit sum number counter.
    total_sum = 0
    # Here I go through all numbers from 1 to 10^n - 1
    for num in range(1, 10 ** n ):
        #Here I use another function to check if num digit is sum number. 
        # If so, the total_sum counter is increased by 1.
        if is_digit_sum_number(num):
            total_sum += num
    return total_sum
#This function checks whether the num number is a digit sum number.
def is_digit_sum_number(num):
    # Here I create a set that contains all digits of 
    # the number num and total_sum calculates the sum of the digits of the set.
    digits = []
    for d in str(num):
        digits.append(int(d))
    total_sum = sum(digits)
    #This function checks whether the number num is a digit sum number by comparing 
    # whether a digit equals the sum of the digits in the set minus the digit. If a 
    # digit fulfils the condition, True is returned and is counted as digit sum number 
    # in the function sum_of_digit_sum_number otherwise not.
    for d in digits:
        if d == total_sum - d:
            return True
    return False

def main ():
    # n is the positive integer number input
    n = int(input("Please give me a positive integer number n: "))
    result = sum_of_digit_sum_numbers(n)
    print("The sum of the digit sum numbers with at most", n, "digits is:", result)
main()