"""
Description: The programme calculates the bmi according to the user's 
input of mass and height and either returns that the bmi is normal (18.5 to 25.0), 
too high or too low. If the bmi is too high, the programme indicates the minimum 
and maximum number of kilograms that should be lost. If the bmi is too low, the 
mintest and maximum number of kilograms that should be gained is indicated. This 
is based on the interval of the normal bmi. 
"""
def main ():
    #person_mass represents the variable of the mass from the person.
    #person_height represents the variable of the height from the person
    person_mass = input('Give me an float number for the variable of the mass!\n')
    person_height = input('Give me an float number for the variable of the height!\n')

    #Here, the bmi function calculate the bmi of the person after a 
    # formular and it use in it the person_mass and the person_height.
    # After the calulation, print returns the bmi value.
    bmi = round(float(person_mass)/(float(person_height) ** 2), 2)
    print(f"Your body mass index is {bmi}.")


    # Here, the if clauses check whether your bmi is unter the healthy 
    # interval of 18.5 to 25.0, in the healthy interval of 18.5 to 25.0 
    # or over the healthy interval of 18.5 to 25.0. If the bmi is unter 
    # the healthy interval, mass_min calculate minimum mass for person 
    # and mass_max the maximum mass, and mass_min_u calculates the minimum mass 
    # in kg that the person would have to take for a healthy bmi and mass_max_u 
    # calculates the maximum value of the mass in kg that would be possible in 
    # the interval. The same principle applies to mass_min_o and mass_max_o for 
    # losing weight.
    if bmi < 18.5:
        mass_min = 18.5 * (float(person_height) **2)
        mass_max = 25 * (float(person_height) **2)
        mass_min_u = round (mass_min - float(person_mass), 3)
        mass_max_u = round ( mass_max - float(person_mass), 3)
        print(f"You are unterweight. You should gain between {mass_min_u} and {mass_max_u} kg")
        print("in order to reach a normal weight.")

    if bmi >= 18.5 and bmi <= 25.0:
        print(f"You have a normal weight.")

    if bmi > 25.0:
        mass_min = 25 * (float(person_height) **2)
        mass_max = 18.5 * (float(person_height) **2)
        mass_min_o = round (float(person_mass) - mass_min, 3)
        mass_max_o = round (float(person_mass) - mass_max, 3)
        print(f"You are overweight. You should lose between {mass_min_o} and {mass_max_o} kg")
        print("in order to reach a normal weight.")

main()