-This folder contains a number of sub-folders. Each sub-folder represents
a training run and all the code need for data cleaning and training the 
machine learning model. 

-NB in the data analytics folder (the folder this document is contained in) each sub_folder i.e Train 1 Train 2 etc also has its own unqiue README file which are essential reads as they contain the PKL file locations 
the csvs needed to run the code and the explanations for what all the notebooks do.

NB Keep in mind 

- since everything such as the pkl files and csv files are provided in the google drive you may need to adjust file paths 
to where the relevant csv files etc are located on your computer after downloading from the google drive.

-The CSV files and PKl files have been linked in the 
README files in each subfolder labelled Train 1 Train 2 etc and can be accessed from the google drive 
they are stored in if necessary by using the links provided in the readme files in each of the Train folders Train 1 Train 2 etc.

It is very important to consider that only files opened for reading and editing have been linked as the output files of a notebook are the input 
files of the next notebook (the notebooks the listed in order in the readme files) as such it is not necessary to link the same files twice.

NB There are 11 total train. Below is a description of each train 

Train 1: Random forest, 2019 NYC Yellow taxis dataset (no weather data)
Train 2: Random forest, 2019 Yellow Taxis + Openweather data 
Train 3: Random forest, 2019 yellow taxis + 2018 yellow taxis no weather 
Train 4: Random forest, 2019 NYC Yellow taxis dataset (no weather data) but with some changes to features to test whether 
changes to features will change the accuracy.
Train 5: Random forest, 2019 NYC Yellow taxis dataset (no weather data) but with hyperparameter change to test effect on accuracy 
Train 6: Random forest, 2019 NYC Yellow taxis dataset (no weather data) Deployment model (since weather data doesnt increase accuracy of predictions) 
Train 7: Logistic regression, 2019 NYC Yellow taxis dataset (no weather data) for comparison to random forest for algorithm selection
Train 8:  K-nearest-neighbour classifier , 2019 NYC Yellow taxis dataset (no weather data) for comparison to random forest for algorithm selection
Train 9:  Decision tree classifier, 2019 NYC Yellow taxis dataset (no weather data) for comparison to random forest for algorithm selection
Train 10:  XGB classifier , 2019 NYC Yellow taxis dataset (no weather data) for comparison to random forest for algorithm selection
Train 11: Neural network, 2019 NYC Yellow taxis dataset (no weather data) for comparison to random forest for algorithm selection


The document labelled data2 enclosed in the same folder as this document contains all the 
taxi zones used for the machine learning 
it includes the locationid and the areas and perimeters of the taxi zones used in the models.
Virtually all manhatten taxi zones have been used bar 2 or 3 due to ambiguity of needed features
like the area of the taxi zone as it was instrumental to the target feature 

The main datasets used include 

(1) the 2019 NYC Yellow taxi trips data, available at:
https://data.cityofnewyork.us/Transportation/2019-Yellow-Taxi-Trip-Data/2upf-qytp

(2) the 2018 NYC Yellow taxi trips data, available at:
https://data.cityofnewyork.us/Transportation/2018-Yellow-Taxi-Trip-Data/t29m-gskq

(3) Openweather historical weather data (enclosed in training run 2 where it is used) 


(4) NYC taxi Zones data (enclosed in the same folder as this document)


