package com.story.algorithm;

import java.util.*;
import com.story.model.*;

// Implements a backtracking algorithm to find optimal paths through a story.
public class StoryPathFinder {
    private final Story story;
    private final List<StoryPath> allPaths;
    private final Map<String, Integer> nodeVisitCount;
    private int exploredPaths;
    
    public StoryPathFinder(Story story) {
        this.story = story;
        this.allPaths = new ArrayList<>();
        this.nodeVisitCount = new HashMap<>();
        this.exploredPaths = 0;
    }
    
    public StoryPath findOptimalPath() {
        return findOptimalPath(null);
    }
    
    // Finds the optimal path for a specific ending type
    public StoryPath findOptimalPath(String preferredEndingType) {
        allPaths.clear();
        nodeVisitCount.clear();
        exploredPaths = 0;
        
        System.out.println("Starting backtracking search for optimal path...");
        if (preferredEndingType != null) {
            System.out.println("Preferred ending type: " + preferredEndingType);
        }
        
        // Start from the initial node
        StoryPath initialPath = new StoryPath();
        backtrack(story.getStartNodeId(), initialPath, new HashSet<>());
        
        System.out.println("Backtracking complete. Explored " + exploredPaths + " paths.");
        System.out.println("Found " + allPaths.size() + " complete paths.");
        
        // Find the best path
        return selectBestPath(preferredEndingType);
    }
    

    private void backtrack(String currentNodeId, StoryPath currentPath, Set<String> visitedInPath) {
        exploredPaths++;
        StoryNode currentNode = story.getNode(currentNodeId);
        if (currentNode == null) {
            return;
        }
        
        nodeVisitCount.put(currentNodeId, nodeVisitCount.getOrDefault(currentNodeId, 0) + 1);
        
        StoryPath extendedPath = currentPath.extend(currentNodeId, null, currentNode.getCgs());
        
        if (currentNode.isEnding()) {
            StoryPath completePath = extendedPath.complete(currentNode.getEndingType());
            allPaths.add(completePath);
            
            if (allPaths.size() % 10 == 0) {
                System.out.println("Found " + allPaths.size() + " complete paths so far...");
            }
            return;
        }
        
        List<Choice> choices = currentNode.getChoices();
        if (choices.isEmpty()) {
            return;
        }

        for (Choice choice : choices) {
            String nextNodeId = choice.getDestination();
            
            if (!visitedInPath.contains(nextNodeId)) {
                Set<String> newVisited = new HashSet<>(visitedInPath);
                newVisited.add(currentNodeId);
                
                List<Choice> newChoices = new ArrayList<>(extendedPath.getChoiceSequence());
                newChoices.add(choice);
                StoryPath pathWithChoice = new StoryPath(extendedPath.getNodeSequence(), newChoices, extendedPath.getCollectedCgs(), null, false);
                backtrack(nextNodeId, pathWithChoice, newVisited);
            }
        }
    }
    

    private StoryPath selectBestPath(String preferredEndingType) {
        if (allPaths.isEmpty()) {
            return null;
        }
        List<StoryPath> candidatePaths = new ArrayList<>(allPaths);
        if (preferredEndingType != null) {
            candidatePaths = allPaths.stream()
                                   .filter(path -> preferredEndingType.equals(path.getEndingType()))
                                   .collect(java.util.stream.Collectors.toList());
            
            if (candidatePaths.isEmpty()) {
                System.out.println("No paths found with ending type: " + preferredEndingType);
                System.out.println("Falling back to all paths.");
                candidatePaths = new ArrayList<>(allPaths);
            }
        }
        
        candidatePaths.sort((a, b) -> Integer.compare(b.calculateScore(), a.calculateScore()));
        StoryPath bestPath = candidatePaths.get(0);        
        printPathStatistics(bestPath, candidatePaths);
        return bestPath;
    }
    
    private void printPathStatistics(StoryPath bestPath, List<StoryPath> candidatePaths) {
        System.out.println("\u001B[44m"+"\n============== PATH FINDING RESULTS =============="+"\u001B[0m");
        System.out.println("Best Path Found:");
        System.out.println("  - Score: " + bestPath.calculateScore());
        System.out.println("  - CGs Collected: " + bestPath.getCgCount() + "/" + story.getTotalCgCount());
        System.out.println("  - Path Length: " + bestPath.getPathLength() + " nodes");
        System.out.println("  - Ending Type: " + bestPath.getEndingType());
        System.out.println("  - Nodes Visited: " + bestPath.getNodeSequence());
        
        System.out.println("\nCGs Collected:");
        bestPath.getCollectedCgs().forEach(cg -> 
            System.out.println("  - " + cg + ": " + story.getCgDescription(cg))
        );
        
        System.out.println("\nChoice Sequence:");
        List<Choice> choices = bestPath.getChoiceSequence();
        for (int i = 0; i < choices.size(); i++) {
            Choice choice = choices.get(i);
            System.out.println("  " + (i + 1) + ". " + choice.getText() + " → " + choice.getDestination());
        }
        
        System.out.println("\u001B[44m"+"\n============== SEARCH STATISTICS ==============="+"\u001B[0m");
        System.out.println("Total paths explored: " + exploredPaths);
        System.out.println("Complete paths found: " + allPaths.size());
        System.out.println("Candidate paths: " + candidatePaths.size());
        
        System.out.println("\nTop 5 Paths by Score:");
        for (int i = 0; i < Math.min(5, candidatePaths.size()); i++) {
            StoryPath path = candidatePaths.get(i);
            System.out.println("  " + (i + 1) + ". Score: " + path.calculateScore() + 
                             ", CGs: " + path.getCgCount() + 
                             ", Ending: " + path.getEndingType());
        }
        
        Map<String, Long> endingCounts = allPaths.stream()
                                                .collect(java.util.stream.Collectors.groupingBy(
                                                    StoryPath::getEndingType,
                                                    java.util.stream.Collectors.counting()
                                                ));
        
        System.out.println("\nEnding Type Distribution:");
        endingCounts.forEach((type, count) -> 
            System.out.println("  - " + type + ": " + count + " paths")
        );
    }
    
    public List<StoryPath> getAllPaths() {
        return new ArrayList<>(allPaths);
    }

    
    public List<StoryPath> getPathsByEndingType(String endingType) {
        List<StoryPath> filtered = allPaths.stream()
                      .filter(path -> endingType.equals(path.getEndingType()))
                      .collect(java.util.stream.Collectors.toList());
        filtered.sort((a, b) -> Integer.compare(b.calculateScore(), a.calculateScore()));
        return filtered;
    }
    
    public Map<String, Integer> getNodeVisitCount() {
        return new HashMap<>(nodeVisitCount);
    }

    
    public int getExploredPathsCount() {
        return exploredPaths;
    }

    public StoryPath findOptimalPathToSpecificEnding(String targetEndingNodeId) {
        allPaths.clear();
        nodeVisitCount.clear();
        exploredPaths = 0;
        
        StoryNode targetNode = story.getNode(targetEndingNodeId);
        if (targetNode == null || !targetNode.isEnding()) {
            System.out.println("\u001B[31m"+"Target node not found or not an ending: " + targetEndingNodeId+ "\u001B[0m");
            return null;
        }
        
        System.out.println("Starting backtracking search for specific ending...");
        System.out.println("Target ending: " + targetNode.getTitle());
        
        StoryPath initialPath = new StoryPath();
        backtrackToSpecificEnding(story.getStartNodeId(), initialPath, new HashSet<>(), targetEndingNodeId);
        
        System.out.println("Backtracking complete. Explored " + exploredPaths + " paths.");
        System.out.println("Found " + allPaths.size() + " paths to target ending.");
        
        return selectBestPathToSpecificEnding(targetEndingNodeId);
    }
    
    private void backtrackToSpecificEnding(String currentNodeId, StoryPath currentPath, Set<String> visitedInPath, String targetEndingNodeId) {
        exploredPaths++;
        
        StoryNode currentNode = story.getNode(currentNodeId);
        if (currentNode == null) {
            return;
        }
        
        nodeVisitCount.put(currentNodeId, nodeVisitCount.getOrDefault(currentNodeId, 0) + 1);        
        StoryPath extendedPath = currentPath.extend(currentNodeId, null, currentNode.getCgs());
        
        if (currentNodeId.equals(targetEndingNodeId) && currentNode.isEnding()) {
            StoryPath completePath = extendedPath.complete(currentNode.getEndingType());
            allPaths.add(completePath);
            
            if (allPaths.size() % 5 == 0) {
                System.out.println("Found " + allPaths.size() + " paths to target ending so far...");
            }
            return;
        }
        
        if (currentNode.isEnding()) {
            return;
        }
        
        List<Choice> choices = currentNode.getChoices();
        if (choices.isEmpty()) {
            return;
        }
        
        for (Choice choice : choices) {
            String nextNodeId = choice.getDestination();
            
            if (!visitedInPath.contains(nextNodeId)) {
                Set<String> newVisited = new HashSet<>(visitedInPath);
                newVisited.add(currentNodeId);
                
                List<Choice> newChoices = new ArrayList<>(extendedPath.getChoiceSequence());
                newChoices.add(choice);
                StoryPath pathWithChoice = new StoryPath(extendedPath.getNodeSequence(), newChoices, extendedPath.getCollectedCgs(), null, false);
                backtrackToSpecificEnding(nextNodeId, pathWithChoice, newVisited, targetEndingNodeId);
            }
        }
    }
    
    private StoryPath selectBestPathToSpecificEnding(String targetEndingNodeId) {
        if (allPaths.isEmpty()) {
            return null;
        }
        
        List<StoryPath> targetPaths = allPaths.stream()
                                            .filter(path -> path.getNodeSequence().get(path.getNodeSequence().size() - 1).equals(targetEndingNodeId))
                                            .collect(java.util.stream.Collectors.toList());
        
        if (targetPaths.isEmpty()) {
            return null;
        }
        
        targetPaths.sort((a, b) -> Integer.compare(b.calculateScore(), a.calculateScore()));
        StoryPath bestPath = targetPaths.get(0);        
        printSpecificEndingPathStatistics(bestPath, targetPaths, targetEndingNodeId);
        
        return bestPath;
    }
    
    private void printSpecificEndingPathStatistics(StoryPath bestPath, List<StoryPath> targetPaths, String targetEndingNodeId) {
        StoryNode targetNode = story.getNode(targetEndingNodeId);
        
        System.out.println("\n============== \u001B[44mRESULTS\u001B[0m ==============");
        System.out.println("Target: " + (targetNode != null ? targetNode.getTitle() : targetEndingNodeId));
        System.out.println("Best Path Found:");
        System.out.println("  - Score: " + bestPath.calculateScore());
        System.out.println("  - CGs Collected: " + bestPath.getCgCount() + "/" + story.getTotalCgCount());
        System.out.println("  - Path Length: " + bestPath.getPathLength() + " nodes");
        System.out.println("  - Ending Type: " + bestPath.getEndingType());
        
        System.out.println("\u001B[34m"+"\n============== SEARCH STATISTICS ==============\u001B[0m");
        System.out.println("Total exploration attempts: " + exploredPaths);
        System.out.println("Successful paths to target: " + targetPaths.size());
        
        if (targetPaths.size() > 1) {
            System.out.println("\nTop paths to this ending:");
            for (int i = 0; i < Math.min(3, targetPaths.size()); i++) {
                StoryPath path = targetPaths.get(i);
                System.out.println("  " + (i + 1) + ". Score: " + path.calculateScore() + 
                                 ", CGs: " + path.getCgCount());
            }
        }
    }
}
