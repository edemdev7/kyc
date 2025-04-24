
### 🎯 Objectif :
Créer un microservice en Java pour gérer le processus KYC (Know Your Customer) en exploitant des services AWS (S3, Lambda, Textract, Rekognition, DynamoDB).

---

## 🔌 **Endpoints API exposés**

### `POST /kyc/submit`
- 📤 **Entrée** : 
  - `photo_id`: image de la pièce d’identité
  - `selfie`: photo de l’utilisateur
  - `user_id`: identifiant de l’utilisateur
- 📁 Upload des fichiers vers un bucket S3 (`kyc-uploads-bucket`), dossier spécifique par `user_id`

### `GET /kyc/status/{user_id}`
- 📥 **Retourne** :
  ```json
  {
    "user_id": "12345",
    "status": "VALIDATED",
    "reason": null
  }
  ```
- Va lire dans **DynamoDB** le statut de l'utilisateur.

---

## ⚙️ **Traitement backend déclenché automatiquement**

### 📦 AWS S3 (Trigger)
- Lorsqu’un utilisateur envoie ses images, elles sont stockées dans `s3://kyc-uploads-bucket/{user_id}/photo_id.jpg` et `selfie.jpg`.
- Le dépôt déclenche un **AWS Lambda** via **S3 Event Notification**.

---

## 🧠 **AWS Lambda – Analyse KYC**
Le Lambda est écrit en Java (avec AWS SDK). Il effectue 3 grandes étapes :

1. **Extraction texte via Textract**
   - Lit l'image de la **pièce d'identité** (photo_id).
   - Récupère :
     - Nom
     - Prénom
     - Date de naissance
     - Numéro du document
   - Vérifie que les informations sont lisibles.

2. **Détection visage**
   - Utilise **AWS Rekognition** pour détecter le visage dans la pièce d’identité.
   - Extrait le visage détecté de `photo_id.jpg`.

3. **Comparaison Selfie vs Document**
   - Utilise `CompareFaces` de Rekognition pour comparer le selfie avec le visage extrait du document.
   - Seuil de confiance typique : ≥ 90%.

---

## 🗃️ **Résultat stocké dans DynamoDB**

- Table : `kyc_status`
- Colonnes :
  - `user_id`
  - `status`: `PENDING`, `VALIDATED`, `REJECTED`
  - `reason`: erreurs éventuelles (e.g. "Textract failed", "Face mismatch")
  - `created_at`, `updated_at`

---

## 🔔 **Notification (optionnelle)**
- Si besoin, notification envoyée via **Amazon SNS** ou appel vers un webhook interne (API Gateway) pour signaler la fin du traitement à d'autres services.

---

