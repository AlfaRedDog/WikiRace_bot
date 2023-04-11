# WikiRace service
## About project
  ### Task
   The input is the title of page A and page B from Wikipedia (domain: en.wikipedia.org). You have to find the shortest path (the number of jumps from one page to   another) from page A to page B.

  ### Authentication
   The system works on a subscription system and provides three subscription options:
   free use (1 request per day)
   Standard subscription (20 requests per day)
   Pro subscription (unlimited number of requests).

  ### Administering
   The system provides the ability to enter certain pages in the list of banned for search / navigation through them. 

  ### Using
   A REST API is provided to interact with the system. The REST API is available and documented through swagger

  ### Deployment
   Deployment of the system is done using the docker compose file in a docker container. 

## Launch notes
* On the command line, open the cloned repository folder
* Run docker on your computer 
* In the previously opened console, call the commands:
  docker compose pull
  docker compose up
* In Telegram, open a bot at https://t.me/Wiki_last_chance_bot
## API

### User-Auth Service

- **POST /authentication**: Authenticate
- **POST /authentication/refresh**: Refresh authentication
- **POST /users**: Register

### Subscription Service

- **POST /subscription/update:** Update subscription level
- **POST /subscription/get:** Get info about user subscription

### WikiRace Service 

- **POST /wikirace/get_short_path:** Finding a shortcut from one wiki article to another
- **POST /banned-titles:** Update the list of excluded articles
- **GET /banned-titles/{userId}:** Get the list of excluded articles

### OpenAPI
* **GET /swagger-ui.html**: Swagger UI
  * Paste bearer token to Authorize window to have access to secured endpoints
  
## Authentication

**Authenticate**

Request:

POST /authentication

```json
{
    "username": "<username>",
    "password": "<password>"
}
```

Response:

```json
{
    "accessToken": "<token>",
    "refreshToken": "<token>"
}
```

Access token should be used in all other requests as header:
`Authorization: Bearer <access token>`
