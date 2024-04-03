

# PursuitProtocol
- Demo: https://youtu.be/M-H5kUmhmwU


## Table of Contents:

- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Deployment](#deployment)
- [Input](#input)
- [Output](#output)
- [Implementation Details](#implementation-details)
- [Dependencies](#dependencies)

## Overview:
This project implements a Police vs. Thief game played on graphs, leveraging Scala, Akka HTTP. The game focuses on strategic movements of Police and Thief characters within a graph-based environment. 

### How the Game Works :
The thief and police hit their respective endpoints to get the information about their state or in other words their current position in the game/graph until one of them meets the winning or losing conditions.

### 1. Graph Setup

- **Original Graph:** Represents the actual graph.
- **Perturbed Graph:** Represents a modified version of the original graph, this is where the game is played.
- ***Both Graphs are constructed using the Google Guava API***

### 2. How the Game is Played?

- Either the Thief or Police can start game by making a get request on their respective endpoint, When a request is made for the first time each player is assigned a node at random,after that they move to the best possible neighbor node. 
- How is the **best neighbor** node selected?
    <br> We have established that when the player starts a game they are assigned a random node, When they make the next request the GameServer finds all the successors of the player's 'current node' once those are computed foreach successor a similarity score is calculated with corresponding node in the Original Graph, then the node with highest similarity score is chosen.
- How is the similarity score calculated?
    <br> The application calculates similarity score using the Jaccard Index, a popular similarity measure used in graph analysis. The Jaccard Index measures the similarity between two sets by dividing the size of the intersection by the size of the union of the sets. In the context of graph analysis, it is commonly used to compute similarities between nodes and edges between two graphs. 
    <br> The Jaccard Index calculations provide a quantitative measure `(range 0.0 - 1.0)` of the similarity between graph components, aiding in the decision to choose a node to move to.

### 3. Winning/Losing Conditions

- If a player is stuck on a node i.e. a node with no successors they lose
- If a player makes an illegal move i.e. they move to a node present in the perturbed graph but not present in the original graph then the player loses
- If a Thief reaches a node which contains valuableData then Thief wins!!
- If Police and Thief reach the same node, Police wins!!


## Features:

- **Graph-Based Strategy**: Players navigate Police and Thief characters across nodes in a graph, employing graph algorithms for strategic movements and interactions.
- **Akka Actors **: Utilizes Akka Actors to encapsulate game functionalities, ensuring independent behavior and asynchronous communication.
- **REST API Interaction**: Offers RESTful endpoints (`/get-node/thief`, `/get-node/police`, `/reset/game`,`/autoClient`) enabling external interaction with the game, providing specific actions and game state retrieval.

## Technologies:
- **Scala**: Primary language for game logic and algorithms.
- **Akka HTTP**: Framework for building REST APIs and managing HTTP requests.
- **Akka Actors**: Core implementation for modeling game entities and managing game state.



## Deployment:

To use the program, follow these steps:

1. **Install Dependencies**: Ensure all necessary dependencies are installed (see [Dependencies](#dependencies)).
2. **Run the Program**: Execute the main program file with appropriate input parameters.
3. **Review Output**: Analyze the logs and examine the computed results.

## Input:

The program takes input in the form of large graphs and their perturbed counterparts for parallel distributed processing. Input data should be formatted according to the specified graph representation.  
Please provide the .ngs , the .ngs.perturbed, the .ngs.yaml files as input to the program in the following directory of the project:  
`inputGraphs/filename  
eg: inputGraphs/20_nodes.ngs `  
The Main invocation file is:  
`Main.scala`  
The code takes in 2 arguments in the run configuration(in intellij), below are the arguments set in my run config please change them according to path on your system:

**Using intellij run config**:

`PATH_TO\resources\20_nodes.ngs.perturbed`  
`PATH_TO\resources\20_nodes.ngs`  

**Using SBT**:

`SBT "run PATH_TO\resources\20_nodes.ngs.perturbed PATH_TO\resources\20_nodes.ngs`

### How to play the game?
- There are 4 endpoints:
- /get-node/police : Make a move for police
- /get-node/thief: Make a move for thief
- /reset/game: Resets the game once an ongoing game is over
- /autoClient: Simulates the game automaticaly,declares the winner and resets the game

**(Note: Once a player wins please reset the game state by sending a get request to `/reset/game`)**
<br>**(Note: only hit `/autoClient` for a fresh game or when the game has been reset)**

## Output:

The program produces the following outputs as logs on the console.

**(Note: Once a player wins please reset the game state by sending a get request to "/reset/game")**

## Implementation Details:

The program is implemented using `Scala` and utilizes `Akka HTTP` for parallel processing and graph analysis. Detailed implementation information can be found in the source code.

### Main.scala
- **Description**: Entry point of the application.
- **Functionality**: Loads graph data, creates graphs, and starts the game server.

### GraphOperations.scala
- **Description**: Handles graph creation, processing, and calculating similarity scores.
- **Functions**:
    - `createGraph`: Creates a graph based on provided nodes and edges.
    - `processGraph`: Processes the graph to determine next moves based on similarity scores.
    - `calculateConfidenceScore`: Calculates Jaccard similarity indices for nodes in the graph.

### GameEntities.scala
- **Description**: Defines entity actors and their behavior.
- **Classes**:
    - `EntityActor`: An actor representing game entities (Thief and Police) and their interactions.

### GameServer.scala
- **Description**: Implements the game server with REST API endpoints.
- **Functionality**:
    - `startGameServer`: Sets up the game server, defines REST endpoints, and manages game state.
    - `handleGameCompletion`: Handles actions upon game completion.
    - `resetGame`: Resets the game state for a new session.

## AutomatedClient.scala

- **Description**: Implements an automated client to interact with the Police vs. Thief game server.
- **Functionality**:
    - Sends GET requests to predefined endpoints (`thiefEndpoint`, `policeEndpoint`, `resetGameEndpoint`) to play the game.
    - Parses responses, handles game state changes, and resets the game if a win condition is reached.
    - Utilizes Akka HTTP to make asynchronous HTTP requests and processes responses using JSON parsing.

This file contains an automated client script that interacts with the game server, plays the Police vs. Thief game, and manages game state based on server responses.


# Dependencies:
- **Scala Version**: 2.13.12
- **SBT version**: 1.9.6
- **ScalaTest Version**: 3.2.9
- **Akka Version**: 2.8.0
- **Akka HTTP Version**: 10.5.0
- **Guava Version**: 31.1-jre

