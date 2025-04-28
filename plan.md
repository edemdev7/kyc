Voici la **description textuelle** de ton **nouveau diagramme de séquence** (avec envoi direct vers S3) :

---

### Acteurs :
- **Client (App Mobile / Web)**
- **AWS S3**
- **KYC Backend**
- **AWS Rekognition**
- **Database (PostgreSQL)**

---

### Flow détaillé :

1. **Client → AWS S3** :
    - Upload direct de :
        - photo d'identité (`photo_id`)
        - selfie (`photo_selfie`)
    - S3 retourne les **URLs publiques/privées**.

2. **Client → KYC Backend (POST /kyc/submit)** :
    - Envoi d'une requête `POST` contenant :
        - `photo_id_url`
        - `photo_selfie_url`
        - (optionnel) **Données déclarées** : `nom`, `prénom`, `sexe`, `date de naissance`

3. **KYC Backend → AWS Rekognition (CompareFaces)** :
    - Appel du service Rekognition pour comparer la `photo_id_url` et la `photo_selfie_url`.
    - Rekognition retourne :
        - Similarité (score de correspondance)
        - Détails éventuels (bounding box, landmarks).

4. **KYC Backend → AWS Rekognition (Textract ou DetectText)** :
    - Extraction de texte sur `photo_id_url` pour obtenir :
        - **Nom**
        - **Prénom**
        - **Sexe**
        - **Date de naissance**
        - (et potentiellement autres infos comme numéro de document)

5. **KYC Backend → Traitement interne** :
    - Comparaison entre les **données extraites** de la pièce et les **données déclarées** par l’utilisateur (nom, prénom, sexe, date de naissance).
    - Calcul de:
        - Score de **validation faciale**.
        - Score de **validation textuelle**.

6. **KYC Backend → Database (PostgreSQL)** :
    - Sauvegarde des résultats :
        - URLs des images
        - Résultats de comparaison faciale
        - Résultats de comparaison des données textuelles
        - Statut (`PENDING`, `VALIDATED`, `REJECTED`)
        - Logs / historique d’erreurs éventuelles

7. **Client → KYC Backend (GET /kyc/status/{id})** :
    - Le client peut récupérer à tout moment l’état (`status`) de son KYC avec un `id`.
    - La réponse contient :
        - Statut global
        - Résultats détaillés (si besoin)

---

### Résumé des endpoints exposés :

| Méthode | Endpoint               | Description                        |
|--------|-------------------------|------------------------------------|
| POST   | `/kyc/submit`             | Envoyer URLs + infos pour traitement KYC |
| GET    | `/kyc/status/{id}`        | Récupérer le statut du traitement  |

