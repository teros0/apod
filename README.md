# NASA Astronomy Picture of the Day Widget & Gallery

Android application that brings NASA's **Astronomy Picture of the Day (APOD)** to your home screen. While the app offers a full-screen gallery and image description, the **primary focus of this project is the Home Screen Widget**, developed to enjoy today's image without opening the app or the website.

<img width="244" height="542" alt="зображення" src="https://github.com/user-attachments/assets/24b89686-c85a-497e-a03a-0b16be6c5ebd" />
<img width="244" height="542" alt="зображення" src="https://github.com/user-attachments/assets/cb0ccab1-6c5b-4be9-8d17-96d0bb2a5bb2" />

## 🛠️ Installation & Setup

To run this project, you will need to provide your own NASA API Key.

1.  **Get an API Key:** Sign up for free at [api.nasa.gov](https://api.nasa.gov/).
2.  **Add the Key:** Open `local.properties` in your project root (this file is ignored by Git) and add:
    ```properties
    NASA_API_KEY=your_actual_key_here
    ```
3.  **Build:** Android Studio will automatically inject this key into the build process using the **Secrets Gradle Plugin**.

## 📜 Credits & Attributions

* **Observatory Icon:** [Observatory icons](https://www.flaticon.com/free-icons/observatory) created by Freepik - Flaticon.
* **Widget Preview Image:** Based on the NASA APOD from January 11, 2026: [The Horsehead Nebula in Infrared](https://apod.nasa.gov/apod/ap260111.html).
* **Data API:** Powered by [NASA Open APIs](https://api.nasa.gov/).

## 📦 Releases

You can find the latest compiled APK in the [Releases](https://github.com/teros0/apod/releases) section of this repository.

---

*Note: This project was developed using Gemini*
