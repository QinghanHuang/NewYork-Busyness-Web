This folder contains the files needed to replicate training run 6 

This is a simplified training run of the random forest model for the current deployment. There are hardware and file size limits for current deployment so this model is smaller and uses fewer features. When the product is further developed this version may be scaled further.

The following notebooks are enclosed but some of the notebooks purposes may overlap i.e there may be further cleaning done in the train file. 
The notebooks are IN THE ORDER THAT THEY WERE RUN IN. In addition to the CSVs below the taxizones csv is available 
in the main folder (the folder all the training run folders are in) 


PMML file 

https://drive.google.com/file/d/18Ws62sRsxEnpxTS498r6IkiNE-DHAAMN/view?usp=sharing



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

(5) 6_0Btrain

the file where the model is trained. Includes test metrics The CSV loaded at the start of the file is available at 

https://drive.google.com/file/d/1ylRm_rIJYVKTWogqWnbUhFcXV-aqxWLP/view?usp=sharing
