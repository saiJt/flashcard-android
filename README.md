# flashcard-android
Personal Android flashcard app built with Kotlin and Jetpack Compose

Android flashcard app development.

I started developing this app because I couldn’t find an existing vocabulary app
that matched how I personally want to study.
Rather than adapting to another app’s workflow, I decided to build my own.

---------------------------
## What was developed
### 2025.12.29
- Implemented the core flashcard study screen using Jetpack Compose
- Added word list selection (lobby) and navigation flow
- Implemented vocabulary:
  - Create word lists
  - Add vocabulary to a list
  - Delete vocabulary
  - Delete word lists
- Integrated Room Database with Repository and ViewModel
- Connected UI to ViewModel state using StateFlow
- Implemented flashcard navigation:
  - Previous / Next
  - Shuffle
- Added study actions:
  - Know / Don’t know tracking
  - Wrong-only review mode
- Implemented safe empty-state handling for newly created word lists
- Fixed crashes caused by invalid index access when vocabulary list was empty
- Refactored flashcard UI into a single, stable composable structure
- Added dialogs for adding and deleting vocabulary and word lists
