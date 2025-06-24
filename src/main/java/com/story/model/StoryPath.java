package com.story.model;

import java.util.*;

// Represents a path through the story
public class StoryPath {
    private final List<String> nodeSequence;
    private final List<Choice> choiceSequence;
    private final Set<String> collectedCgs;
    private final String endingType;
    private final boolean isComplete;
    
    public StoryPath() {
        this.nodeSequence = new ArrayList<>();
        this.choiceSequence = new ArrayList<>();
        this.collectedCgs = new HashSet<>();
        this.endingType = null;
        this.isComplete = false;
    }
    
    public StoryPath(List<String> nodeSequence, List<Choice> choiceSequence, 
                     Set<String> collectedCgs, String endingType, boolean isComplete) {
        this.nodeSequence = new ArrayList<>(nodeSequence);
        this.choiceSequence = new ArrayList<>(choiceSequence);
        this.collectedCgs = new HashSet<>(collectedCgs);
        this.endingType = endingType;
        this.isComplete = isComplete;
    }
    
    // Creates a copy of this path with an additional node and choice.
    public StoryPath extend(String nodeId, Choice choice, List<String> newCgs) {
        List<String> newNodeSequence = new ArrayList<>(nodeSequence);
        newNodeSequence.add(nodeId);
        
        List<Choice> newChoiceSequence = new ArrayList<>(choiceSequence);
        if (choice != null) {
            newChoiceSequence.add(choice);
        }
        
        Set<String> newCollectedCgs = new HashSet<>(collectedCgs);
        if (newCgs != null) {
            newCollectedCgs.addAll(newCgs);
        }
        
        return new StoryPath(newNodeSequence, newChoiceSequence, 
                           newCollectedCgs, endingType, isComplete);
    }
    
    // Creates a complete path by marking it as finished with an ending type.
    public StoryPath complete(String endingType) {
        return new StoryPath(nodeSequence, choiceSequence, 
                           collectedCgs, endingType, true);
    }
    
    public List<String> getNodeSequence() {
        return new ArrayList<>(nodeSequence);
    }
    
    public List<Choice> getChoiceSequence() {
        return new ArrayList<>(choiceSequence);
    }
    
    public Set<String> getCollectedCgs() {
        return new HashSet<>(collectedCgs);
    }
    
    public String getEndingType() {
        return endingType;
    }
    
    public boolean isComplete() {
        return isComplete;
    }
    
    public int getCgCount() {
        return collectedCgs.size();
    }
    
    public int getPathLength() {
        return nodeSequence.size();
    }
    
    public String getCurrentNode() {
        return nodeSequence.isEmpty() ? null : nodeSequence.get(nodeSequence.size() - 1);
    }
    
    // Calculates a score for this path based on CG count and ending type.
    public int calculateScore() {
        int score = collectedCgs.size() * 10; // Base score from CGs
        
        if (endingType != null) {
            switch (endingType.toLowerCase()) {
                case "best":
                    score += 50;
                    break;
                case "secret":
                    score += 30;
                    break;
                case "good":
                    score += 20;
                    break;
                default:
                    score += 10;
                    break;
            }
        }
        
        return score;
    }
    
    @Override
    public String toString() {
        return String.format("StoryPath{nodes=%d, choices=%d, cgs=%d, ending='%s', complete=%s, score=%d}", 
                           nodeSequence.size(), choiceSequence.size(), 
                           collectedCgs.size(), endingType, isComplete, calculateScore());
    }
}
