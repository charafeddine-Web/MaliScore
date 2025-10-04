# 🏦 MaliScore - Système de Scoring Crédit

MaliScore est une application Java orientée objet qui vise à transformer le processus d'octroi de crédit dans le secteur de la micro-finance marocaine. Le projet propose un système de scoring automatisé permettant d'évaluer la solvabilité des clients de manière rapide, transparente et fiable.

## 📋 Table des Matières

- [Fonctionnalités](#-fonctionnalités)
- [Architecture](#-architecture)
- [Modèle de Scoring](#-modèle-de-scoring)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [Structure du Projet](#-structure-du-projet)
- [Base de Données](#-base-de-données)
- [Développement](#-développement)

## 🚀 Fonctionnalités

### Gestion des Clients
- ✅ Création et gestion des profils clients (Employés et Professionnels)
- ✅ Calcul automatique du score de crédit
- ✅ Historique des changements de score
- ✅ Modification et suppression des clients

### Gestion des Crédits
- ✅ Demande de crédit avec validation automatique
- ✅ Système de décision automatisé (Accord immédiat, Étude manuelle, Refus)
- ✅ Génération d'échéanciers de paiement
- ✅ Suivi des statuts de crédit

### Analytics et Décisions
- ✅ Segmentation des clients par risque
- ✅ Analyse des clients éligibles au crédit immobilier
- ✅ Identification des clients à risque
- ✅ Ciblage pour campagnes de crédit consommation
- ✅ Statistiques par type d'emploi

### Gestion des Paiements
- ✅ Enregistrement des paiements
- ✅ Suivi des retards et incidents
- ✅ Recalcul automatique des scores
- ✅ Génération d'incidents de paiement

## 🏗️ Architecture

Le projet suit une architecture en couches (layered architecture) :

```
📁 src/
├── 🎯 Main.java                    # Point d'entrée de l'application
├── 📊 model/                       # Modèles de données
│   ├── Personne.java              # Classe abstraite client
│   ├── Employe.java               # Client employé
│   ├── Professionnel.java         # Client professionnel
│   ├── Credit.java                # Modèle crédit
│   ├── Echeance.java              # Modèle échéance
│   ├── Incident.java              # Modèle incident
│   ├── ScoreHistory.java          # Historique des scores
│   └── enums/                     # Énumérations
├── 🔧 service/                     # Couche métier
│   ├── ScoringService.java        # Calcul des scores
│   ├── ClientService.java         # Gestion clients
│   ├── CreditService.java         # Gestion crédits
│   ├── AnalyticsService.java      # Analyses et rapports
│   ├── DecisionService.java       # Prise de décision
│   └── ...
├── 💾 repository/                  # Couche d'accès aux données
│   ├── DatabaseConnection.java    # Connexion base de données
│   ├── ClientRepository.java      # CRUD clients
│   ├── CreditRepository.java      # CRUD crédits
│   └── ...
└── 🖥️ ui/                          # Interface utilisateur
    ├── MenuPranc.java             # Menu principal
    ├── MenuClient.java            # Menu clients
    ├── MenuCredit.java            # Menu crédits
    └── ...
```

## 📊 Modèle de Scoring

Le système de scoring MaliScore évalue les clients sur 5 composants principaux :

### 1. 🏢 Stabilité Professionnelle (0-25 points)
- **Type de contrat** : CDI Public (25), CDI Privé Grande (20), CDI PME (15), CDD/Intérim (5)
- **Ancienneté** : +2 points par année d'ancienneté
- **Secteur d'activité** : Public (5), Grande Entreprise (3), PME (1)

### 2. 💰 Capacité Financière (0-25 points)
- **Revenus mensuels** : 0-5000 DH (5), 5001-15000 DH (15), 15001+ DH (25)
- **Ratio revenus/enfants** : Bonus pour revenus élevés par enfant

### 3. 📈 Historique Client (0-25 points)
- **Historique des crédits** : Analyse des crédits précédents
- **Ponctualité des paiements** : Bonus pour paiements à l'heure
- **Incidents de paiement** : Pénalités pour retards et impayés

### 4. 👨‍👩‍👧‍👦 Relation Client (0-10 points)
- **Âge** : 26-35 ans (8), 36-55 ans (10), 18-25 ans (4), 55+ ans (6)
- **Situation familiale** : Marié (3), Autre (2)
- **Nombre d'enfants** : 0 enfant (2), 1-2 enfants (1), 3+ enfants (0)

### 5. 🏠 Patrimoine (0-15 points)
- **Investissements** : 15 points si investissements
- **Placements** : 15 points si placements

### 🎯 Seuils de Décision
- **80+ points** : Accord immédiat
- **60-79 points** : Étude manuelle
- **< 60 points** : Refus automatique

## 🛠️ Installation

### Prérequis
- ☕ Java 8 ou supérieur
- 🗄️ MySQL 5.7 ou supérieur (optionnel)
- 📁 IDE Java (IntelliJ IDEA, Eclipse, VS Code)

### Étapes d'installation

1. **Cloner le projet**
```bash
git clone 
cd MaliScore
```

2. **Compiler le projet**
```bash
cd src
javac -cp . *.java
```

3. **Exécuter l'application**
```bash
java -cp . Main
```

## ⚙️ Configuration

### Configuration de la Base de Données

1. **Créer la base de données MySQL**
```sql
CREATE DATABASE maliscore;
```

2. **Exécuter le script SQL**
```bash
mysql -u username -p maliscore < db
```

3. **Configurer les paramètres de connexion**
Modifiez le fichier `src/resources/ConfigDB.java` :
```java
private String url = "jdbc:mysql://localhost:3306/maliscore";
private String username = "votre_username";
private String password = "votre_password";
```

### Mode Sans Base de Données
L'application peut fonctionner en mode local sans base de données :
- Les scores sont calculés localement
- Les données ne sont pas persistées
- Parfait pour les tests et démonstrations

## 🎮 Utilisation

### Démarrage de l'Application
```bash
java -cp . Main
```

### Menu Principal
```
==================================================
MALISCORE - SYSTEME DE SCORING CREDIT
==================================================
1. 👥 Gestion des clients
2. 💰 Gestion des crédits
3. 📊 Analytics & Décisions
4. 💳 Gestion des paiements
5. [X] Quitter
==================================================
```

### Ajout d'un Client
1. Sélectionner "Gestion des clients" → "Ajouter un client"
2. Choisir le type : Employé ou Professionnel
3. Remplir les informations personnelles et professionnelles
4. Le score est calculé automatiquement

### Demande de Crédit
1. Sélectionner "Gestion des crédits" → "Ajouter un crédit"
2. Saisir l'ID du client
3. Définir le montant et la durée
4. Le système propose automatiquement une décision

## 📁 Structure du Projet

```
MaliScore/
├── 📄 README.md                   # Documentation du projet
├── 📄 db                          # Script SQL de création de la base
├── 📁 src/                        # Code source Java
│   ├── 🎯 Main.java              # Point d'entrée
│   ├── 📊 model/                 # Modèles de données
│   ├── 🔧 service/               # Services métier
│   ├── 💾 repository/            # Accès aux données
│   ├── 🖥️ ui/                    # Interface utilisateur
│   └── ⚙️ resources/             # Configuration
└── 📁 .git/                      # Contrôle de version Git
```

## 🗄️ Base de Données

### Tables Principales

- **`personne`** : Informations des clients
- **`credit`** : Demandes et octrois de crédits
- **`echeance`** : Échéanciers de paiement
- **`incident`** : Incidents de paiement
- **`score_history`** : Historique des changements de score

### Relations
- Un client peut avoir plusieurs crédits
- Un crédit a plusieurs échéances
- Une échéance peut générer des incidents
- Les changements de score sont tracés

## 🛠️ Développement

### Ajout de Nouvelles Fonctionnalités

1. **Nouveau modèle** : Créer dans `src/model/`
2. **Nouvelle logique métier** : Ajouter dans `src/service/`
3. **Nouvel accès données** : Implémenter dans `src/repository/`
4. **Nouvelle interface** : Développer dans `src/ui/`

### Tests
```bash
# Compilation
javac -cp . *.java

# Exécution des tests
java -cp . TestClass
```

### Bonnes Pratiques
- ✅ Respecter l'architecture en couches
- ✅ Utiliser des noms explicites
- ✅ Commenter le code complexe
- ✅ Gérer les exceptions
- ✅ Valider les entrées utilisateur

## 📞 Support

Pour toute question ou problème :
1. Vérifiez la documentation
2. Consultez les logs d'erreur
3. Testez en mode local (sans base de données)

## 📝 Licence

Ce projet est développé dans le cadre d'une formation en développement Java.

---

**MaliScore** - Transformant la micro-finance marocaine grâce à l'intelligence artificielle et au scoring automatisé. 🚀