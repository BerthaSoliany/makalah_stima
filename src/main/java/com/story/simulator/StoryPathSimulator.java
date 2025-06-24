package com.story.simulator;
import java.util.*;
import com.story.model.*;

// Simulates the execution of a story path
public class StoryPathSimulator {
    private final Story story;
    private boolean enableDelays;
    private int delayMilliseconds;
    
    public StoryPathSimulator(Story story) {
        this.story = story;
        this.enableDelays = true;
        this.delayMilliseconds = 1500; // 1.5 seconds between scenes
    }
    
    public void setEnableDelays(boolean enableDelays) {
        this.enableDelays = enableDelays;
    }
    
    public void setDelayMilliseconds(int delayMilliseconds) {
        this.delayMilliseconds = delayMilliseconds;
    }
    
    public void simulatePath(StoryPath path) {
        if (path == null || !path.isComplete()) {
            System.out.println("\u001B[31m"+"Cannot simulate an incomplete or null path."+"\u001B[0m");
            return;
        }
        
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    STORY PATH SIMULATION                     ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        printPathSummary(path);
        
        if (enableDelays) {
            System.out.println("Starting simulation in 3 seconds...");
            sleep(3000);
        }
        
        simulateInteractivePlaythrough(path);
        
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    SIMULATION COMPLETE                       ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        
        printFinalResults(path);
    }
    
    private void simulateInteractivePlaythrough(StoryPath path) {
        List<String> nodeSequence = path.getNodeSequence();
        List<Choice> choiceSequence = path.getChoiceSequence();
        Set<String> collectedCgs = new HashSet<>();
        
        for (int i = 0; i < nodeSequence.size(); i++) {
            String nodeId = nodeSequence.get(i);
            StoryNode node = story.getNode(nodeId);
            
            if (node == null) {
                System.out.println("\u001B[31m"+"ERROR: Node not found: " + nodeId+"\u001B[0m");
                continue;
            }
            
            // Display the current scene
            displayScene(node, i + 1, nodeSequence.size());
            
            // Collect CGs from this node
            List<String> newCgs = collectCGs(node, collectedCgs);
            if (!newCgs.isEmpty()) {
                displayCGCollection(newCgs);
            }
            
            // If this is not the last node, show the choice made
            if (i < choiceSequence.size()) {
                Choice madeChoice = choiceSequence.get(i);
                displayChoiceMade(node, madeChoice);
            }
            
            if (enableDelays && i < nodeSequence.size() - 1) {
                sleep(delayMilliseconds);
            }
        }
    }
    

    private void displayScene(StoryNode node, int sceneNumber, int totalScenes) {
        System.out.println("─────────────────────────────────────────────────────────────");
        System.out.printf("                    SCENE %d/%d                          ", sceneNumber, totalScenes);
        System.out.println("\n─────────────────────────────────────────────────────────────");
        System.out.println();
        System.out.println("\u001B[34m* Location: " + node.getTitle()+ "\u001B[0m");
        System.out.println();
        System.out.println(wrapText(node.getDescription(), 60));
        System.out.println();
        
        if (node.isEnding()) {
            System.out.println("\u001B[42m"+"* ENDING REACHED: " + node.getEndingType().toUpperCase()+"\u001B[0m");
            System.out.println();
        }
    }
    

    private List<String> collectCGs(StoryNode node, Set<String> collectedCgs) {
        List<String> newCgs = new ArrayList<>();
        
        for (String cg : node.getCgs()) {
            if (!collectedCgs.contains(cg)) {
                collectedCgs.add(cg);
                newCgs.add(cg);
            }
        }
        
        return newCgs;
    }
    

    private void displayCGCollection(List<String> newCgs) {
        if (newCgs.size() == 1) {
            System.out.println("\u001B[32m"+"* CG UNLOCKED: " + newCgs.get(0)+"\u001B[0m");
            System.out.println("   " + story.getCgDescription(newCgs.get(0)));
        } else {
            System.out.println("\u001B[32m"+"* CGs UNLOCKED:"+ "\u001B[0m");
            for (String cg : newCgs) {
                System.out.println("   • " + cg + ": " + story.getCgDescription(cg));
            }
        }
        System.out.println();
    }
    
    private void displayChoiceMade(StoryNode node, Choice madeChoice) {
        if (node.getChoices().size() > 1) {
            System.out.println("Available Choices:");
            for (Choice choice : node.getChoices()) {
                String marker = choice.equals(madeChoice) ? " >>> " : "   ";
                String emphasis = choice.equals(madeChoice) ? " <-- SELECTED" : "";
                System.out.println(marker + choice.getId() + ". " + choice.getText() + emphasis);
            }
        } else {
            System.out.println("-->  " + madeChoice.getText());
        }
        System.out.println();
    }
    

    private void printPathSummary(StoryPath path) {
        System.out.println("============== \u001B[34mPath Summary\u001B[0m ==============");
        System.out.println("  * Score: " + path.calculateScore());
        System.out.println("  * CGs to Collect: " + path.getCgCount() + "/" + story.getTotalCgCount());
        System.out.println("  * Scenes: " + path.getPathLength());
        System.out.println("  * Ending: " + path.getEndingType().toUpperCase());
        System.out.println();
    }
    

    private void printFinalResults(StoryPath path) {
        System.out.println("\u001B[44m"+"============== Final Results =============="+"\u001B[0m");
        System.out.println("  * Total Score: " + path.calculateScore());
        System.out.println("  * CGs Collected: " + path.getCgCount() + "/" + story.getTotalCgCount());
        System.out.println("  * Collection Rate: " + 
                         String.format("%.1f%%", (path.getCgCount() * 100.0) / story.getTotalCgCount()));
        System.out.println("  * Ending Achieved: " + path.getEndingType().toUpperCase());
        
        System.out.println("\n============== \u001B[34mAll Collected CGs\u001B[0m ==============");
        List<String> sortedCgs = new ArrayList<>(path.getCollectedCgs());
        Collections.sort(sortedCgs);
        
        for (String cg : sortedCgs) {
            System.out.println("  • " + cg + ": " + story.getCgDescription(cg));
        }
        
        System.out.println("\n============== \u001B[34mJourney Summary\u001B[0m ==============");
        List<String> nodes = path.getNodeSequence();
        for (int i = 0; i < nodes.size(); i++) {
            StoryNode node = story.getNode(nodes.get(i));
            String arrow = (i < nodes.size() - 1) ? " --> " : "";
            System.out.print(node.getTitle() + arrow);
            
            if ((i + 1) % 3 == 0 && i < nodes.size() - 1) {
                System.out.println();
                System.out.print("     ");
            }
        }
        System.out.println();
    }
    

    private String wrapText(String text, int width) {
        if (text.length() <= width) {
            return text;
        }
        
        StringBuilder wrapped = new StringBuilder();
        String[] words = text.split(" ");
        int currentLineLength = 0;
        
        for (String word : words) {
            if (currentLineLength + word.length() + 1 > width) {
                wrapped.append("\n");
                currentLineLength = 0;
            }
            
            if (currentLineLength > 0) {
                wrapped.append(" ");
                currentLineLength++;
            }
            
            wrapped.append(word);
            currentLineLength += word.length();
        }
        
        return wrapped.toString();
    }
    

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    

    public void previewPath(StoryPath path) {
        if (path == null || !path.isComplete()) {
            System.out.println("\u001B[32m"+"Cannot preview an incomplete or null path."+"\u001B[0m");
            return;
        }
        
        System.out.println("\u001B[44m"+"============== PATH PREVIEW =============="+"\u001B[0m");
        printPathSummary(path);
        
        System.out.println("Route:");
        List<String> nodes = path.getNodeSequence();
        List<Choice> choices = path.getChoiceSequence();
        
        for (int i = 0; i < nodes.size(); i++) {
            StoryNode node = story.getNode(nodes.get(i));
            System.out.print((i + 1) + ". " + node.getTitle());
            
            if (i < choices.size()) {
                Choice choice = choices.get(i);
                System.out.print(" --> [" + choice.getText() + "]");
            }
            System.out.println();
        }
        
        System.out.println("\nCollected CGs: " + 
                         String.join(", ", path.getCollectedCgs()));
    }
    

    public void simulateOptimalPathInteractively(StoryPath optimalPath, Scanner scanner) {
        if (optimalPath == null || !optimalPath.isComplete()) {
            System.out.println("\u001B[32m"+"Cannot simulate an incomplete or null path."+"\u001B[0m");
            return;
        }
        
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          INTERACTIVE OPTIMAL PATH SIMULATION                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("Follow the optimal path by making the highlighted choices!");
        System.out.println("   The optimal choices will be marked with >>>");
        System.out.println();
        
        List<String> nodeSequence = optimalPath.getNodeSequence();
        List<Choice> optimalChoices = optimalPath.getChoiceSequence();
        Set<String> collectedCgs = new HashSet<>();
        
        for (int i = 0; i < nodeSequence.size(); i++) {
            String nodeId = nodeSequence.get(i);
            StoryNode node = story.getNode(nodeId);
            
            if (node == null) {
                System.out.println("\u001B[32m"+"ERROR: Node not found: " + nodeId+"\u001B[0m");
                continue;
            }
            
            System.out.println("─────────────────────────────────────────────────────────────");
            System.out.printf("                    SCENE %d/%d                          ", i + 1, nodeSequence.size());
            System.out.println("\n─────────────────────────────────────────────────────────────\n");
            System.out.println("* " + node.getTitle());
            System.out.println("─".repeat(60));
            System.out.println(node.getDescription());
            System.out.println();
            
            // Collect and display CGs
            List<String> newCgs = collectCGs(node, collectedCgs);
            if (!newCgs.isEmpty()) {
                System.out.println("\u001B[32m"+"* CG UNLOCKED:"+ "\u001B[0m");
                for (String cg : newCgs) {
                    System.out.println("  * " + cg + " - " + story.getCgDescription(cg));
                }
                System.out.println();
            }
            
            // Check if ending
            if (node.isEnding()) {
                System.out.println("\u001B[32m"+"* STORY ENDING REACHED!"+ "\u001B[0m");
                System.out.println("Ending Type: " + node.getEndingType().toUpperCase());
                break;
            }
            
            // Show choices if not the last node
            if (i < optimalChoices.size()) {
                Choice optimalChoice = optimalChoices.get(i);
                List<Choice> availableChoices = node.getChoices();
                
                System.out.println("* Choose your path:");
                for (Choice choice : availableChoices) {
                    String marker = choice.equals(optimalChoice) ? ">>>" : "  ";
                    System.out.println(marker + " " + choice.getId() + ". " + choice.getText());
                }
                System.out.println();
                System.out.println("\u001B[34m"+"* TIP: Choice " + optimalChoice.getId() + " is optimal for maximum CGs!"+ "\u001B[0m");
                

                while (true) {
                    System.out.print("Enter your choice (1-" + availableChoices.size() + "): ");
                    String input = scanner.nextLine().trim();
                    
                    try {
                        int choiceId = Integer.parseInt(input);
                        Choice selectedChoice = availableChoices.stream()
                                                               .filter(c -> c.getId() == choiceId)
                                                               .findFirst()
                                                               .orElse(null);
                        
                        if (selectedChoice != null) {
                            if (selectedChoice.equals(optimalChoice)) {
                                System.out.println("* Great choice! You're following the optimal path!");
                            } else {
                                System.out.println("\u001B[31m"+"   That's not the optimal choice, but it's your decision!"+ "\u001B[0m");
                                System.out.println("   Note: This will deviate from the optimal CG collection path.");
                            }
                            System.out.println("You chose: " + selectedChoice.getText());
                            System.out.println();
                            break;
                        } else {
                            System.out.println("\u001B[31m"+"Invalid choice. Please try again."+ "\u001B[0m");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter a valid number.");
                    }
                }
                
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
                System.out.println();
            }
        }
        
        // Final summary
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║              INTERACTIVE SIMULATION COMPLETE                 ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("* Your Journey Results:");
        System.out.println("  * CGs Collected: " + collectedCgs.size() + "/" + story.getTotalCgCount());
        System.out.println("  * Collection Rate: " + 
                         String.format("%.1f%%", (collectedCgs.size() * 100.0) / story.getTotalCgCount()));
        
        if (!collectedCgs.isEmpty()) {
            System.out.println("\n* CGs You Collected:");
            List<String> sortedCgs = new ArrayList<>(collectedCgs);
            Collections.sort(sortedCgs);
            for (String cg : sortedCgs) {
                System.out.println("  • " + cg + " - " + story.getCgDescription(cg));
            }
        }
    }
}
