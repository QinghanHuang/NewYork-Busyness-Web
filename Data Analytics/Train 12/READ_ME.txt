This folder contains the files needed to replicate training run 12


this is the 12th training run and second training run for the deployment model in order to study the effects of adding the most important weather features to the 1st version of the deployment model. Weather data has previously been shown not to increase accuracy but we will see if it increases accuracy when only the most important weather features (based on the feature importances doc in Train 2 folder) are used 

The following notebooks are enclosed but some of the notebooks purposes may overlap 
i.e there may be further cleaning done in the train file. 

The notebooks are IN THE ORDER THAT THEY WERE RUN IN. In addition to the CSVs below the taxizones csv is available 
in the main folder (the folder all the training run folders are in) 



PMML file for train 12 

https://drive.google.com/file/d/1-7pCSAYMkh_h70tOEKJhPJIhLinXSprs/view?usp=sharing


(1) Filtertomanhatten - filters out the non manhatten aspects of the data.

The dataset loaded at the beginning of this file is the 2019 raw yellow taxis dataset available at 

https://data.cityofnewyork.us/Transportation/2019-Yellow-Taxi-Trip-Data/2upf-qytp


(2) cleaning_2019 - basic cleaning for 2019 dataset, the dataset loaded at the start of this notebook is available at 

https://drive.google.com/file/d/1zKro6XM0aMYEU9iqqutu8HLAw6fXsb1Z/view?usp=sharing




(3) featureselector1_3 - further cleaning and selection of features 

the CSV Loaded at the start of this notebook is available at 

https://drive.google.com/file/d/1ZQsiESF_a35HME_3CoLziJZXXdXDPbId/view?usp=sharing


(4) addingweather2_2 - adds the historical weather data from openweather to the yellow taxis data 

the main dataset loaded at the beginning of this file is available at 

https://drive.google.com/file/d/1ZTyTZ9s0yZVuJlvEO-L2QnC0F92bmeUa/view?usp=sharing

the weather dataset is available in the same folder as this readme file. labelled 

new_york_weather_final.csv




(5) feature_complete6.012 - further cleaning feature selection and encoding the features.

the CSV file used to load at the start of this notebook is available at 

https://drive.google.com/file/d/1HGPrgkFAMeSiqHNP10Ph-FfsFkJGixVH/view?usp=sharing





(5) 12_012D(1)train.ipynb (train file)
the file where the model is trained. Includes test metrics The CSV loaded at the start of the file is available at 

https://drive.google.com/file/d/1qj4WEG7ipvuhXkBjQiv5Y65g6_lkd_8-/view?usp=sharing
