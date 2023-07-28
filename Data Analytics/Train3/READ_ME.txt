This folder contains the files needed to replicate training run 3 

This is the thirdtraining run for the Random forest model using the 2019 yellow taxis dataset concatenated with the 2018 yellow taxis dataset without the weather data.

The following notebooks are enclosed but some of the notebooks purposes may overlap i.e there may be further cleaning done in the train file. 
The notebooks are IN THE ORDER THAT THEY WERE RUN IN. In addition to the CSVs below the taxizones csv is available 
in the main folder (the folder all the training run folders are in) 


first the PKl file and the feature importances are available at 


PKl file for Train 3

https://drive.google.com/file/d/183dBjaFDjnesyO97d9S7Mclz3xi54J4X/view?usp=sharing


feature importances 


https://drive.google.com/file/d/1d54q7Ap6mkewnjEC2PmkC195Ba6SmiA4/view?usp=sharing



(1) Filtertomanhatten - filters out the non manhatten aspects of the data.

The dataset loaded at the beginning of this file is the 2019 raw yellow taxis dataset available at 

https://data.cityofnewyork.us/Transportation/2019-Yellow-Taxi-Trip-Data/2upf-qytp


(2) cleaning_2019 - basic cleaning for 2019 dataset, the dataset loaded at the start of this notebook is available at 

https://drive.google.com/file/d/1zKro6XM0aMYEU9iqqutu8HLAw6fXsb1Z/view?usp=sharing



(3) filtertomanhatten2018- filters out non manhatten aspects of the data. The dataset loaded at the beginning of this file is the 2019 raw yellow taxis dataset available at 

https://data.cityofnewyork.us/Transportation/2018-Yellow-Taxi-Trip-Data/t29m-gskq



(4) cleaning_2018_1


basic cleaning for 2018 yellow taxis dataset. the main csv loaded at the start of the file is available at 

https://drive.google.com/file/d/12AQEtylFT5WKZf8u1zbTZn3vA_jW6jx_/view?usp=sharing


(5) featureselector1_3 - further cleaning and selection of features 

the CSV Loaded at the start of this notebook is available at 


https://drive.google.com/file/d/1ZTyTZ9s0yZVuJlvEO-L2QnC0F92bmeUa/view?usp=sharing


(6) featureselector3_25 - further cleaning and selection of features FOR THE 2018 dataset 

the csv file loaded at the start of this notebook is available at 

https://drive.google.com/file/d/1--RIxWe3CNzUHdwdVfoBaab0fMhHo5Yc/view?usp=sharing


(7) feature_complete3_25 - further cleaning and encoding the features.

the CSV files used to load at the start of this notebook is available at 

(a) 320manhattenyellowtaxis.csv

https://drive.google.com/file/d/1ZTyTZ9s0yZVuJlvEO-L2QnC0F92bmeUa/view?usp=sharing

AND 

(b) 2018.31manhattenyellowtaxis.csv

https://drive.google.com/file/d/15IolmDWBuIfBsGNxA1kTsaQkIcBi7UUj/view?usp=sharing




(8) 3_25train

the file where the model is trained. Includes test metrics The CSV loaded at the start of the file is available at 

https://drive.google.com/file/d/1_7Rkv1DCAwyLYNHj0ru0VtA2jrkQfGhk/view?usp=sharing

