package com.story.model;

import java.util.*;

// Represents the complete story structure with all nodes and metadata
public class Story {
    private final String title;
    private final String description;
    private final Map<String, StoryNode> nodes;
    private final Map<String, String> cgDescriptions;
    private final String startNodeId;
    
    public Story(String title, String description, Map<String, StoryNode> nodes, 
                 Map<String, String> cgDescriptions, String startNodeId) {
        this.title = title;
        this.description = description;
        this.nodes = new HashMap<>(nodes);
        this.cgDescriptions = new HashMap<>(cgDescriptions);
        this.startNodeId = startNodeId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, StoryNode> getNodes() {
        return new HashMap<>(nodes);
    }
    
    public Map<String, String> getCgDescriptions() {
        return new HashMap<>(cgDescriptions);
    }
    
    public String getStartNodeId() {
        return startNodeId;
    }
    
    public StoryNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }
    
    public StoryNode getStartNode() {
        return nodes.get(startNodeId);
    }
    
    public String getCgDescription(String cgId) {
        return cgDescriptions.getOrDefault(cgId, "Unknown CG: " + cgId);
    }
    
    public Set<String> getAllCgIds() {
        Set<String> allCgs = new HashSet<>();
        for (StoryNode node : nodes.values()) {
            allCgs.addAll(node.getCgs());
        }
        return allCgs;
    }
    
    public List<StoryNode> getEndingNodes() {
        return nodes.values().stream()
                   .filter(StoryNode::isEnding)
                   .toList();
    }
    
    public int getTotalCgCount() {
        return getAllCgIds().size();
    }
    
    public int getTotalNodeCount() {
        return nodes.size();
    }
    
    public boolean isValidPath(List<String> nodeIds) {
        if (nodeIds.isEmpty() || !nodeIds.get(0).equals(startNodeId)) {
            return false;
        }
        
        for (int i = 0; i < nodeIds.size() - 1; i++) {
            StoryNode currentNode = getNode(nodeIds.get(i));
            String nextNodeId = nodeIds.get(i + 1);
            
            if (currentNode == null) {
                return false;
            }
            
            boolean hasValidChoice = currentNode.getChoices().stream()
                                               .anyMatch(choice -> choice.getDestination().equals(nextNodeId));
            
            if (!hasValidChoice) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("Story{title='%s', nodes=%d, totalCGs=%d}", 
                           title, nodes.size(), getTotalCgCount());
    }
}
