import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Bibliotheque extends JFrame {
    // Je définis les composants de l'interface
    JTextField titreField = new JTextField(10);
    JTextField auteurField = new JTextField(10);
    JTable table = new JTable();
    DefaultTableModel model;

    // Je configure l’accès à la base de données appelée "bibliotheque"
    String url = "jdbc:mysql://localhost:3306/bibliotheque";
    String user = "root"; // modifie si besoin
    String password = ""; // modifie si besoin

    public Bibliotheque() {
        // Je configure la fenêtre principale
        setTitle("Gestion de Bibliothèque");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Je construis la partie haute avec les champs et boutons
        JPanel top = new JPanel();
        top.add(new JLabel("Titre:")); top.add(titreField);
        top.add(new JLabel("Auteur:")); top.add(auteurField);
        JButton ajouterBtn = new JButton("Ajouter");
        JButton supprimerBtn = new JButton("Supprimer");
        JButton emprunterBtn = new JButton("Emprunter");
        JButton retourBtn = new JButton("Retour");
        top.add(ajouterBtn); top.add(supprimerBtn);
        top.add(emprunterBtn); top.add(retourBtn);
        add(top, BorderLayout.NORTH);

        // Je prépare la table d’affichage
        model = new DefaultTableModel(new String[]{"ID", "Titre", "Auteur", "Disponible"}, 0);
        table.setModel(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Je remplis la table avec les livres
        afficherLivres();

        // Quand je clique sur "Ajouter"
        ajouterBtn.addActionListener(e -> {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            if (titre.isEmpty() || auteur.isEmpty()) return;
            try (Connection c = DriverManager.getConnection(url, user, password)) {
                PreparedStatement ps = c.prepareStatement("INSERT INTO livres (titre, auteur, disponible) VALUES (?, ?, true)");
                ps.setString(1, titre); ps.setString(2, auteur);
                ps.executeUpdate(); afficherLivres();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Quand je clique sur "Supprimer"
        supprimerBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            int id = (int) model.getValueAt(row, 0);
            try (Connection c = DriverManager.getConnection(url, user, password)) {
                PreparedStatement ps = c.prepareStatement("DELETE FROM livres WHERE id = ?");
                ps.setInt(1, id); ps.executeUpdate(); afficherLivres();
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        // Quand je clique sur "Emprunter"
        emprunterBtn.addActionListener(e -> changerDisponibilite(false));

        // Quand je clique sur "Retour"
        retourBtn.addActionListener(e -> changerDisponibilite(true));
    }

    // J’affiche tous les livres dans la table
    void afficherLivres() {
        model.setRowCount(0);
        try (Connection c = DriverManager.getConnection(url, user, password)) {
            ResultSet rs = c.createStatement().executeQuery("SELECT * FROM livres");
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("auteur"),
                    rs.getBoolean("disponible")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    // J’emprunte ou je rends un livre
    void changerDisponibilite(boolean dispo) {
        int row = table.getSelectedRow();
        if (row == -1) return;
        int id = (int) model.getValueAt(row, 0);
        try (Connection c = DriverManager.getConnection(url, user, password)) {
            PreparedStatement ps = c.prepareStatement("UPDATE livres SET disponible = ? WHERE id = ?");
            ps.setBoolean(1, dispo); ps.setInt(2, id);
            ps.executeUpdate(); afficherLivres();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public static void main(String[] args) {
        // Je lance l’application Swing
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (Exception e) { }
        SwingUtilities.invokeLater(() -> new Bibliotheque().setVisible(true));
    }
}
