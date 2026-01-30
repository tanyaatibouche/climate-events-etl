# Climate Events ETL - Scala Project

Pipeline ETL (Extract, Transform, Load) pour l'analyse d'evenements climatiques mondiaux.

## Description

Ce projet implemente un pipeline de traitement de donnees en Scala qui:
- Charge des donnees JSON d'evenements climatiques
- Valide et nettoie les données (suppression des invalides et doublons)
- Calcule des statistiques et analyses
- Genere des rapports en JSON et TXT

## Structure du projet

```
scala_project/
├── src/main/scala/
│   ├── Main.scala            # Point d'entree, orchestration ETL
│   ├── Event.scala           # Modeles de donnees (case classes)
│   ├── DataLoader.scala      # Chargement et parsing JSON
│   ├── DataValidator.scala   # Validation et deduplication
│   ├── StatCalculator.scala  # Calculs statistiques
│   ├── ReportGenerator.scala # Generation des rapports
│   └── BonusAnalytics.scala  # Analyses supplementaires
├── data/
│   ├── data_clean.json       # 100 evenements propres
│   ├── data_dirty.json       # 500 evenements avec erreurs
│   └── data_large.json       # 10000 evenements (performance)
├── reports/                  # Rapports generes (output)
├── build.sbt
└── README.md
```

## Modele de donnees

### Event
```scala
case class Event(
    id: Int,
    eventType: String,      // Hurricane, Flood, Earthquake, Wildfire, Drought, Tornado
    name: Option[String],
    country: String,
    region: String,
    date: String,
    year: Int,
    severity: Int,          // 1-5
    casualties: Int,
    affected: Int,
    damage: Option[Double],
    temperature: Option[Double],
    windSpeed: Option[Int]
)
```

## Pipeline ETL

### 1. Extract (DataLoader)
- Lecture des fichiers JSON
- Parsing avec Circe
- Collecte des erreurs de parsing

### 2. Transform (DataValidator)
- Validation des champs:
  - `year` entre 1900 et 2025
  - `severity` entre 1 et 5
  - `casualties` >= 0
  - `eventType` dans la liste autorisee
- Suppression des doublons par `id`

### 3. Load (ReportGenerator)
- Generation du rapport JSON complet
- Generation du rapport TXT lisible
- Generation du rapport bonus (analyses supplementaires)

## Statistiques calculees

| Statistique | Description |
|-------------|-------------|
| `events_by_type` | Nombre d'evenements par type |
| `events_by_severity` | Nombre d'evenements par niveau de severite |
| `events_by_year` | Nombre d'evenements sur les 5 dernieres annees |
| `events_by_country` | Top 10 des pays les plus touches |
| `deadliest_events` | Top 10 des evenements les plus meurtriers |
| `most_expensive_events` | Top 10 des evenements les plus couteux |
| `total_casualties` | Total des victimes |
| `total_affected` | Total des personnes affectees |
| `total_damage` | Total des degats economiques |
| `average_casualties_by_type` | Moyenne des victimes par type |
| `most_affected_regions` | Top 5 des regions les plus touchees |

## Analyses bonus (BonusAnalytics)

- Evenements par decennie
- Evenements de severite 5 par decennie
- Evenements majeurs nommes (Top 10)
- Statistiques des ouragans nommes
- Top regions par nombre d'evenements
- Type le plus frequent par region

## Technologies

- **Scala 3.3.1**
- **Circe 0.14.6** - Parsing JSON
- **SBT** - Build tool
- **MUnit** - Tests unitaires

## Execution

```bash
# Compiler et executer
sbt run

# Executer les tests
sbt test
```

## Output

Le programme traite les 3 fichiers de donnees et genere:

```
data/data_clean.json: 0.045s
data/data_dirty.json: 0.089s
data/data_large.json: 0.812s
Finished: 3 success, 0 failed
```

### Fichiers generes

Pour chaque fichier source `data_X.json`:
- `reports/results_data_X.json` - Rapport JSON complet
- `reports/report_data_X.txt` - Rapport TXT lisible
- `reports/bonus_data_X.txt` - Analyses supplementaires

## Format du rapport JSON

```json
{
  "statistics": {
    "total_events_parsed": 500,
    "total_events_valid": 100,
    "parsing_errors": 20,
    "duplicates_removed": 321
  },
  "events_by_type": {
    "Hurricane": 15,
    "Flood": 18,
    ...
  },
  "events_by_severity": {
    "1": 20,
    "2": 25,
    ...
  },
  "deadliest_events": [...],
  "most_expensive_events": [...],
  "total_casualties": 1234567,
  "total_affected": 98765432,
  "total_damage": 123456.78,
  ...
}
```

## Architecture

```
Main
  │
  ├── DataLoader.loadEvents()
  │     └── Either[String, LoadResult]
  │
  ├── DataValidator.isValid() / removeDuplicates()
  │     └── List[Event]
  │
  ├── StatCalculator.*
  │     └── Calculs statistiques
  │
  └── ReportGenerator
        ├── generateReport() → OutputAnalysisReport
        ├── writeReport()    → JSON + TXT
        └── writeBonusReport() → TXT bonus
```

## Gestion des erreurs

Le projet utilise `Either[String, T]` pour la gestion fonctionnelle des erreurs:
- `Left(errorMessage)` en cas d'echec
- `Right(result)` en cas de succes

Les erreurs sont propagees et agregees, permettant de traiter tous les fichiers meme si certains echouent.

## Auteur

Projet realisé par Tanya TIBOUCHE & Fitahiany Michèle MBOHOAZY dans le cadre du cours de Scala - EFREI Paris
