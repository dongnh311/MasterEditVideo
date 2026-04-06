# MasterEditVideo

An Android video editing application powered by **OpenGL ES** for real-time GPU rendering, **ExoPlayer** for media playback, and **FFmpeg** for video processing.

<img width="729" alt="Screenshot 2023-01-11 at 08 31 18" src="https://user-images.githubusercontent.com/40257252/211697261-4f50f8de-0541-4803-bad6-6a47051d00b0.png">

---

## Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Rendering Pipeline](#rendering-pipeline)
- [Transitions](#transitions)
- [Tech Stack](#tech-stack)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Permissions](#permissions)

---

## Features

| Feature | Status |
|---|---|
| Play video | :white_check_mark: |
| Play image | :white_check_mark: |
| Play background music | :white_check_mark: |
| Apply transitions (25+) | :white_check_mark: |
| Timeline editor | :white_check_mark: |
| Media picker (video & image) | :white_check_mark: |
| Audio volume mixing | :white_check_mark: |
| Merge video and image | :construction: In progress |
| Apply filters (Brightness, Contrast, Gamma) | :construction: In progress |
| Apply effects | :construction: In progress |
| Multi-screen graph layouts | :construction: In progress |
| Trim video | :black_square_button: Planned |
| Trim music | :black_square_button: Planned |
| Export edited video | :black_square_button: Planned |

---

## Architecture

The project follows a **multi-module architecture** with a clear separation between the UI layer and the core editing engine.

```
┌─────────────────────────────────────────────────────────────┐
│                        app module                           │
│  ┌─────────────┐ ┌────────────┐ ┌─────────────────────────┐│
│  │  Activities  │ │  Fragments │ │  Adapters / Dialogs     ││
│  │  ┌────────┐  │ │ Transition │ │  AdapterMedia           ││
│  │  │  Main  │  │ │ Filter     │ │  AdapterSpecial         ││
│  │  │Activity│  │ │ Effect     │ │  AdapterMusic           ││
│  │  └────────┘  │ │ Graph      │ │  DialogTool             ││
│  │  ┌────────┐  │ │ Special    │ │  DialogMixVolume        ││
│  │  │ Media  │  │ │ PickMusic  │ │                         ││
│  │  │ Pick   │  │ └────────────┘ └─────────────────────────┘│
│  │  └────────┘  │                                           │
│  └─────────────┘  Singleton / LiveData / Retrofit           │
└──────────────────────────┬──────────────────────────────────┘
                           │ depends on
┌──────────────────────────▼──────────────────────────────────┐
│                    masteredit module                         │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              ManagerPlayerMedia (Orchestrator)        │   │
│  │  ┌──────────────┐ ┌────────────┐ ┌───────────────┐  │   │
│  │  │ VideoPlayer  │ │  Music     │ │  PreViewLayout│  │   │
│  │  │ Control      │ │  Player    │ │  Control      │  │   │
│  │  │  └ExoManager │ │  Control   │ │  └GLPlayerView│  │   │
│  │  └──────────────┘ └────────────┘ └───────────────┘  │   │
│  │  ┌──────────────────────────────────────────────────┐│   │
│  │  │           SpecialPlayControl                     ││   │
│  │  │  Transitions │ Filters │ Effects │ Graphs        ││   │
│  │  └──────────────────────────────────────────────────┘│   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────┐ ┌───────────────┐ ┌───────────────────┐  │
│  │  GL Renderer  │ │  GLSL Shaders │ │  Models & Enums   │  │
│  └──────────────┘ └───────────────┘ └───────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Key Components

| Component | Role |
|---|---|
| **ManagerPlayerMedia** | Central orchestrator that coordinates video playback, music, transitions, and effects |
| **VideoPlayerControl** | Wraps ExoPlayer for video/image playback and media queue management |
| **MusicPlayerControl** | Manages background music playback and volume |
| **PreViewLayoutControl** | Manages the OpenGL preview surface and filter chains |
| **SpecialPlayControl** | Handles transitions, effects, and graph layout rendering |
| **GLPlayerRenderer** | Core OpenGL ES renderer with shader-based processing |
| **TimeLineControl** | Visual timeline editor with track management |

---

## Project Structure

```
MasterEditVideo/
├── app/                                    # UI & Application module
│   └── src/main/java/com/dongnh/mastereditvideo/
│       ├── app/                            # Application class
│       ├── base/                           # Base API response models
│       ├── const/                          # Event & media constants
│       ├── model/                          # Data models (Music, Tab, MixSound)
│       ├── singleton/                      # Shared LiveData state
│       ├── utils/
│       │   ├── adapter/                    # RecyclerView adapters
│       │   ├── control/                    # Timeline & duration controls
│       │   ├── dialog/                     # Tool & volume dialogs
│       │   ├── exts/                       # Kotlin extensions
│       │   ├── interfaces/                 # Callback interfaces
│       │   ├── retrofit/                   # Network layer (Retrofit + OkHttp)
│       │   └── view/                       # Custom views
│       └── view/
│           ├── main/                       # MainActivity (main editor)
│           ├── pickmedia/                  # MediaPickActivity
│           ├── transition/                 # TransitionFragment
│           ├── filter/                     # FilterFragment
│           ├── effect/                     # EffectFragment
│           ├── graph/                      # GraphFragment
│           ├── special/                    # SpecialFragment
│           └── pickmusic/                  # PickMusicFragment
│
├── masteredit/                             # Core editing engine library
│   └── src/main/
│       ├── java/com/dongnh/masteredit/
│       │   ├── base/                       # Abstract transition classes
│       │   ├── const/                      # Filter, media, transition constants
│       │   ├── control/                    # Player & preview controls
│       │   ├── eglcore/                    # EGL configuration
│       │   ├── enums/                      # Format, resource, scale enums
│       │   ├── filter/                     # GL filters (Brightness, Contrast, Gamma)
│       │   ├── gl/                         # OpenGL utilities & FBO management
│       │   ├── graph/                      # Multi-screen layout effects
│       │   ├── manager/                    # ManagerPlayerMedia
│       │   ├── model/                      # MediaModel, MusicModel, SpecialModel
│       │   ├── render/                     # GLPlayerRenderer
│       │   ├── transition/                 # 25+ transition effects
│       │   └── utils/                      # ExoManager, extensions, custom views
│       └── assets/
│           ├── transition/                 # Transition JSON + thumbnails + GLSL shaders
│           ├── graph/                      # Graph layout JSON + thumbnails + shaders
│           ├── filter/                     # Filter definitions
│           └── effect/                     # Effect definitions + FFmpeg shaders
│
├── build.gradle                            # Root build config
├── settings.gradle                         # Module settings
└── gradle.properties                       # Gradle JVM & AndroidX config
```

---

## Rendering Pipeline

The app uses a custom OpenGL ES rendering pipeline to process and display video frames in real-time:

```
  ┌──────────────┐     ┌──────────────────┐     ┌───────────────────┐
  │  Media Files  │────>│    ExoPlayer     │────>│  SurfaceTexture   │
  │ (Video/Image) │     │  (decode frames) │     │  (frame buffer)   │
  └──────────────┘     └──────────────────┘     └────────┬──────────┘
                                                         │
                                                         ▼
                                              ┌─────────────────────┐
                                              │   GLPlayerRenderer  │
                                              │  (OpenGL ES 2.0)    │
                                              └────────┬────────────┘
                                                       │
                        ┌──────────────────────────────┼──────────────────┐
                        │                              │                  │
                        ▼                              ▼                  ▼
              ┌──────────────────┐        ┌─────────────────┐   ┌──────────────┐
              │   Filter Chain   │        │   Transitions   │   │    Graph     │
              │ Brightness       │        │  (GLSL shaders) │   │   Layouts    │
              │ Contrast         │        │  Progress-based │   │  2/3/4/6     │
              │ Gamma            │        │  animation      │   │  screen      │
              │ LUT              │        │  (0.0 → 1.0)    │   │  splits      │
              └────────┬─────────┘        └────────┬────────┘   └──────┬───────┘
                       │                           │                   │
                       └───────────────┬───────────┘───────────────────┘
                                       ▼
                              ┌──────────────────┐
                              │   GLPlayerView   │
                              │  (Preview Output) │
                              └──────────────────┘
```

### Transition Architecture

Each transition effect follows a three-layer pattern:

```
  ┌─────────────────────┐
  │  *Transition class  │  Logic & lifecycle (extends AbstractTransition)
  │  e.g. FadeTransition│
  └─────────┬───────────┘
            │ uses
  ┌─────────▼───────────┐
  │  *TransDrawer class │  OpenGL draw calls & texture binding
  │  e.g. FadeTransDrawer
  └─────────┬───────────┘
            │ uses
  ┌─────────▼───────────┐
  │  *TransShader class │  GLSL vertex + fragment shaders
  │  e.g. FadeTransShader
  └─────────────────────┘
```

---

## Transitions

25+ built-in transition effects, all rendered via GLSL shaders:

| | | | | |
|:---:|:---:|:---:|:---:|:---:|
| Window Slice | Simple Zoom | Cross Zoom | Luminance Melt | Cross Hatch |
| Wipe Right | Wipe Left | Wipe Down | Wipe Up | Dreamy Zoom |
| Fade | Directional Wipe | Wind | Inverted Page Curl | Swap |
| Cube | Circle Open | PinWheel | Angular | Hexagonalize |
| Pixelize | Perlin | Bounce | | |

---

## Tech Stack

### Core

| Technology | Version | Purpose |
|---|---|---|
| **Kotlin** | 1.7.20 | Primary language |
| **Android Gradle Plugin** | 7.3.1 | Build system |
| **OpenGL ES 2.0** | - | Real-time GPU rendering |
| **ExoPlayer** | 2.18.2 | Media playback (video, audio) |
| **FFmpeg Kit** | 5.1 (min) | Video processing & encoding |
| **GPUImage** | 2.1.0 | GPU-based image filtering |

### Android Jetpack & UI

| Library | Version | Purpose |
|---|---|---|
| Core KTX | 1.9.0 | Kotlin extensions for Android |
| AppCompat | 1.6.0 | Backward-compatible UI |
| Material Components | 1.7.0 | Material Design UI |
| ConstraintLayout | 2.1.4 | Flexible layouts |
| RecyclerView | 1.2.1 | Scrollable lists |
| Lifecycle (LiveData + ViewModel) | 2.5.1 | Reactive state management |
| Data Binding | - | View binding |
| Flexbox | 3.0.0 | Flexible box layouts |

### Networking & Data

| Library | Version | Purpose |
|---|---|---|
| Retrofit | 2.9.0 | REST API client |
| OkHttp | 5.0.0-alpha.11 | HTTP client + logging |
| Gson | 2.10.1 | JSON serialization |
| Glide | 4.14.2 | Image loading & caching |

### Utilities

| Library | Version | Purpose |
|---|---|---|
| Timber | 5.0.1 | Logging |
| CircleImageView | 3.1.0 | Circular image views |

---

## Requirements

- **Android Studio**: Arctic Fox or later
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 33 (Android 13)
- **Java**: 1.8+
- **Kotlin**: 1.7.20+

---

## Getting Started

1. **Clone the repository**

   ```bash
   git clone https://github.com/user/MasterEditVideo.git
   ```

2. **Open in Android Studio**

   Open the project root directory in Android Studio.

3. **Sync Gradle**

   Let Android Studio download all dependencies and sync the project.

4. **Build & Run**

   Select a device or emulator (API 24+) and click **Run**.

> **Note:** The app runs in **landscape mode** by default.

---

## Permissions

| Permission | Purpose | API Level |
|---|---|---|
| `READ_EXTERNAL_STORAGE` | Access media files on device | < 33 |
| `WRITE_EXTERNAL_STORAGE` | Save exported videos | < 33 |
| `READ_MEDIA_IMAGES` | Access images | 33+ |
| `READ_MEDIA_VIDEO` | Access videos | 33+ |
| `READ_MEDIA_AUDIO` | Access audio files | 33+ |
| `INTERNET` | Download music & assets | All |
| `ACCESS_NETWORK_STATE` | Check network connectivity | All |

---

## License

This project is for demo and educational purposes.
