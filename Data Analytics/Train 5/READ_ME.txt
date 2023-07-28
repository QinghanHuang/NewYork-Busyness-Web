This folder contains the files needed to replicate training run 5

This is the fofth training run for the Random forest model and uses the 2019 yellow taxis dataset without the weather data. The difference between this and Train 1 is the hyperparameters are changed to study the effect of a different number of trees in the random forest model 

The following notebooks are enclosed but some of the notebooks purposes may overlap i.e there may be further cleaning done in the train file. 
The notebooks are IN THE ORDER THAT THEY WERE RUN IN. In addition to the CSVs below the taxizones csv is available 
in the main folder (the folder all the training run folders are in) 


first the PKl file and the feature importances are available at 


PKl file for Train 5

https://drive.google.com/file/d/1--vCaFa06u1Ygtc7wbNp8RvkD9pu_d_t/view?usp=sharing


feature importances available at 


https://drive.google.com/file/d/1-7sgT_hcNjqsKDeurx7CPOlWlkUnJ8Pp/view?usp=sharing



(1) Filtertomanhatten - filters out the non manhatten aspects of the data.

The dataset loaded at the beginning of this file is the 2019 raw yellow taxis dataset available at 

https://data.cityofnewyork.us/Transportation/2019-Yellow-Taxi-Trip-Data/2upf-qytp


(2) cleaning_2019 - basic cleaning for 2019 dataset, the dataset loaded at the start of this notebook is available at 

https://drive.google.com/file/d/1zKro6XM0aMYEU9iqqutu8HLAw6fXsb1Z/view?usp=sharing




(3) featureselector1_3 - further cleaning and selection of features 

the CSV Loaded at the start of this notebook is available at 


https://drive.google.com/file/d/1ZTyTZ9s0yZVuJlvEO-L2QnC0F92bmeUa/view?usp=sharing


(4) feature_complete1_3 - further cleaning and encoding the features.

the CSV file used to load at the start of this notebook is available at 

https://drive.google.com/file/d/1ZTyTZ9s0yZVuJlvEO-L2QnC0F92bmeUa/view?usp=sharing

(5) 5_3train

the file where the model is trained. Includes test metrics. The difference between this and 1_3train is the hyperparamters are changed to study the effect of a different number of trees in the random forest model 

The CSV loaded at the start of the file is available at 

https://drive.google.com/file/d/1TF12jAi8hY2UD-PQFKqf5nFpPlpDNsiA/view?usp=sharing