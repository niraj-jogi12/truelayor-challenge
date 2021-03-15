# truelayor-challenge
Repository for TrueFilm which is film investment company. This project enables them to understand which films have performed well in the past so that they can make better decisions.

**1. How to run it and how to query the data.**
I have made this project reproducible and automated with the help of shell script run.sh and config file . 
Without installing/developing any scheduler , we can scheduled this shell script and config to run in crob tab with the command shown below. 

nohup sh run.sh connection.cfg &

This shell script invokes Java code which loads data into Postgres Database in table moviesdetails and connection.cfg is configuration to connect to postgres database.

a) moviesmetadata table which has movie metadata and their budget to revenue ratio.
b) moviesratings table which has movie id and their ratings.
c) movieslist table which has movie name and wikipage url.

We can query the data externally also with the help of postgres sql added in run.sh.

We can also use Jenkins, an open source tool to build continuous integration and continuous delivery (CI/CD) pipelines which can help us in automating it to gain as much flexibility as possible while accelerating the development effort. 

**2. Why you chosen each tool.**
Tools used in the projects are Shell Script , Java , Maven.

Shell Script :  It can works like a scheduler and we can make this process automated by scheduling it in the cron tab. Also for any changes needed like database connection or queries we can simply change shell script and config file.

Java : I am very much confirmtable to code in Java as I am being working in Java from last 10+ years . Also I have used CopyManager API to copy data from csv into Postgres which is highy efficient.

Maven : This tool provides allows developers to build and document the lifecycle framework. Maven focuses on the simplification and standardization of the building process, taking care of the Builds,Documentation,Dependencies,Reports,Distribution,Releases etc.

**3. Any Algorithm choices.**
a) Once the data grows in future , we can do complete processing of this project in Google Cloud . 
As a first step we can do lift and shift migration of this project in to Google Cloud . 
Once we are sucessfully done with cloud migration then we can design this solution completly in Google Cloud. 
We can use Compute instances along with dataflow/dataproc pipelines to run the data processing and Bigquery to store the data which can be used for analysis purpose later . 

b) We can also use Bigdata Mapreduce framework to process huge data and then finally we can load into hive as well if we need to get rid of Postgres.


This project enables them to understand which films have performed well in the past so that they can make better decisions.

1. How to run it and how to query the data.
I have made this project reproducible and automated with the help of shell script run.sh and config file . 
Without installing/developing any scheduler , we can scheduled this shell script and config to run in crob tab with the command shown below. 

nohup sh run.sh connection.cfg &

This shell script invokes Java code which loads data into Postgres Database in table moviesdetails and connection.cfg is configuration to connect to postgres database.

a) moviesmetadata table which has movie metadata and their budget to revenue ratio.
b) moviesratings table which has movie id and their ratings.
c) movieslist table which has movie name and wikipage url.

We can query the data externally also with the help of postgres sql added in run.sh.

We can also use Jenkins, an open source tool to build continuous integration and continuous delivery (CI/CD) pipelines which can help us in automating it to gain as much flexibility as possible while accelerating the development effort. 

2. Why you chosen each tool.
Tools used in the projects are Shell Script , Java , Maven.

Shell Script :  It can works like a scheduler and we can make this process automated by scheduling it in the cron tab. Also for any changes needed like database connection or queries we can simply change shell script and config file.

Java : I am very much confirmtable to code in Java as I am being working in Java from last 10+ years . Also I have used CopyManager API to copy data from csv into Postgres which is highy efficient.

Maven : This tool provides allows developers to build and document the lifecycle framework. Maven focuses on the simplification and standardization of the building process, taking care of the Builds,Documentation,Dependencies,Reports,Distribution,Releases etc.

3. Any Algorithm choices.
a) Once the data grows in future , we can do complete processing of this project in Google Cloud . 
As a first step we can do lift and shift migration of this project in to Google Cloud . 
Once we are sucessfully done with cloud migration then we can design this solution completly in Google Cloud. 
We can use Compute instances along with dataflow/dataproc pipelines to run the data processing and Bigquery to store the data which can be used for analysis purpose later . 

b) We can also use Bigdata Mapreduce framework to process huge data and then finally we can load into hive as well if we need to get rid of Postgres.


**4. How  you would test the correctness.**

I have written JUnit testcases to check the data in csv files locally before loading into Postgres database . We can perform analysis on the data loaded in postgres and execute queries to understand which films have performed well in the past based on their highest ratio values.

