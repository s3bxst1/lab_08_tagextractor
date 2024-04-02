import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.*;

public class TagExtractorGUI extends JFrame {
    private JTextArea textArea;
    private JButton selectFileButton;
    private JButton selectStopWordsButton;
    private JButton extractTagsButton;
    private JButton saveTagsButton;

    private File selectedFile;
    private Set<String> stopWords;
    private Map<String, Integer> tagFrequency;

    public TagExtractorGUI() {
        super("Tag Extractor");

        textArea = new JTextArea(20, 40);
        JScrollPane scrollPane = new JScrollPane(textArea);
        selectFileButton = new JButton("Select Text File");
        selectStopWordsButton = new JButton("Select Stop Words File");
        extractTagsButton = new JButton("Extract Tags");
        saveTagsButton = new JButton("Save Tags");

        selectFileButton.addActionListener(e -> selectFile());
        selectStopWordsButton.addActionListener(e -> selectStopWords());
        extractTagsButton.addActionListener(e -> extractTags());
        saveTagsButton.addActionListener(e -> saveTags());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2));
        buttonPanel.add(selectFileButton);
        buttonPanel.add(selectStopWordsButton);
        buttonPanel.add(extractTagsButton);
        buttonPanel.add(saveTagsButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            textArea.append("Selected file: " + selectedFile.getName() + "\n");
        }
    }

    private void selectStopWords() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File stopWordsFile = fileChooser.getSelectedFile();
            stopWords = loadStopWords(stopWordsFile);
            textArea.append("Selected stop words file: " + stopWordsFile.getName() + "\n");
        }
    }

    private Set<String> loadStopWords(File stopWordsFile) {
        Set<String> stopWords = new TreeSet<>();
        try (Scanner scanner = new Scanner(stopWordsFile)) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Stop words file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return stopWords;
    }

    private void extractTags() {
        if (selectedFile == null || stopWords == null) {
            JOptionPane.showMessageDialog(this, "Please select text file and stop words file first.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tagFrequency = new TreeMap<>();
        try (Scanner scanner = new Scanner(selectedFile)) {
            while (scanner.hasNext()) {
                String word = scanner.next().replaceAll("[^a-zA-Z]", "").toLowerCase();
                if (!stopWords.contains(word)) {
                    tagFrequency.put(word, tagFrequency.getOrDefault(word, 0) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Text file not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        textArea.append("Tags extracted from file: \n");
        for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
            textArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private void saveTags() {
        if (tagFrequency == null) {
            JOptionPane.showMessageDialog(this, "No tags extracted yet.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.setFileFilter(filter);
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
                    writer.println(entry.getKey() + ": " + entry.getValue());
                }
                JOptionPane.showMessageDialog(this, "Tags saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error saving tags.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractorGUI::new);
    }
}