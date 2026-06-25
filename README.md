# Recipe System Backend

Spring Boot REST API backend for managing recipes. Users can register, log in, create recipes, browse recipes, comment, rate recipes, and add recipes to favorites. Admin users can manage users, categories, and recipes.

## Technologies

- Java 17+
- Spring Boot 3
- Maven
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Bean Validation
- H2 database
- Lombok

## Main Features

- User registration and login with JWT
- Role-based security: `USER` and `ADMIN`
- Recipe CRUD
- Category management
- Comments for recipes
- Ratings from 1 to 5
- Favorite recipes
- Admin user management
- Filtering, sorting, and pagination for recipes

## How To Run

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:recipe_system
username: sa
password:
```

## Environment Variables

Example for IntelliJ:

```text
JWT_SECRET=my-super-secret-jwt-key-1234567890;ADMIN_USERNAME=admin;ADMIN_EMAIL=admin@example.com;ADMIN_PASSWORD=admin123
```

`ADMIN_*` variables are optional. If they are set, the application creates a default admin user.

## Auth Endpoints

```http
POST /api/auth/register
POST /api/auth/login
GET /api/auth/me
```

Register body:

```json
{
  "username": "student1",
  "email": "student1@example.com",
  "password": "password123"
}
```

Login body:

```json
{
  "email": "student1@example.com",
  "password": "password123"
}
```

## Recipe Endpoints

```http
GET /api/recipes
GET /api/recipes/{id}
POST /api/recipes
PUT /api/recipes/{id}
DELETE /api/recipes/{id}
GET /api/recipes/my
```

Create recipe body:

```json
{
  "title": "Pancakes",
  "description": "Easy homemade pancakes",
  "ingredients": "Flour, milk, eggs, sugar",
  "instructions": "Mix ingredients and cook on a pan",
  "cookingTimeMinutes": 20,
  "servings": 4,
  "difficulty": "EASY",
  "categoryId": 1
}
```

## Category Endpoints

```http
GET /api/categories
POST /api/admin/categories
PUT /api/admin/categories/{id}
DELETE /api/admin/categories/{id}
```

## Comment Endpoints

```http
POST /api/recipes/{recipeId}/comments
PUT /api/comments/{commentId}
DELETE /api/comments/{commentId}
```

## Rating Endpoints

```http
POST /api/recipes/{recipeId}/ratings
PUT /api/recipes/{recipeId}/ratings
GET /api/recipes/{recipeId}/ratings/average
```

Rating body:

```json
{
  "value": 5
}
```

## Favorite Endpoints

```http
POST /api/recipes/{recipeId}/favorites
DELETE /api/recipes/{recipeId}/favorites
GET /api/favorites/my
```

## Admin Endpoints

```http
GET /api/admin/users
PUT /api/admin/users/{id}/block
PUT /api/admin/users/{id}/activate
DELETE /api/admin/recipes/{id}
PUT /api/admin/recipes/{id}/status
GET /api/admin/statistics
```

## Business Logic

- Duplicate emails are not allowed.
- Passwords are encrypted with BCrypt.
- New users receive the `USER` role.
- Admin can manage users and recipes.
- Users can edit/delete only their own recipes.
- Users can edit/delete only their own comments.
- A user can rate the same recipe only once.
- Rating must be from 1 to 5.
- A user can add the same recipe to favorites only once.
- Blocked users cannot create recipes, comments, ratings, or favorites.

## Security

Public endpoints:

```text
POST /api/auth/register
POST /api/auth/login
GET /api/recipes
GET /api/recipes/{id}
GET /api/categories
```

Protected endpoints require JWT token:

```text
Authorization: Bearer token
```

Admin endpoints require `ADMIN` role:

```text
/api/admin/**
```

## Project Architecture

The project uses standard Spring Boot layered architecture:

```text
Controller -> Service -> Repository
```

- Controller handles HTTP requests.
- Service contains business logic.
- Repository works with the database.
- DTOs are used for request and response data.
- Global exception handler returns clean error messages.
