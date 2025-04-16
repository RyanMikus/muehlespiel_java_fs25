import javax.swing.*;
import java.awt.*;

public class GUI {


    // GUI-elements
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // colors
    private final Color BACKGROUND_COLOR = new Color(17, 35, 38); // rgb(57, 79, 88)
    private final Color BUTTON_COLOR = new Color(250, 240, 220); // rgb(195, 156, 92)

    // fonts
    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 20);

    // paths
    private final String LOGO_PATH = "/resources/muehle_logo.png";
    private final String BOARD_IMAGE_PATH = "/resources/muehle_board.png";

    public GUI() {


        window = new JFrame("Mühle");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 750);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setBackground(BACKGROUND_COLOR);

        // main panel GUI
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(BACKGROUND_COLOR);

        // screens
        //cardPanel.add(createHomeScreen(), "Home");
        //cardPanel.add(createMenuScreen(), "Menu");
        //cardPanel.add(createNewGameScreen(), "newGame");
        //cardPanel.add(createLoadGameScreen(), "loadGame");
        cardPanel.add(createBoardScreen(), "Board");

        window.add(cardPanel);
        window.setVisible(true);

    } // GUI

    /**
     * Creates the Home Screen JPanel
     * 
     * @return JPanel
     */
    private JPanel createHomeScreen() {

        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttoPanel = new JPanel(new GridBagLayout());
        buttoPanel.setOpaque(false);

        JButton startButton = new JButton("spiel starten");
        styleButton(startButton);

        startButton.addActionListener((e) -> {
            cardLayout.show(cardPanel, "Menu");
        });

        buttoPanel.add(startButton, new GridBagConstraints());

        // adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttoPanel);

        return panel;

    } // createHomeScreen

    /**
     * Creates the Menu Screen JPanel
     * 
     * @return JPanel
     */
    private JPanel createMenuScreen() {

        // main panel Menu Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = { "neues spiel", "spiel laden", "zurück" };
        String[] screenNames = { "newGame", "loadGame", "Home" };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            String screenName = screenNames[i];
            button.addActionListener(e -> {
                cardLayout.show(cardPanel, screenName);
            });

            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        // adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttonPanel);

        return panel;
    } // createMenuScreen

    /**
     * Creates the New Game Screen JPanel
     * 
     * @return JPanel
     */
    private JPanel createNewGameScreen() {

        // main panel New Game Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = { "einzelspieler", "mehrspieler", "zurück" };
        String[] screenNames = { "Board", "Board", "Menu" };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            String screenName = screenNames[i];
            button.addActionListener(e -> {
                cardLayout.show(cardPanel, screenName);
            });

            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        // adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttonPanel);

        return panel;
    } // createNewGameScreen

    /**
     * Creates the Load Screen Screen JPanel
     * 
     * @return JPanel
     */
    private JPanel createLoadGameScreen() {

        // main panel Load Game Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = { "auswählen", "zurück" };
        String[] screenNames = { "", "Menu" };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            String screenName = screenNames[i];
            button.addActionListener(e -> {
                cardLayout.show(cardPanel, screenName);
            });

            gbc.gridx = i;
            gbc.insets = new Insets(0, 0, 0, (i < buttonTexts.length - 1) ? 20 : 0);
            buttonPanel.add(button, gbc);
        }

        // adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttonPanel);

        return panel;
    } // createNewGameScreen

    /**
     * Creates the Board Screen Screen JPanel
     * 
     * @return JPanel
     */
    private JPanel createBoardScreen() {

        // main panel New Game Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // timer
        JPanel timerPanel = new JPanel();

        // board
        JPanel boardPanel = new JPanel();

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); 

        String[] buttonTexts = { "undo", "redo", "rules", "save", "load", "quit" };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button); 

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        cardLayout.show(cardPanel, "Menu");
                        break;
                }
            });

            // position
            gbc.gridx = i % 3; 
            gbc.gridy = i / 3; 
            buttonPanel.add(button, gbc);
        }

        // adding all components
        panel.add(timerPanel);
        panel.add(boardPanel);
        panel.add(buttonPanel);

        return panel;

    } // createBoardScreen

    /**
     * Creates a specific image which is shown on multiple Screens.
     * This method is only used to avoid code duplicates.
     * 
     * @return JLabel
     */
    private JLabel createHomeImage() {

        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(BOARD_IMAGE_PATH));
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
            image.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        return image;
    } // createHomeImage

    /**
     * Creates a specific logo which is shown on multiple Screens.
     * This method is only used to avoid code duplicates.
     * 
     * @return JLabel
     */
    private JLabel createHomeLogo() {

        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(LOGO_PATH));
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
            logo.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        return logo;
    } // createHomeLogo

    /**
     * Styles a button
     * 
     * @param button
     */
    public void styleButton(JButton button) {

        // Style
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(BACKGROUND_COLOR);
        button.setForeground(BUTTON_COLOR);
        button.setFont(BUTTON_FONT);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(BUTTON_COLOR, 3));
        button.setFocusPainted(false);

        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
                button.setForeground(BACKGROUND_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BACKGROUND_COLOR);
                button.setForeground(BUTTON_COLOR);
            }
        });
    }// styleButton

    /**/
    public void updateTimer(int timeLeft) {

    }

    /**/
    public void showTimeOverMsg(PlayerEntity currentPlayer) {

    }

    public static void main(String[] args) {
        new GUI();
    } // main
} // GUi
