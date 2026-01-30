## Table des matières
- [Vue d'ensemble technique](#-vue-densemble-technique)
- [Structure du projet](#-structure-du-projet)
- [UI & Composables — Pattern Screen vs Content](#-ui--composables--pattern-screen-vs-content)
- [Thème & Spacing](#-thème--spacing)
- [Thème & Dynamic Colors](#-thème--dynamic-colors)
- [Créer un Repository](#-créer-un-repository)
- [Navigation Type-Safe](#-navigation-type-safe)
- [Navigation interne — Sous-screens authentifiés](#-navigation-interne--sous-screens-authentifiés)
- [Réseau / API](#-réseau--api)
- [DTOs & Mapping](#-dtos--mapping)
- [Logging](#-logging--timber)
- [Ressources](#-ressources)
- [Injection (Koin)](#-injection-koin)
- [Internationalisation](#-internationalisation)
- [Commandes](#-commandes)
- [Schéma Architecture (Mermaid)](#-schéma-architecture-mermaid)
# Skeleton Android — Jetpack Compose

Ce dépôt contient le skeleton Android de l'entreprise, conçu pour créer des applications robustes, scalables et testables.  
Il impose des conventions strictes : **Clean Architecture**, **Jetpack Compose** (pattern *Smart vs Dumb*), **Koin** pour la DI, **Navigation type-safe**, et gestion sécurisée des données.

## 🛠 Vue d'ensemble technique

- **Langage** : Kotlin
- **UI** : Jetpack Compose (Material 3)
- **Navigation** : Type-Safe Navigation (DSL)
- **DI** : Koin (modules dans `core/di`)
- **Réseau** : Retrofit + Moshi
- **Architecture** : Clean Architecture (domain / data / ui) + MVVM
- **Logging** : Timber

## 📂 Structure du projet

```
app/
├── src/main/java/com/devid/skeleton/
│   ├── core/di/                # Modules Koin (DataModule, AppModule, NetworkModule)
│   ├── data/                   # Couche Data
│   │   ├── dtos/               # Objets API (Moshi)
│   │   ├── mappers/            # DTO -> Domain
│   │   ├── network/            # Interfaces Retrofit
│   │   └── repositories/       # Implémentations
│   ├── domain/                 # Couche Métier
│   │   ├── entities/           # Modèles purs
│   │   └── repositories/       # Interfaces (contrats)
│   └── ui/                     # Présentation
│       ├── common/             # Composables génériques
│       ├── theme/              # Thème / Typo / Couleurs / Spacing
│       └── screens/            # Features
│           └── featureName/
│               ├── screen/     # Screen (Smart)
│               └── viewModel/  # ViewModel
└── res/
```

## 🎨 UI & Composables — Pattern "Screen vs Content"

### 1. Smart Component — `Screen`

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navigateToDetails: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadData() }

    HomeContent(
        state = state,
        onActionClick = { viewModel.doAction(); navigateToDetails() }
    )
}
```

### 2. Dumb Component — `Content`

```kotlin
@Composable
fun HomeContent(
    state: HomeUiState,
    onActionClick: () -> Unit
) {
    Box(Modifier.padding(innerPadding)) {
        // UI
    }
}

@PreviewScreenSizes
@Composable
private fun HomeContentPreview() {
    AppTheme {
        HomeContent(state = HomeUiState(), onActionClick = {})
    }
}
```

## 📐 Thème & Spacing

Toujours utiliser :

```kotlin
Modifier.padding(MaterialTheme.spacing.medium)
Arrangement.spacedBy(MaterialTheme.spacing.small)
```

## 🏗 Créer un Repository

### Domain

```kotlin
interface MyRepository {
    suspend fun fetchItems(): List<Item>
}
```

### Data

```kotlin
class MyRepositoryImpl(
    private val api: ApiService,
    private val mapper: ItemDtoMapper
) : MyRepository {
    override suspend fun fetchItems() =
        api.getItems().map { mapper.mapToDomain(it) }
}
```

### DI

```kotlin
singleOf(::MyRepositoryImpl) { bind<MyRepository>() }
```

## 🧭 Navigation Type-Safe

```kotlin
val state by userRepository.appState.collectAsStateWithLifecycle()

LaunchedEffect(state) {
    navController.navigate(
        when(state) {
            AppState.AUTHENTICATED -> AuthenticatedRoute
            AppState.UNAUTHENTICATED -> UnAuthenticatedRoute
            AppState.SPLASH -> SplashRoute
        }
    ) {
        popUpTo(0) { inclusive = true }
    }
}
```

## 🧭 Flow Global — Bootstrap Navigation

Le skeleton sépare clairement **le flux de navigation global** (basé sur l’état applicatif) des **flux internes aux features**.  
Le point d’entrée principal de l’application est `BootstrapNavigation`, qui observe l’état utilisateur via `UserRepository` et affiche dynamiquement l’écran correspondant.

```kotlin
@Composable
fun BootstrapNavigation(
    userRepository: UserRepository = koinInject()
) {
    val state by userRepository.appState.collectAsStateWithLifecycle()

    Crossfade(targetState = state) { appState ->
        when (appState) {
            AppState.SPLASH -> SplashScreen()
            AppState.UNAUTHENTICATED -> SignInScreen()
            AppState.AUTHENTICATED -> AuthenticatedNavigation()
        }
    }
}
```

Ce mécanisme assure :
- une séparation nette entre **état applicatif** et **navigation interne**,  
- un bootstrap simple et lisible,  
- une gestion fluide des transitions majeures (Splash → Connexion → App).

## 🔀 Navigation interne — Sous-screens authentifiés

Une fois l’utilisateur authentifié, l’application bascule dans le graphe `AuthenticatedNavigation`.  
Ce graphe est indépendant du flow global et ne gère que les navigations internes de la zone connectée.

```kotlin
@Composable
fun AuthenticatedNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeRoute
    ) {
        composable<HomeRoute> { HomeScreen() }
        composable<ProfileRoute> { entry ->
            val args = entry.arguments
            ProfileScreen(args.userId)
        }
    }
}
```

### Déclaration des routes type-safe

```kotlin
@Serializable
object HomeRoute

@Serializable
data class ProfileRoute(val userId: String? = null)
```

Avantages :
- Pas de chaînes de navigation `"home"`, `"profile?id=123"`  
- Validation automatique des paramètres  
- Navigation fortement typée et sécurisée  

## 🌐 Réseau / API

- Interfaces Retrofit → `data/network`
- DTOs uniquement
- `BaseUrl` via `BuildConfig`

## 📡 DTOs & Mapping

```kotlin
@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val name: String,
    val accessToken: String
)

fun UserDto.mapToEntity() = UserEntity(
    id = UUID.fromString(id),
    name = name,
    accessToken = accessToken
)
```

## 🪵 Logging — Timber

```kotlin
Timber.d("Debug")
Timber.e(exception, "Erreur")
```

## 🖼 Ressources

- Images → `drawable/`
- Icons → `mipmap/`

## 💉 Injection (Koin)

```kotlin
viewModelOf(::MyFeatureViewModel)
singleOf(::MyRepositoryImpl) { bind<MyRepository>() }
```

## 🌍 Internationalisation

```xml
<string name="home_button_login">Login</string>
```

## 🚀 Commandes

```bash
./gradlew installDebug
./gradlew testDebugUnitTest
./gradlew connectedAndroidTest
```
