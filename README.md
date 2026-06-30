# Last-Minute Life Saver — Backend

Backend for an AI productivity app that helps you actually get things done before deadlines hit, instead of just reminding you they exist.

Live API: https://lifesaver-backend-8jta.onrender.com
Frontend repo: https://github.com/kanaksng123-arch/lifesaver-frontend

## What it does

Handles auth, task/habit storage, and talks to Gemini to do the actual "thinking" — prioritizing your tasks, telling you what to do right now based on how much energy you have, and building you a day plan.

## Stack

- Java 17 + Spring Boot 3.5
- Spring Security + JWT for auth
- MongoDB Atlas
- Gemini API (called server-side via WebClient, so the key never touches the frontend)
- Deployed on Render via Docker

## API

| Method | Endpoint | What it does |
|---|---|---|
| POST | `/api/auth/register` | Sign up |
| POST | `/api/auth/login` | Log in, get a JWT |
| GET/POST/PUT/DELETE | `/api/tasks` | Task CRUD |
| PATCH | `/api/tasks/{id}/done` | Mark task complete |
| GET | `/api/tasks/analytics` | Completion rate, overdue/pending counts |
| GET/POST/DELETE | `/api/habits` | Habit CRUD |
| PATCH | `/api/habits/{id}/checkin` | Check in, updates streak |
| POST | `/api/ai/prioritize` | Gemini ranks your pending tasks |
| POST | `/api/ai/now` | Gemini picks one task and gives you a 5-min plan |
| POST | `/api/ai/plan` | Gemini builds an hour-by-hour schedule |

Everything except `/api/auth/**` needs `Authorization: Bearer <token>`.

## Running it locally

```bash
git clone https://github.com/kanaksng123-arch/lifesaver.git
cd lifesaver
```

Set these env vars (Run Config in IntelliJ, or export in your shell):

```
MONGODB_URI=
JWT_SECRET=
GEMINI_API_KEY=
CORS_ORIGINS=http://localhost:5173
```

Then:

```bash
mvn spring-boot:run
```

Runs on `localhost:8080`.

## Author

Kanak Singh — B.Tech EEE, BPIT (GGSIPU)
