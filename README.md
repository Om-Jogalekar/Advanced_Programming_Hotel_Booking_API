HOTEL BOOKING MANAGEMENT SYSTEM (BACKEND API)

Introduction

The Hotel Booking Management System is a backend RESTful API developed using Spring Boot. The system is designed to manage hotel bookings, users, and guest details efficiently. It provides secure authentication, booking lifecycle management, and reporting features for hotel owners.

This project demonstrates the implementation of real-world backend concepts such as layered architecture, database interaction, and REST API design.

Objectives
To develop a scalable hotel booking backend system
To implement secure user authentication and authorization
To manage booking workflows efficiently
To generate revenue and booking reports
To handle booking expiration automatically
Technologies Used

Java – Programming Language
Spring Boot – Backend Framework
Spring Security – Authentication and Authorization
Hibernate (JPA) – ORM Framework
MySQL / PostgreSQL – Database
Maven – Build Tool
Postman – API Testing Tool

System Architecture

The system follows a layered architecture:

Client (Postman)
↓
Controller Layer
↓
Service Layer
↓
Repository Layer
↓
Database

Controller Layer handles HTTP requests.
Service Layer contains business logic.
Repository Layer interacts with the database.

Database Design

Entity Relationships:

User → One-to-Many → Booking
Hotel → One-to-Many → Booking
Booking → One-to-Many → Guest
User → One-to-Many → Hotel

<img width="1820" height="971" alt="ER_Diagram" src="https://github.com/user-attachments/assets/e54b26ef-a53a-4c37-9d2f-7c8ea71ae250" />

Modules Description

6.1 User Module
Handles user registration and login. Implements JWT-based authentication and role-based access control.

6.2 Hotel Module
Allows hotel creation by owners and retrieval of hotel details.

6.3 Booking Module
Handles booking creation, updates, deletion, and lifecycle management.

6.4 Guest Module
Allows adding guests to a booking and maintains guest details such as name, gender, and age.

6.5 Report Module
Generates reports for hotels including total bookings, total revenue, and average revenue.

Booking Workflow

RESERVED → GUEST_ADDED → CONFIRMED → COMPLETED / EXPIRED

Booking starts in RESERVED state.
After adding guests, it moves to GUEST_ADDED.
After confirmation, it becomes CONFIRMED.
Finally, it ends as COMPLETED or EXPIRED.

API Design

Example: Get Hotel Report

Endpoint:
GET /hotels/{hotelId}/reports?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD

Description:
Returns booking statistics and revenue for a given hotel within a specified date range.

Business Logic and Validations
Only the booking owner can modify a booking
Guests can only be added when booking is in RESERVED state
Expired bookings cannot be modified
Only the hotel owner can access reports
Booking Expiry Handling

The system includes logic to handle expired bookings.

Bookings expire after a certain time period
Expired bookings are either marked as EXPIRED or deleted using scheduled tasks
Testing

The API is tested using Postman.

All endpoints are tested with sample requests
Authentication is verified using JWT tokens
Edge cases such as expired bookings are handled and tested
Conclusion

The Hotel Booking Management System successfully demonstrates the development of a scalable backend API using Spring Boot. It integrates authentication, booking management, and reporting features while following best practices in software design.

This project provides a strong foundation for real-world applications and can be extended with additional features.

Future Enhancements
Payment gateway integration
Email or SMS notifications
Admin dashboard
Caching using Redis
Deployment using Docker
