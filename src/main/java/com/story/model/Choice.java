package com.story.model;

import java.util.Objects;

// Represents a choice that a player can make in the story.
public class Choice {
    private final int id;
    private final String text;
    private final String destination;
    
    public Choice(int id, String text, String destination) {
        this.id = id;
        this.text = text;
        this.destination = destination;
    }
    
    public int getId() {
        return id;
    }
    
    public String getText() {
        return text;
    }
    
    public String getDestination() {
        return destination;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Choice choice = (Choice) obj;
        return id == choice.id && 
               Objects.equals(text, choice.text) && 
               Objects.equals(destination, choice.destination);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, text, destination);
    }
    
    @Override
    public String toString() {
        return String.format("Choice{id=%d, text='%s', destination='%s'}", 
                           id, text, destination);
    }
}
