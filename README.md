# Admission Assistant Chatbot

## Overview
The **Admission Assistant Chatbot** is an AI-powered application designed to assist students with their college-related queries. The system leverages **Retrieval-Augmented Generation (RAG)** for intelligent responses and features a **Flask-based API** that integrates seamlessly with an **Android app** built using **Kotlin and Jetpack**. The chatbot helps students by answering admission-related questions, while an admin panel allows college directors to manage and update yearly admission data.

## Features
### 🎓 Student Interface
- **Login & Signup**: Secure authentication system.
- **Query Resolution**: Ask admission-related questions and receive AI-generated responses.
- **Firestore Integration**: Fetches relevant college data stored in the database.

### 🏫 Admin (Director) Interface
- **Profile Management**: The admin (college director) can create and manage their profile.
- **College Data Upload**: Uploads yearly admission-related data in **PDF format**.
- **Text Extraction**: Extracts relevant data from PDFs and stores it in **Firestore Database**.

### 🔥 Backend (Flask API)
- **RAG-based AI Model**: Provides intelligent and context-aware responses.
- **REST API**: Serves responses to the Android app.
- **Firestore Integration**: Stores and retrieves student queries and admission-related data.

## Tech Stack
### 📱 Android App
- **Kotlin & Jetpack**: Modern UI development.
- **Firebase Firestore**: Cloud database for real-time storage and retrieval.
- **Retrofit**: API communication with the Flask backend.

### 🖥️ Backend (API)
- **Flask**: Lightweight Python framework.
- **RAG (Retrieval-Augmented Generation)**: Enhances chatbot response accuracy.
- **PyMuPDF / PDFMiner**: Extracts text from uploaded PDF documents.

## Installation & Setup
### 🔧 Backend (Flask API)
1. Clone the repository:
   ```bash
   git clone https://github.com/dhananjayingole/Chatbot.git
   cd Chatbot/backend
   ```
2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
3. Run the Flask server:
   ```bash
   python Gemini2.py
   ```

### 📲 Android App
1. Open the project in **Android Studio**.
2. Set up Firebase Firestore.
3. Run the app on an emulator or a physical device.

## 📌 Future Enhancements
- Implement **voice-based query support**.
- Improve **UI/UX** for a more interactive experience.
- Add **multi-language support**.


