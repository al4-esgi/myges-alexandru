# MyGES - Documentation d'Authentification

## Fonctionnalité d'Authentification Implémentée

### Architecture MVVM

L'authentification suit l'architecture MVVM (Model-View-ViewModel) :

#### 1. **View** - `SignInScreen.kt`
- Interface utilisateur avec Jetpack Compose
- Champs de saisie pour email et mot de passe
- Affichage des erreurs
- Bouton de connexion avec état de chargement

#### 2. **ViewModel** - `SignInViewModel.kt`
- Gestion de l'état de l'UI (loading, erreurs, champs)
- Validation des champs
- Appel au repository pour l'authentification
- Gestion des erreurs avec messages localisés

#### 3. **Repository** - `UserRepositoryImpl.kt`
- Logique métier d'authentification
- Appel à l'API MyGES
- Extraction du token depuis la redirection
- Sauvegarde du token
- Mise à jour de l'intercepteur avec le token

### Flux d'Authentification MyGES

1. **Saisie des identifiants** : L'utilisateur entre son email et mot de passe
2. **Appel d'authentification** : 
   - Création d'un header Basic Auth (Base64)
   - Appel GET vers `https://authentication.kordis.fr/oauth/authorize?response_type=token&client_id=skolae-app`
3. **Récupération du token** :
   - Le serveur retourne une redirection (302)
   - Le token est extrait du header `Location`
   - Format: `comreseaugesskolae:/oauth2redirect#access_token=TOKEN&token_type=bearer`
4. **Sauvegarde du token** :
   - Token sauvegardé dans DataStore (persistance locale)
   - Token ajouté à l'intercepteur pour les requêtes futures
5. **Vérification** :
   - Appel à `/me/profile` pour récupérer les infos utilisateur
   - Mise à jour de l'état de l'application (AUTHENTICATED)

### Composants Créés

#### DTOs (Data Transfer Objects)
- **AuthResponseDto.kt** : Réponse d'authentification
- **ProfileDto.kt** : Profil utilisateur MyGES

#### Network
- **Api.kt** : Interface Retrofit avec endpoints MyGES
  - `authenticate()` : Authentification
  - `getProfile()` : Récupération du profil
  
- **AuthInterceptor.kt** : Intercepteur OkHttp
  - Ajoute le token Bearer aux requêtes
  - Headers spécifiques MyGES (User-Agent, Accept)
  
- **LoggingInterceptor.kt** : Logs des requêtes (debug)

#### Configuration
- **DataModule.kt** mis à jour :
  - OkHttpClient avec intercepteurs
  - Base URL : `https://api.kordis.fr/`
  - Désactivation des redirections (nécessaire pour récupérer le token)

### Gestion des Erreurs

- **Identifiants incorrects** : Message "Identifiants incorrects"
- **Erreur réseau** : Message "Erreur de connexion"
- **Autres erreurs** : Message avec détails de l'exception
- **Champs vides** : Validation locale "Veuillez remplir tous les champs"

### Sécurité

- Token stocké de manière sécurisée dans DataStore
- Pas de stockage du mot de passe
- HTTPS pour toutes les communications
- Basic Auth uniquement pour l'authentification initiale

### Tests

Pour tester l'authentification :

1. Lancer l'application
2. Saisir vos identifiants MyGES
3. Cliquer sur "Connexion"
4. En cas de succès, vous serez redirigé vers l'écran authentifié
5. Le token sera automatiquement utilisé pour les futures requêtes API

### Prochaines Étapes

Avec cette base d'authentification, vous pouvez maintenant implémenter :

1. **Agenda** - `GET /me/agenda?start={timestamp}&end={timestamp}`
2. **Notes** - `GET /me/{year}/grades`
3. **Absences** - `GET /me/{year}/absences`
4. **Cours** - `GET /me/{year}/courses`
5. **Professeurs** - `GET /me/{year}/teachers`
6. **Classes** - `GET /me/{year}/classes`
7. **Actualités** - `GET /me/news?page={page}`
8. **Bannières** - `GET /me/news/banners`

Chaque requête utilisera automatiquement le token via l'`AuthInterceptor`.
