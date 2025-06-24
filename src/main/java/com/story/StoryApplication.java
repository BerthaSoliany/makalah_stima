package com.story;

import java.util.*;
import com.story.algorithm.StoryPathFinder;
import com.story.model.*;
import com.story.simulator.StoryPathSimulator;
import com.story.util.StoryLoader;

public class StoryApplication {
    
    private Story story;
    private StoryPath lastFoundPath;
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════╗");
        System.out.println("║        BRANCHING NARRATIVE OPTIMIZATION SYSTEM        ║");
        System.out.println("║              Using Backtracking Algorithm             ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            // Initialize the application
            StoryApplication app = new StoryApplication();
            app.run();
            
        } catch (Exception e) {
            System.err.println("\u001B[31m"+"An error occurred: " + e.getMessage()+ "\u001B[0m");
            e.printStackTrace();
        }
    }

    public void run() {
        // Initialize the story
        System.out.println("Initializing story data...");
        story = loadStory();
        
        if (story == null) {
            System.err.println("\u001B[31m"+"Failed to load story. Exiting."+ "\u001B[0m");
            return;
        }
        
        displayStoryInfo(story);
        
        // Main menu loop
        try (Scanner scanner = new Scanner(System.in)) {
            boolean running = true;
            
            while (running) {
                displayMainMenu();
                String choice = scanner.nextLine().trim();
                
                switch (choice) {
                    case "1":
                        findOptimalPathMode(scanner);
                        break;
                    case "2":
                        simulationMode(scanner);
                        break;
                    case "3":
                        running = false;
                        System.out.println("\u001B[34m"+"Thank you for using the Branching Narrative Optimization System!"+ "\u001B[0m");
                        break;
                    default:
                        System.out.println("\u001B[31m"+"Invalid choice. Please enter 1, 2, or 3."+ "\u001B[0m");
                        break;
                }
                  if (running) {
                    System.out.println("\nPress Enter to continue...");
                    try {
                        scanner.nextLine();
                    } catch (Exception e) {
                        running = false;
                    }
                }
            }
        }
    }
    
    private void displayMainMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                         \u001B[44mMAIN MENU\u001B[0m");
        System.out.println("=".repeat(60));
        System.out.println("1. Find Optimal Path (Backtracking Algorithm)");
        System.out.println("2. Play Story Simulation");
        System.out.println("3. Exit");
        System.out.println("=".repeat(60));
        System.out.print("Enter your choice (1-3): ");
    }
    
    // Mode 1: Find optimal path using backtracking
    private void findOptimalPathMode(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    \u001B[44mOPTIMAL PATH FINDER\u001B[0m");
        System.out.println("=".repeat(60));
        
        String selectedCharacter = selectCharacter(scanner);
        if (selectedCharacter == null) {
            System.out.println("No character selected. Returning to main menu.");
            return;
        }
        
        String selectedEndingType = selectEndingTypeForCharacter(scanner, selectedCharacter);
        if (selectedEndingType == null) {
            System.out.println("No ending type selected. Returning to main menu.");
            return;
        }
        
        String targetEndingNodeId = selectedCharacter + "_" + selectedEndingType + "_ending";
        StoryNode targetNode = story.getNode(targetEndingNodeId);
        
        if (targetNode == null) {
            System.out.println("\u001B[31m"+"Target ending not found: " + targetEndingNodeId + "\u001B[0m");
            return;
        }
        
        // backtracking algorithm
        System.out.println("\u001B[34m"+"\nRunning backtracking algorithm..."+"\u001B[0m");
        System.out.println("Target: " + selectedCharacter.toUpperCase() + "'s " + selectedEndingType.toUpperCase() + " ending");
        System.out.println("Looking for optimal path to: " + targetNode.getTitle());
        
        StoryPathFinder pathFinder = new StoryPathFinder(story);
        StoryPath optimalPath = pathFinder.findOptimalPathToSpecificEnding(targetEndingNodeId);
        
        if (optimalPath != null) {
            lastFoundPath = optimalPath;
            displayOptimalPath(optimalPath);
            
            // Ask if user wants detailed analysis
            System.out.print("\nWould you like to see detailed algorithm analysis? (y/n): ");
            String analysisChoice = scanner.nextLine().trim().toLowerCase();
            if (analysisChoice.equals("y") || analysisChoice.equals("yes")) {
                performDetailedAnalysis(pathFinder, story);
            }
        } else {
            System.out.println("\u001B[31m"+"No optimal path found to " + selectedCharacter.toUpperCase() + "'s " + selectedEndingType.toUpperCase() + " ending."+ "\u001B[0m");
            System.out.println("This ending might not be reachable from the story start.");
        }
    }
    

    private String selectCharacter(Scanner scanner) {
        System.out.println("\n============== \u001B[34mAvailable Characters\u001B[0m ==============");
        System.out.println("1. Jumin Han - CEO heir with a mysterious past");
        System.out.println("2. Seven (707) - Quirky hacker with hidden depths");  
        System.out.println("3. Zen - Narcissistic but passionate actor");
        
        System.out.print("\nSelect a character (1-3): ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                return "jumin";
            case "2": 
                return "seven";
            case "3":
                return "zen";
            default:
                System.out.println("\u001B[31m"+"Invalid choice."+ "\u001B[0m");
                return null;
        }
    }
    

    private String selectEndingTypeForCharacter(Scanner scanner, String character) {
        System.out.println("\n============== \u001B[34mAvailable endings for " + character.toUpperCase() + " \u001B[0m==============");
        
        // Get available endings for this character
        List<String> availableEndings = getAvailableEndingsForCharacter(character);
        
        if (availableEndings.isEmpty()) {
            System.out.println("No endings found for " + character);
            return null;
        }
        
        for (int i = 0; i < availableEndings.size(); i++) {
            String endingType = availableEndings.get(i);
            String endingNodeId = character + "_" + endingType + "_ending";
            StoryNode endingNode = story.getNode(endingNodeId);
            
            System.out.println((i + 1) + ". " + endingType.toUpperCase() + " - " + 
                             (endingNode != null ? endingNode.getTitle() : "Unknown"));
        }
        
        System.out.print("\nSelect ending type (1-" + availableEndings.size() + "): ");
        String choice = scanner.nextLine().trim();
        
        try {
            int choiceNum = Integer.parseInt(choice);
            if (choiceNum >= 1 && choiceNum <= availableEndings.size()) {
                return availableEndings.get(choiceNum - 1);
            } else {
                System.out.println("\u001B[31m"+"Invalid choice."+ "\u001B[0m");
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("\u001B[31m"+"Invalid input."+ "\u001B[0m");
            return null;
        }
    }
    

    private List<String> getAvailableEndingsForCharacter(String character) {
        List<String> endings = new ArrayList<>();
        String prefix = character + "_";
        String suffix = "_ending";
        
        for (StoryNode node : story.getEndingNodes()) {
            String nodeId = node.getId();
            if (nodeId.startsWith(prefix) && nodeId.endsWith(suffix)) {
                String endingType = nodeId.substring(prefix.length(), nodeId.length() - suffix.length());
                endings.add(endingType);
            }
        }
        
        Collections.sort(endings);
        return endings;
    }
    
    // Mode 2: Story simulation
    private void simulationMode(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                       \u001B[44mSTORY SIMULATION\u001B[0m");
        System.out.println("=".repeat(60));
        
        StoryPathSimulator simulator = new StoryPathSimulator(story);
        
        System.out.println("Simulation options:");
        System.out.println("1. Simulate optimal path (if found in Path Finder)");
        System.out.println("2. Free exploration (choose your own path)");
        
        if (lastFoundPath == null) {
            System.out.println("\u001B[33m"+"\nNote: No optimal path found yet. Only free exploration available."+ "\u001B[0m");
            System.out.print("Press Enter to start free exploration: ");
            scanner.nextLine();
            freeExplorationMode(scanner);
        } else {
            System.out.print("Enter your choice (1-2): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    simulateOptimalPath(simulator, scanner);
                    break;
                case "2":
                    freeExplorationMode(scanner);
                    break;
                default:
                    System.out.println("\u001B[31m"+"Invalid choice. Starting free exploration..."+ "\u001B[0m");
                    freeExplorationMode(scanner);
                    break;
            }
        }
    }

    
    private void simulateOptimalPath(StoryPathSimulator simulator, Scanner scanner) {
        System.out.println("\u001B[34m"+"\nOptimal Path Simulation Options"+ "\u001B[0m");
        System.out.println("=".repeat(40));
        System.out.println("1. Interactive Mode - Make choices yourself following optimal path");
        System.out.println("2. Auto Mode - Watch the optimal path play out automatically");
        
        System.out.print("Choose simulation mode (1-2): ");
        String choice = scanner.nextLine().trim();
        
        switch (choice) {
            case "1":
                System.out.println("\u001B[34m"+"\nInteractive Mode Selected"+ "\u001B[0m");
                System.out.println("You'll see the optimal choices highlighted with >>>");
                System.out.println("Press Enter when ready to start...");
                scanner.nextLine();
                simulator.simulateOptimalPathInteractively(lastFoundPath, scanner);
                break;
                
            case "2":
                System.out.println("\u001B[34m"+"\nAuto Mode Selected"+ "\u001B[0m");
                simulator.previewPath(lastFoundPath);
                
                System.out.print("Start full simulation with delays? (y/n): ");
                String delayChoice = scanner.nextLine().trim().toLowerCase();
                
                if (delayChoice.equals("y") || delayChoice.equals("yes")) {
                    simulator.simulatePath(lastFoundPath);
                } else {
                    simulator.setEnableDelays(false);
                    simulator.simulatePath(lastFoundPath);
                }
                break;
                
            default:
                System.out.println("\u001B[31m"+"Invalid choice. Starting interactive mode..."+ "\u001B[0m");
                simulator.simulateOptimalPathInteractively(lastFoundPath, scanner);
                break;
        }
    }
    
    private void freeExplorationMode(Scanner scanner) {
        System.out.println("\u001B[34m"+"\nFree Exploration Mode"+ "\u001B[0m");
        System.out.println("=".repeat(40));
        System.out.println("Make your own choices and see where the story leads!");
        
        String currentNodeId = story.getStartNodeId();
        Set<String> collectedCgs = new HashSet<>();
        List<String> pathTaken = new ArrayList<>();
        List<Choice> choicesMade = new ArrayList<>();
        
        while (true) {
            StoryNode currentNode = story.getNode(currentNodeId);
            if (currentNode == null) {
                System.out.println("\u001B[31m"+"Error: Invalid story node reached."+ "\u001B[0m");
                break;
            }
            
            pathTaken.add(currentNodeId);
            
            System.out.println("\n" + "─".repeat(50));
            System.out.println("\u001B[34m" + currentNode.getTitle()+ "\u001B[0m");
            System.out.println("─".repeat(50));
            System.out.println(currentNode.getDescription());
            
            List<String> newCgs = new ArrayList<>();
            for (String cg : currentNode.getCgs()) {
                if (!collectedCgs.contains(cg)) {
                    collectedCgs.add(cg);
                    newCgs.add(cg);
                }
            }
            
            if (!newCgs.isEmpty()) {
                System.out.println("\u001B[32m"+"\nCG Unlocked:"+ "\u001B[0m");
                for (String cg : newCgs) {
                    System.out.println("\u001B[34m"+"  * "+ "\u001B[0m" + cg + ": " + story.getCgDescription(cg));
                }
            }
            
            if (currentNode.isEnding()) {
                System.out.println("\u001B[32m"+"\nSTORY ENDING REACHED!"+ "\u001B[0m");
                System.out.println("Ending Type: " + currentNode.getEndingType().toUpperCase());
                
                // Show final statistics
                StoryPath completedPath = new StoryPath(pathTaken, choicesMade, collectedCgs, 
                                                      currentNode.getEndingType(), true);
                System.out.println("\u001B[34m"+"\nYour Journey Statistics:"+ "\u001B[0m");
                System.out.println("  Score: " + completedPath.calculateScore());
                System.out.println("  CGs Collected: " + collectedCgs.size() + "/" + story.getTotalCgCount());
                System.out.println("  Path Length: " + pathTaken.size() + " scenes");
                break;
            }
            
            List<Choice> choices = currentNode.getChoices();
            if (choices.isEmpty()) {
                System.out.println("\u001B[31m"+"No choices available. Story ended unexpectedly."+ "\u001B[0m");
                break;
            }
            
            System.out.println("\u001B[34m"+"\nWhat do you choose?"+ "\u001B[0m");
            for (Choice choice : choices) {
                System.out.println("  " + choice.getId() + ". " + choice.getText());
            }
            
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();
            
            Choice selectedChoice = null;
            try {
                int choiceId = Integer.parseInt(input);
                selectedChoice = choices.stream()
                                       .filter(c -> c.getId() == choiceId)
                                       .findFirst()
                                       .orElse(null);
            } catch (NumberFormatException e) {
            }
            
            if (selectedChoice == null) {
                System.out.println("\u001B[31m"+"Invalid choice. Please try again."+ "\u001B[0m");
                continue;
            }
            
            choicesMade.add(selectedChoice);
            currentNodeId = selectedChoice.getDestination();
            
            System.out.println("You chose: " + selectedChoice.getText());
        }
    }

    // Displays the optimal path found by the algorithm
    private void displayOptimalPath(StoryPath path) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("\u001B[32m"+"               * OPTIMAL PATH FOUND *"+ "\u001B[0m");
        System.out.println("=".repeat(70));
        
        System.out.println("\n\u001B[34mPATH SUMMARY\u001B[0m");
        System.out.println("Score: " +path.calculateScore()+ " points\n" + 
                         "CGs: " + path.getCgCount() + "/" + story.getTotalCgCount()+ 
                         "\nLength: " + path.getPathLength() + " scenes");
        System.out.println("Ending: " + path.getEndingType().toUpperCase() + 
                         "\nCollection Rate: " + (path.getCgCount() * 100.0) / story.getTotalCgCount() + " %");
        
        System.out.println("\u001B[32m"+"\nYOUR OPTIMAL ROUTE (Follow this path for maximum CGs):"+ "\u001B[0m");
        System.out.println("─".repeat(70));
        
        List<String> nodeSequence = path.getNodeSequence();
        List<Choice> choiceSequence = path.getChoiceSequence();
        
        for (int i = 0; i < nodeSequence.size(); i++) {
            String nodeId = nodeSequence.get(i);
            StoryNode node = story.getNode(nodeId);
            
            System.out.println("\u001B[44m"+"|> STEP " + (i + 1) + ": " + node.getTitle()+ "\u001B[0m");
            
            // Show CGs collected at this node
            if (!node.getCgs().isEmpty()) {
                for (String cg : node.getCgs()) {
                    System.out.println("\u001B[32m"+"  * CG UNLOCKED: " + cg+"\u001B[0m");
                }
            }
            
            // Show the choice to make at this node
            if (i < choiceSequence.size()) {
                Choice choice = choiceSequence.get(i);
                System.out.println("\u001B[34m"+"  * CHOOSE: \"" + choice.getText() + "\""+ "\u001B[0m");
                System.out.println();
            } else if (node.isEnding()) {
                System.out.println("\u001B[42m"+"\n  * FINAL DESTINATION REACHED! *"+ "\u001B[0m");
            }
        }
        
        System.out.println("\n"+"─".repeat(70));
        System.out.println("\u001B[34m"+"TOTAL CGs COLLECTED IN THIS ROUTE:"+ "\u001B[0m");
        System.out.println("─".repeat(70));
        
        List<String> sortedCgs = new ArrayList<>(path.getCollectedCgs());
        Collections.sort(sortedCgs);
        
        for (int i = 0; i < sortedCgs.size(); i++) {
            String cg = sortedCgs.get(i);
            System.out.println(String.format("%2d", i + 1) + ". " + cg + " - " + story.getCgDescription(cg));
        }
        
        System.out.println("\u001B[32m"+"\n* RESULT: This is the BEST PATH for your chosen character and ending!"+ "\u001B[0m");
        System.out.println("    Following this exact route will maximize your CG collection.");
    }
    

    private void performDetailedAnalysis(StoryPathFinder pathFinder, Story story) {
        System.out.println("\u001B[34m"+"\n============== DETAILED ALGORITHM ANALYSIS ==============\n"+ "\u001B[0m");
        
        List<StoryPath> allPaths = pathFinder.getAllPaths();
        
        System.out.println("\u001B[34m"+"Search Performance:"+ "\u001B[0m");
        System.out.println("  Total paths explored: " + pathFinder.getExploredPathsCount());
        System.out.println("  Complete paths found: " + allPaths.size());
        
        if (!allPaths.isEmpty()) {
            // Calculate statistics
            OptionalDouble avgScore = allPaths.stream().mapToInt(StoryPath::calculateScore).average();
            OptionalDouble avgCgs = allPaths.stream().mapToInt(StoryPath::getCgCount).average();
            OptionalDouble avgLength = allPaths.stream().mapToInt(StoryPath::getPathLength).average();
            
            int maxScore = allPaths.stream().mapToInt(StoryPath::calculateScore).max().orElse(0);
            int minScore = allPaths.stream().mapToInt(StoryPath::calculateScore).min().orElse(0);
            
            System.out.println("  Score range: " + minScore + " - " + maxScore);
            System.out.println("  Average score: " + String.format("%.1f", avgScore.orElse(0.0)));
            System.out.println("  Average CGs: " + String.format("%.1f", avgCgs.orElse(0.0)));
            System.out.println("  Average path length: " + String.format("%.1f", avgLength.orElse(0.0)));
            
            // Top paths
            System.out.println("\u001B[34m"+"\nTop 3 Paths by Score:"+ "\u001B[0m");
            List<StoryPath> sortedPaths = new ArrayList<>(allPaths);
            sortedPaths.sort((a, b) -> Integer.compare(b.calculateScore(), a.calculateScore()));
            
            for (int i = 0; i < Math.min(3, sortedPaths.size()); i++) {
                StoryPath path = sortedPaths.get(i);
                System.out.println("  " + (i + 1) + ". Score: " + path.calculateScore() + 
                                 ", CGs: " + path.getCgCount() + 
                                 ", Ending: " + path.getEndingType());
            }
            
            // Ending distribution
            Map<String, Long> endingCounts = allPaths.stream()
                                                    .collect(java.util.stream.Collectors.groupingBy(
                                                        StoryPath::getEndingType,
                                                        java.util.stream.Collectors.counting()
                                                    ));
            
            System.out.println("\u001B[34m"+"\nEnding Type Distribution:"+ "\u001B[0m");
            endingCounts.forEach((type, count) -> 
                System.out.println("  " + type.toUpperCase() + ": " + count + " paths")
            );
            
            // CG analysis
            Map<String, Long> cgFrequency = allPaths.stream()
                                                  .flatMap(path -> path.getCollectedCgs().stream())
                                                  .collect(java.util.stream.Collectors.groupingBy(
                                                      cg -> cg,
                                                      java.util.stream.Collectors.counting()
                                                  ));
            
            System.out.println("\u001B[34m"+"\nCG Collection Frequency:"+ "\u001B[0m");
            cgFrequency.entrySet().stream()
                      .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                      .limit(5)
                      .forEach(entry -> {
                          double percentage = (entry.getValue() * 100.0) / allPaths.size();
                          System.out.println("  " + entry.getKey() + ": " + 
                                           String.format("%.1f%%", percentage) + " of paths");
                      });
        }
    }
    private Story loadStory() {
        try {
            StoryLoader loader = new StoryLoader();
            Story story = loader.createSampleStory();
            
            // Validate the story structure
            if (!loader.validateStory(story)) {
                System.err.println("\u001B[31m"+"Story validation failed."+ "\u001B[0m");
                return null;
            }
            
            System.out.println("\u001B[32m"+"Story loaded and validated successfully."+ "\u001B[0m");
            return story;
            
        } catch (Exception e) {
            System.err.println("\u001B[31m"+"Error loading story: " + e.getMessage()+ "\u001B[0m");
            return null;
        }
    }
    

    private void displayStoryInfo(Story story) {
        System.out.println("\u001B[34m"+"\n============== Story Information =============="+ "\u001B[0m");
        System.out.println("  Title: " + story.getTitle());
        System.out.println("  Description: " + story.getDescription());
        System.out.println("  Total Nodes: " + story.getTotalNodeCount());
        System.out.println("  Total CGs: " + story.getTotalCgCount());
        System.out.println("  Ending Nodes: " + story.getEndingNodes().size());
        System.out.println("  Start Node: " + story.getStartNodeId());
        
        // Show ending types
        Map<String, Long> endingTypes = story.getEndingNodes().stream()
                                            .collect(java.util.stream.Collectors.groupingBy(
                                                StoryNode::getEndingType,
                                                java.util.stream.Collectors.counting()
                                            ));
          System.out.println("  Ending Types: " + endingTypes);
    }
}
