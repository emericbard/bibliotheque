# Application de Gestion de Bibliothèque

Cette application Java permet de gérer une bibliothèque avec interface graphique.

## Fonctionnalités

- Ajouter un livre
- Supprimer un livre
- Emprunter / Retourner un livre
- Interface Swing
- Stockage des données dans MySQL avec JDBC

## Technologies utilisées

- Java 8+
- Swing
- JDBC
- MySQL

## Lancer l'application

1. Créer la base de données :

```sql
CREATE DATABASE IF NOT EXISTS bibliotheque;
USE bibliotheque;
CREATE TABLE livres (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titre VARCHAR(255),
  auteur VARCHAR(255),
  disponible BOOLEAN DEFAULT TRUE
);
