# MatchMate 💕
 
A modern matrimonial/match discovery Android application built using **Kotlin and Jetpack Compose**.

The application fetches user profiles from the Random User API, allows users to accept or decline matches, persists match decisions locally, supports pagination, and continues to work with cached data when the device is offline.

---

## ✨ Features

- Discover match profiles
- Accept or decline matches
- Separate Accepted and Declined tabs
- Filter matches by:
  - All
  - Men
  - Women
- Paginated profile loading
- Pull/toolbar refresh
- Offline cached data
- Local persistence using Room
- Pending local actions stored for synchronization
- Loading, error, empty and offline states
- Snackbar feedback after Accept/Decline actions
- Modern dating-app inspired UI
- Jetpack Compose UI
- MVI-style state and intent handling
- StateFlow-based reactive state management

---

## 🛠 Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Material 3**
- **Coroutines**
- **StateFlow**
- **Room**
- **Retrofit**
- **Gson**
- **Coil**
- **KSP**

---

## 🏗 Architecture

The project follows a layered architecture with an MVI-style presentation layer.

```text
┌─────────────────────────────┐
│        Jetpack Compose      │
│             UI              │
└──────────────┬──────────────┘
               │
               │ Intent
               ▼
┌─────────────────────────────┐
│         ViewModel           │
│      State + Effects        │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│         Repository          │
│     Single Data Access      │
│          Point              │
└────────────┬───────┬────────┘
             │       │
             ▼       ▼
      ┌──────────┐ ┌──────────┐
      │  Room    │ │ Retrofit │
      │ Database │ │   API    │
      └──────────┘ └──────────┘
