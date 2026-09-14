"""
Description: This program simulates a university management system with students, facultys and courses. It uses object-oriented 
programming to enable interactions such as enrollment, grading and course assignment. A menu system provides an interface for 
managing this data. 
"""

# The person class is for the person methods.
class Person:
    #The first method def__init__(self, name, age, role) is the constructor (initialiser) that is called when an 
    # object of the class is created. Here it initialises four attributes of the object.
    def __init__(self, name, age, role):
        self.name = name
        self.age = age
        self.role = role

    #This second method def describe(self) displays the information of a person. It displays the name, 
    # the role and the age.
    def describe(self):
        print(f"{self.role}: {self.name}, Age: {self.age}")

#This class represents a student who inherits the Person attributes from the Person class. In addition, 
# there is a student ID and a list of enrolled courses with the associated data.
class Student(Person):
    #The constructor of the Student class inherits the attributes (name, age, role) from the Person 
    # class and also initializes the student ID and the dictionary for enrolled courses.
    def __init__(self, name, age, student_id):
        super().__init__(name, age, "Student")
        self.student_id = student_id
        self.enrolled_courses = {}

    #This method registers the student in a course. To do this, the course code is inserted into the 
    # dictionary with a placeholder (None) for grade and calls the enroll_students method to register 
    # the student there.  
    def enroll_in_course(self, course):
        self.enrolled_courses[course.course_code] = None
        course.enroll_student(self)

    #This method assigns the student a grade for an enrolled course. If the student is not enrolled 
    # in the course, an error message is displayed.
    def Assign_grade(self, course_code, grade):
        if course_code in self.enrolled_courses:
            self.enrolled_courses[course_code] = grade
        else:
            print(f"{self.name} is not enrolled in {course_code}")

    #Returns a detailed description of the student, including the ID and enrolled courses with grade. 
    # The method first calls the describe () method of the parent class.
    def describe(self):
        super().describe()
        print(f"Student ID: {self.student_id}")
        if self.enrolled_courses:
            print("Enrolled Courses & Grades:")
            for course, grade in self.enrolled_courses.items():
                if grade is not None:
                    print(f"  {course}: {grade}")
                else:
                    print(f"  {course}: Not Graded")
        else:
            print("No courses enrolled.")

#The Faculty class inherits from the Person class and adds specific 
# attributes and methods.
class Faculty(Person):
    #The constructor of the Faculty class inherits name but does not require age. 
    # Therefore None is used. It initializes the Faculty ID and the list of assigned 
    # courses (course code only).
    def __init__(self, name, faculty_id):
        super().__init__(name, None, "Faculty") # age is not needed
        self.faculty_id = faculty_id
        self.assigned_courses = []

    # Assigns a course to the faculty. Adds the course code to the list of taught 
    # courses and registers the assignment.
    def assign_course(self, course):
        self.assigned_courses.append(course.course_code)
        course.assign_faculty(self)

    #Assigns a grade to a student in a taught course. If the faculty does not 
    # teach the course, an error message is displayed.
    def Assign_student_grade(self, student, course_code, grade):
        if course_code in self.assigned_courses:
            student.assign_grade(course_code, grade)
        else:
            print(f"{self.name} does not teach {course_code}")

    #Gives a detailed description of the faculty, including the ID and the assigned 
    # courses. Before doing so, it first calls the describe() method of the parent class.
    def describe(self):
        super().describe()
        print(f"Faculty ID: {self.faculty_id}")
        if self.assigned_courses:
            print("Assigned Courses:", ", ".join(self.assigned_courses))
        else:
            print("Assigned Courses: None")

# The course class represents a course with a name and a unique course code. Each course 
# can be assigned to a faculty and contain a list of enrolled students.
class Course:
    #The constructor initializes the course name, course code, faculty and an empty 
    # list of enrolled courses.
    def __init__(self, course_name, course_code):
        self.course_name = course_name
        self.course_code = course_code
        self.faculty = None
        self.students = []

    #This method assigns a faculty to the course.
    def assign_faculty(self, faculty):
        self.faculty = faculty

    #This method enrolls a student in the course by adding it to the student list.
    def enroll_student(self, student):
        self.students.append(student)

    #This method outputs a detailed overview of the course, including name, code, 
    # faculty and enrolled students.
    def show_course_details(self):
        print(f"Course: {self.course_name} ({self.course_code})")
        if self.faculty:
            print(f"Faculty: {self.faculty.name}")
        else:
            print("Faculty: Not Assigned")
        print("Students Enrolled:")
        for student in self.students:
            print(f"  {student.name} (ID: {student.student_id})")


#Dictionaries for storing students, faculty members and courses
students = {}
faculties = {}
courses = {}

# Menu System
#This function runs in a while endless loop and offers various 
# options for managing students, faculties and courses.
def main():
    while True:
        print("\nUniversity Management System")
        print("1. Add Student")
        print("2. Add Faculty")
        print("3. Assign Faculty to Course")
        print("4. Enroll Student in Course")
        print("5. Assign Grade to Student")
        print("6. Display Student Details")
        print("7. Display Faculty Details")
        print("8. Show Course Details")
        print("9. Exit")

        choice = input("Enter choice: ")
        
        #Option 1 lets you create and save a new student.
        if choice == "1":
            name = input("Enter student name: ")
            age = input("Enter age: ")
            student_id = input("Enter student ID: ")
            students[student_id] = Student(name, int(age), student_id)
        
        #Option 2 creates and saves a new faculty.
        elif choice == "2":
            name = input("Enter faculty name: ")
            faculty_id = input("Enter faculty ID: ")
            faculties[faculty_id] = Faculty(name, faculty_id) # age removed

        #Option 3 lets you assign a course to a faculty.
        elif choice == "3":
            course_code = input("Enter course code: ")
            faculty_id = input("Enter faculty ID: ")

            #Checks whether the course and faculty exist.
            if course_code in courses and faculty_id in faculties:
                faculties[faculty_id].assign_course(courses[course_code])
            else:
                print("Invalid course or faculty ID.") #Error message if the entry is invalid.

        #Option 4 allows students to be enrolled in a course
        elif choice == "4":
            student_id = input("Enter student ID: ")
            course_code = input("Enter course code: ")

            #Checks whether the student and the course exist
            if student_id in students and course_code in courses:
                students[student_id].enroll_in_course(courses[course_code]) #Enroll student
            else:
                print("Invalid student or course ID.") #Error message if the entry is incorrect

        #Option 5 allows you to assign a grade to a student.
        elif choice == "5":
            student_id = input("Enter student ID: ")
            course_code = input("Enter course code: ")
            grade = input("Enter grade: ")

            #Checks whether the student exists
            if student_id in students:
                students[student_id].Assign_grade(course_code, grade) #Assign grade
            else:
                print("Invalid student ID.") #Error message for incorrect entry

        #Option 6 displays the student details.
        elif choice == "6":
            student_id = input("Enter student ID: ")

            if student_id in students:
                students[student_id].describe() #Display student details
            else:
                print("Student not found.") #Error message

        #Option 7 displays the faculty.
        elif choice == "7":
            faculty_id = input("Enter faculty ID: ")

            if faculty_id in faculties:
                faculties[faculty_id].describe() #Display faculty details
            else:
                print("Faculty not found.") #Error message

        #Option 8 displays course details.
        elif choice == "8":
            course_code = input("Enter course code: ")

            if course_code in courses:
                courses[course_code].show_course_details() #Output course details
            else:
                print("Course not found.") #Error message

        #Option 9 ends the while loop.
        elif choice == "9":
            print("You exiting the University Management System. Goodbye!") #Output farewell message
            break #Exit loop, program is terminated

        else:
            print("Invalid choice! Please try again.") #Error message for invalid input

# Sample Courses for testing the programm
courses["KEN1001"] = Course("Computer Science", "KEN1001")
courses["KEN1002"] = Course("Mathematics for Dummies - Why are you studing?", "KEN1002")
courses["KEN1003"] = Course("Introduction into Univeristy Mortivation", "KEN1003")
courses["KEN1004"] = Course("Chemstry 2 - Molekular cuisine?", "KEN1004")
courses["KEN1005"] = Course("Introduction to Social Science - How to communicate with people?", "KEN1005")

main()