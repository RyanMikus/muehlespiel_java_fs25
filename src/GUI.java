import javax.swing.*;
import java.awt.*;

public class GUI {
    
    // GUI-elements
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // colors
    private final Color backgroundColor = new Color(17,35,38);      // rgb(57, 79, 88)
    private final Color buttonColor = new Color(250, 240, 220);      // rgb(195, 156, 92)

    // fonts
    private final Font buttonFont = new Font("Arial", Font.BOLD, 20);

    // paths
    private final String LOGO_PATH = "/resources/muehle_logo.png";
    private final String BOARD_IMAGE_PATH = "/resources/muehle_board.png";

    public GUI(){
        window = new JFrame("Mühle");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(900, 750);
        window.setResizable(false);
        window.setLocationRelativeTo(null);
        window.setBackground(backgroundColor);

        // main panel GUI
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(backgroundColor);

        // screens
        cardPanel.add(createHomeScreen(), "Home");
        cardPanel.add(createMenuScreen(), "Menu");
        cardPanel.add(createNewGameScreen(), "newGame");
        cardPanel.add(createLoadGameScreen(), "loadGame");

        window.add(cardPanel);
        window.setVisible(true);

    } // GUI

    private JPanel createHomeScreen(){
        
        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 0, 20, 0));

        // logo - position top 
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(LOGO_PATH));    
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        
        // logo - position middle
        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(BOARD_IMAGE_PATH));    
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }


        // button - position bottom
        JPanel buttoPanel = new JPanel(new GridBagLayout());
        buttoPanel.setOpaque(false);

        JButton startButton = new JButton("spiel starten");
        styleButton(startButton);
        
        startButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "Menu");
        });
        
        buttoPanel.add(startButton, new GridBagConstraints());

        // adding all components
        panel.add(logo);
        panel.add(image);
        panel.add(buttoPanel);
        
        return panel;

    } // createHomeScreen

    private JPanel createMenuScreen(){
        
        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // logo - position top 
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(LOGO_PATH));    
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        
        // logo - position middle
        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(BOARD_IMAGE_PATH));    
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }


        // button - position bottom
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton newGameButton = new JButton("neues spiel");
        styleButton(newGameButton);
        newGameButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "newGame");
        });

         
        JButton loadGameButton = new JButton("spiel laden");
        styleButton(loadGameButton);
        loadGameButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "loadGame");
        });

        JButton backButton = new JButton("zurück");
        styleButton(backButton);
        backButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "Home");
        });

        // positions of the buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;  // same row

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 20); // Rechts-Abstand 20px
        buttonPanel.add(newGameButton, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 20);
        buttonPanel.add(loadGameButton, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(backButton, gbc);

        // adding all components
        panel.add(logo);
        panel.add(image);
        panel.add(buttonPanel);
        
        return panel;
    } // createMenuScreen

    private JPanel createNewGameScreen(){
        
        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // logo - position top 
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(LOGO_PATH));    
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        
        // logo - position middle
        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(BOARD_IMAGE_PATH));    
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }


        // button - position bottom
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton newGameButton = new JButton("einzelspieler");
        styleButton(newGameButton);

         
        JButton loadGameButton = new JButton("mehrspieler");
        styleButton(loadGameButton);

        JButton backButton = new JButton("zurück");
        styleButton(backButton);
        backButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "Menu");
        });

        // positions of the buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;  // same row

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 20); // Rechts-Abstand 20px
        buttonPanel.add(newGameButton, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 20);
        buttonPanel.add(loadGameButton, gbc);

        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(backButton, gbc);

        // adding all components
        panel.add(logo);
        panel.add(image);
        panel.add(buttonPanel);
        
        return panel;
    } // createNewGameScreen

    private JPanel createLoadGameScreen(){
        
        // main panel Home Screen
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // logo - position top 
        JLabel logo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(LOGO_PATH));    
            Image scaled = icon.getImage().getScaledInstance(450, 180, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaled));
            logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }
        
        // logo - position middle
        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(BOARD_IMAGE_PATH));    
            Image scaled = icon.getImage().getScaledInstance(350, 250, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }


        // button - position bottom
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        JButton newGameButton = new JButton("auswählen");
        styleButton(newGameButton);

        JButton backButton = new JButton("zurück");
        styleButton(backButton);
        backButton.addActionListener((e) ->{
            cardLayout.show(cardPanel, "Menu");
        });

        // positions of the buttons
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;  // same row

        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 20); // Rechts-Abstand 20px
        buttonPanel.add(newGameButton, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        buttonPanel.add(backButton, gbc);

        // adding all components
        panel.add(logo);
        panel.add(image);
        panel.add(buttonPanel);
        
        return panel;
    } // createNewGameScreen


    /* help method to style buttons */
    public void styleButton(JButton button) {
        // Style
        button.setPreferredSize(new Dimension(180, 50));
        button.setBackground(backgroundColor);
        button.setForeground(buttonColor);
        button.setFont(buttonFont);
        button.setText(button.getText().toUpperCase());
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(buttonColor, 3));
        button.setFocusPainted(false);
    
        // Hover
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(buttonColor);
                button.setForeground(backgroundColor);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
                button.setForeground(buttonColor);
            }
        });

    }// styleButton


    public static void main(String[] args) {
        new GUI();
    } // main
} // GUi
