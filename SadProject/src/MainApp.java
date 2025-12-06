/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.awt.GridLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author nabil
 */
public class MainApp extends javax.swing.JFrame {

    double Mat[][];
    int N1, N2;
    JTextField[] alternativeFields;
    double scC;
    double sdD;

    public void saveResultsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer les résultats");
        fileChooser.setSelectedFile(new java.io.File("ELECTRE_I_Results.txt"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("Fichiers texte (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println("              RAPPORT D'ANALYSE ELECTRE I");
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println();

                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                writer.println("Date: " + now.format(formatter));
                writer.println();

                writer.println("► CONFIGURATION:");
                writer.println("   Nombre d'alternatives: " + N1);
                writer.println("   Nombre de critères: " + N2);
                writer.println("   Seuil de concordance (c): " + scC);
                writer.println("   Seuil de discordance (d): " + sdD);
                writer.println();

                writer.println("► ALTERNATIVES:");
                for (int i = 0; i < N1; i++) {
                    writer.println("   A" + (i + 1) + ": " + alternativeFields[i].getText());
                }
                writer.println();

                writer.println("► CRITÈRES:");
                for (int j = 0; j < N2; j++) {
                    String nom = Tab2.getValueAt(j, 1).toString();
                    String poids = Tab2.getValueAt(j, 2).toString();
                    String type = Tab2.getValueAt(j, 3).toString();
                    writer.println("   C" + (j + 1) + ": " + nom + " (Poids: " + poids + ", " + type + ")");
                }
                writer.println();

                // Matrice des performances
                writer.println("► MATRICE DES PERFORMANCES:");
                writer.print("        ");
                for (int j = 0; j < N2; j++) {
                    writer.print(String.format("C%-10d", j + 1));
                }
                writer.println();
                for (int i = 0; i < N1; i++) {
                    writer.print(String.format("A%-5d", i + 1));
                    for (int j = 0; j < N2; j++) {
                        writer.print(String.format("%-11.2f", Mat[i][j]));
                    }
                    writer.println();
                }
                writer.println();

                // Résultats détaillés
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println("                    RÉSULTATS DÉTAILLÉS");
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println();
                writer.println(resultText.getText());

                writer.println();
                writer.println("═══════════════════════════════════════════════════════════");
                writer.println("                    FIN DU RAPPORT");
                writer.println("═══════════════════════════════════════════════════════════");

                JOptionPane.showMessageDialog(this,
                        "Les résultats ont été enregistrés avec succès!\n" + filePath,
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'enregistrement du fichier:\n" + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean readDecisionMatrix() {
        N1 = Integer.parseInt(NVAL1.getText());
        N2 = Integer.parseInt(NVAL2.getText());
        Mat = new double[N1][N2];

        for (int i = 0; i < N1; i++) {
            for (int j = 0; j < N2; j++) {
                try {
                    Object val = MatPe.getValueAt(i, j);
                    if (val == null || val.toString().trim().isEmpty()) {
                        return false;
                    }
                    Mat[i][j] = Double.parseDouble(val.toString());
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateWeights() {
        for (int j = 0; j < N2; j++) {
            try {
                Object val = Tab2.getValueAt(j, 2);
                if (val == null || val.toString().trim().isEmpty()) {
                    return false;
                }
                double weight = Double.parseDouble(val.toString());
                if (weight <= 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    public boolean validateConfiguration() {
        try {
            int n1 = Integer.parseInt(NVAL1.getText().trim());
            int n2 = Integer.parseInt(NVAL2.getText().trim());
            if (n1 < 2 || n2 < 1) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public void generateTabConfig() {
        DefaultTableModel model2 = new DefaultTableModel();
        model2.addColumn("Critère");
        model2.addColumn("Nom");
        model2.addColumn("Poids");
        model2.addColumn("Type (Max/Min)");
        for (int j = 0; j < N2; j++) {
            model2.addRow(new Object[]{"C" + (j + 1), "Critère " + (j + 1), 1, "Maximiser"});
        }
        Tab2.setModel(model2);

        JComboBox<String> comboBox = new JComboBox<>(new String[]{"Maximiser", "Minimiser"});
        Tab2.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
    }

    public void runMethodeFun() {
        resultText.setText("");

        resultText.append("╔═════════════════════════════════╗\n");
        resultText.append("║              ELECTRE I          ║\n");
        resultText.append("╚═════════════════════════════════╝\n\n");

        for (int i = 0; i < N1; i++) {
            for (int j = 0; j < N2; j++) {
                try {
                    Mat[i][j] = Double.parseDouble(MatPe.getValueAt(i, j).toString());
                } catch (NumberFormatException e) {
                    Mat[i][j] = 0;
                }
            }
        }

        double[] weights = new double[N2];
        boolean[] isMax = new boolean[N2];
        double totalWeight = 0;

        for (int j = 0; j < N2; j++) {
            try {
                weights[j] = Double.parseDouble(Tab2.getValueAt(j, 2).toString());
                totalWeight += weights[j];
            } catch (NumberFormatException e) {
                weights[j] = 1;
                totalWeight += 1;
            }
            String type = Tab2.getValueAt(j, 3).toString();
            isMax[j] = type.equals("Maximiser");
        }

        for (int j = 0; j < N2; j++) {
            weights[j] = weights[j] / totalWeight;
        }

        resultText.append("► Matrice des Performances:\n");
        resultText.append("   ");
        for (int j = 0; j < N2; j++) {
            resultText.append(String.format("C%-8d", j + 1));
        }
        resultText.append("\n");
        for (int i = 0; i < N1; i++) {
            resultText.append(String.format("A%d: ", i + 1));
            for (int j = 0; j < N2; j++) {
                resultText.append(String.format("%-9.2f", Mat[i][j]));
            }
            resultText.append("\n");
        }

        resultText.append("\n► Poids normalisés: ");
        for (int j = 0; j < N2; j++) {
            resultText.append(String.format("w%d=%.3f  ", j + 1, weights[j]));
        }
        resultText.append("\n");

        resultText.append("► Seuil de concordance (c): " + scC + "\n");
        resultText.append("► Seuil de discordance (d): " + sdD + "\n\n");

        double[][] concordance = new double[N1][N1];

        for (int a = 0; a < N1; a++) {
            for (int b = 0; b < N1; b++) {
                if (a != b) {
                    double sum = 0;
                    for (int j = 0; j < N2; j++) {
                        boolean aBetterOrEqual;
                        if (isMax[j]) {
                            aBetterOrEqual = Mat[a][j] >= Mat[b][j];
                        } else {
                            aBetterOrEqual = Mat[a][j] <= Mat[b][j];
                        }
                        if (aBetterOrEqual) {
                            sum += weights[j];
                        }
                    }
                    concordance[a][b] = sum;
                }
            }
        }

        resultText.append("► Matrice de Concordance:\n");
        resultText.append("      ");
        for (int b = 0; b < N1; b++) {
            resultText.append(String.format("A%-6d", b + 1));
        }
        resultText.append("\n");
        for (int a = 0; a < N1; a++) {
            resultText.append(String.format("A%d:   ", a + 1));
            for (int b = 0; b < N1; b++) {
                if (a == b) {
                    resultText.append("  -    ");
                } else {
                    resultText.append(String.format("%.3f  ", concordance[a][b]));
                }
            }
            resultText.append("\n");
        }

        double[][] discordance = new double[N1][N1];

        double[] range = new double[N2];
        for (int j = 0; j < N2; j++) {
            double min = Mat[0][j], max = Mat[0][j];
            for (int i = 1; i < N1; i++) {
                if (Mat[i][j] < min) {
                    min = Mat[i][j];
                }
                if (Mat[i][j] > max) {
                    max = Mat[i][j];
                }
            }
            range[j] = (max - min) > 0 ? (max - min) : 1;
        }

        for (int a = 0; a < N1; a++) {
            for (int b = 0; b < N1; b++) {
                if (a != b) {
                    double maxDisc = 0;
                    for (int j = 0; j < N2; j++) {
                        double diff;
                        if (isMax[j]) {
                            diff = (Mat[b][j] - Mat[a][j]) / range[j];
                        } else {
                            diff = (Mat[a][j] - Mat[b][j]) / range[j];
                        }
                        if (diff > maxDisc) {
                            maxDisc = diff;
                        }
                    }
                    discordance[a][b] = Math.max(0, maxDisc);
                }
            }
        }

        resultText.append("\n► Matrice de Discordance:\n");
        resultText.append("      ");
        for (int b = 0; b < N1; b++) {
            resultText.append(String.format("A%-6d", b + 1));
        }
        resultText.append("\n");
        for (int a = 0; a < N1; a++) {
            resultText.append(String.format("A%d:   ", a + 1));
            for (int b = 0; b < N1; b++) {
                if (a == b) {
                    resultText.append("  -    ");
                } else {
                    resultText.append(String.format("%.3f  ", discordance[a][b]));
                }
            }
            resultText.append("\n");
        }

        int[][] dominance = new int[N1][N1];

        for (int a = 0; a < N1; a++) {
            for (int b = 0; b < N1; b++) {
                if (a != b) {
                    if (concordance[a][b] >= scC && discordance[a][b] <= sdD) {
                        dominance[a][b] = 1;
                    }
                }
            }
        }

        resultText.append("\n► Matrice de Surclassement (Dominance):\n");
        resultText.append("      ");
        for (int b = 0; b < N1; b++) {
            resultText.append(String.format("A%-4d", b + 1));
        }
        resultText.append("\n");
        for (int a = 0; a < N1; a++) {
            resultText.append(String.format("A%d:   ", a + 1));
            for (int b = 0; b < N1; b++) {
                if (a == b) {
                    resultText.append("  -  ");
                } else {
                    resultText.append(String.format("  %d  ", dominance[a][b]));
                }
            }
            resultText.append("\n");
        }

        resultText.append("\n╔═══════════════════════════════╗\n");
        resultText.append("║          RÉSULTATS FINAUX       ║\n");
        resultText.append("╚═════════════════════════════════╝\n\n");

        int[] dominates = new int[N1];
        int[] dominated = new int[N1];

        for (int a = 0; a < N1; a++) {
            for (int b = 0; b < N1; b++) {
                if (dominance[a][b] == 1) {
                    dominates[a]++;
                    dominated[b]++;
                }
            }
        }

        resultText.append("► Analyse des alternatives:\n");
        StringBuilder kernel = new StringBuilder();
        for (int i = 0; i < N1; i++) {
            String altName = alternativeFields[i].getText();
            resultText.append(String.format("   A%d (%s): surclasse %d alternative(s), surclassée par %d\n",
                    i + 1, altName, dominates[i], dominated[i]));
            if (dominated[i] == 0) {
                if (kernel.length() > 0) {
                    kernel.append(", ");
                }
                kernel.append("A").append(i + 1).append(" (").append(altName).append(")");
            }
        }

        resultText.append("\n► NOYAU (Meilleures alternatives):\n");
        if (kernel.length() > 0) {
            resultText.append("   ★ " + kernel.toString() + " ★\n");
        } else {
            resultText.append("   Aucune alternative dominante trouvée.\n");
        }

        resultText.append("\n══════════════════════════════════════════════════════════════\n");

        DefaultTableModel resultModel = new DefaultTableModel(
                new String[]{"Alternative", "Statut"}, 0
        );

        for (int i = 0; i < N1; i++) {
            String altName = alternativeFields[i].getText();
            String statut;
            if (dominated[i] == 0) {
                statut = "* RECOMMANDÉ";
            } else {
                statut = "Non retenu";
            }
            resultModel.addRow(new Object[]{altName, statut});
        }

        jTable1.setModel(resultModel);
        jTable1.setRowHeight(40);
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        jTable1.getTableHeader().setBackground(new java.awt.Color(46, 204, 113));
        jTable1.getTableHeader().setForeground(java.awt.Color.WHITE);
    }

    public void exampleData() {

        NVAL1.setText("4");
        NVAL2.setText("3");
        jButton3ActionPerformed(null);

        MatPe.setValueAt(25000, 0, 0);
        MatPe.setValueAt(7, 0, 1);
        MatPe.setValueAt(15, 0, 2);
        MatPe.setValueAt(30000, 1, 0);
        MatPe.setValueAt(6, 1, 1);
        MatPe.setValueAt(14, 1, 2);
        MatPe.setValueAt(28000, 2, 0);
        MatPe.setValueAt(8, 2, 1);
        MatPe.setValueAt(13, 2, 2);
        MatPe.setValueAt(27000, 3, 0);
        MatPe.setValueAt(7, 3, 1);
        MatPe.setValueAt(16, 3, 2);

        alternativeFields[0].setText("Toyota Corolla");
        alternativeFields[1].setText("VW Golf");
        alternativeFields[2].setText("Renault Clio");
        alternativeFields[3].setText("Hyundai i20");

        Tab2.setValueAt(0.5, 0, 2);
        Tab2.setValueAt(0.3, 1, 2);
        Tab2.setValueAt(0.2, 2, 2);

        Tab2.setValueAt("Minimiser", 0, 3);
        Tab2.setValueAt("Maximiser", 1, 3);
        Tab2.setValueAt("Maximiser", 2, 3);

        Tab2.setValueAt("Prix", 0, 1);
        Tab2.setValueAt("Qualité", 1, 1);
        Tab2.setValueAt("Durabilité", 2, 1);
    }
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainApp.class.getName());

    /**
     * Creates new form MainApp
     */
    public MainApp() {
        initComponents();

        jTabbedPane5.setEnabledAt(1, false);
        jTabbedPane5.setEnabledAt(2, false);
        jTabbedPane5.setEnabledAt(3, false);

        jPanel1.setBackground(new java.awt.Color(236, 240, 241));
        jPanel2.setBackground(new java.awt.Color(236, 240, 241));
        jPanel3.setBackground(new java.awt.Color(236, 240, 241));
        jPanel7.setBackground(new java.awt.Color(236, 240, 241));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        NomsAlternativesPanel.setBackground(new java.awt.Color(255, 255, 255));

        setupParametersTab();
        setupTables();
    }

    private void setupTables() {
        MatPe.setRowHeight(30);
        MatPe.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        MatPe.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        MatPe.getTableHeader().setBackground(new java.awt.Color(52, 152, 219));
        MatPe.getTableHeader().setForeground(java.awt.Color.WHITE);
        MatPe.setGridColor(new java.awt.Color(189, 195, 199));
        MatPe.setSelectionBackground(new java.awt.Color(174, 214, 241));

        Tab2.setRowHeight(30);
        Tab2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        Tab2.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        Tab2.getTableHeader().setBackground(new java.awt.Color(46, 204, 113));
        Tab2.getTableHeader().setForeground(java.awt.Color.WHITE);
        Tab2.setGridColor(new java.awt.Color(189, 195, 199));
        Tab2.setSelectionBackground(new java.awt.Color(171, 235, 198));

        jTable1.setRowHeight(35);
        jTable1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        jTable1.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jTable1.getTableHeader().setBackground(new java.awt.Color(46, 204, 113));
        jTable1.getTableHeader().setForeground(java.awt.Color.WHITE);
        jTable1.setGridColor(new java.awt.Color(189, 195, 199));
        jTable1.setSelectionBackground(new java.awt.Color(171, 235, 198));
    }

    private void setupParametersTab() {
        JLabel titleLabel = new JLabel("<html><b style='color:#2980B9; font-size:16px;'>Paramètres de la Méthode ELECTRE I</b></html>");

        JLabel descLabel = new JLabel("<html><div style='width:700px;'>"
                + "<b style='color:#2980B9;'>Seuil de concordance (c)</b> : Proportion minimale de critères pour lesquels une alternative "
                + "doit être au moins aussi bonne qu'une autre pour la surclasser. <b>Valeur recommandée: 0.6 à 0.8</b><br><br>"
                + "<b style='color:#2980B9;'>Seuil de discordance (d)</b> : Désaccord maximum toléré sur un critère. "
                + "<b>Valeur recommandée: 0.2 à 0.4</b>"
                + "</div></html>");

        jPanel3.add(titleLabel);
        jPanel3.add(descLabel);

        titleLabel.setBounds(50, 20, 600, 30);
        descLabel.setBounds(50, 60, 800, 80);

        jPanel3.setLayout(null);

        jLabel3.setBounds(50, 180, 200, 25);
        SC.setBounds(260, 180, 400, 50);
        scLabel.setBounds(680, 190, 60, 25);

        jLabel4.setBounds(50, 280, 200, 25);
        SD.setBounds(260, 280, 400, 50);
        sdLabel.setBounds(680, 290, 60, 25);

        runMethod.setBounds(300, 380, 300, 40);

        jPanel3.add(jLabel3);
        jPanel3.add(SC);
        jPanel3.add(scLabel);
        jPanel3.add(jLabel4);
        jPanel3.add(SD);
        jPanel3.add(sdLabel);
        jPanel3.add(runMethod);

        SC.setMinimum(50);
        SC.setMaximum(100);
        SC.setValue(70);
        SC.setMajorTickSpacing(10);
        SC.setMinorTickSpacing(5);
        SC.setPaintTicks(true);
        SC.setPaintLabels(true);

        SD.setMinimum(0);
        SD.setMaximum(50);
        SD.setValue(30);
        SD.setMajorTickSpacing(10);
        SD.setMinorTickSpacing(5);
        SD.setPaintTicks(true);
        SD.setPaintLabels(true);

        jLabel3.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jLabel3.setForeground(new java.awt.Color(41, 128, 185));
        jLabel4.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        jLabel4.setForeground(new java.awt.Color(41, 128, 185));

        scLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        scLabel.setForeground(new java.awt.Color(41, 128, 185));
        sdLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        sdLabel.setForeground(new java.awt.Color(41, 128, 185));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane5 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        NVAL1 = new javax.swing.JTextField();
        NVAL2 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        NomsAlternativesPanel = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        MatPe = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Tab2 = new javax.swing.JTable();
        exampleBtn = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        SC = new javax.swing.JSlider();
        SD = new javax.swing.JSlider();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        runMethod = new javax.swing.JButton();
        scLabel = new javax.swing.JLabel();
        sdLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        resultText = new javax.swing.JTextArea();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        saveResult = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setText("Nombre d'alternatives :");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setText("Nombre de critères :");

        NVAL1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        NVAL1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        NVAL1.setText("3");
        NVAL1.setToolTipText("");
        NVAL1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NVAL1ActionPerformed(evt);
            }
        });

        NVAL2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        NVAL2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        NVAL2.setText("3");
        NVAL2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NVAL2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(46, 204, 113));
        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton3.setText("Créer la Matrice de Décision ->");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setText("Configuration du Problème de Décision");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        jLabel9.setText("<html><p style='width:400px'>Définissez le nombre d'alternatives (options à comparer)                  + et le nombre de critères (caractéristiques d'évaluation) pour votre problème de décision.</p></html>");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(25992, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                        .addComponent(NVAL2, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addContainerGap())
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                        .addComponent(NVAL1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGap(328, 328, 328)))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(331, 331, 331))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(260, 260, 260))))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addComponent(jLabel8)
                                .addGap(28, 28, 28)
                                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(NVAL1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(42, 42, 42)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(NVAL2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addGap(52, 52, 52)
                                .addComponent(jButton3)
                                .addContainerGap(360, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Configuration", jPanel1);

        jButton4.setBackground(new java.awt.Color(46, 204, 113));
        jButton4.setText("Valider et Continuer");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        NomsAlternativesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(52, 152, 219), 2), "Liste des Alternatives", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(52, 152, 219))); // NOI18N

        jLabel5.setText("A1");

        javax.swing.GroupLayout NomsAlternativesPanelLayout = new javax.swing.GroupLayout(NomsAlternativesPanel);
        NomsAlternativesPanel.setLayout(NomsAlternativesPanelLayout);
        NomsAlternativesPanelLayout.setHorizontalGroup(
                NomsAlternativesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(NomsAlternativesPanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel5)
                                .addGap(32, 32, 32)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(64, Short.MAX_VALUE))
        );
        NomsAlternativesPanelLayout.setVerticalGroup(
                NomsAlternativesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(NomsAlternativesPanelLayout.createSequentialGroup()
                                .addGap(38, 38, 38)
                                .addGroup(NomsAlternativesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(132, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(52, 152, 219), 2), "Matrice de Performances (Valeurs)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(52, 152, 219))); // NOI18N

        MatPe.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {},
                    {},
                    {},
                    {}
                },
                new String[]{}
        ));
        jScrollPane1.setViewportView(MatPe);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(28, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 204, 113), 2), "Configuration des Critères (Noms, Poids et Type)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(46, 204, 113))); // NOI18N

        Tab2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {},
                    {},
                    {},
                    {}
                },
                new String[]{}
        ));
        jScrollPane2.setViewportView(Tab2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 919, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(51, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(47, Short.MAX_VALUE))
        );

        exampleBtn.setBackground(new java.awt.Color(241, 196, 15));
        exampleBtn.setText("Example");
        exampleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exampleBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(exampleBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(36, 36, 36))
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(NomsAlternativesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25679, Short.MAX_VALUE)
                                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(28, 28, 28))))
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(exampleBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(NomsAlternativesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73))
        );

        jTabbedPane5.addTab("Données", jPanel2);

        SC.setValue(70);
        SC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SCStateChanged(evt);
            }
        });

        SD.setMaximum(50);
        SD.setValue(30);
        SD.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                SDStateChanged(evt);
            }
        });

        jLabel3.setText("Seuil de concordance (c) :");

        jLabel4.setText("Seuil de discordance (d) :");

        runMethod.setBackground(new java.awt.Color(46, 204, 113));
        runMethod.setText(">>> Exécuter ELECTRE I <<<");
        runMethod.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMethodActionPerformed(evt);
            }
        });

        scLabel.setText("0.70");

        sdLabel.setText("0.30");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addContainerGap(25787, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(46, 46, 46))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(58, 58, 58)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(SC, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(SD, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 402, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(sdLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(scLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(338, 338, 338))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(328, 328, 328)
                                .addComponent(runMethod, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(213, 213, 213)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(SC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(scLabel))
                                .addGap(76, 76, 76)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(SD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel4)
                                        .addComponent(sdLabel))
                                .addGap(60, 60, 60)
                                .addComponent(runMethod)
                                .addContainerGap(314, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Paramètres", jPanel3);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(231, 76, 60), 2), "Résultats Détaillés ELECTRE I", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(231, 76, 60))); // NOI18N

        resultText.setBackground(new java.awt.Color(45, 52, 54));
        resultText.setColumns(20);
        resultText.setForeground(new java.awt.Color(178, 190, 195));
        resultText.setRows(5);
        jScrollPane3.setViewportView(resultText);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 751, Short.MAX_VALUE)
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(60, Short.MAX_VALUE))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(46, 204, 113), 2), "Tableau des Recommandations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(46, 204, 113))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {},
                    {},
                    {},
                    {}
                },
                new String[]{}
        ));
        jScrollPane4.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addContainerGap(23, Short.MAX_VALUE)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(84, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(155, 89, 182));
        jButton1.setText("Nouvelle Analyse");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        saveResult.setBackground(new java.awt.Color(241, 196, 15));
        saveResult.setText("Save Result");
        saveResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveResultActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap(25631, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(saveResult, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(27, 27, 27)
                                                .addComponent(jButton1)
                                                .addGap(48, 48, 48))))
        );
        jPanel7Layout.setVerticalGroup(
                jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(35, 35, 35)
                                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jButton1)
                                                        .addComponent(saveResult)))
                                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(135, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Résultats", jPanel7);

        jLabel7.setText("NABIL");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(195, 195, 195)
                                .addComponent(jLabel7)
                                .addContainerGap(26545, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
                jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(jLabel7)
                                .addContainerGap(631, Short.MAX_VALUE))
        );

        jTabbedPane5.addTab("Aide", jPanel9);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jTabbedPane5)
                                .addGap(600, 600, 600))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jTabbedPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(126, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if (!validateConfiguration()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des nombres valides.\nNombre d'alternatives >= 2\nNombre de critères >= 1",
                    "Erreur de configuration", JOptionPane.ERROR_MESSAGE);
            return;
        }

        N1 = Integer.parseInt(NVAL1.getText());
        N2 = Integer.parseInt(NVAL2.getText());
        Mat = new double[N1][N2];

        NomsAlternativesPanel.removeAll();
        NomsAlternativesPanel.setLayout(new GridLayout(N1, 2, 5, 10));
        alternativeFields = new JTextField[N1];

        for (int i = 0; i < N1; i++) {
            JLabel label = new JLabel("A" + (i + 1) + ":");
            label.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            alternativeFields[i] = new JTextField("Alternative " + (i + 1));
            NomsAlternativesPanel.add(label);
            NomsAlternativesPanel.add(alternativeFields[i]);
        }
        NomsAlternativesPanel.revalidate();
        NomsAlternativesPanel.repaint();

        String[] columnNames = new String[N2];
        for (int i = 0; i < N2; i++) {
            columnNames[i] = "C" + (i + 1);
        }

        DefaultTableModel model = new DefaultTableModel(columnNames, N1);
        MatPe.setModel(model);

        for (int i = 0; i < N1; i++) {
            for (int j = 0; j < N2; j++) {
                model.setValueAt("", i, j);
            }

        }
        generateTabConfig();

        jTabbedPane5.setEnabledAt(1, true);
        jTabbedPane5.setSelectedIndex(1);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        if (!readDecisionMatrix()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez remplir toutes les cellules de la matrice avec des nombres valides.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!validateWeights()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des poids valides (nombres positifs) pour tous les critères.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        jTabbedPane5.setEnabledAt(2, true);
        jTabbedPane5.setSelectedIndex(2);
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed

    private void runMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runMethodActionPerformed
        scC = SC.getValue() / 100.0;
        sdD = SD.getValue() / 100.0;
        runMethodeFun();
        jTabbedPane5.setEnabledAt(3, true);
        jTabbedPane5.setSelectedIndex(3);
        // TODO add your handling code here:
    }//GEN-LAST:event_runMethodActionPerformed

    private void NVAL1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NVAL1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NVAL1ActionPerformed

    private void NVAL2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NVAL2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_NVAL2ActionPerformed

    private void SCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SCStateChanged
        scLabel.setText(String.valueOf(SC.getValue() / 100.0));
        // TODO add your handling code here:
    }//GEN-LAST:event_SCStateChanged

    private void SDStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SDStateChanged
        sdLabel.setText(String.valueOf(SD.getValue() / 100.0));
        // TODO add your handling code here:
    }//GEN-LAST:event_SDStateChanged

    private void exampleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exampleBtnActionPerformed
        boolean proceed = JOptionPane.showConfirmDialog(this,
                "Cette action va écraser toutes les données actuelles \n"
                + " nombre d'alternatives : 4, nombre de critères : 3, \n"
                + "Voulez-vous continuer?",
                "Confirmer l'exemple",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        if (proceed) {
            exampleData();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_exampleBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        boolean check = JOptionPane.showConfirmDialog(this,
                "Cette action va réinitialiser toutes les données \n"
                + "Voulez-vous continuer?",
                "Confirmer Nouvelle Analyse",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        if (check) {
            NVAL1.setText("3");
            NVAL2.setText("3");

            resultText.setText("");

            MatPe.setModel(new DefaultTableModel());
            Tab2.setModel(new DefaultTableModel());
            jTable1.setModel(new DefaultTableModel());

            jTabbedPane5.setEnabledAt(1, false);
            jTabbedPane5.setEnabledAt(2, false);
            jTabbedPane5.setEnabledAt(3, false);

            jTabbedPane5.setSelectedIndex(0);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void saveResultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveResultActionPerformed
        boolean check = JOptionPane.showConfirmDialog(this,
                "Voulez-vous enregistrer les résultats dans un fichier texte?",
                "Confirmer Enregistrement",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
        if (check) {
            saveResultsToFile();
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_saveResultActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainApp().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable MatPe;
    private javax.swing.JTextField NVAL1;
    private javax.swing.JTextField NVAL2;
    private javax.swing.JPanel NomsAlternativesPanel;
    private javax.swing.JSlider SC;
    private javax.swing.JSlider SD;
    private javax.swing.JTable Tab2;
    private javax.swing.JButton exampleBtn;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextArea resultText;
    private javax.swing.JButton runMethod;
    private javax.swing.JButton saveResult;
    private javax.swing.JLabel scLabel;
    private javax.swing.JLabel sdLabel;
    // End of variables declaration//GEN-END:variables
}
