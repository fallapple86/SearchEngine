# SearchEngine
## Preprocessing
### Purpose 
Source code in this folder are used to preprocess HTML pages (which are saved as txt files). The HTML pages will be tokenized (Stopwords will be excluded) and the inverted index file will be generated based on TF-IDF. At the end of the preprocessing, a Mapping file, a Dict file, and a Post file will be generated for the query stage.
### Compile
To compile the source code, JavaCC is needed, which can be downloaded [here](https://javacc.org/).
#### Example script to compile:
> javacc index.jj

> javac *.java

> java index *inputDir* *outputDir*

## Search Engine Web Application
The Dict file, Mapping file, and the Post file generated from last step should be placed to the resources directory. 