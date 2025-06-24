package com.story.model;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

// Represents a story node in the branching narrative
// Each node contains story content, available choices, and CGs that can be unlocked
public class StoryNode {
    private final String id;
    private final String title;
    private final String description;
    private final List<String> cgs;
    private final List<Choice> choices;
    private final boolean isEnding;
    private final String endingType;
    
    public StoryNode(String id, String title, String description, 
                     List<String> cgs, List<Choice> choices, 
                     boolean isEnding, String endingType) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.cgs = new ArrayList<>(cgs != null ? cgs : new ArrayList<>());
        this.choices = new ArrayList<>(choices != null ? choices : new ArrayList<>());
        this.isEnding = isEnding;
        this.endingType = endingType;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<String> getCgs() {
        return new ArrayList<>(cgs);
    }
    
    public List<Choice> getChoices() {
        return new ArrayList<>(choices);
    }
    
    public boolean isEnding() {
        return isEnding;
    }
    
    public String getEndingType() {
        return endingType;
    }
    
    public boolean hasChoices() {
        return !choices.isEmpty();
    }
    
    public int getCgCount() {
        return cgs.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StoryNode node = (StoryNode) obj;
        return Objects.equals(id, node.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return String.format("StoryNode{id='%s', title='%s', cgs=%d, choices=%d, ending=%s}", 
                           id, title, cgs.size(), choices.size(), isEnding);
    }
}
