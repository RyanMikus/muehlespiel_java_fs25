import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class MenuScreenPanel {
    /**
     * Creates the Menu Screen JPanel
     *
     * @return The menu screen panel.
     */
    public static JPanel createMenuScreen(CardLayout cardLayout, JPanel cardPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // Necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = {"neues spiel", "spiel laden", "zurück"};

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            StyleComponents.styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "newGame");
                        break;
                    case 1:
                        JPanel newBoardPanel = BoardScreenPanel.createBoardScreen(cardLayout, cardPanel, "singleplayer", GUI.difficulty);
                        newBoardPanel.setName("Board");
                        cardPanel.add(newBoardPanel, "Board");

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Spielstand laden");
                        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

                        int toLoad = fileChooser.showOpenDialog(null);
                        if (toLoad == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            cardLayout.show(cardPanel, "Board");
                            GUI.slider.setEnabled(false);
                            GameManager.getInstance().loadGame(selectedFile);
                        }
                        break;
                    case 2:
                        cardLayout.show(cardPanel, "Home");
                        break;
                }
            });

            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        // Adding all components
        panel.add(HomeScreenPanel.createHomeLogo());
        panel.add(HomeScreenPanel.createHomeImage());
        panel.add(buttonPanel);

        return panel;
    }

}
