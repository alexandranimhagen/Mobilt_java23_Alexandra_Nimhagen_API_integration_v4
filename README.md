# Mobilt_java23_Alexandra_Nimhagen_API_integration_v4
A Kotlin-based weather app that fetches data from the OpenWeatherMap API, displaying weather details with notifications and backstack navigation

Projektrapport API integration v.4
WeatherApp är en Android-applikation byggd med Kotlin som använder OpenWeather API för att hämta och visa väderinformation för specifika städer. Applikationen är uppdelad i fyra fragment: Home, Weather Details, Settings och About. Användaren kan navigera mellan dessa fragment med hjälp av Androids navigation component. API-integration hanteras med Retrofit, och användarens stad sparas i SharedPreferences för framtida användning.
Användning av API
OpenWeather API används för att hämta väderdata baserat på användarens valda stad. API returnerar detaljerad information som temperatur, lufttryck, fuktighet, vindhastighet samt tidpunkterna för soluppgång och solnedgång.
Endpoints
Den huvudsakliga endpoint som används i projektet är:
Current weather data: https://api.openweathermap.org/data/2.5/weather?q={city}&appid={API_KEY}&units=metric
Parametrar:
q - Den stad användaren söker väderinformation för.
appid - Användarens API-nyckel.
units - Sätter enheten till metriska mått (Celsius).
Flöde efter API-anrop
Användaren anger stad i Settings-fragmentet och trycker på “Spara”. Denna stad sparas i SharedPreferences för framtida anrop. I Home-fragmentet skickas en request till API när användaren trycker på “Visa väderdetaljer”. Datan som returneras från OpenWeather API mappas till vår WeatherResponse-dataklass.
Efter att datan har hämtats uppdateras UI i WeatherDetailsFragment. Informationen som visas inkluderar temperatur, min/max-temperatur, vindhastighet, tryck, fuktighet samt tider för soluppgång och solnedgång. Om något fel uppstår visas ett felmeddelande i applikationen.
Datahantering efter request
Den returnerade JSON-datan mappas till dataklasser i Kotlin med hjälp av Gson, som är integrerat via Retrofit. Exempel på dataklasser inkluderar WeatherResponse, Main, Weather, och Sys. Dessa dataklasser hanterar olika delar av väderinformationen som returneras från API. Efter mappningen uppdateras användargränssnittet i WeatherDetailsFragment för att visa den senaste informationen.
Struktur och design
Applikationen är strukturerad med ett MVVM-designmönster för att separera logik och presentation. Retrofit används för att hantera API-anrop, och LiveData används för att observera API-svaren och uppdatera UI. Fragmenten kommunicerar via Androids navigeringskomponent, som hanterar backstack och fragment-navigering.
MainActivity: Huvudaktiviteten som innehåller NavHostFragment och hanterar fragmentnavigeringen.
HomeFragment: Används som startpunkt och låter användaren navigera till andra fragment.
WeatherDetailsFragment: Visar väderinformationen som hämtats från API
.
SettingsFragment: Låter användaren ange och spara en stad.
AboutFragment: Innehåller information om applikationen.


