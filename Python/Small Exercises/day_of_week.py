"""
Description: The programme uses the formula to calculate which 
day of the week it was on a particular date. 
"""

import math


def main ():
    #Here, you can see the variables d for chosen day, m for the chosen 
    # month and y for the chosen year.
    d = input()
    m = input()
    y = input()


    #Here, you have a if clause that shows if the chosen month is January or 
    # February because than you must modify the y. The modifyed y is called in 
    # the if clause y_new.
    if int(m) < 3:
        y_new = int(y) - 1
        m_new = int(m) + 12
        d_0 = ((int(d) + math.floor (2.6 * ((m_new + 9) % 12 + 1) - 0.2) + y_new%100 + math.floor((y_new% 100 )/ 4)+ math.floor (y_new/400) - 2 * math.floor(y_new/100) - 1) % 7 +7)%7 +1  


    #This is the formular of the calculation if the chosen month is not January 
    # or February. Then you use this formular. d_0 represents the number of the date.
    else:
        d_0 = ((int(d) + math.floor (2.6 * ((int(m) + 9) % 12 + 1) - 0.2) + int(y)%100 + math.floor((int(y)% 100 )/ 4)+ math.floor (int(y)/400) - 2 * math.floor(int(y)/100) - 1) % 7 +7)%7 +1


    #This if clauses check whether the date is a Monday, Tuesday, Wednesday, Thursday, 
    # Friday, Saturday or Sunday. Therefore, you use the number of the date d_0. Is the 
    # number 1 then the date was on a Monday, is the number 2 than the date was on Tuesday 
    # and so on. Depending on the number, you get the output which day of the week it was.
    if d_0 == 7 :
        print(f"{d}.{m}.{y} is a Sunday.")
    if d_0 == 1 :
        print(f"{d}.{m}.{y} is a Monday.")
    if d_0 == 2 :
        print(f"{d}.{m}.{y} is a Tuesday.")
    if d_0 == 3 :
        print(f"{d}.{m}.{y} is a Wednesday.")
    if d_0 == 4:
        print(f"{d}.{m}.{y} is a Thursday.")
    if d_0 == 5:
        print(f"{d}.{m}.{y} is a Friday.")
    if d_0 == 6:
        print(f"{d}.{m}.{y} is a Saturday.")

main()