package branded.weather.widget;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import java.util.Random;
import javafx.scene.input.KeyEvent;

public class Main extends Application {
    
    // UI Components
    private Label cityLabel;
    private Label temperatureLabel;
    private Label conditionLabel;
    private Label uvIndexLabel;
    private Label commuteLabel;
    private TextField cityInput;
    private Circle radarCircle;
    private HBox forecastBox;
    
    private Random random = new Random();
    
    @Override
    public void start(Stage primaryStage) {
        // ========== 1. CREATE BORDERPANE (REQUIREMENT) ==========
        BorderPane root = new BorderPane();
        
        // ========== 2. TOP SECTION: City Name Heading ==========
        VBox topSection = createTopSection();
        root.setTop(topSection);
        
        // ========== 3. CENTER SECTION: Weather Data ==========
        HBox centerSection = createCenterSection();
        root.setCenter(centerSection);
        
        // ========== 4. BOTTOM SECTION: Forecast ==========
        VBox bottomSection = createBottomSection();
        root.setBottom(bottomSection);
        
        // ========== 5. CREATE SCENE AND SHOW ==========
        Scene scene = new Scene(root, 750, 600);
        
        // Apply inline styles
        applyStyles(root);
        
        primaryStage.setTitle("Urban Pulse Labs - Weather Widget");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Show initial data
        updateWeatherForCity("NEW YORK");
    }
    
    private VBox createTopSection() {
        cityLabel = new Label("URBAN PULSE LABS");
        cityLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: #ECF0F1;");
        
        cityInput = new TextField();
        cityInput.setPromptText("Enter city name (letters only)...");
        cityInput.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 250;");
        
        // ====== RESTRICT INPUT: ONLY LETTERS AND SPACES ======
        cityInput.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            // Allow only: letters (A-Z, a-z), space, hyphen, apostrophe for city names
            if (!character.matches("[A-Za-z\\s\\-' ]")) {
                event.consume(); // Block the input
                showInputError("Only letters, spaces, hyphens and apostrophes allowed");
            }
        });
        
        Button refreshBtn = new Button("UPDATE WEATHER");
        refreshBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10 20;");
        
        // REQUIREMENT: Disable button if TextField is empty
        refreshBtn.disableProperty().bind(Bindings.isEmpty(cityInput.textProperty()));
        
        refreshBtn.setOnAction(e -> {
            String city = cityInput.getText().trim();
            if (!city.isEmpty() && isValidCityName(city)) {
                updateWeatherForCity(city);
                cityInput.clear();
            } else {
                showInputError("Please enter a valid city name");
            }
        });
        
        HBox inputBox = new HBox(15, cityInput, refreshBtn);
        inputBox.setAlignment(Pos.CENTER);
        
        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 10 0;");
        
        VBox topBox = new VBox(10, cityLabel, separator, inputBox);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(20));
        topBox.setStyle("-fx-background-color: #2C3E50;");
        
        return topBox;
    }
    
    private boolean isValidCityName(String city) {
        // Validate city name: only letters, spaces, hyphens, apostrophes
        // Must have at least 2 characters
        // Must start with a letter
        if (city.length() < 2) return false;
        if (!Character.isLetter(city.charAt(0))) return false;
        
        // Check each character
        for (char c : city.toCharArray()) {
            if (!Character.isLetter(c) && c != ' ' && c != '-' && c != '\'') {
                return false;
            }
        }
        
        // Must contain at least one letter
        boolean hasLetter = false;
        for (char c : city.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
                break;
            }
        }
        
        return hasLetter;
    }
    
    private void showInputError(String message) {
        // Create a temporary error alert
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Invalid Input");
        alert.setHeaderText("City Name Error");
        alert.setContentText(message);
        alert.showAndWait();
        
        // Flash the text field red
        cityInput.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-border-color: #E74C3C; -fx-border-width: 2;");
        
        // Reset after 1 second
        new javafx.animation.PauseTransition(Duration.seconds(1)).play();
        new javafx.animation.PauseTransition(Duration.seconds(1)).setOnFinished(event -> {
            cityInput.setStyle("-fx-background-color: #34495E; -fx-text-fill: white; -fx-border-color: #3498DB;");
        });
    }
    
    private HBox createCenterSection() {
        // Left side: Temperature and condition
        temperatureLabel = new Label("72°F");
        temperatureLabel.setStyle("-fx-font-size: 72px; -fx-font-weight: 900; -fx-text-fill: #ECF0F1;");
        
        conditionLabel = new Label("SUNNY");
        conditionLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: #BDC3C7; -fx-font-weight: 600;");
        
        // Urban Pulse Theme: Radar Circle (REQUIREMENT: Relevant Shape)
        radarCircle = new Circle(50);
        radarCircle.setStyle("-fx-fill: rgba(52, 152, 219, 0.2); -fx-stroke: #3498DB; -fx-stroke-width: 2;");
        
        // REQUIREMENT: Add animation
        startRadarAnimation();
        
        VBox weatherBox = new VBox(15, temperatureLabel, conditionLabel, new StackPane(radarCircle));
        weatherBox.setAlignment(Pos.CENTER);
        weatherBox.setPadding(new Insets(30));
        
        // Right side: Urban metrics
        uvIndexLabel = new Label("UV INDEX: 6 (HIGH)");
        uvIndexLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #F39C12; -fx-font-weight: bold;");
        
        commuteLabel = new Label("COMMUTE: MODERATE");
        commuteLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2ECC71; -fx-font-weight: bold;");
        
        Label windLabel = new Label("WIND: 12 mph NW");
        windLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #BDC3C7;");
        
        // Building silhouette (Urban theme)
        HBox buildings = new HBox(5);
        buildings.setAlignment(Pos.BOTTOM_CENTER);
        Rectangle[] buildingRects = {
            new Rectangle(15, 40), new Rectangle(20, 60),
            new Rectangle(25, 80), new Rectangle(20, 55),
            new Rectangle(15, 35)
        };
        for (Rectangle r : buildingRects) {
            r.setStyle("-fx-fill: #34495E; -fx-stroke: #3498DB; -fx-stroke-width: 1;");
            buildings.getChildren().add(r);
        }
        
        VBox metricsBox = new VBox(20, uvIndexLabel, commuteLabel, windLabel, buildings);
        metricsBox.setAlignment(Pos.CENTER);
        metricsBox.setPadding(new Insets(30));
        metricsBox.setStyle("-fx-background-color: rgba(52, 73, 94, 0.6); -fx-background-radius: 15;");
        
        HBox centerBox = new HBox(40, weatherBox, metricsBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));
        
        return centerBox;
    }
    
    private VBox createBottomSection() {
        forecastBox = new HBox(25);
        forecastBox.setAlignment(Pos.CENTER);
        
        Label forecastTitle = new Label("3-DAY URBAN FORECAST");
        forecastTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: #3498DB; -fx-font-weight: bold;");
        
        // Create initial forecast
        updateForecast("NEW YORK");
        
        VBox bottomBox = new VBox(15, forecastTitle, forecastBox);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(25));
        bottomBox.setStyle("-fx-background-color: rgba(52, 73, 94, 0.5);");
        
        return bottomBox;
    }
    
    private void updateWeatherForCity(String cityName) {
        // Validate city name again
        if (!isValidCityName(cityName)) {
            showInputError("Invalid city name. Please use only letters, spaces, hyphens and apostrophes.");
            return;
        }
        
        // Update city label
        cityLabel.setText(cityName.toUpperCase());
        
        // Generate weather based on city name
        int hash = cityName.toUpperCase().hashCode();
        Random cityRandom = new Random(hash);
        
        // Temperature
        int temperature = 50 + Math.abs(hash % 40); // 50-90°F
        temperatureLabel.setText(temperature + "°F");
        
        // Condition
        String[] conditions = {"SUNNY", "PARTLY CLOUDY", "CLOUDY", "RAINY", "CLEAR", "THUNDERSTORMS"};
        String condition = conditions[Math.abs(hash % conditions.length)];
        conditionLabel.setText(condition);
        
        // Urban Pulse Enhancements:
        // 1. UV Index
        int uvIndex = Math.abs(hash % 11); // 0-10
        String uvLevel;
        if (uvIndex <= 2) uvLevel = "LOW";
        else if (uvIndex <= 5) uvLevel = "MODERATE";
        else if (uvIndex <= 7) uvLevel = "HIGH";
        else uvLevel = "VERY HIGH";
        uvIndexLabel.setText("UV INDEX: " + uvIndex + " (" + uvLevel + ")");
        
        // 2. Commute Impact
        String commute;
        if (condition.equals("RAINY") || condition.equals("THUNDERSTORMS")) {
            commute = "HEAVY DELAYS";
        } else if (condition.equals("CLOUDY")) {
            commute = "MODERATE";
        } else {
            commute = "GOOD";
        }
        commuteLabel.setText("COMMUTE: " + commute);
        
        // Update forecast
        updateForecast(cityName);
    }
    
    private void updateForecast(String cityName) {
        forecastBox.getChildren().clear();
        
        String[] days = {"TODAY", "TOMORROW", "FRI"};
        int hash = cityName.toUpperCase().hashCode();
        Random cityRandom = new Random(hash);
        
        for (int i = 0; i < 3; i++) {
            VBox dayCard = createForecastCard(
                days[i],
                60 + cityRandom.nextInt(20), // Temperature 60-80°F
                cityRandom,
                i // Day index for temperature trend
            );
            forecastBox.getChildren().add(dayCard);
        }
    }
    
    private VBox createForecastCard(String day, int temp, Random cityRandom, int dayIndex) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #34495E; -fx-background-radius: 10; -fx-border-color: #3498DB; -fx-border-width: 1;");
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #3D566E; -fx-background-radius: 10; -fx-border-color: #E74C3C; -fx-border-width: 1;");
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: #34495E; -fx-background-radius: 10; -fx-border-color: #3498DB; -fx-border-width: 1;");
        });
        
        Label dayLabel = new Label(day);
        dayLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3498DB;");
        
        // Create realistic temperature range (high/low)
        int highTemp = temp + cityRandom.nextInt(5);
        int lowTemp = temp - 5 - cityRandom.nextInt(5);
        Label tempLabel = new Label(highTemp + "° / " + lowTemp + "°");
        tempLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: 900; -fx-text-fill: white;");
        
        // Condition based on temperature
        String condition;
        String icon;
        if (temp < 45) {
            condition = "COLD";
            icon = "❄️";
        } else if (temp > 85) {
            condition = "HOT";
            icon = "🔥";
        } else {
            String[] conditions = {"SUNNY", "PARTLY CLOUDY", "CLOUDY", "RAINY"};
            condition = conditions[cityRandom.nextInt(conditions.length)];
            switch(condition) {
                case "SUNNY": icon = "☀️"; break;
                case "PARTLY CLOUDY": icon = "⛅"; break;
                case "CLOUDY": icon = "☁️"; break;
                case "RAINY": icon = "🌧️"; break;
                default: icon = "⛅";
            }
        }
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label condLabel = new Label(condition);
        condLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #BDC3C7;");
        
        // Transportation suggestion (Urban Pulse enhancement)
        String suggestion = getTransportationSuggestion(condition, highTemp, lowTemp);
        Label suggestLabel = new Label(suggestion);
        suggestLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #2ECC71; -fx-font-style: italic;");
        
        card.getChildren().addAll(dayLabel, tempLabel, iconLabel, condLabel, suggestLabel);
        return card;
    }
    
    private String getTransportationSuggestion(String condition, int highTemp, int lowTemp) {
        if (condition.equals("RAINY") || condition.equals("THUNDERSTORMS")) {
            return "→ Use subway";
        } else if (condition.equals("SUNNY") && highTemp > 75) {
            return "→ Bike recommended";
        } else if (lowTemp < 40) {
            return "→ Car recommended";
        } else if (condition.equals("CLOUDY")) {
            return "→ Bus optimal";
        } else {
            return "→ Walk/Bike";
        }
    }
    
    private void startRadarAnimation() {
        // REQUIREMENT: Subtle animation relevant to company theme
        ScaleTransition scale = new ScaleTransition(Duration.seconds(3), radarCircle);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        
        RotateTransition rotate = new RotateTransition(Duration.seconds(6), radarCircle);
        rotate.setFromAngle(0);
        rotate.setToAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        
        ParallelTransition anim = new ParallelTransition(scale, rotate);
        anim.play();
    }
    
    private void applyStyles(BorderPane root) {
        // Apply base style
        root.setStyle("-fx-background-color: #2C3E50;");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}