"""
Description: This programme shows various diagrams of the sex ratio on the islands, bill 
length vs. bill depth ratio of the different species, the averages of the flipper length of all 
species and the distribution of body mass of all penguins.
"""
# %%
# Importing the required libraries
import matplotlib.pyplot as plt     # For the creation of diagrams
import pandas as pd     # For working with data in tabular form




# %%
# Loading data from a CSV file
df = pd.read_csv('penguins-1.csv')
df_cleaned = df.dropna()    # Removing rows with missing values (NaN)
df_cleaned.duplicated()     # Checking for duplicate entries
df_no_duplicates = df_cleaned.drop_duplicates()     # Removing duplicates from the data



# %%
# Creating a scatter plot for the bill length and bill depth.

fig, ax = plt.subplots()    # Creating a new figure and axis

# Iteration over the different penguin species, filtering by species and 
# displaying the species in different colors in the scatter plot
for num, s in enumerate(sorted(df_no_duplicates.species.unique())):
    subset = df_no_duplicates[df_no_duplicates.species == s]
    ax.scatter(subset["bill_length_mm"], subset["bill_depth_mm"], 
               color=f"C{num}", alpha=0.7, label=s)

# Set axis title
ax.set_xlabel("Flipper Length (mm)")
ax.set_ylabel("Bill Depth (mm)")
# Add legend
ax.legend()
# Show diagram
plt.show()


# %%
# Create a bar chart for the average pinball length per species

df_new = df_no_duplicates.loc[:,["species","flipper_length_mm"]]    # Selection of the columns “species” and “flipper_length_mm”
new= df_new.groupby("species").mean()   # Sort by species and calculate the mean value of the pinball length
fig, ax = plt.subplots()        # Creating a new figure and axis

# Iterate over the species and add bars to the chart with different colors for the species
for num, s in enumerate (new.index):
    ax.bar(s, new.loc[s,"flipper_length_mm"], 
               color=f"C{num}", alpha=0.7, label=s)

#Set axis title
ax.set_xlabel("Species")
ax.set_ylabel("Average flipper length in mm")
# Add legend
ax.legend()
# Show diagram
plt.show()



# %%
# Creating a histogram for the distribution of body mass

# Ask the user for the number of bins (input is converted to an integer)
bins_input = int(input("Give me a number for bins: ")) 
# Create the histogram with the specified bins
plt.hist(df_no_duplicates["body_mass_g"], bins = bins_input, edgecolor = "black", alpha = 0.7)
# Add a grid to the y-axis
plt.grid(axis = "y", linestyle = "--", alpha = 0.7)
# Set axis title and diagram title
plt.ylabel("count")
plt.xlabel("body_mass_g")
plt.title("Penguin Body Mass Distribution")
# Display diagram
plt.show()

# %%
#Create pie charts for gender distribution on different islands

# Clean up the “sex” column (remove spaces and capitalize first letters)
df_no_duplicates["sex"] = df_no_duplicates["sex"].str.strip().str.capitalize()

# Retrieve the unique island names from the data
island = df_no_duplicates["island"].unique()

# Create subplots (one pie chart per island)
fig, axes = plt.subplots(1,len(island), figsize = (15, 5))  #One row with as many plots as islands

# Iterate over the islands and associated axes
for ax, island in zip (axes, island):
    # Counting the genders on each island
    sex_counts = df_no_duplicates[df_no_duplicates["island"] == island]["sex"].value_counts()

    # Creating the pie chart for each island
    ax.pie(sex_counts, labels = sex_counts.index, autopct = "%1.1f%%", startangle = 90)
    ax.set_title(f"Sex Ratio on {island}")

# Display diagrams
plt.show()