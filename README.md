# Evolving  Evaluation  Functions  for  Game  Agents  with  CMA-ES

## About

Final project for an Artificial Intelligence class. Utilized an existing microRTS agent to evolve an evaluation function.
 
Created by [Jake Gutierrez](https://github.com/jakegut) and [Maxx Batterton](https://github.com/MBatt1)

## Setup

This project was written in Java and uses two libraries, both sources are compiled and found in `/libs`:

 - MicroRTS: https://github.com/santiontanon/microrts
 - CMA-ES: http://cma.gforge.inria.fr/cmaes_sourcecode_page.html#java
 
 You will need to add these JAR files as libraries in your appropriate IDE.
 
 The source code to CoacAI is also included, a winning agent in the microRTS CoG 2020 tournament: https://github.com/Coac/coac-ai-microrts
 
## Running

There are two main files that you can run: `CMAEvolver` and `CMATourney` both found within `src/com/jakegut/microrts`.

Running `CMAEvolver` will start the CMA-ES evolution process for 50 iterations, with each iteration having a population of 10.
The results of the evolution process will be saved within the `output` folder with the format: `fitness-<N>-<unix_timestamp>.txt`.

Running `CMATourney` will run multiple games of an evolved agent (with a hard-coded weight vector) with a specified list of AIs and maps.
The results of the matches will be saved within the `output` folder with the format: `tourney_<unix_timestamp>.txt`.

## Visualization

The results of the output were visualized with Pandas, matplotlib, and Seaborn within a Jupyter notebook. You can find each notebook file within `output` (look for the files ending in `ipynb`).

Description of each relevant notebook file:

 - `BestWeights.ipynb` - Visualized weight vectors into a heatmap for each evolution process
 - `fitness.ipynb` - Visualized the mean fitness across multiple evolution runs
 - `tourney.ipynb` - Visualized results of the "tournament" into a bar chart
 
 