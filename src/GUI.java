import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI {
    
    // GUI-elements
    private JFrame window;
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // colors
    private final Color backgroundColor = new Color(17,35,38);      // rgb(27,40,45)
    private final Color titleColor = new Color(255, 255, 255);      // rgb(255, 255, 255)
    private final Color buttonColor = new Color(195, 156, 92);      // rgb(195, 156, 92)
    private final Color buttonTextColor = new Color(30, 30, 30);    // rgb(30, 30, 30)

    // fonts
    private final Font titleFont = new Font("Verdana", Font.BOLD, 40);
    private final Font buttonFont = new Font("Arial", Font.BOLD, 20);

    public GUI(){
        window = new JFrame("Mühle");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(700, 600);
        window.setResizable(false);
        //window.setLocationRelativeTo(null);
        window.setBackground(backgroundColor);

        // main panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout); // Cardlayout for multiple screens
        cardPanel.setBackground(backgroundColor);

        // screens
        cardPanel.add(createHomeScreen(), "Home");
        cardPanel.add(createMenuScreen(), "Menu");
        //cardPanel.add(createGameScreen(), "Game");

        window.add(cardPanel);
        window.setVisible(true);

    } // GUI

    private JPanel createHomeScreen(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(backgroundColor);

        JLabel title = new JLabel("Wilkommen bei Mühle");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(titleFont);
        title.setForeground(titleColor);

        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/muehle.png"));    
            Image scaled = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }

        JButton startButton = new JButton("Spiel starten");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(startButton);
        startButton.addActionListener((ActionEvent e) ->{
            cardLayout.show(cardPanel, "Menu");
        });

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(image);
        panel.add(Box.createVerticalStrut(30));
        panel.add(startButton);
        panel.add(Box.createVerticalGlue());

        return panel;

    } // createHomeScreen

    private JPanel createMenuScreen(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        panel.setBackground(backgroundColor);

        JLabel title = new JLabel("Zeit für ein Spiel");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setFont(titleFont);
        title.setForeground(titleColor);

        JLabel image = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/muehle.png"));    
            Image scaled = icon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);
            image.setIcon(new ImageIcon(scaled));
            image.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            System.out.println("Bild nicht gefunden: " + e.getMessage());
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        JButton newGameButton = new JButton("Neues Spiel");
        newGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(newGameButton);

        JButton loadGameButton = new JButton("Spiel laden");
        loadGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        styleButton(loadGameButton);

        JButton backButton = new JButton("Zurück");
        styleButton(backButton);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener((ActionEvent e) ->{
            cardLayout.show(cardPanel, "Home");
        });

        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(backButton);

        panel.add(Box.createVerticalGlue());
        panel.add(title);
        panel.add(Box.createVerticalStrut(30));
        panel.add(image);
        panel.add(Box.createVerticalStrut(30));
        panel.add(buttonPanel);
        panel.add(Box.createVerticalGlue());

        return panel;
    } // createMenuScreen

    // help method to style buttons
    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(180, 40));
        button.setBackground(buttonColor);
        button.setForeground(buttonTextColor);
        button.setFont(buttonFont);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setText(button.getText().toUpperCase());
    }// styleButton


    public static void main(String[] args) {
        new GUI();
    } // main
}
