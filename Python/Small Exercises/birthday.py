""""
Description: The program ask the birthday of the user as three integers 
(day, month, year). Then the possible outputs are that the current date is 
the birthday of the user and the age, or it outputs how many days you have 
to wait until your next birthday.
"""

import datetime

# The calulations of the number of the days since 01.01.1900 to a date.
def day_count_since_1900 (day, month, year):
    a = (14 - month)//12
    y = year - 1900 - a
    m = month + 12 * a - 3
    return  day + (153 * m + 2)//5 + 365 * y + y//4 + -y//100 + y//400


def main ():
    # Variables for the current date. That means current day, month and year.
    now = datetime.date.today()
    current_day = now.day
    current_month = now.month
    current_year = now.year
    # Variables for the birthday of the user. The user must give a integer input.
    birth_day = int(input("Enter your birth day: "))
    birth_month = int(input("Enter your birth month: "))
    birth_year = int(input("Enter your birth year: "))
    # Variable age is for the while loop and calculation later.
    age = 0
    # Variable for the calulation of days for the next birthday.
    Current_date = day_count_since_1900(current_day, current_month, current_year)
    # While loop that count your age.
    while day_count_since_1900(current_day, current_month, current_year) > day_count_since_1900(birth_day, birth_month, birth_year + age):
        age += 1
    # Variable age_current is the second variable for the calculation of days 
    # for the next birthdays.
    age_current = day_count_since_1900(birth_day, birth_month, birth_year + age)
    # The if-clauses for the possible options.
    # First if-clause: option that the current date and the birth date is equal 
    # that mean the user have his birth day today and the age.
    if current_day == birth_day and current_month == birth_month:
        print(f"Congratulations, today is your birthday and you are {age} years old!")
    # The second if-clause: option that the birth day is later this year and on the current 
    # date or already been. The output is the duration of waiting to the birth day and the age 
    if birth_day > current_day and birth_month >= current_month:
        days_to_birthday = age_current - Current_date
        print(f"In {days_to_birthday} days you will be {age} years old.")
    # The third if-clause: option that the birth day was already and the output 
    # give you the duration to the next birth day with age.
    if birth_day < current_day and birth_month <= current_month:
        new_age = age_current - Current_date
        print(f"In {new_age} days you will be {age} years old.")
main()