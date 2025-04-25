import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class GUI implements Observer{

    // region Fields
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JLabel turnTime;
    private JSlider slider;
    private GUIBoard guiBoard;

    private static GUI instance;
    private GameManager gameManager;
    private BoardManager boardManager;

    private final Color BACKGROUND_COLOR = new Color(17, 35, 38); // rgb(57, 79, 88)
    private final Color BUTTON_COLOR = new Color(250, 240, 220); // rgb(195, 156, 92)
    private final Color TIMER_COLOR = new Color(250, 240, 220); // rgb(195, 156, 92)
    private final Color TURN_TIMER_COLOR = new Color(255, 230, 0); // rgb(255, 230, 0)

    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 20);
    private final Font TIMER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font TURN_TIMER_FONT = new Font("Arial", Font.BOLD, 20);

    private final String LOGO_PATH = "/resources/muehle_logo.png";
    private final String BOARD_IMAGE_PATH = "/resources/muehle_board.png";
    // endregion

    //region Initialization
    /**
     * Initializes the GUI.
     * Creates the window, main panel and all different screens.
     */
    public GUI() {
        
        window = new JFrame("Mühle");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 750);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        // window.setLocation(2100, 350);;
        window.setBackground(BACKGROUND_COLOR);

        // main panel GUI
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(BACKGROUND_COLOR);

        // screens
        //cardPanel.add(createHomeScreen(), "Home");
        //cardPanel.add(createMenuScreen(), "Menu");
        cardPanel.add(createNewGameScreen(), "newGame");
        //cardPanel.add(createLoadGameScreen(), "loadGame");

        window.add(cardPanel);
        window.setVisible(true);

    } // GUI

    /**
     * Creates a new Instance of this Manager, if there is none already.
     *
     * @return Instance if one already existed, creates a new one otherwise.
     */
    public static GUI getInstance() {
        if (instance == null) {
            instance = new GUI();
        }
        return instance;
    }

    /**
     * Setter for the managers to avoid recursive constructor calls.
     *
     * @param gameManager The instance of the game manager.
     * @param boardManager The instance of the board manager.
     */
    public void initializeManagers(GameManager gameManager, BoardManager boardManager){
        this.gameManager = gameManager;
        this.boardManager = boardManager;

        // Register to the observable
        boardManager.addObserver(this);
    }
    //endregion

    //region Screen creation
    /**
     * Creates the Home Screen JPanel.
     * 
     * @return The home screen panel.
     */
    private JPanel createHomeScreen() {

        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton startButton = new JButton("spiel starten");
        styleButton(startButton);

        startButton.addActionListener((e) -> {
            cardLayout.show(cardPanel, "Menu");
        });

        buttonPanel.add(startButton, new GridBagConstraints());

        // adding all components
        panel.add(createHomeLogo());
        panel.add(createHomeImage());
        panel.add(buttonPanel);

        return panel;

    } // createHomeScreen

    /**
     * Creates the Menu Screen JPanel
     * 
     * @return The menu screen panel.
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
     * Creates the New Game Screen JPanel.
     * 
     * @return The new game screen panel.
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
                cardPanel.add(createBoardScreen(), "Board");
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
     * Creates the Load Screen JPanel.
     * 
     * @return The load screen panel.
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
     * Creates the Board Screen JPanel.
     * 
     * @return The board screen panel.
     */
    private JPanel createBoardScreen() {

        // main Panel Board Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 35, 40));
        panel.setOpaque(false);

        // time Panel
        JPanel timePanel = new JPanel();
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.Y_AXIS));
        timePanel.setOpaque(false);
        timePanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 0, 100));

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setOpaque(false);

        JLabel sliderLabel = new JLabel("Bedenkzeit: 30s");
        sliderLabel.setForeground(TIMER_COLOR);
        sliderLabel.setFont(TIMER_FONT);
        sliderLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        slider = new JSlider(10, 60, 30);
        slider.setMajorTickSpacing(10);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setOpaque(false);
        slider.setForeground(TIMER_COLOR);
        slider.setFont(TIMER_FONT);
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 50));

        slider.addChangeListener(e -> {
            sliderLabel.setText("Bedenkzeit: " + slider.getValue() + "s");
            turnTime.setText("Verbleibende Zeit: " + slider.getValue() + "s");
        });

        JButton startButton = new JButton("Spiel starten");
        styleButton(startButton);
        
        startButton.addActionListener(e -> {
            if(!gameManager.getGameStatus()){
                slider.setEnabled(false);
                gameManager.startGame();
            }
            else{
                System.out.println("Game is already running!");
            }
        });

        sliderPanel.add(sliderLabel);
        sliderPanel.add(slider);
        sliderPanel.add(startButton);

        turnTime = new JLabel("Verbleibende Zeit: 30s");
        turnTime.setForeground(TURN_TIMER_COLOR);
        turnTime.setFont(TURN_TIMER_FONT);
        turnTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnTime.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        timePanel.add(sliderPanel);
        timePanel.add(turnTime);

        // board panel
        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(900, 475));
        boardPanel.setMinimumSize(new Dimension(900, 475));
        boardPanel.setMaximumSize(new Dimension(900, 475));
        boardPanel.setOpaque(false);

        guiBoard = new GUIBoard(gameManager, boardManager);
        boardPanel.add(guiBoard, BorderLayout.CENTER);

        // button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        String[] buttonTexts = { "undo", "redo", "save", "load", "rules", "quit" };

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleBoardButton(button);

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
            gbc.gridx = i % 6;
            gbc.gridy = 0;
            buttonPanel.add(button, gbc);
        }

        // for debugging
        //timePanel.setBorder(BorderFactory.createLineBorder(Color.red));
        //boardPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        
        // ---------------- COMPOSE MAIN PANEL ----------------
        panel.add(timePanel);
        panel.add(boardPanel);
        panel.add(buttonPanel);
        return panel;

    } // createBoardScreen
    //endregion

    //region Design
    /**
     * Creates a specific image which is shown on multiple Screens.
     * This method is only used to avoid code duplicates.
     * 
     * @return The created image as a label.
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
     * @return The created logo as a label.
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
     * Styles a button by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     * 
     * @param button The button that should get styled.
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

    /**
     * Styles a button by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     * 
     * @param button The button that should get styled.
     */
    public void styleBoardButton(JButton button) {

        // Style
        button.setPreferredSize(new Dimension(130, 40));
        button.setMinimumSize(new Dimension(130, 40));
        button.setMaximumSize(new Dimension(130, 40));
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
    }// styleBoardButton
    //endregion

    //region Events
    /**
     * Updates the text displaying the remaining time to perform a move.
     *
     * @param timeLeft The value that should get displayed.
     */
    public void updateTimer(int timeLeft) {
        turnTime.setText("Verbleibende Zeit: " + timeLeft + "s");
    }

    /**
     * TODO: Implement different Messages depending on the way the game ended. Get correct winner.
     * TODO: Either modify this method or implement showGameOverScreen correctly.
     * @param currentPlayer
     */
    public void showTimeOverMsg(PlayerEntity currentPlayer) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Game Over");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);
        panel.setLayout(new BorderLayout());

        // JLabel label = new JLabel(currentPlayer.getName() + " hat gewonnen!",
        // SwingConstants.CENTER);
        JLabel label = new JLabel("xxx hat gewonnen", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(label, BorderLayout.CENTER);

        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(e -> dialog.dispose());
        panel.add(closeButton, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * TODO: Implement correctly or modify showTimeOverMsg
     * @param winnerEntity
     */
    public void showGameOverScreen(PlayerEntity winnerEntity) {

    }

    /**
     * Getter for initial Time per Turn.
     *
     * @return The maximum time each player should have to perform a move, set by a slider.
     */
    public int getTimePerTurn() {
        return slider.getValue();
    }

    /**
     * Initiates the remove stone process by enabling remove mode in the GUI board.
     * This allows the player to select an opponent's stone for removal.
     * The provided callback will be triggered once the removal attempt is
     * completed.
     *
     * @param callback A Consumer<Boolean> that receives true if a stone was
     *                 successfully removed,
     *                 or false if the removal was aborted or invalid.
     */
    public void removeStone(Consumer<Boolean> callback) {
        guiBoard.removeStone(callback);
    }

    /**
     * This method gets called, whenever the board gets updated (by its observable).
     * Repaints the GUI.
     */
    @Override
    public void update() {
        guiBoard.repaint();
    }
    //endregion

    public static void main(String[] args) {
        GameManager.getInstance();
    } // main
} // GUi
