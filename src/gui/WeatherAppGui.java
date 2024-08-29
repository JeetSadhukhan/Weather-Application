package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.json.simple.JSONObject;
import backend.WeatherApp;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;

    public WeatherAppGui() {
        // setup our gui and the title
        super("Weather App");

        // set the size of the gui
        setSize(450, 650);

        // load gui at the center of the screen
        setLocationRelativeTo(null);

        // configure the gui to end the program
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // prevent any resize of our gui
        setResizable(false);

        // disable the by default layout of swing
        setLayout(null);

        // component
        addGuiComponent();
    }

    private void addGuiComponent() {
        // search field
        JTextField searchTextField = new JTextField();
        searchTextField.setBounds(15, 15, 351 , 45);
        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));


        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(0, 125, 450, 217);

        // temperature text
        JLabel temperatureText = new JLabel("10 c");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD,48));
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        // weather condition
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 405, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);

        // humidity image
        JLabel humidityImage = new JLabel(loadImage("src\\assets\\humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);

        // humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));

        // windspeed image
        JLabel windspeedImage = new JLabel(loadImage("src\\assets\\windspeed.png"));
        windspeedImage.setBounds(220, 500, 74, 66);

        // windspeed text
        JLabel windspeedText = new JLabel("<html><b>Humidity</b> 15km/h</html>");
        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));

         // search button
        JButton searchButton = new JButton(loadImage("src\\assets\\search.png"));
        searchButton.setBounds(375, 13, 47, 45);
        searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();
                
                // vaildate input - remove whites space
                // example : kol kata
                if(userInput.replaceAll("\\s", " ").length()<= 0){
                    return;
                }
                
                //retrive weather data 
                weatherData = WeatherApp.getWeatherData(userInput);
                

                // ...............update................

                //update weather image
                String weatherCondition = (String)weatherData.get("weather Application");

                // depanding on the condition, we will  update the weather image
                //with the condition
                switch(weatherCondition){
                    case "Clear":
                    weatherConditionImage.setIcon(loadImage("src\\assets\\clear.png"));
                    break;
                    case "Cloudy":
                    weatherConditionImage.setIcon(loadImage("src\\assets\\cloudy.png"));
                    break;
                    case "Rain":
                    weatherConditionImage.setIcon(loadImage("src\\assets\\rain.png"));
                    break;
                    case "Snow":
                    weatherConditionImage.setIcon(loadImage("src\\assets\\snow.png"));
                    break;
                }

                // update temperature text

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                // update weather condition

                weatherConditionDesc.setText(weatherCondition);

                //update humidity text

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                //update windSpeed text

                long windspeed = (long) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");



            
                


            }

        });


        // add the components
        add(searchTextField);
        add(searchButton);
        add(weatherConditionImage);
        add(temperatureText);
        add(weatherConditionDesc);
        add(humidityText);
        add(humidityImage);
        add(windspeedImage);
        add(windspeedText);


       

    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            // read the image file from the path given
            BufferedImage image = ImageIO.read(new File(resourcePath));

            // return an image icon so that our component can render it
            return new ImageIcon(image);

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Could not find resource");

        return null;

    }

}

