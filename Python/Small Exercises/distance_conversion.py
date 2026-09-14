"""
Description: The simple python program takes an integer value as input representing 
a distance measured  in millimeters and outputs that distance converted to 
kilometers, meters, centimeters and millimeters. 
"""
import math

def main():
    #This here is the integer value that representing the distance measure in millimeters. 
    n_milimeters = input('Give me an integer number!\n')


    #This here, calculate how many kilometers, meters, centimeters and millimeters it are.
    kilometers = math.floor(int(n_milimeters)/1000000)
    meters = math.floor((int(n_milimeters)/1000)-(kilometers*1000))
    centimeters = math.floor((int(n_milimeters)/10)-(kilometers*100000)-(meters*100))
    milimeters = math.floor((int(n_milimeters))-(kilometers*1000000)-(meters*1000)-(centimeters*10))


    #Here, I print at first the first part of the sentence, and the if clauses regulate whether, 
    # depending on the number, the number of kilometres, metres, centimetres and millimetres occurs 
    # in the rest of the sentence. If parameter 0, only the other parameter above 0 are output. 
    print(f"{n_milimeters} millimeters is equal to", end=" ")
    if kilometers > 0:
        print(f"{kilometers} kilometers", end=", ")
    if meters > 0:
        print(f"{meters} meters", end=", ")
    if centimeters > 0:
        print(f"{centimeters} centimeters", end=", ")
    if milimeters > 0:
        print(f"{milimeters} milimeters", end=".")

main()