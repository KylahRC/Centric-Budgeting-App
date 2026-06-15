<img width="200" height="200" alt="image" src="https://github.com/user-attachments/assets/bc6b6b72-edc3-42a1-9528-ae84e290fbd4" />

# Centric Budgeting App

## Purpose
Centric is a personal budgeting application designed for South African students who face challenges like shared living expenses, invisible digital spending, and subscription fatigue. 

The app helps users:

- Register securely and manage thier own financial profile.

- Track expenses by category with CRUD functionality (create, read, update, delete).

- Set monthly and category‑specific budget goals.

- Split shared expenses with roommates or friends using a dynamic split engine.

- Visualize spending trends through dashboards and charts.

The goal is to unify the best features of existing apps (Mints automation, YNABs discipline, Buddys social features) into a tool tailored for the student experience.



## Design Considerations
Centric follows Neo‑Minimalism and Glassmorphism design trends.

- Custom logo and app icon designed to reflect financial unity and growth.

  - Teal (#26A69A) for safe‑to‑spend zones.

  - Midnight Navy (#1A237E) for headers/navigation.

  - Coral (#FF7043) for overspending alerts and split notifications.

- Glassy Neutral layers for depth and modernity.

- WCAG‑compliant high‑contrast palette ensures usability for visually impaired users.

- Fast touch feedback.

- Designed to run consistently without crashes, with local data storage for privacy.
<img width="250" height="450" alt="Screenshot_20260615_144115_Centric Budgeting" src="https://github.com/user-attachments/assets/cb76394e-78f3-4c8c-9f6c-59aff3ca132f" />




## Screens
### Login/Registration
- Secure entry with username and password using Firebase Authentication. Users can create accounts or log in to existing ones. The app takes a wealth of dtat from the user upon registration to feel professional.

### Home
- Displays a live “Remaining balance” figure, recent expenses, and buttons to add funds and veiw/add expenses.

### Add Expense
- Users log transactions with amount, date, description and category. The data is fetched from Firestore.

### Category Totals
- Shows spending per category over a monthly period, with totals displayed clearly.

## Technical Implementation
### Firebase Firestore
- Used for storing users and user data. We store the balance, goals, categories and informoation about them and expenses and information about those. Each expense references a category by its Firestore document ID, ensuring accurate totals and chart labels.

### Authentication
- Firebase Auth manages secure user registration and login.

### Local Privacy
- Data is stored in Firestore under each user’s document, with no external sharing.

### Charts & Dashboards
- MPAndroidChart used for pie charts and progress rings, updated dynamically with Firestore data.


## GitHub Usage
### Version Control
- The project is managed through GitHub, with commits documenting incremental progress (UI design, Firebase integration, bug fixes).

### Collaboration
- We worked in parallel, using the main branch and using pull/push requests to commit and update our code.

### Documentation
- This README serves as the central reference for project purpose, design, and technical considerations.

## Video Links
### Part 2
- https://youtu.be/Y7bBS8ovhMs?si=vf7fbvXqYUUIxKh3

###Part 3
- LINK

