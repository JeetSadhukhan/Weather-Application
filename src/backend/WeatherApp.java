package backend;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

//retreive weather  data from API - this backend logic will fatch the latest weather
// data from the external API and return it . The GUI will
//display this data to the user

public class WeatherApp {

    // fetch weather data for given location
    public static JSONObject getWeatherData(String locationName) {
        // Get Location cordinates using the GeoLocation api

        JSONArray locationData = getLocationData(locationName);

        // so to use the weather Forecast API, we need to get the latitude and longitude
        // of the location
        // which we get from the geolocation API
        // extract latitude and longitude data
        // the Geolocation API returns up a list of different countries that have the
        // entered city

        JSONObject location = (JSONObject) locationData.get(0);
        double latitute = (double) location.get("latitute");
        double longitude = (double) location.get("longitute");

        // build api request url with location cordinates

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitute + "&longitude=" + longitude
                + "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia%2FTokyo";

        try {
            // call API and get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check the response states
            if (conn.getResponseCode() != 200) {
                System.out.println("Error : could not connect to API");
                return null;

            }

            // store resulting Json data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());
            while (scanner.hasNext()) {
                // read and store the data into the string builder
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();

            // close url connection
            conn.disconnect();

            // parse through our data
            JSONParser parser = new JSONParser();
            JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data
            JSONObject hourly = new JSONObject();
            // we went to get the current hour's data
            // so we need to get the index of our current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexCurrentTime(time);

            // get temperature
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            // get weather code
            // will be used to find the weather description
            JSONArray weatherCode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

            // get humidity data
            JSONArray relativeHumidity = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) relativeHumidity.get(index);

            // get wind speed data
            JSONArray windspeedData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windspeedData.get(index);

            // build the weather json data object that weare going to access in our frontend
            // to store the data we will need to give the value and id in a way

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    // retrivees geographic coordinates for given location name
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in location name to + to add to API requestformat
        // example : New York it will replace the space when we use the API line and
        // submit our data

        locationName = locationName.replaceAll(" ", "+");

        // build API url with location perameter
        // here we need to get the URl of our API
        // and then replace the name values with ours name
        // so when we call our API, we are going to be passing it our location

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // call the api and get response
            // to make a HTTP request like our API call, we need a HTTP client like
            // HTTPURLConnection class
            // we are going to make a separate method to instantiate it because we will be
            // doing this multipletime in this class

            HttpURLConnection conn = fetchApiResponse(urlString);

            // ckeck response status
            // 200 means successful connection
            if (conn.getResponseCode() != 200) {

                System.out.println("Error : Could not connect to API");
                return null;
            } else {
                // store the response of the API result
                StringBuilder resultsJson = new StringBuilder();

                // we will use a scanner to read the JSON data that is returned fron our API
                // call
                // we do this using a while loop and using the hashNext()

                Scanner sc = new Scanner(conn.getInputStream());

                // read and storethe resulting json data into our string builder
                // if there is json data yo be read, then we store it into our resultJson String
                while (sc.hasNext()) {
                    resultsJson.append(sc.nextLine());

                }

                // close scanner
                sc.close();

                // close url connection
                conn.disconnect();

                // parse the JS0N string into a JSON obj (parse is json word mean convert)
                // the reason why we are parsing is so that we can access the data more properly

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultsJson));

                // get the list of location data the API gtenerated from the Icoation name
                // when we are trying to get the "results" it will return us [data]
                // which is why we store it in a JSONArray
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // could not find location
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            // attempt to create a connection
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to get the response
            conn.setRequestMethod("GET");

            // connect to our API
            conn.connect();

            return conn;
        } catch (IOException e) {
            e.printStackTrace();

        }

        // could not make any connection
        return null;

    }

    private static int findIndexCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();

        // ittereate through the time list and see which one match our current time
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)) {
                // return the index value
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime() {
        // get current date and time
        LocalDateTime currenDateTime = LocalDateTime.now();

        // format date to be 2023-09-02T00:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'Y'HH':00'");

        // print the current date and time
        String formatDateTime = currenDateTime.format(formatter);

        return formatDateTime;
    }

    private static String convertWeatherCode(long weatherCode) {

        String weatherCondition = "";
        if (weatherCode == 0L) {
            // clear
            weatherCondition = "Clear";
        } else if (weatherCode > 0L && weatherCode <= 3L) {
            // cloudy
            weatherCondition = "Cloudy";
        } else if (weatherCode > 57L && weatherCode <= 67L) {
            // rainy
            weatherCondition = "Rainy";
        } else if (weatherCode > 71L && weatherCode <= 77L) {
            // snow
            weatherCondition = "Snow";
        }
        return weatherCondition;

    }

}
