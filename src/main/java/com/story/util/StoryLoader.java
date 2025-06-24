package com.story.util;

import java.util.*;
import com.story.model.*;
import java.io.*;
import java.nio.file.*;

//Utility class for loading story data from JSON files
public class StoryLoader {
    
    public StoryLoader() {
    }
    
    // Loads a story from a JSON file
    public Story loadStoryFromJson(String jsonFilePath) throws IOException {
        String jsonContent = Files.readString(Paths.get(jsonFilePath));
        return parseStoryFromJson(jsonContent);
    }
    
    // Creates a sample story by loading from the default JSON file

    public Story createSampleStory() {
        try {
            return loadStoryFromJson("story_data.json");
        } catch (IOException e) {
            System.err.println("Warning: Could not load story from JSON file. Error: " + e.getMessage());
            System.err.println("Creating fallback story...");
            return createFallbackStory();
        }
    }
    
    // Parses JSON content and creates a Story object
    private Story parseStoryFromJson(String jsonContent) {
        try {
            JsonObject root = parseJsonObject(jsonContent);
            
            String title = root.getString("title");
            String description = root.getString("description");
            
            // Parse nodes
            Map<String, StoryNode> nodes = new HashMap<>();
            JsonObject nodesObj = root.getObject("nodes");
            for (String nodeId : nodesObj.keys()) {
                JsonObject nodeObj = nodesObj.getObject(nodeId);
                StoryNode node = parseStoryNode(nodeObj);
                nodes.put(nodeId, node);
            }
            
            // Parse CG descriptions
            Map<String, String> cgDescriptions = new HashMap<>();
            JsonObject cgObj = root.getObject("cgDescriptions");
            for (String cgId : cgObj.keys()) {
                cgDescriptions.put(cgId, cgObj.getString(cgId));
            }
            
            return new Story(title, description, nodes, cgDescriptions, "start");
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON: " + e.getMessage(), e);
        }
    }
    
    // Parses a story node from JSON object
    private StoryNode parseStoryNode(JsonObject nodeObj) {
        String id = nodeObj.getString("id");
        String title = nodeObj.getString("title");
        String description = nodeObj.getString("description");
        
        // Parse CGs
        List<String> cgs = new ArrayList<>();
        JsonArray cgsArray = nodeObj.getArray("cgs");
        if (cgsArray != null) {
            for (int i = 0; i < cgsArray.size(); i++) {
                cgs.add(cgsArray.getString(i));
            }
        }
        
        // Parse choices
        List<Choice> choices = new ArrayList<>();
        JsonArray choicesArray = nodeObj.getArray("choices");
        if (choicesArray != null) {
            for (int i = 0; i < choicesArray.size(); i++) {
                JsonObject choiceObj = choicesArray.getObject(i);
                int choiceId = choiceObj.getInt("id");
                String text = choiceObj.getString("text");
                String destination = choiceObj.getString("destination");
                choices.add(new Choice(choiceId, text, destination));
            }
        }
        
        // Check if it's an ending
        boolean isEnding = nodeObj.getBoolean("isEnding", false);
        String endingType = nodeObj.getString("endingType", null);
        
        return new StoryNode(id, title, description, cgs, choices, isEnding, endingType);
    }
    
    // Creates a fallback story in case JSON loading fails.
    private Story createFallbackStory() {
        Map<String, StoryNode> nodes = new HashMap<>();
        Map<String, String> cgDescriptions = new HashMap<>();        
        // Create minimal fallback story
        nodes.put("start", new StoryNode("start", "Simple Story", 
                                       "A basic story for testing purposes.", 
                                       new ArrayList<>(), 
                                       Arrays.asList(new Choice(1, "Continue", "end")), 
                                       false, null));
        nodes.put("end", new StoryNode("end", "The End", 
                                     "Thank you for playing!", 
                                     new ArrayList<>(), new ArrayList<>(), 
                                     true, "normal"));
        
        return new Story("Fallback Story", "A simple fallback story", nodes, cgDescriptions, "start");
    }
    
    // Validates that a story is properly structured.
    public boolean validateStory(Story story) {
        // Check that all choice destinations exist
        for (StoryNode node : story.getNodes().values()) {
            for (Choice choice : node.getChoices()) {
                if (story.getNode(choice.getDestination()) == null) {
                    System.err.println("Invalid destination: " + choice.getDestination() + 
                                     " in node: " + node.getId());
                    return false;
                }
            }
        }
        
        // Check that there's at least one ending
        if (story.getEndingNodes().isEmpty()) {
            System.err.println("Story has no ending nodes");
            return false;
        }
        
        // Check that start node exists
        if (story.getStartNode() == null) {
            System.err.println("Start node not found: " + story.getStartNodeId());
            return false;
        }
        
        return true;
    }
        
    // Parses a JSON string into a JsonObject.
    private JsonObject parseJsonObject(String json) {
        JsonParser parser = new JsonParser(json.trim());
        return parser.parseObject();
    }
    
    private static class JsonParser {
        private final String json;
        private int pos = 0;
        
        public JsonParser(String json) {
            this.json = json;
        }
        
        public JsonObject parseObject() {
            skipWhitespace();
            if (json.charAt(pos) != '{') {
                throw new RuntimeException("Expected '{' at position " + pos);
            }
            pos++; // skip '{'
            skipWhitespace();
            
            Map<String, Object> map = new HashMap<>();
            
            if (json.charAt(pos) == '}') {
                pos++; // skip '}'
                return new JsonObject(map);
            }
            
            while (pos < json.length()) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                
                if (json.charAt(pos) != ':') {
                    throw new RuntimeException("Expected ':' at position " + pos);
                }
                pos++; // skip ':'
                skipWhitespace();
                
                Object value = parseValue();
                map.put(key, value);
                
                skipWhitespace();
                if (pos >= json.length()) break;
                
                if (json.charAt(pos) == '}') {
                    pos++; // skip '}'
                    break;
                } else if (json.charAt(pos) == ',') {
                    pos++; // skip ','
                } else {
                    throw new RuntimeException("Expected ',' or '}' at position " + pos);
                }
            }
            
            return new JsonObject(map);
        }
        
        private Object parseValue() {
            skipWhitespace();
            char c = json.charAt(pos);
            
            if (c == '"') {
                return parseString();
            } else if (c == '{') {
                return parseObject();
            } else if (c == '[') {
                return parseArray();
            } else if (c == 't' || c == 'f') {
                return parseBoolean();
            } else if (c == 'n') {
                return parseNull();
            } else if (Character.isDigit(c) || c == '-') {
                return parseNumber();
            } else {
                throw new RuntimeException("Unexpected character '" + c + "' at position " + pos);
            }
        }
        
        private String parseString() {
            if (json.charAt(pos) != '"') {
                throw new RuntimeException("Expected '\"' at position " + pos);
            }
            pos++; // skip '"'
            
            StringBuilder sb = new StringBuilder();
            while (pos < json.length() && json.charAt(pos) != '"') {
                char c = json.charAt(pos);
                if (c == '\\') {
                    pos++; // skip '\'
                    if (pos >= json.length()) {
                        throw new RuntimeException("Unexpected end of string");
                    }
                    char escaped = json.charAt(pos);
                    switch (escaped) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        default: sb.append(escaped); break;
                    }
                } else {
                    sb.append(c);
                }
                pos++;
            }
            
            if (pos >= json.length() || json.charAt(pos) != '"') {
                throw new RuntimeException("Unterminated string at position " + pos);
            }
            pos++; // skip closing '"'
            
            return sb.toString();
        }
        
        private JsonArray parseArray() {
            if (json.charAt(pos) != '[') {
                throw new RuntimeException("Expected '[' at position " + pos);
            }
            pos++; // skip '['
            skipWhitespace();
            
            List<Object> list = new ArrayList<>();
            
            if (json.charAt(pos) == ']') {
                pos++; // skip ']'
                return new JsonArray(list);
            }
            
            while (pos < json.length()) {
                Object value = parseValue();
                list.add(value);
                
                skipWhitespace();
                if (pos >= json.length()) break;
                
                if (json.charAt(pos) == ']') {
                    pos++; // skip ']'
                    break;
                } else if (json.charAt(pos) == ',') {
                    pos++; // skip ','
                    skipWhitespace();
                } else {
                    throw new RuntimeException("Expected ',' or ']' at position " + pos);
                }
            }
            
            return new JsonArray(list);
        }
        
        private Boolean parseBoolean() {
            if (json.startsWith("true", pos)) {
                pos += 4;
                return true;
            } else if (json.startsWith("false", pos)) {
                pos += 5;
                return false;
            } else {
                throw new RuntimeException("Expected boolean at position " + pos);
            }
        }
        
        private Object parseNull() {
            if (json.startsWith("null", pos)) {
                pos += 4;
                return null;
            } else {
                throw new RuntimeException("Expected null at position " + pos);
            }
        }
        
        private Integer parseNumber() {
            int start = pos;
            if (json.charAt(pos) == '-') {
                pos++;
            }
            while (pos < json.length() && Character.isDigit(json.charAt(pos))) {
                pos++;
            }
            String numStr = json.substring(start, pos);
            return Integer.parseInt(numStr);
        }
        
        private void skipWhitespace() {
            while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
                pos++;
            }
        }
    }
    
    // Simple JSON object wrapper
    private static class JsonObject {
        private final Map<String, Object> map;
        
        public JsonObject(Map<String, Object> map) {
            this.map = map;
        }
        
        public String getString(String key) {
            return (String) map.get(key);
        }
        
        public String getString(String key, String defaultValue) {
            Object value = map.get(key);
            return value != null ? (String) value : defaultValue;
        }
        
        public int getInt(String key) {
            return (Integer) map.get(key);
        }
        
        public boolean getBoolean(String key, boolean defaultValue) {
            Object value = map.get(key);
            return value != null ? (Boolean) value : defaultValue;
        }
        
        public JsonObject getObject(String key) {
            return (JsonObject) map.get(key);
        }
        
        public JsonArray getArray(String key) {
            return (JsonArray) map.get(key);
        }
        
        public Set<String> keys() {
            return map.keySet();
        }
    }
    
    // JSON array wrapper.
    private static class JsonArray {
        private final List<Object> list;
        
        public JsonArray(List<Object> list) {
            this.list = list;
        }
        
        public int size() {
            return list.size();
        }
          public String getString(int index) {
            return (String) list.get(index);
        }
        
        public JsonObject getObject(int index) {
            return (JsonObject) list.get(index);
        }
    }
}
