# Finding an Optimal Path to a Desired Ending with Maximum CGs in Otome Game Using Backtracking
A Java implementation of a backtracking algorithm for finding optimal paths in branching narrative structures (visual novels). The system uses a **character-based route structure** where users first select a character, then choose their desired ending type to find the optimal path with maximum CG collection. In this program, we're using Mystic Messenger character to make the simulation more real.


## Table of Content
- [Project Structute](#project-structure)
- [Features](#features)
- [User Flow](#user-flow)
- [Algorithm Details](#algorithm-details)
- [Sample Story](#sample-story)
- [Quick Start](#quick-start)
- [How to Use the Program](#how-to-use-the-program)
- [Key Classes](#key-classes)


## Project Structure
```
src/

├── main/java/com/academy/story/
│   ├── StoryApplication.java         # Main application entry point
│   ├── model/                        # Data models
│   │   ├── Choice.java               # Represents a choice in the story
│   │   ├── Story.java                # Complete story structure
│   │   ├── StoryNode.java            # Individual story node
│   │   └── StoryPath.java            # Path through the story
│   ├── algorithm/                    # Backtracking algorithm
│   │   └── StoryPathFinder.java      # Main pathfinding algorithm
│   ├── simulator/                    # Path simulation
│   │   └── StoryPathSimulator.java   # Interactive path demonstration
│   └── util/                         # Utilities
│       └── StoryLoader.java          # Story data with character routes
└── story_data.json                   # Character-based story data
```


## Features
### 1. Character-Based Story Structure
- **Character Routes**: Separate story paths for Jumin, Seven (707), and Zen
- **Character-Specific Endings**: Each character has their own set of ending types
  - **Jumin**: Good ending, Bad ending
  - **Seven**: Good ending, Bad ending, Normal ending, Secret ending  
  - **Zen**: Good ending, Bad ending, Normal ending
- **CGs (Computer Graphics)**: Character-specific collectible artwork
- **Unique Narratives**: Each character has distinct story themes and progression

### 2. Backtracking Algorithm
- **Complete Path Exploration**: Examines all possible narrative branches
- **Character-Ending Specific**: Finds optimal paths to a specific character's specific ending
- **CG Maximization**: Prioritizes paths that collect the most character graphics
- **Cycle Prevention**: Avoids infinite loops in story structure

### 3. Two Main Modes
- **Path Finding Mode**: Select character → Select ending type → Get optimal path
- **Simulation Mode**: Play through the found optimal path or explore freely


## User Flow
1. **Character Selection**: Choose between Jumin, Seven, or Zen
2. **Ending Selection**: Pick available ending types for chosen character
3. **Algorithm Execution**: Backtracking finds optimal path to that specific ending
4. **Result Display**: Highlighted route with maximum CG collection
5. **Simulation Options**: Follow optimal path or explore freely


## Algorithm Details
The algorithm uses recursive backtracking to:
1. Start from the story beginning
2. Explore only paths that can reach the target character's specific ending
3. Track visited nodes to prevent cycles
4. Collect CGs along each path
5. Identify complete paths that reach ending nodes
6. Score paths based on CG count and ending quality

### Scoring System
- **Base Score**: 10 points per CG collected
- **Ending Bonus**: 
  - Best ending: +50 points
  - Secret ending: +30 points  
  - Good ending: +20 points
  - Other endings: +10 points

## Sample Story
The included story follows character-based routes with multiple possible endings:
- **Jumin Route**: 2 endings (good, bad) - CEO with mysterious business connections
- **Seven Route**: 4 endings (good, bad, normal, secret) - Hacker with government ties
- **Zen Route**: 3 endings (good, bad, normal) - Actor with ego challenges
- **24 Total CGs**: Collectible artwork distributed across all routes
- **33 Total Nodes**: Complete branching narrative structure with 10 ending nodes
- **Character-Specific Narratives**: Each route has unique themes and progression


## Quick Start
### Prerequisites
- Java 11 or higher
- Command line access

### Running the Program
```bash
# Option 1: Use the build scripts (Windows)
.\build.bat    # Compile the project
.\run.bat      # Run the application

# Option 2: Manual compilation and execution
javac -d bin -cp src src/main/java/com/story/*.java src/main/java/com/story/model/*.java src/main/java/com/story/algorithm/*.java src/main/java/com/story/simulator/*.java src/main/java/com/story/util/*.java
java -cp bin com.story.StoryApplication
```


## How to Use the Program
The application has a menu-driven interface with two main modes:
### **Mode 1: Find Optimal Path (Backtracking Algorithm)**
1. Select option `1` from the main menu
2. Choose your character:
   - `1` Jumin Han
   - `2` Seven (707)
   - `3` Zen
3. Choose ending type for your selected character:
4. The algorithm will run and show you the optimal path with:
   - Complete step-by-step route with highlighted choices
   - All CGs collected along the way (up to 25 total available)
   - Final score and statistics
   - Character-specific narrative progression
5. Optionally view detailed algorithm analysis

### **Mode 2: Play Story Simulation**
1. Select option `2` from the main menu
2. Choose simulation type:
   - **Interactive Mode**: Make choices yourself while following optimal path guidance
   - **Auto Mode**: Watch the optimal path play out automatically
   - **Free exploration**: Make your own choices and explore the story
3. Experience the interactive story with:   - Scene-by-scene narration
   - CG unlock notifications (24 total collectible artwork pieces)
   - Choice selection interface with optimal path hints
   - Final journey statistics

### **Program Flow Example**
```
Main Menu → [1] Find Optimal Path → Choose "Seven" → Choose "Secret" ending
         ↓
Algorithm finds: Chat → Seven Route → Serious → Truth → Agency → Vulnerable → Secret Ending
         ↓
Main Menu → [2] Story Simulation → Interactive Mode → Follow optimal choices
```


## Key Classes
### StoryPathFinder
The core backtracking algorithm that:
- Recursively explores all possible paths
- Maintains path state and CG collection
- Implements cycle detection
- Scores and ranks discovered paths

### StoryPathSimulator  
Interactive simulation system that:
- Displays story segments with formatting
- Shows CG unlocks in real-time
- Highlights player choices
- Provides journey statistics

### Story Data Models
- **Story**: Complete narrative structure loaded from JSON
- **StoryNode**: Individual story segments with choices and CGs
- **Choice**: Branching decision points with destinations
- **StoryPath**: Sequence of nodes and choices discovered by the algorithm