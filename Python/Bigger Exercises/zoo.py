"""
Description: The programme is a management system for a zoo that manages various aspects of the zoo. These would be the animals with their 
names, their species, their age, medical history and their hairiness. Furthermore, the names of the employees, their role in the zoo, their 
working hours and their responsibility for animals are also recorded and managed. The system allows to return the medical records, the total 
working hours of each employee, the description of the animals and the employee's responsibility for the animals. 
"""
# The Animal class is for the animal methods.
class Animal:
    #The first method def__init__(self, name, species, age) is the constructor (initialiser) that is called when an 
    # object of the class is created. Here it initialises four attributes of the object.
    def __init__(self, name, species, age):
        self.name = name
        self.species = species
        self.age = age
        self.medical_records = []

    #This second method def describe(self) displays the information of the animals. It displays the name, 
    # the species and the age.
    def describe(self):
        print(f"{self.name} ({self.species}) - Age: {self.age}")

    #This second method def describe(self) displays the information of the animals. It displays the name, 
    # the species and the age.
    def add_medical_record(self, record):
        #Adds the given medical record to the list
        self.medical_records.append(record)
        #The confirmation message that the record has been added.
        print(f"The medical record for {self.name} has been added.")

    #The fourth method allows to display the medical history of the animal.
    def show_medical_history(self):
        #It displays the title of the medical history.
        print(f"Medical history for {self.name}:")
        #Iterates through each medical record in the list and outputs each record from the animal.
        for record in self.medical_records:
            print(f"- {record}")


#The Mammal class is for the methods of the animals that are mammels.
class Mammal(Animal):
    #The constructor of the Mammel class calls the constructor of the 
    # Animal class and initialises an additional attribute ‘has_fur’.
    def __init__(self, name, species, age, has_fur=True):
        super().__init__(name, species, age)
        self.has_fur = has_fur
    
    #This method describes the mammals, calls the describe method of the Animal class to display the 
    # general information and returns the information about the animal's fur in the print line.
    def describe(self):
        super().describe()
        print(f"Has Fur: {'yes' if self.has_fur else 'no'}")


#The Bird is for the methods of the animals that are birds.
class Bird(Animal):
    #The constructor of the Bird class calls the constructor of the Animal class and initialises 
    # an additional attribute ‘can_fly’.
    def __init__(self, name, species, age, can_fly=True):
        super().__init__(name, species, age)
        self.can_fly = can_fly
    
    #This method describes the mammals, calls the describe method of the Animal class to display the 
    # general information and returns the information about the animal's ability to fly in the print line.
    def describe(self):
        super().describe()
        print(f"Can Fly: {'yes' if self.can_fly else 'no'}")


# The Staff class represents an employee object.
class Staff:
    #The constructor of the Staff class initialises the attributes name of the employee, role of the employee, 
    # the empty dictionary for the working hours of the employee and an empty list for the assigned animals of 
    # the employee.
    def __init__(self, name, role):
        self.name = name
        self.role = role
        self.working_hours = {}  # Standardmäßig ein leeres Dictionary
        self.assigned_animals = []

    #This method allows you to add working hours for a specific date, checks the date for existence and adds 
    # the hours to the existing hours if the date already exists.
    def add_working_hours(self, date, hours):
        if date in self.working_hours:
            self.working_hours[date] += hours
        else:
            self.working_hours[date] = hours

    # This method calculates the total working hours of the employee by adding up all the values in the 
    # working_hours dictionary and outputs them.
    def display_working_hours(self):
        total_hours = sum(self.working_hours.values())
        print(f"{self.name} has worked {total_hours} hours in total.")

    #This method assigns an animal to the employee by adding the animal to the assigned_animals list.
    def assign_animal(self, animal):
        self.assigned_animals.append(animal)
        print(f"{animal.name} assigned to {self.name}.")
    
    #This method outputs a list of the names of all animals assigned to the employee. If no animals 
    # are assigned, it returns a corresponding message.
    def list_assigned_animals(self):
        if not self.assigned_animals:
            return f"No animals assigned to {self.name}"

        assigned_animal_names = []
        for animal in self.assigned_animals:
            assigned_animal_names.append(animal.name)

        return f"Animals assigned to {self.name}: " + ", ".join(assigned_animal_names)


#This function searches for an animal in a list of animals by name and returns the animal 
# if it is found. Otherwise it returns None.
def proof_animal(name, animals):
    for animal in animals:
        if animal.name == name:
            return animal
    return None

#This function searches for an employee in a list of employees by name and returns the 
# employee if found. Otherwise it returns None.        
def proof_staff(name, staff_members):
    for staff in staff_members:
        if staff.name == name:
            return staff
    return None



# This is the main function of the programme that manages the zoo management system. 
# It runs a loop that displays the menu and processes user input.
def main():
    #These lists store the animals and employees in the system.
    animals = []
    staff_members = []

    #This loop displays the programme's menu and waits for user input. It runs 
    # continuously until the user selects the ‘10’ option to exit the programme.
    while True:
        print("\nZoo Management System Menu:")
        print("1. Add Animal")
        print("2. Add Staff Member")
        print("3. Describe an Animal using its Name")
        print("4. Assign Animal to Staff Member")
        print("5. List Assigned Animals for Staff Member")
        print("6. Update Working Hours for Staff Member")
        print("7. Display Working Hours for Staff Member")
        print("8. Display Medical Records of an Animal")
        print("9. Add Medical Records of an Animal")
        print("10. Exit")

        decision = input("Enter your choice: ")

        #Allows a new animal to be added to the zoo by asking the user for its name, 
        # species, age and special characteristics (e.g. fur for mammals or flying ability for birds).
        if decision == "1":
            name = input("Enter animal name: ")
            species = input("Enter species: ")
            age = int(input("Enter age: "))
            type_choice = input("Is it a Mammal or Bird? (M/B): ").upper()
            
            # The user is asked for the type_choice of the animal and if the answer is valid, one of 
            # the two options is added and the user is asked for the flying ability or the fur.
            if type_choice == "M":
                has_fur = input("Does it have fur? (yes/no): ").lower()
                animals.append(Mammal(name, species, age, has_fur))
            elif type_choice == "B":
                can_fly = input("Can it fly? (yes/no): ").lower()
                animals.append(Bird(name, species, age, can_fly))
            else:
                print("Invalid input! The animal was not added.")

        #Adds a new employee to the zoo management system by asking the user for the 
        # employee's name and role.
        elif decision == "2":
            name = input("Enter staff name: ")
            role = input("Enter role: ")
            staff_members.append(Staff(name, role))

        #If the user selects ‘3’, an animal is described by its name. The proof_animal 
        # checks the existence of the animal with a for loop. If it exists, it returns the 
        # description of the animal; if it does not exist, it states that it could not be found.
        elif decision == "3":
            name = input("Enter animal name to describe: ")
            animal = proof_animal(name, animals)
            if animal:
                animal.describe()
            else:
                print("Animal not found.")

        #If the user selects “4”, an animal is assigned to an employee, the existence of the 
        # names is checked with the functions (proof_staff, proof_animal) and if both the employee 
        # and the animal exist, the animal is assigned.
        elif decision == "4":
            staff_name = input("Enter staff name: ")
            animal_name = input("Enter animal name: ")

            staff = proof_staff(staff_name, staff_members)
            animal = proof_animal(animal_name, animals)

            if staff and animal:
                staff.assign_animal(animal)
            else:
                print("Invalid staff or animal name.")
        
        #If the user selects “5”, the assigned animals for an employee are listed.
        elif decision == "5":
            staff_name = input("Enter staff name: ")
            staff = proof_staff(staff_name, staff_members)
            if staff:
                print(staff.list_assigned_animals()) #Lists all assigned animals.
            else:
                print("Staff not found.")

        #If the user selects “6”, the working hours of an employee are updated, if there is a 
        # problem finding the name of the employee or an invalid entry for working hours, a note 
        # is returned.
        elif decision == "6":
            staff_name = input("Enter staff name: ")
            date = input("Enter date (DD-MM-YYYY): ")
            try:
                hours = int(input("Enter hours worked: "))
                staff = proof_staff(staff_name, staff_members)
                if staff:
                    staff.add_working_hours(date, hours) #Adds working hours for the specified date.
                    print(f"Hours updated for {staff_name}.")
                else:
                    print("Staff not found.")
            except ValueError:
                print("Please enter a valid number for hours.")

        #If the user selects “7”, the total working hours for an employee are displayed.
        elif decision == "7":
            staff_name = input("Enter staff name: ")
            staff = proof_staff(staff_name, staff_members)
            if staff:
                staff.display_working_hours() #Displays the employee's total working hours.
            else:
                print("Staff not found.")

        #If the user selects “8”, the medical records of an animal are displayed.
        elif decision == "8":
            name = input("Enter animal name: ")
            animal = proof_animal(animal_name, animals)
            if animal:
                animal.show_medical_history() #Displays the medical history of the animal.
            else:
                print("Animal not found.")

        #If the user selects “9”, a medical record is added for an animal.
        elif decision == "9":
            name = input("Enter animal name: ")
            record = input("Enter medical record: ")

            animal = proof_animal(animal_name, animals)
            if animal:
                animal.add_medical_record(record) #Adds the medical record.
            else:
                print("Animal not found.")

        #If the user selects “10”, the program is terminated.
        elif decision == "10":
            print("You exiting the Zoo Management System. Goodbye!")
            break  # Exits the loop only when "10" is chosen
        
        #If an invalid selection is made, an error message is displayed.
        else:
            print("Invalid input! Please try again.")


# Run the program
main()