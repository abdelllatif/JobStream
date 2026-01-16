# JobStream - Guide Complet des API

## Vue d'ensemble du projet

JobStream est une plateforme de recrutement moderne qui connecte les candidats, les recruteurs et les entreprises. Ce document fournit une vue détaillée de toutes les API disponibles avec leur ordre d'utilisation et leur gestion.

---

## 📋 Table des Matières

1. [Architecture des API](#architecture-des-api)
2. [Flux d'Utilisation Recommandé](#flux-dutilisation-recommandé)
3. [Authentification](#authentification)
4. [Gestion des Utilisateurs](#gestion-des-utilisateurs)
5. [Gestion des Entreprises](#gestion-des-entreprises)
6. [Gestion des Offres d'Emploi](#gestion-des-offres-demploi)
7. [Gestion des Profils Candidats](#gestion-des-profils-candidats)
8. [Gestion des Candidatures](#gestion-des-candidatures)
9. [Messagerie](#messagerie)
10. [Recherche Avancée](#recherche-avancée)
11. [Paiements et Abonnements](#paiements-et-abonnements)
12. [Gestion des Erreurs](#gestion-des-erreurs)

---

## 🏗️ Architecture des API

### Base URL
```
http://localhost:8080/api
```

### Format des Réponses
- **Succès**: JSON avec les données demandées
- **Erreur**: JSON avec message d'erreur et code HTTP approprié

### En-têtes Requis
```json
{
  "Content-Type": "application/json",
  "Authorization": "Bearer <token_jwt>"
}
```

---

## 🔄 Flux d'Utilisation Recommandé

### 1. Initialisation (Pour tout utilisateur)
1. **Inscription** → Créer un compte
2. **Connexion** → Obtenir un token JWT
3. **Création de profil** → Compléter les informations

### 2. Flux Candidat
1. Créer son profil candidat
2. Rechercher des offres
3. Postuler aux offres intéressantes
4. Gérer ses candidatures
5. Communiquer avec les recruteurs

### 3. Flux Recruteur
1. Créer/modifier son profil entreprise
2. Publier des offres d'emploi
3. Rechercher des candidats
4. Gérer les candidatures reçues
5. Communiquer avec les candidats

---

## 🔐 Authentification

### Inscription d'utilisateur
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "role": "CANDIDATE" // CANDIDATE, RECRUITER, ADMIN
}
```

**Réponse:**
```json
{
  "id": 1,
  "email": "john.doe@example.com",
  "role": "CANDIDATE",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Connexion
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

**Réponse:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "john.doe@example.com",
    "role": "CANDIDATE"
  }
}
```

### Déconnexion
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

---

## 👥 Gestion des Utilisateurs

### Obtenir le profil utilisateur
```http
GET /api/users/profile
Authorization: Bearer <token>
```

### Mettre à jour le profil
```http
PUT /api/users/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Smith",
  "phone": "+33612345678"
}
```

---

## 🏢 Gestion des Entreprises

### Créer une entreprise
```http
POST /api/companies
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Tech Company",
  "description": "Une entreprise technologique innovante",
  "website": "https://techcompany.com",
  "industry": "Technology",
  "size": "100-500",
  "location": "Paris, France"
}
```

### Lister toutes les entreprises
```http
GET /api/companies
```

### Obtenir une entreprise par ID
```http
GET /api/companies/{companyId}
```

### Mettre à jour une entreprise
```http
PUT /api/companies/{companyId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Updated Company Name",
  "description": "Description mise à jour",
  "website": "https://updated-company.com"
}
```

### Supprimer une entreprise
```http
DELETE /api/companies/{companyId}
Authorization: Bearer <token>
```

---

## 💼 Gestion des Offres d'Emploi

### Créer une offre
```http
POST /api/jobs
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Développeur Java Senior",
  "description": "Nous recherchons un développeur Java expérimenté...",
  "location": "Paris",
  "contractType": "CDI",
  "salaryMin": 60000,
  "salaryMax": 80000,
  "remote": true,
  "companyId": 1,
  "domainId": 1
}
```

### Lister toutes les offres
```http
GET /api/jobs?page=0&size=20
```

### Obtenir une offre par ID
```http
GET /api/jobs/{jobId}
```

### Mettre à jour une offre
```http
PUT /api/jobs/{jobId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Titre mis à jour",
  "description": "Description mise à jour...",
  "location": "Lyon",
  "contractType": "CDD"
}
```

### Supprimer une offre
```http
DELETE /api/jobs/{jobId}
Authorization: Bearer <token>
```

---

## 👨‍💼 Gestion des Profils Candidats

### Créer un profil candidat
```http
POST /api/candidate-profiles
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Développeur Full Stack",
  "summary": "Développeur avec 5+ ans d'expérience...",
  "skills": ["Java", "Spring Boot", "React", "PostgreSQL"],
  "experience": "5 ans",
  "education": "Master en Informatique",
  "location": "Paris",
  "resumeUrl": "https://example.com/cv.pdf",
  "linkedinUrl": "https://linkedin.com/in/johndoe"
}
```

### Lister tous les profils candidats
```http
GET /api/candidate-profiles
```

### Obtenir un profil par ID
```http
GET /api/candidate-profiles/{profileId}
Authorization: Bearer <token>
```

### Mettre à jour un profil
```http
PUT /api/candidate-profiles/{profileId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Développeur Senior Full Stack",
  "summary": "Résumé mis à jour...",
  "skills": ["Java", "Spring Boot", "React", "Docker", "Kubernetes"]
}
```

### Supprimer un profil
```http
DELETE /api/candidate-profiles/{profileId}
Authorization: Bearer <token>
```

---

## 📄 Gestion des Candidatures

### Créer une candidature
```http
POST /api/applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": 1,
  "candidateId": 1,
  "coverLetter": "Je suis très intéressé par ce poste...",
  "status": "PENDING"
}
```

### Lister toutes les candidatures
```http
GET /api/applications
Authorization: Bearer <token>
```

### Obtenir une candidature par ID
```http
GET /api/applications/{applicationId}
Authorization: Bearer <token>
```

### Mettre à jour une candidature
```http
PUT /api/applications/{applicationId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "ACCEPTED",
  "coverLetter": "Lettre de motivation mise à jour..."
}
```

### Supprimer une candidature
```http
DELETE /api/applications/{applicationId}
Authorization: Bearer <token>
```

---

## 💬 Messagerie

### Envoyer un message
```http
POST /api/messages/send?senderId={senderId}&receiverId={receiverId}&content={content}&jobId={jobId}
Authorization: Bearer <token>
```

### Obtenir un message par ID
```http
GET /api/messages/{messageId}
Authorization: Bearer <token>
```

### Obtenir une conversation
```http
GET /api/messages/conversation/{userId1}/{userId2}
Authorization: Bearer <token>
```

### Obtenir une conversation paginée
```http
GET /api/messages/conversation/{userId1}/{userId2}/paginated?page=0&size=20
Authorization: Bearer <token>
```

### Obtenir les messages d'un utilisateur
```http
GET /api/messages/user/{userId}
Authorization: Bearer <token>
```

### Obtenir les messages non lus
```http
GET /api/messages/unread/{userId}
Authorization: Bearer <token>
```

### Marquer un message comme lu
```http
PUT /api/messages/read/{messageId}
Authorization: Bearer <token>
```

### Marquer une conversation comme lue
```http
PUT /api/messages/read-conversation/{userId1}/{userId2}
Authorization: Bearer <token>
```

### Supprimer un message
```http
DELETE /api/messages/{messageId}
Authorization: Bearer <token>
```

### Supprimer une conversation
```http
DELETE /api/messages/conversation/{userId1}/{userId2}
Authorization: Bearer <token>
```

### Obtenir le nombre de messages non lus
```http
GET /api/messages/unread-count/{userId}
Authorization: Bearer <token>
```

### Obtenir les partenaires de conversation
```http
GET /api/messages/partners/{userId}
Authorization: Bearer <token>
```

---

## 🔍 Recherche Avancée

### Rechercher des offres
```http
GET /api/search/jobs?keyword=Java&location=Paris&contractType=CDI&page=0&size=20
```

### Recherche avancée d'offres
```http
GET /api/search/jobs/advanced?keyword=Java&location=Paris&minSalary=50000&maxSalary=80000&experienceLevel=SENIOR&remote=true&sortBy=postedAt&sortOrder=desc&page=0&size=20
```

### Obtenir les offres recommandées
```http
GET /api/search/jobs/recommended/{userId}?limit=10
Authorization: Bearer <token>
```

### Rechercher des candidats
```http
GET /api/search/candidates?keyword=Java&location=Paris&skills=Spring&skills=React&page=0&size=20
Authorization: Bearer <token>
```

### Rechercher des recruteurs
```http
GET /api/search/recruiters?keyword=Tech&company=TechCompany&page=0&size=20
Authorization: Bearer <token>
```

### Obtenir des suggestions
```http
GET /api/search/suggestions?query=Java&type=skills
```

---

## 💳 Paiements et Abonnements

### Créer un paiement
```http
POST /api/payments/create?userId={userId}&planType=PREMIUM_MONTHLY&amount=9.99&currency=USD
Authorization: Bearer <token>
```

### Approuver un paiement
```http
POST /api/payments/approve/{paymentId}
Authorization: Bearer <token>
```

### Exécuter un paiement
```http
POST /api/payments/execute/{paymentId}?payerId={payerId}
Authorization: Bearer <token>
```

### Capturer un paiement
```http
POST /api/payments/capture/{orderId}
Authorization: Bearer <token>
```

### Rembourser un paiement
```http
POST /api/payments/refund/{paymentId}
Authorization: Bearer <token>
```

### Obtenir un paiement par ID PayPal
```http
GET /api/payments/paypal/{paypalPaymentId}
Authorization: Bearer <token>
```

### Obtenir un paiement par ID de commande
```http
GET /api/payments/order/{orderId}
Authorization: Bearer <token>
```

### Obtenir les paiements d'un utilisateur
```http
GET /api/payments/user/{userId}
Authorization: Bearer <token>
```

### Obtenir les plans disponibles
```http
GET /api/payments/plans
Authorization: Bearer <token>
```

---

## ⚠️ Gestion des Erreurs

### Codes d'erreur courants

- **400 Bad Request**: Requête invalide
- **401 Unauthorized**: Non authentifié
- **403 Forbidden**: Permissions insuffisantes
- **404 Not Found**: Ressource non trouvée
- **409 Conflict**: Conflit de données
- **500 Internal Server Error**: Erreur serveur

### Format des réponses d'erreur
```json
{
  "timestamp": "2024-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Offre d'emploi non trouvée",
  "path": "/api/jobs/999"
}
```

---

## 📝 Bonnes Pratiques

### 1. Gestion des Tokens
- Toujours inclure le token JWT dans les en-têtes pour les requêtes protégées
- Rafraîchir le token avant expiration
- Stocker le token de manière sécurisée

### 2. Pagination
- Utiliser les paramètres `page` et `size` pour les listes
- Commencer à `page=0`
- Taille recommandée: 20 éléments par page

### 3. Recherche
- Utiliser la recherche avancée pour des résultats précis
- Combiner plusieurs filtres pour optimiser les résultats
- Utiliser les suggestions pour l'autocomplétion

### 4. Gestion des erreurs
- Toujours vérifier les codes de statut HTTP
- Implémenter une gestion robuste des erreurs côté client
- Afficher des messages clairs aux utilisateurs

### 5. Performance
- Utiliser les endpoints paginés pour les grandes listes
- Mettre en cache les données fréquemment accédées
- Limiter le nombre de requêtes simultanées

---

## 🔗 Variables d'Environnement

Pour utiliser la collection Postman, configurez ces variables:

```json
{
  "baseUrl": "http://localhost:8080",
  "token": "votre_token_jwt",
  "jobId": "1",
  "applicationId": "1",
  "companyId": "1",
  "profileId": "1",
  "messageId": "1",
  "userId": "1",
  "senderId": "1",
  "receiverId": "2",
  "paymentId": "PAY-123456789",
  "payerId": "PAYER-123456789",
  "orderId": "ORDER-123456789"
}
```

---

## 📞 Support

Pour toute question technique ou problème avec les API:
1. Vérifier la documentation ci-dessus
2. Consulter les logs de l'application
3. Contacter l'équipe de développement

---

**JobStream API Documentation** - Version 1.0
*Dernière mise à jour: Janvier 2024*
