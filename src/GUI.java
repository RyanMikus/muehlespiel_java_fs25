import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class GUI implements Observer {

    // region Fields
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JMenuBar menuBar;
    private JLabel gamePrompt;
    private JLabel turnTime;
    private JSlider slider;
    private GUIBoard guiBoard;

    private static GUI instance;

    // Color-Picker for RGB Values : rgb(0, 153, 255)
    private final Color BACKGROUND_COLOR = new Color(17, 35, 38);
    private final Color BUTTON_COLOR = new Color(250, 240, 220);
    private final Color TIMER_COLOR = new Color(250, 240, 220);
    private final Color TURN_TIMER_COLOR = new Color(255, 230, 0);
    private final Color GAME_OVER_BACKGROUND_COLOR = new Color(47, 47, 47);
    private final Color GAME_OVER_BUTTON_COLOR = new Color(0, 255, 0);

    private final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 20);
    private final Font TIMER_FONT = new Font("Arial", Font.BOLD, 16);
    private final Font TURN_TIMER_FONT = new Font("Arial", Font.BOLD, 20);
    private final Font GAME_OVER_BUTTON_FONT = new Font("Arial", Font.BOLD, 20);

    private final String LOGO_PATH = "/resources/muehle_logo.png";
    private final String BOARD_IMAGE_PATH = "/resources/muehle_board.png";
    private final String DRAW_GIF_PATH = "/resources/draw.gif";
    private final String WIN_GIF_PATH = "/resources/win.gif";

    private String mode;
    private DifficultyStrategy difficulty;
    // endregion

    // region Initialization

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

        // menu Bar
        menuBar = new JMenuBar();
        menuBar.add(createHelpMenu());
        menuBar.add(createCreditsMenu());

        // main panel GUI
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(BACKGROUND_COLOR);

        // screens
        cardPanel.add(createHomeScreen(), "Home");
        cardPanel.add(createMenuScreen(), "Menu");
        cardPanel.add(createNewGameScreen(), "newGame");
        cardPanel.add(createDifficultyScreen(), "Difficulty");

        window.setJMenuBar(menuBar);
        window.add(cardPanel);
        window.setVisible(true);

        BoardManager.getInstance().addObserver(this);

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
    // endregion

    // region Menu creation

    /**
     * Creates the "Help" menu of the application, which contains items providing
     * information or assistance, such as the game rules.
     *
     * @return The "Help" menu populated with relevant menu items.
     */
    private JMenu createHelpMenu() {
        JMenu help = new JMenu("Hilfe");
        JMenuItem rules = new JMenuItem("Regeln");

        String rulesText =

                "<html><div style='text-align: center;'>" +
                        "<h3>1. Spielziel:</h3>" +
                        "Ziel ist es, alle gegnerischen Steine zu entfernen oder ihn zu blockieren,<br>" +
                        "sodass er keinen gültigen Zug mehr machen kann.<br><br>" +

                        "<h3>2. Spielphasen:</h3>" +
                        "<u>a) Setzphase:</u><br>" +
                        "Jeder Spieler setzt abwechselnd einen Stein auf ein freies Feld.<br>" +
                        "Wenn eine Mühle (3 Steine in einer Linie) entsteht, darf ein gegnerischer Stein entfernt werden.<br><br>"
                        +

                        "<u>b) Zugphase:</u><br>" +
                        "Nachdem alle Steine gesetzt wurden, bewegen die Spieler abwechselnd einen Stein<br>" +
                        "auf ein benachbartes Feld. Wieder gilt: bei einer Mühle darf ein gegnerischer Stein entfernt werden.<br><br>"
                        +

                        "<u>c) Springphase:</u><br>" +
                        "Wenn ein Spieler nur noch 3 Steine hat, darf er mit diesen auf ein beliebiges freies Feld springen.<br><br>"
                        +

                        "<h3>3. Mühle-Regel:</h3>" +
                        "Eine Mühle entsteht, wenn 3 eigene Steine in einer geraden Linie stehen.<br>" +
                        "Eine Mühle darf beliebig oft geöffnet und neu gebildet werden, um Steine zu entfernen.<br><br>"
                        +

                        "<h3>4. Steine entfernen:</h3>" +
                        "Es dürfen keine Steine aus bestehenden Mühlen entfernt werden –<br>" +
                        "es sei denn, alle gegnerischen Steine sind in Mühlen.<br><br>" +

                        "<h3>5. Spielende:</h3>" +
                        "Das Spiel endet, wenn ein Spieler:<br>" +
                        "- nur noch 2 Steine hat (verliert)<br>" +
                        "- keinen gültigen Zug mehr machen kann (verliert)<br>" +
                        "- die Bedenkzeit abgelaufen ist (verliert).<br><br>" +
                        "Oder wenn beide Spieler innrhalb von 20 Zügen keine Mühle formen" +
                        "</div></html>";

        rules.addActionListener(
                e -> JOptionPane.showMessageDialog(window, rulesText, "Regeln", JOptionPane.PLAIN_MESSAGE));

        help.add(rules);

        return help;
    }

    /**
     * Creates the "Credits" menu which displays information about the contributors
     * to the project when clicked. The menu, when interacted with, triggers a
     * pop-up dialog containing formatted credit text.
     *
     * @return The constructed "Credits" menu as a JMenu instance.
     */
    private JMenu createCreditsMenu() {
        JMenu credits = new JMenu("Credits");

        String creditsText = "<html><div style='text-align: center;'>" +
                "<h2>Silvan Balloni - SCRUM Master</h2>" +
                "Hat die Regeln so verdrahtet, dass selbst Aristoteles beim Spielen nicken würde.<br>Ohne ihn gäbe es entweder 47 Züge auf einmal - oder gar keine.<br><br>"
                +
                "Dank ihm gibt es endlich einen Spielablauf, der mit jeder Aktion neue Fragen aufwirft.<br> Seine Game-Logik hat die Grenzen der Realität überschritten - leider auch die der Spielbarkeit.<br><br>"
                +
                "<h2>Rino Diskuss - Product Owner</h2>" +
                "Seine KI ist so schlau, dass sie beim Spielen gegen sich selbst verloren hat - freiwillig, aus Höflichkeit.<br><br>"
                +
                "Ihre KI trifft Entscheidungen auf Basis von über 7 Millionen Parametern - keiner davon sinnvoll.<br>Sie spielt zwar nicht gut, aber sie spielt überzeugt. Manchmal sogar gegen sich selbst. Und verliert trotzdem.<br><br>"
                +
                "<h2>Christian Sutter - Praktikant</h2>" +
                "Designte die Oberfläche so edel, dass der Louvre sie für die Dauerausstellung angefragt hat.<br> Die Buttons sind ergonomisch, empathisch und geben einem das Gefühl, geliebt zu werden.<br><br>"
                +
                "Investierte 95 % der Entwicklungszeit in die perfekten Schattenverläufe und Button-Rundungen.<br> Die GUI glänzt, funkelt und tanzt - nur leider weiß niemand, wie man das Spiel startet. Oder beendet. Oder spielt."
                +
                "</div></html>";

        credits.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(window, creditsText, "Credits", JOptionPane.PLAIN_MESSAGE);
            }
        });

        return credits;
    }
    // endregion

    // region Screen creation

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

        String[] buttonTexts = {"neues spiel", "spiel laden", "zurück"};

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "newGame");
                        break;
                    case 1:
                        JPanel newBoardPanel = createBoardScreen("singleplayer", difficulty);
                        newBoardPanel.setName("Board");
                        cardPanel.add(newBoardPanel, "Board");

                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Spielstand laden");
                        fileChooser.setFileFilter(new FileNameExtensionFilter("JSON-Dateien", "json"));

                        int toLoad = fileChooser.showOpenDialog(null);
                        if (toLoad == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = fileChooser.getSelectedFile();
                            cardLayout.show(cardPanel, "Board");
                            slider.setEnabled(false);
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

        String[] buttonTexts = {"einzelspieler", "mehrspieler", "zurück"};

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        cardLayout.show(cardPanel, "Difficulty");
                        break;
                    case 1:
                        mode = "multiplayer";
                        difficulty = null;
                        cardPanel.add(createBoardScreen(mode, difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 2:
                        cardLayout.show(cardPanel, "Menu");
                        break;

                }
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
     * Creates the Difficulty Screen JPanel.
     *
     * @return The difficulty screen panel.
     */
    private JPanel createDifficultyScreen() {

        // main panel Dfficulty Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        // buttons
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; // same row

        String[] buttonTexts = {"leicht", "mittel", "schwer", "zurück"};

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        mode = "singleplayer";
                        difficulty = new EasyDifficulty();
                        cardPanel.add(createBoardScreen(mode, difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 1:
                        mode = "singleplayer";
                        difficulty = new MediumDifficulty();
                        cardPanel.add(createBoardScreen(mode, difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 2:
                        mode = "singleplayer";
                        difficulty = new HardDifficulty();
                        cardPanel.add(createBoardScreen(mode, difficulty), "Board");
                        cardLayout.show(cardPanel, "Board");
                        break;
                    case 3:
                        cardLayout.show(cardPanel, "newGame");
                        break;
                }
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

        String[] buttonTexts = {"auswählen", "zurück"};
        String[] screenNames = {"", "Menu"};

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
    private JPanel createBoardScreen(String mode, DifficultyStrategy difficulty) {
        GameManager gameManager = GameManager.getInstance();

        // main Panel Board Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 35, 40));
        panel.setOpaque(false);

        // top Panel
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 0, 100));

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
        sliderPanel.setOpaque(false);

        JLabel sliderLabel = new JLabel("Bedenkzeit: 30s");
        sliderLabel.setForeground(TIMER_COLOR);
        sliderLabel.setFont(TIMER_FONT);
        sliderLabel.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 50));

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
            if (!gameManager.getGameStatus()) {
                gameManager.setTimePerTurn(slider.getValue());
                slider.setEnabled(false);
                gameManager.startGame(mode, difficulty);
            } else {
                System.out.println("[WARNING] - Game is already running!");
            }
        });

        sliderPanel.add(sliderLabel);
        sliderPanel.add(slider);
        sliderPanel.add(startButton);

        JPanel promptTimePanel = new JPanel();
        promptTimePanel.setLayout(new BoxLayout(promptTimePanel, BoxLayout.X_AXIS));
        promptTimePanel.setOpaque(false);

        gamePrompt = new JLabel("Bedenkzeit einstellen und Spiel starten");
        gamePrompt.setForeground(TURN_TIMER_COLOR);
        gamePrompt.setFont(TURN_TIMER_FONT);
        gamePrompt.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 50));

        turnTime = new JLabel("Verbleibende Zeit: 30s");
        turnTime.setForeground(TURN_TIMER_COLOR);
        turnTime.setFont(TURN_TIMER_FONT);
        turnTime.setAlignmentX(Component.CENTER_ALIGNMENT);
        turnTime.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        promptTimePanel.add(gamePrompt);
        promptTimePanel.add(Box.createHorizontalGlue());
        promptTimePanel.add(turnTime);

        topPanel.add(sliderPanel);
        topPanel.add(promptTimePanel);

        // board panel
        JPanel boardPanel = new JPanel();
        boardPanel.setPreferredSize(new Dimension(900, 475));
        boardPanel.setMinimumSize(new Dimension(900, 475));
        boardPanel.setMaximumSize(new Dimension(900, 475));
        boardPanel.setOpaque(false);

        guiBoard = new GUIBoard();
        boardPanel.add(guiBoard, BorderLayout.CENTER);
        gameManager.initializeListeners(guiBoard);

        // button panel
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // necessary positions settings
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        String[] buttonTexts = {"undo", "redo", "speichern", "beenden"};

        for (int i = 0; i < buttonTexts.length; i++) {
            JButton button = new JButton(buttonTexts[i]);
            styleBoardButton(button);

            int buttonIndex = i;
            button.addActionListener(e -> {
                switch (buttonIndex) {
                    case 0:
                        if (!getGuiBoard().getRemoveMode() && !(gameManager.getCurrentPlayer() instanceof CPUPlayer)) {
                            gameManager.undoMove();
                        }
                        break;
                    case 1:
                        if (!getGuiBoard().getRemoveMode()) {
                            gameManager.redoMove();
                        }
                        break;
                    case 2:
                        if (!gameManager.getGameStatus()) {
                            break;
                        }

                        gameManager.stopTurnTimer();
                        JFileChooser chooser = new JFileChooser();
                        chooser.setDialogTitle("Spiel speichern");
                        chooser.setFileFilter(
                                new javax.swing.filechooser.FileNameExtensionFilter("JSON-Dateien", "json"));
                        int toSave = chooser.showSaveDialog(null);

                        if (toSave == JFileChooser.APPROVE_OPTION) {
                            File selectedFile = chooser.getSelectedFile();
                            if (!selectedFile.getName().endsWith(".json")) {
                                selectedFile = new File(selectedFile.getAbsolutePath() + ".json");
                            }

                            gameManager.saveGame(selectedFile);
                        }

                        break;
                    case 3:
                        cardLayout.show(cardPanel, "Menu");
                        gameManager.resetGame();
                        break;
                }
            });
            // position
            gbc.gridx = i % 6;
            gbc.gridy = 0;
            buttonPanel.add(button, gbc);
        }

        // for debugging
        // timePanel.setBorder(BorderFactory.createLineBorder(Color.red));
        // boardPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        // buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));

        // ---------------- COMPOSE MAIN PANEL ----------------
        panel.add(topPanel);
        panel.add(boardPanel);
        panel.add(buttonPanel);
        panel.setName("Board");
        return panel;

    } // createBoardScreen
    // endregion

    // region Design

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
            System.out.println("[ERROR] - Image not found: " + e.getMessage());
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
            System.out.println("[ERROR] - Image not found: " + e.getMessage());
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

    /**
     * Styles a button by changing its colors, font, opacity and border.
     * Also changes the behaviour on hovering over the button.
     *
     * @param button The button that should get styled.
     */
    public void styleGameOverMsgButton(JButton button) {

        // Style
        button.setPreferredSize(new Dimension(200, 50));
        button.setMinimumSize(new Dimension(200, 50));
        button.setMaximumSize(new Dimension(200, 50));
        button.setBackground(GAME_OVER_BUTTON_COLOR);
        button.setForeground(GAME_OVER_BACKGROUND_COLOR);
        button.setFont(BUTTON_FONT);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(GAME_OVER_BUTTON_COLOR, 3));
        button.setFocusPainted(false);

        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(GAME_OVER_BACKGROUND_COLOR);
                button.setForeground(GAME_OVER_BUTTON_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(GAME_OVER_BUTTON_COLOR);
                button.setForeground(GAME_OVER_BACKGROUND_COLOR);
            }
        });
    }// styleBoardButton

    // endregion

    // region Events

    /**
     * Updates the text displaying the remaining time to perform a move.
     *
     * @param timeLeft The value that should get displayed.
     */
    public void updateTimer(int timeLeft) {
        turnTime.setText("Verbleibende Zeit: " + timeLeft + "s");
    }

    public void updateStatus(String text, Color color) {
        gamePrompt.setText(text);
        gamePrompt.setForeground(color);
    }

    /**
     * Build the Game Over Pop-Up Window. Depending on the way the game
     * ends, the Window will show different Messages.
     *
     * @param winner The player that has won the game.
     * @param loser  The player that has lost the game. The opponent of the winner.
     */
    public void showGameOverMsg(PlayerEntity winner, PlayerEntity loser) {
        GameManager gameManager = GameManager.getInstance();

        JDialog dialog = new JDialog();
        dialog.setTitle("Game Over");
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setResizable(false);


        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(GAME_OVER_BACKGROUND_COLOR);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel topText = new JLabel("GAME OVER", SwingConstants.CENTER);
        topText.setFont(new Font("Arial", Font.BOLD, 70));
        topText.setForeground(Color.WHITE);
        topText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel gifLabel = new JLabel();
        gifLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setPreferredSize(new Dimension(250, 250));
        gifLabel.setMaximumSize(new Dimension(250, 250));
        gifLabel.setMinimumSize(new Dimension(250, 250));

        JLabel winnerText = new JLabel();
        winnerText.setFont(new Font("Arial", Font.BOLD, 50));
        winnerText.setForeground(Color.WHITE);
        winnerText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoText = new JLabel();
        infoText.setFont(new Font("Arial", Font.BOLD, 20));
        infoText.setForeground(Color.WHITE);
        infoText.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        infoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoText.setHorizontalAlignment(SwingConstants.CENTER);

        JButton newGameButton = new JButton("Neues Spiel");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleGameOverMsgButton(newGameButton);

        newGameButton.addActionListener(e -> {
            dialog.dispose();
            gameManager.resetGame();
            cardLayout.show(cardPanel, "Board");
        });

        Image winGif = (new ImageIcon(getClass().getResource(WIN_GIF_PATH))).getImage().getScaledInstance(250, 250,
                Image.SCALE_DEFAULT);
        Image drawGif = (new ImageIcon(getClass().getResource(DRAW_GIF_PATH))).getImage().getScaledInstance(250, 250,
                Image.SCALE_DEFAULT);

        String winnerOutput = "";
        String loserOutput = "";
        if (winner != null) {
            if (winner.getName().equalsIgnoreCase("white")) {
                winnerOutput = "Weiss";
                loserOutput = "Schwarz";
            } else {
                winnerOutput = "Schwarz";
                loserOutput = "Weiss";
            }
        }

        switch (gameManager.lossCause) {
            case STONECOUNT:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText("Sieger: " + winnerOutput);
                infoText.setText(loserOutput + " hat weniger als 3 Steine");
                break;
            case BLOCKED:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText("Sieger: " + winnerOutput);
                infoText.setText(loserOutput + " wurde blockiert. Keine Züge mehr möglich");
                break;
            case STALEMATE:
                gifLabel.setIcon(new ImageIcon(drawGif));
                winnerText.setText("Unentschieden");
                infoText.setText("Während 20 Zügen wurde keine Mühle gebildet");
                break;
            case TIMEOUT:
                gifLabel.setIcon(new ImageIcon(winGif));
                winnerText.setText("Sieger: " + winnerOutput);
                infoText.setText("Spielzeit von " + loserOutput + " abgelaufen");
                break;
        }

        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(topText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(gifLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(winnerText);
        mainPanel.add(infoText);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(newGameButton);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    /**
     * Getter for the initialized guiBoard.
     *
     * @return The guiBoard that creates the board and repaints on change.
     */
    public GUIBoard getGuiBoard() {
        return guiBoard;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * This method gets called, whenever the board gets updated (by its observable).
     * Repaints the GUI.
     */
    @Override
    public void update() {
        guiBoard.repaint();
    }

    public void reset() {
        slider.setValue(30);
        slider.setEnabled(true);

        Component[] components = cardPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("Board")) {
                cardPanel.remove(comp);
                break;
            }
        }

        JPanel newBoardPanel = createBoardScreen(mode, difficulty);
        newBoardPanel.setName("Board");
        cardPanel.add(newBoardPanel, "Board");

        GUIBoard newGuiBoard = findGuiBoardIn(newBoardPanel);
        if (newGuiBoard != null) {
            newGuiBoard.resetState();
            GameManager.getInstance().initializeListeners(newGuiBoard);
        } else {
            System.err.println("[ERROR] - Konnte GUIBoard im neuen BoardPanel nicht finden!");
        }
    }

    private GUIBoard findGuiBoardIn(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof GUIBoard)
                return (GUIBoard) c;
            if (c instanceof Container) {
                GUIBoard found = findGuiBoardIn((Container) c);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
    // endregion

    public static void main(String[] args) {
        GameManager.getInstance();
    } // main
} // GUi
