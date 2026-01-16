# JobStream - Plateforme de Recrutement Moderne

**Le flux d'opportunités, à portée de main.**

JobStream est une plateforme web moderne de recrutement permettant de connecter des candidats, des entreprises et des recruteurs via une interface simple, rapide et professionnelle. Inspirée de plateformes comme LinkedIn, elle offre des fonctionnalités premium et une architecture robuste.

## 🚀 Fonctionnalités Principales

### Pour les Candidats
- ✅ Création de profil professionnel complet
- ✅ Téléversement de CV (PDF)
- ✅ Recherche avancée d'offres d'emploi
- ✅ Candidature directe aux offres
- ✅ Suivi des candidatures
- ✅ Notifications en temps réel
- ✅ Gestion des connexions professionnelles
- ✅ Fonctionnalités Premium (visiteurs de profil, visibilité améliorée)

### Pour les Recruteurs/Entreprises
- ✅ Création et gestion de profil entreprise
- ✅ Publication et gestion des offres d'emploi
- ✅ Gestion des candidatures
- ✅ Accès aux profils candidats
- ✅ Messagerie interne
- ✅ Statistiques et analytics

### Fonctionnalités Premium
- 🔥 Voir les visiteurs du profil
- 🔥 Mise en avant dans les résultats
- 🔥 Feed avancé
- 🔥 Boost de visibilité automatique
- 🔥 Messagerie illimitée
- 🔥 Priority Apply

## 🛠 Stack Technique

### Backend
- **Java 17** - Langage principal
- **Spring Boot 3.5.9** - Framework principal
- **Spring Security** - Sécurité avec JWT
- **Spring Data JPA** - Accès aux données
- **PostgreSQL** - Base de données principale
- **Liquibase** - Gestion versionnée de la base de données
- **MapStruct** - Mapping Entity-DTO
- **Elasticsearch** - Recherche avancée
- **WebSocket** - Notifications temps réel

### Intégrations Externes
- **Google OAuth2** - Authentification sociale
- **PayPal** - Paiements premium
- **Email Service** - Notifications par email

### DevOps
- **Docker** - Conteneurisation
- **GitHub Actions** - CI/CD
- **Maven** - Gestion des dépendances

## 📁 Architecture du Projet

```
jobstream/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/job/
│   │       │   ├── config/           # Configurations Spring
│   │       │   ├── controller/       # API REST Controllers
│   │       │   ├── service/          # Interfaces de services
│   │       │   ├── service/impl/     # Implémentations des services
│   │       │   ├── repository/       # Repositories JPA
│   │       │   ├── dto/              # Data Transfer Objects
│   │       │   ├── mapper/           # MapStruct mappers
│   │       │   ├── entity/           # Entités JPA
│   │       │   ├── exception/        # Gestion des exceptions
│   │       │   ├── security/         # Configuration sécurité
│   │       │   ├── websocket/        # Handlers WebSocket
│   │       │   ├── enums/            # Énumérations
│   │       │   └── util/             # Utilitaires
│   │       └── resources/
│   │           ├── application.yml   # Configuration application
│   │           └── db/changelog/      # Scripts Liquibase
│   ├── pom.xml                       # Dépendances Maven
│   └── Dockerfile                    # Configuration Docker
├── frontend/                         # Application Angular (à implémenter)
├── devops/                           # Configuration DevOps
└── README.md                         # Documentation
```

## 🚀 Démarrage Rapide

### Prérequis
- Java 17+
- Maven 3.6+
- PostgreSQL 13+
- Docker (optionnel)

### Configuration de l'Environnement

1. **Cloner le repository**
```bash
git clone <repository-url>
cd jobstream
```

2. **Configurer les variables d'environnement**
Créer un fichier `.env` à la racine du projet:
```env
# Database Configuration
POSTGRES_DB=jobstream
POSTGRES_USER=jobstream
POSTGRES_PASSWORD=your_password
DB_URL=jdbc:postgresql://localhost:5432/jobstream
DB_USER=jobstream
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your_very_long_and_secure_jwt_secret_key_here

# Liquibase Configuration
LIQUIBASE_CHANGELOG=classpath:db/changelog/db.changelog-master.yaml

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/api/oauth2/success

# PayPal Configuration
PAYPAL_CLIENT_ID=your_paypal_client_id
PAYPAL_CLIENT_SECRET=your_paypal_client_secret
PAYPAL_MODE=sandbox

# Email Configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Elasticsearch
ELASTICSEARCH_URIS=http://localhost:9200

# File Upload
FILE_UPLOAD_DIR=./uploads
```

3. **Démarrer la base de données**
```bash
# Avec Docker
docker-compose up -d postgres

# Ou manuellement avec PostgreSQL
# Créer la base de données 'jobstream'
```

4. **Lancer l'application**
```bash
# Développement
mvn spring-boot:run

# Production
mvn clean package
java -jar target/JobStream-0.0.1-SNAPSHOT.jar
```

L'application sera disponible sur `http://localhost:8080`

## 📚 Documentation API

### Authentification

#### Inscription
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "CANDIDATE"
}
```

#### Connexion
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

#### Google OAuth2
```http
GET /oauth2/authorization/google
```

### Gestion des Offres d'Emploi

#### Lister toutes les offres
```http
GET /api/jobs?page=0&size=20
```

#### Rechercher des offres
```http
GET /api/jobs/search?keyword=java&location=paris&contractType=CDI
```

#### Créer une offre (Recruteur)
```http
POST /api/jobs
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Développeur Java Senior",
  "description": "Description du poste...",
  "location": "Paris",
  "contractType": "CDI",
  "companyId": 1,
  "domainId": 1
}
```

### Gestion des Candidatures

#### Postuler à une offre
```http
POST /api/applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 1,
  "candidateId": 1,
  "coverLetter": "Lettre de motivation..."
}
```

### Fonctionnalités Premium

#### S'abonner Premium
```http
POST /api/premium/subscribe?userId=1&planType=PREMIUM_MONTHLY
Authorization: Bearer <token>
```

#### Vérifier le statut Premium
```http
GET /api/premium/check/1
Authorization: Bearer <token>
```

### Connexions Professionnelles

#### Envoyer une demande de connexion
```http
POST /api/connections/request?requesterId=1&receiverId=2
Authorization: Bearer <token>
```

#### Accepter une connexion
```http
PUT /api/connections/accept/1
Authorization: Bearer <token>
```

### Messagerie

#### Envoyer un message
```http
POST /api/messages/send?senderId=1&receiverId=2&content=Bonjour!
Authorization: Bearer <token>
```

#### Voir la conversation
```http
GET /api/messages/conversation/1/2
Authorization: Bearer <token>
```

### Notifications

#### Lister les notifications
```http
GET /api/notifications/user/1
Authorization: Bearer <token>
```

#### Marquer comme lu
```http
PUT /api/notifications/read/1
Authorization: Bearer <token>
```

### Recherche Avancée

#### Recherche avancée d'offres
```http
GET /api/search/jobs/advanced?keyword=java&location=paris&minSalary=50000&remote=true
```

#### Suggestions de recherche
```http
GET /api/search/suggestions?query=java&type=skills
```

### Upload de Fichiers

#### Uploader un CV
```http
POST /api/files/upload-cv
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <cv.pdf>
userId: 1
```

### Paiements PayPal

#### Créer un paiement
```http
POST /api/payments/create?userId=1&planType=PREMIUM_MONTHLY&amount=9.99&currency=USD
Authorization: Bearer <token>
```

#### Exécuter un paiement
```http
POST /api/payments/execute/PAY-1234567890?payerId=PAYER123
Authorization: Bearer <token>
```

## 🔧 Configuration

### Base de Données
La configuration utilise PostgreSQL avec Liquibase pour la gestion des migrations. Les tables sont créées automatiquement au démarrage.

### Sécurité
- JWT pour l'authentification stateless
- BCrypt pour le hashage des mots de passe
- OAuth2 pour l'authentification Google
- Rôles et permissions basés sur Spring Security

### WebSocket
Les notifications en temps réel sont gérées via WebSocket sur le endpoint `/ws/notifications`.

### Elasticsearch
La recherche avancée utilise Elasticsearch pour des performances optimales.

## 🧪 Tests

### Lancer les tests
```bash
mvn test
```

### Tests d'intégration
```bash
mvn test -Dspring.profiles.active=test
```

## 📦 Déploiement

### Docker
```bash
# Build l'image
docker build -t jobstream-backend .

# Lancer le conteneur
docker run -p 8080:8080 jobstream-backend
```

### Docker Compose
```bash
docker-compose up -d
```

### Production
Pour la production, utilisez le profil `prod` et configurez les variables d'environnement appropriées.

## 🤝 Contribuer

1. Fork le projet
2. Créer une branche feature (`git checkout -b feature/amazing-feature`)
3. Commit les changements (`git commit -m 'Add amazing feature'`)
4. Push vers la branche (`git push origin feature/amazing-feature`)
5. Ouvrir une Pull Request

## 📝 Licence

Ce projet est sous licence MIT - voir le fichier [LICENSE](LICENSE) pour les détails.

## 📞 Support

Pour toute question ou support, veuillez contacter l'équipe de développement.

---

**JobStream** - *Le flux d'opportunités, à portée de main.*
