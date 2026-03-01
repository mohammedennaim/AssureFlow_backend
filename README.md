# 🛡️ AssureFlow - Plateforme d'Assurance Microservices

AssureFlow est une plateforme cloud-native conçue avec une architecture **Microservices** en utilisant **Spring Boot 3**, **PostgreSQL**, **Apache Kafka**, **Redis**, et orchestrée de manière optimale via **Docker Compose**.

Ce document explique le fonctionnement global du projet, l'architecture, et particulièrement le rôle central de l'API Gateway.

---

## 🏗️ Architecture Globale

L'application est découpée en **7 microservices distincts**, accompagnés de **5 services d'infrastructure**. 

Pour des raisons de **sécurité** et de **gestion des ressources**, **aucun microservice n'expose ses ports directement à l'extérieur** (host machine). Tout le trafic entrant, qu'il s'agisse des utilisateurs finaux ou des applications clientes (Frontend Angular/React, Apps mobiles), doit obligatoirement passer par un seul point d'entrée : **L'API Gateway**.

### 📦 1. Les Microservices Applicatifs (Spring Boot)
Ces services contiennent la logique métier. Ils discutent avec leur propre base de données (Isolation des bases de données) et communiquent entre eux asynchrone via Kafka (Event-driven).

1. **`iam-service`** : Identity & Access Management. Gère les utilisateurs (Admins, Agents, Clients), l'authentification (génération JWT), et les rôles.
2. **`client-service`** : Gère les profils des assurés, leurs informations personnelles et leur historique.
3. **`policy-service`** : Gère les contrats d'assurance (devis, création, renouvellement, annulation).
4. **`billing-service`** : Gère la facturation, les paiements et les reçus.
5. **`claims-service`** : Gère les déclarations de sinistres et leur suivi.
6. **`workflow-service`** : Gère les processus de validation (ex: approbation d'un gros sinistre par un manager).
7. **`notification-service`** : Envoie des notifications (Email/SMS) basées sur les événements Kafka (ex: contrat créé, sinistre remboursé).

### ⚙️ 2. Les Services d'Infrastructure
1. **PostgreSQL** : La base de données relationnelle. Le script `init-databases.sh` crée automatiquement une base de données séparée pour chaque microservice (ex: `iam_db`, `policy_db`).
2. **Apache Kafka (KRaft Mode)** : Le bus de messages (Message Broker) utilisé pour la communication asynchrone entre les microservices. 
3. **Redis** : Utilisé pour le cache (par exemple, pour stocker les profils utilisateurs fréquemment consultés) et potentiellement la gestion des sessions bloquées (token blacklisting). Il est sécurisé par un mot de passe (`REDIS_PASSWORD`).
4. **Prometheus** : Base de données de séries temporelles qui collecte automatiquement les métriques matérielles et applicatives (`/actuator/prometheus`) de tous nos services toutes les 15 secondes.
5. **Grafana** : Outil de visualisation qui lit les données de Prometheus pour afficher de beaux dashboards sur l'état de santé du système (CPU, RAM, requêtes par seconde, erreurs).

---

## 🚪 Le Rôle de l'API Gateway (Le Gardien du Temple)

L'API Gateway (`api-gateway`) est bâti avec **Spring Cloud Gateway**. Il écoute sur le port public **8080**.

Dans une architecture sans API Gateway, l'application Frontend devrait connaître l'adresse IP et le port de CHAQUE microservice (ex: `http://localhost:8081` pour IAM, `http://localhost:8082` pour Policy, etc.). C'est un cauchemar à maintenir, surtout si les IP changent ou si vous scalez (redimensionnez) un service.

**Pourquoi l'API Gateway est-il indispensable ?**

1. **Point d'entrée unique (Single Entry Point)** : 
   Le Frontend ne discute qu'avec `http://localhost:8080`. C'est le Gateway qui se charge de lire l'URL (`/api/v1/auth/...`) et de **rediriger** la requête (Reverse Proxy) vers le bon microservice (ici, `iam-service`).

2. **Sécurité accrue (Security & Hiding)** : 
   Les microservices sont invisibles depuis l'extérieur. Ils sont cachés dans le réseau Docker privé (`insurance-net`). Seul le port 8080 du Gateway est ouvert. Impossible pour un hacker d'attaquer directement la base de données ou un service interne.

3. **Gestion du CORS Globale** :
   Au lieu de configurer le CORS (Cross-Origin Resource Sharing) dans chaque microservice pour autoriser le frontend Angular/React à appeler les APIs, le CORS est configuré une seule fois, au niveau du Gateway.

4. **Découplage** :
   Si demain vous décidez de fusionner `billing-service` et `policy-service`, ou de changer la technologie interne d'un service, le Frontend ne remarquera rien. Les routes de l'API Gateway masqueront cette complexité.

5. **Agréger Swagger (Documentation API)** :
   Le Gateway permet de regrouper la documentation de tous les services en un seul endroit. (ex: `/swagger-ui/index.html` qui forward vers `iam-service`).

---

## 🚀 Comment lancer le projet (Déploiement Local)

L'ensemble de l'infrastructure est défini dans le fichier `docker-compose.yml`.

### Prérequis
- Docker et Docker Compose installés.
- Les variables de sécurité définies dans le fichier `.env` à la racine (Postgres DB, mots de passe Redis & Grafana).

### Démarrage

1. Assurez-vous d'être à la racine du projet (là où se trouve le `docker-compose.yml`).
2. Lancez la construction des images (optimisées avec Alpine pour être légères) et le démarrage :

```bash
docker compose up -d --build
```

*(Note : `-d` lance en mode détaché, `--build` force la recompilation avec Maven si le code source a changé. La compilation est effectuée **dans** Docker de manière optimisée grâce au profil Maven `-Ddocker.build=true` qui ignore les plugins incompatibles).*

L'opération va démarrer 12 containers : 
- 8 applications Spring Boot (1 API Gateway + 7 microservices)
- 4 infrastructures (Postgres, Kafka, Redis, Prometheus, Grafana, pgAdmin)

### 🩺 Vérifier l'état
Consultez l'état de santé (Healthchecks) de tous les containers pour vérifier qu'ils sont bien "healthy" ou "Up" :
```bash
docker compose ps -a
```

---

## 🌐 URLs Importantes (Localhost)

Une fois tous les containers lancés (comptez 20-30 secondes pour que Spring Boot démarre), vous pouvez accéder aux services :

| Interface | URL | Identifiants |
| :--- | :--- | :--- |
| **API Gateway (APIs)** | `http://localhost:8080/api/v1/...` | N/A |
| **Swagger UI (IAM)** | `http://localhost:8080/swagger-ui/index.html` | N/A (Route Gateway) |
| **Swagger UI (IAM - Direct)** | `http://localhost:8086/swagger-ui/index.html` | N/A (Port direct exposé) |
| **Grafana (Dashboards)** | `http://localhost:3000` | User: `admin` / Pass: `grafana_secret_2024` (Défini dans `.env`) |
| **Prometheus (Métriques)** | `http://localhost:9090` | N/A |
| **pgAdmin (PostgreSQL GUI)** | `http://localhost:5050` | User: `admin@assureflow.com` / Pass: `pgadmin_secret_2024` (Défini dans `.env`) |

### 🛑 Arrêter le projet
Pour stopper tous les containers tout en conservant les données (bases de données, statistiques) :
```bash
docker compose down
```

Pour stopper ET supprimer toutes les données (remise à zéro) :
```bash
docker compose down -v
```

---

## 🛠️ Astuces DevOps & Optimisations Réalisées

1. **Multistage Dockerfile (Alpine)** : Au lieu d'utiliser des images Ubuntu/Debian lourdes (~200MB), les runtime Docker utilisent `eclipse-temurin:17-jre-focal` ou Alpine, réduisant drastiquement l'empreinte mémoire, et compilent d'abord via une image Builder.
2. **Resource Limits** : Configuré dans le `docker-compose.yml`, pour qu'aucun bug (comme une fuite de mémoire) dans un microservice ne puisse crasher votre ordinateur en consommant 100% du CPU/RAM.
3. **Healthchecks intelligents** : Assurent qu'un microservice (ex: `iam-service`) ne démarre que LORSQUE Kafka, Postgres et Redis sont réellement opérationnels et en bonne santé.
