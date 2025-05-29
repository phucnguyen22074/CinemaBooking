CinemaBooking - Online Movie Ticket Booking System
CinemaBooking is a robust web application designed for seamless movie ticket booking and cinema management. Built with Spring Boot, it provides an intuitive experience for moviegoers and comprehensive administrative tools for cinema operators. The system supports user authentication, movie and theater filtering, seat selection, ticket purchasing with PayPal integration, and full CRUD operations for administrative tasks.
Features
User Features

Account Management:
Sign Up / Sign In: Create a new account or log in securely to access the system.
Profile Updates: Modify personal details such as name, email, and password.


Movie and Theater Filtering: Search for movies and theaters by location, genre, or showtime.
Seat Selection and Ticket Purchase: Interactive seat map for selecting seats and completing ticket transactions via PayPal for secure and convenient payments.
Booking History: View a detailed history of purchased tickets, including movie details, theater, seat, showtime, and payment status.

Admin Features

Admin Login: Secure access to the admin dashboard using dedicated admin credentials.
CRUD Operations: Full Create, Read, Update, and Delete functionality for the following entities:
Users: Manage user accounts (e.g., registration, role assignment).
Theaters: Administer cinema locations, including details like address and brand.
Screens: Manage screening rooms within theaters (e.g., room number, capacity).
Seats: Configure seat layouts and statuses for each screen.
Roles: Define and assign user roles (e.g., admin, user).
Movies: Manage movie details (e.g., title, duration, release date).
Genres: Categorize movies by genre (e.g., action, drama, horror).
Directors: Maintain records of movie directors.
Brands: Manage cinema chain brands (e.g., CGV, Lotte).
Actors: Track actor information associated with movies.



Technology Stack

Backend: Spring Boot, Spring Security (authentication and authorization), Spring Data JPA.
Frontend: Thymeleaf (or React/Angular, depending on implementation), Bootstrap for responsive design.
Database: MySQL or PostgreSQL (H2 for development).
Payment Integration: PayPal API for secure ticket transactions.
Build Tool: Maven.
Version Control: Git.
IDE: IntelliJ IDEA or equivalent.

System Requirements

Java 17 or higher.
Maven 3.6 or later.
MySQL or PostgreSQL (configured as per setup instructions).
PayPal developer account for payment integration (see PayPal setup).
Modern web browser (e.g., Chrome, Firefox, Edge).

Installation Guide

Clone the Repository:git clone https://github.com/phucnguyen22074/CinemaBooking.git
cd CinemaBooking


Configure the Database:
Create a database (e.g., cinema_booking) in MySQL or PostgreSQL.
Update the src/main/resources/application.properties file with your database credentials:spring.datasource.url=jdbc:mysql://localhost:3306/cinema_booking
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update




Configure PayPal:
Create a PayPal developer account at developer.paypal.com.
Obtain clientId and clientSecret for the PayPal Sandbox or Live environment.
Add PayPal credentials to application.properties:paypal.client.id=your_paypal_client_id
paypal.client.secret=your_paypal_client_secret
paypal.mode=sandbox  # or live




Install Dependencies:mvn clean install


Run the Application:mvnDevelop spring-boot:run


Access the application at http://localhost:8080.

Usage Instructions
For Users

Sign Up / Sign In: Navigate to the homepage and use the "Sign Up" or "Sign In" options.
Browse Movies and Theaters: Use filters to find movies or theaters by location, genre, or showtime.
Select Seats: Choose a showtime and pick seats from the interactive seat map.
Purchase Tickets: Confirm booking details and complete payment via PayPal.
View Booking History: Access the "Booking History" section to review past transactions, including payment details.

For Admins

Admin Login: Log in with admin credentials (contact the system administrator for details).
Manage Data:
Access the admin dashboard (e.g., /admin) to perform CRUD operations.
Manage entities such as users, theaters, screens, seats, roles, movies, genres, directors, brands, and actors via dedicated interfaces.



Database Schema
The system relies on the following core tables:

users: Stores user information (e.g., name, email, password, role).
theaters: Contains cinema details (e.g., name, address, brand).
screens: Manages screening rooms (e.g., room number, capacity, theater).
seats: Tracks seat details (e.g., position, status, screen).
roles: Defines user roles (e.g., admin, user).
movies: Stores movie metadata (e.g., title, duration, director, genres).
genres: Categorizes movies by genre.
directors: Maintains director information.
brands: Records cinema chain brands.
actors: Stores actor details linked to movies.

Security Notes

Configuration Files: Exclude application.properties from version control by adding it to .gitignore. Provide a template file (application.properties.example) for reference:spring.datasource.url=jdbc:mysql://localhost:3306/cinema_booking
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
paypal.client.id=
paypal.client.secret=
paypal.mode=sTwitter


Admin Credentials: Admin accounts must be configured securely and not exposed in public documentation.
PayPal Credentials: Do not commit PayPal clientId or clientSecret to the repository.

Troubleshooting

Database Connection Issues: Verify database credentials and ensure the database server is running.
PayPal Payment Issues: Ensure valid PayPal Sandbox or Live credentials are configured correctly.
Application Errors: Check logs in the console or application.log for detailed error messages.
Git Push Errors: Ensure the correct branch (main or master) is used and commits are made before pushing:git add .
git commit -m "Add PayPal integration"
git push origin main



Contributing
Contributions are welcome! To contribute:

Fork the repository.
Create a feature branch (git checkout -b feature/your-feature).
Commit your changes (git commit -m "Add your feature").
Push to the branch (git push origin feature/your-feature).
Open a Pull Request.

Please report bugs or suggest enhancements via the Issues section on GitHub.
Contact

Author: Phúc Nguyễn
Email: phucnguyen220704@gmail.com
GitHub: phucnguyen22074

