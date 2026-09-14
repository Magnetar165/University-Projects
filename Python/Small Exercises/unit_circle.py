"""
Description: The simple python program takes as input two double values representing 
the x- and y-coordinate of a point in the plane, and outputs whether the point (x, y) lies 
inside, outside or on the unit circle, i.e. the circle of radius 1 centered at the origin (0, 0). 
"""

def main():
        #The variable pointx is the x-coordinate and pointy is the y-coordinate of your 
        # point to be check.The variable radius is variable of radius of the circle, 
        # centerx is the x-coordinate and centery is the y-coordinate of the center of the circle.
        pointx = input ('Give me number for the x-cordinate!\n')
        pointy = input ('Give me number for the y-cordinate!\n')
        radius = 1
        centerx = 0
        centery = 0


        # point_pos calculate the value of point to be check. squarer calculate the 
        # square of the radius.
        point_pos = ((float(pointx) - centerx) ** 2) + ((float(pointy) - centery) ** 2)
        squarer = radius ** 2

        #The if clauses check whether the point is outside, inside or on the circle.
        if point_pos > squarer:
                print (f"The point ({pointx}, {pointy}) is outside of the circle")
        if point_pos < squarer:
                print (f"The point ({pointx}, {pointy}) is inside of the circle")
        if point_pos == squarer:
                print (f"The point ({pointx}, {pointy}) is on the circle")

main()