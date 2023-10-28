# Stack
Fuel your passion for fitness with our dedicated app. Record and celebrate your workouts, visualize your impressive fitness journey, and connect with like-minded gym enthusiasts who share your zeal for a healthy lifestyle.
## Table of Contents
- [Skills](#Skills)
- [Database Structure](#Database-Sturcture)
- [Features](#Features)
  - [Home Page](#Home-Page)
  - [Workout Page](#Workout-Page)
  - [Timer](#Timer)
  - [Exercise Detail Page](#Exercise-Detail-Page)
  - [Statistics Page](#Statistics-Page)
  - [Find Gym Bro Page](#Find-Gym-Bro-Page)
- [Installation](#Installation)

## Skills
* MVVM
* DI(Hilt)
* Room
* Firestore
* SharedPreference
* Mock
* JUnit
* Retrofit
* Moshi
* Gson
* Jetpack Components
* Coroutine
* Glide
* Lottie
* Youtube Player
* MPAndroid
* Chaquopy
* OpenAI
* Google Maps, Places, Location API
* Service
* Foreground Service
* Notifications
* Runtime Permissions
* Intent, Pending Intent
* RecyclerView
* Material3 CarouselView, Chips

## Database Sturcture
* Room
![](https://github.com/jason-chueh/Stack/assets/76050749/024d973d-6bc8-40dd-ba66-808cf8a3afc9)


## Features

<table>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/08c7580f-6610-4346-ab6f-9891973e6a75" alt="Home Page GIF" width="200"></td>
    <td>
      <h3 align="left">Home Page:</h3>
        <p><strong>Techniques:</strong> 
        Carousal View, Lottie, Image Runtime Permission, Glide</p>
      <p><strong>Features:</strong> The homepage serves as your fitness dashboard, where your workout history is meticulously recorded. By simply tapping the floating button in the bottom right corner, you can seamlessly navigate to our template library. There, you'll discover an array of workout templates, including your very own custom creations, ready to power your fitness journey. </p>
    </td>
  </tr>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/b30186c2-333c-4afd-965b-9960875f8507" alt="Workout Page GIF" width="200"></td>
    <td>
      <h3 align="left">Workout Page:</h3>
        <p><strong>Techniques:</strong> Nested RecyclerView, ItemTouchHelper
        </p>
      <p><strong>Features:</strong> The Workout Recording Page is your ultimate fitness companion, designed for precise workout tracking. When you opt for a template, it seamlessly imports exercises, reps, and weights into your recording. Each exercise entry is not only draggable, allowing you to tailor your workout to your liking but also swipable for effortless removal with a simple swipe gesture.</p>
    </td>
  </tr>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/289dbe3e-b288-4d08-b23e-ef04ee4d7373" alt="Timer GIF" width="200"></td>
    <td>
      <h3 align="left">Timer:</h3>
        <p><strong>Techniques:</strong> 
        Service, Foreground Service, Notification, Pending Intent</p>
      <p><strong>Description:</strong> An interval timer designed for your set breaks, providing real-time notifications that allow you to return to your workout with a single click.</p>
    </td>
  </tr>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/eed2447b-7f93-4497-966e-fc7999643efb" alt="Detail Page GIF" width="200"></td>
    <td>
      <h3 align="left">Exercise Detail Page:</h3>
      <p><strong>Techniques:</strong> Youtube Player, OpenAI, Python Web Scrapper, Chaquopy, Html TagHandler</p>
      <p><strong>Description:</strong>The Exercise Detail Page is your ultimate guide to performing exercises correctly. It not only offers step-by-step instructions but also provides YouTube tutorials. When you click on the video, the player automatically starts playing the YouTube video. Additionally, the video's transcript is extracted and processed by an OpenAI model to provide comprehensive instructions for your convenience.
        </p>
      </td>
  </tr>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/771e54ae-fb04-4d29-948a-efa81e8ca01b" alt="Vocabulary Page GIF" width="200"></td>
    <td>
      <h3 align="left">Statistics Page:</h3>
      <p><strong>Techniques:</strong> MPAndroid, ChipGroup</p>
      <p><strong>Description:</strong> The Statistics Page brings your exercise records to life with interactive visualizations. It allows you to filter exercises you've previously trained, select specific exercises and record types, and instantly generate line charts to track your progress.</p>
    </td>
  </tr>
  <tr>
    <td><img src="https://github.com/jason-chueh/Stack/assets/76050749/ed03a819-cd35-4c57-97b5-cc00bb66d055" alt="Find Gym Bro Page GIF" width="200"></td>
    <td>
      <h3 align="left">Find Gym Bro Page:</h3>
      <p><strong>Techniques:</strong> Google Map Api, Google Client Api, Google Distance Matrix Api</p>
      <p><strong>Description:</strong> Find Gym Bro Page displays registered users on a Google map, sorted by proximity. User can start a chat by clicking on a user's profile picture.
        </p>
    </td>
  </tr>
  
</table>

## Installation
1. Run Stack on Android Studio
    * Clone this project to your local folder
    ```
    git clone git@github.com:jason-chueh/Stack.git
    ```
    * Apply for API keys for [Google Cloud API](https://cloud.google.com/apis?hl=zh-tw), [OpenAI API](https://platform.openai.com/), [ExerciseDB API](https://rapidapi.com/justin-WFnsXH_t6/api/exercisedb/pricing), and store them in the local.properties file using the format below:
    ```
    MAPS_API_KEY= your_google_cloud_key
    GPT_KEY= your_openAI_key
    EXERCISE_KEY = your_exerciseDB_key
2. Install Stack on Android Device
    * Download the Stack [apk](https://drive.google.com/drive/u/0/folders/1mq7F-MsWv0HfzGYtMBBfLxOpTJzF9zIM)
    * Execute the apk and install Stack
