import javax.swing.*;
import java.awt.*;

public class NewGameScreenPanel {
    /**
     * Creates the New Game Screen JPanel.
     *
     * @return The new game screen panel.
     */
    public static JPanel createNewGameScreen(CardLayout cardLayout, JPanel cardPanel) {
        // main panel New Game Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JPanel buttonPanel = createButtonPanel(cardLayout, cardPanel);

        // Adding all components
        panel.add(HomeScreenPanel.createHomeLogo());
        panel.add(HomeScreenPanel.createHomeImage());
        panel.add(buttonPanel);

        return panel;
    }

    private static JPanel createButtonPanel(CardLayout cardLayout, JPanel cardPanel) {
        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = {"einzelspieler", "mehrspieler", "zurück"};

        // Add actions
        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "Difficulty");
                        break;
                    case 1:
                        GUI.mode = "multiplayer";
                        GUI.difficulty = null;
                        cardPanel.add(BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, GUI.mode, GUI.difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 2:
                        cardLayout.show(cardPanel, "Menu");
                        break;

                }
            });

            // Position
            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        return buttonPanel;
    }
}
