import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class QuickLinkShortenerGUI extends JFrame {
    private static final String FILE_NAME = "url_mappings.txt";
    private Map<String, String> urlMap = new HashMap<>();
    private JTextField longUrlField, shortUrlField, lookupField;
    private JTextArea resultArea;

    public QuickLinkShortenerGUI() {
        // Initialize the result area
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Load saved URLs after resultArea is initialized
        loadUrls();

        // Set up the GUI window
        setTitle("QuickLink Shortener");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel for inputting long URLs and generating short URLs
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Enter Long URL:"));
        longUrlField = new JTextField();
        inputPanel.add(longUrlField);

        JButton shortenButton = new JButton("Shorten URL");
        shortenButton.addActionListener(e -> shortenUrl());
        inputPanel.add(shortenButton);

        shortUrlField = new JTextField();
        shortUrlField.setEditable(false);
        inputPanel.add(shortUrlField);

        // Panel for looking up original URLs from short URLs
        JPanel lookupPanel = new JPanel(new GridLayout(2, 2));
        lookupPanel.add(new JLabel("Enter Short URL:"));
        lookupField = new JTextField();
        lookupPanel.add(lookupField);

        JButton lookupButton = new JButton("Retrieve Original URL");
        lookupButton.addActionListener(e -> retrieveOriginalUrl());
        lookupPanel.add(lookupButton);

        // Adding panels to the main window
        add(inputPanel, BorderLayout.NORTH);
        add(lookupPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Shorten the entered long URL and display the result
    private void shortenUrl() {
        String longUrl = longUrlField.getText().trim();

        // Basic URL validation
        if (longUrl.isEmpty() || !longUrl.startsWith("http")) {
            resultArea.append("Please enter a valid URL.\n");
            return;
        }

        // Check if URL is already shortened
        String existingShortUrl = getKeyByValue(urlMap, longUrl);
        if (existingShortUrl != null) {
            shortUrlField.setText(existingShortUrl);
            resultArea.append("This URL has already been shortened: " + existingShortUrl + "\n");
            return;
        }

        // Generate a unique short URL
        String shortUrl;
        do {
            shortUrl = generateShortUrl();
        } while (urlMap.containsKey(shortUrl)); // Ensure uniqueness

        urlMap.put(shortUrl, longUrl);
        shortUrlField.setText(shortUrl);
        resultArea.append("Shortened URL: " + shortUrl + " -> " + longUrl + "\n");
        saveUrls(); // Save URL mapping
    }

    // Retrieve the original URL from the short URL entered
    private void retrieveOriginalUrl() {
        String shortUrl = lookupField.getText().trim();

        if (urlMap.containsKey(shortUrl)) {
            String originalUrl = urlMap.get(shortUrl);
            resultArea.append("Original URL for " + shortUrl + ": " + originalUrl + "\n");
        } else {
            resultArea.append("No matching URL found for: " + shortUrl + "\n");
        }
    }

    // Generate a unique short URL
    private String generateShortUrl() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder shortUrl = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int randomIndex = (int) (Math.random() * characters.length());
            shortUrl.append(characters.charAt(randomIndex));
        }

        return shortUrl.toString();
    }

    // Utility function to get key by value from HashMap
    private String getKeyByValue(Map<String, String> map, String value) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Save URL mappings to a file
    private void saveUrls() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                writer.write(entry.getKey() + "," + entry.getValue());
                writer.newLine();
            }
            resultArea.append("URLs saved to " + FILE_NAME + "\n");
        } catch (IOException e) {
            resultArea.append("Error saving URLs: " + e.getMessage() + "\n");
        }
    }

    // Load URL mappings from a file
    private void loadUrls() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) { // Ensure there are exactly two parts
                    urlMap.put(parts[0].trim(), parts[1].trim()); // Trim whitespace
                } else {
                    resultArea.append("Invalid line format: " + line + "\n");
                }
            }
            resultArea.append("URLs loaded from " + FILE_NAME + "\n");
        } catch (IOException e) {
            resultArea.append("No existing URL mappings found or error reading file: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(QuickLinkShortenerGUI::new);
    }
}
