#Semantic Web Research Project

The goal of this research project was to analyze the usage of
[Schema.org's ontology](http://schema.org/) on this [dataset]
(http://webdatacommons.org/structureddata/2013-11/stats/schema_org_subsets.html)
hosted by Web Data Commons. There were two planned outcomes of
this research project:

* Help standardize Schema.org's markup to help prevent mistakes in its usage.
* Create a guide for using Schema.org's ontology and a list of most common
mistakes to help developers use the ontology correctly.

##Accomplishments

During my time working on this research project I had many accomplishments
that I'm proud of and are listed below:

* Wrote a web crawler to convert Schema.org's ontology into a usable RDFa
representation.
* Optimized our Virtuoso Server to reduce run time from a full day to
couple hours.
* Parsed through 200 GB of data to analyze the usage of the ontology.
* Presented findings a the Lotico Semantic Web MeetUp at Google's NYC
Headquarters in November, 2015.

##Project Information

This directory contains two main classes that serve different purposes:

###WebDataCommonsParser

This class is used to create an array of the different classes represented
by the WebDataCommons dataset to test against the Schema.org ontology

###SchemaOrgParser

This class is used to convert Schema.org's ontology from marked up HTML pages
 into a RDF/XML file for use while testing the Web Data Commons datasets

##How to run

The project is contained within a Gradle build to manage dependencies and
make execution of different main classes easier

To run the WebDataCommonsParser use the following Gradle task:

    gradle runWebData

To run the SchemaOrgParser use the following Gradle task

    gradle runSchema
