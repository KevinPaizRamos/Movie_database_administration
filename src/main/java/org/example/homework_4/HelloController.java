package org.example.homework_4;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HelloController {
    /**
     * initialize method used to create the database and table, commented for now will probably be used later
     */
    @FXML
    private TextField salesTF;

    @FXML
    private TextField titleTF;

    @FXML
    private TextField yearTF;

    @FXML
    private TableColumn<Movie, Integer> tableColumYear;

    @FXML
    private TableColumn<Movie, Double> tableColumnSales;

    @FXML
    private TableColumn<Movie, String> tableColumnTittle;

    @FXML
    private TableView tableViewMovies;
    @FXML
    private Label statusLabel;




public void initialize() {
        tableColumnTittle.setCellValueFactory(new PropertyValueFactory<Movie, String>("title"));
        tableColumYear.setCellValueFactory(new PropertyValueFactory<Movie, Integer>("year"));
        tableColumnSales.setCellValueFactory(new PropertyValueFactory<Movie, Double>("sales"));

    ObservableList<Movie> movies = tableViewMovies.getItems();

    }


    //***************************************************************
    private  void createDataBase(){
        String dbFilePath= ".//MovieDB.accdb";
        String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
        File dbFile = new File(dbFilePath);
        if (!dbFile.exists()) {
            try (Database db =
                         DatabaseBuilder.create(Database.FileFormat.V2010, new File(dbFilePath))) {
                System.out.println("The database file has been created.");
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }
    }//End of createDataBase

    public void addRecord(){
        String title = titleTF.getText();
        String year = yearTF.getText();
        String sales = salesTF.getText();

        addRecord(title, year, sales);

        statusLabel.setText("A movie has been inserted: "+title);
        statusLabel.setVisible(true);

    }//End of addRecord

    public void addRecord(String title, String year, String sales) {
        String titleError = Validation.validateTittle(title);
        String yearError = Validation.validateYear(year);
        String salesError = Validation.validateSales(sales);

        if (!titleError.isEmpty() || !yearError.isEmpty() || !salesError.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Please correct the following errors:");
            alert.setContentText(titleError + "\n" + yearError + "\n" + salesError);
            alert.showAndWait();
        } else {
            try {

                Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
                String sql = "INSERT INTO movie (Title, Year, Sales) VALUES (?, ?, ?)";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, title);
                preparedStatement.setInt(2, Integer.parseInt(year));
                preparedStatement.setDouble(3, Double.parseDouble(sales));
                preparedStatement.executeUpdate();
                Movie movie = new Movie(title, Integer.parseInt(year), Double.parseDouble(sales));
                tableViewMovies.getItems().add(movie);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }
    public void deleteRecord(){
        Movie movie = (Movie) tableViewMovies.getSelectionModel().getSelectedItem();
        if (movie != null) {
            String title = movie.getTitle();
            String year = String.valueOf(movie.getYear());
            String sales = String.valueOf(movie.getSales());
            try {
                Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
                String sql = "DELETE FROM movie WHERE Title = ? AND Year = ? AND Sales = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, title);
                preparedStatement.setInt(2, Integer.parseInt(year));
                preparedStatement.setDouble(3, Double.parseDouble(sales));
                preparedStatement.executeUpdate();
                tableViewMovies.getItems().remove(movie);
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        statusLabel.setText("A movie has been deleted: "+movie.getTitle());
        statusLabel.setVisible(true);
    }

    public void createTable(){
        try {
            String dbFilePath= ".//MovieDB.accdb";
            String databaseURL = "jdbc:ucanaccess://" + dbFilePath;
            Connection conn = DriverManager.getConnection(databaseURL);
            dropTable();

            String sql;
            sql = "CREATE TABLE movie (Title nvarchar(225), Year INT, Sales DOUBLE)";
            Statement createTableStatement = conn.createStatement();
            createTableStatement.execute(sql);
            conn.commit();
            System.out.println("Table created successfully");
        } catch (
                SQLException sqlException) {
            sqlException.printStackTrace();
        }
         statusLabel.setText("Table created successfully");
        statusLabel.setVisible(true);
    }//End of createTable

    private  void dropTable(){
        String sql = "DROP TABLE movie";
        PreparedStatement preparedStatement = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
            preparedStatement = conn.prepareStatement(sql);
            int rowsDeleted = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }//end of dropTable

  public void importJSON(){
        File file = OpenFileDialog();

if(file != null){
    loadJsonToDB(file);
    tableViewMovies.getItems().clear();
    try {
        Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movie");
        while (resultSet.next()) {
            String title = resultSet.getString("Title");
            int year = resultSet.getInt("Year");
            double sales = resultSet.getDouble("Sales");
            Movie movie = new Movie(title, year, sales);
            tableViewMovies.getItems().add(movie);
        }
    } catch (SQLException sqlException) {
        sqlException.printStackTrace();
    }

}
statusLabel.setText("import data from "+file.getAbsolutePath());
statusLabel.setVisible(true);

  }

  public void ExportJSON() {
      FileChooser fileChooser = new FileChooser();
      FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
      fileChooser.getExtensionFilters().add(extFilter);
      fileChooser.setInitialDirectory(new File("."));
      File file = fileChooser.showSaveDialog(null);


      if (file != null) {
          try {
              List<Movie> movies = new ArrayList<>();
              Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
              Statement statement = conn.createStatement();
              ResultSet resultSet = statement.executeQuery("SELECT * FROM movie");
              while (resultSet.next()) {
                  String title = resultSet.getString("Title");
                  int year = resultSet.getInt("Year");
                  double sales = resultSet.getDouble("Sales");
                  Movie movie = new Movie(title, year, sales);
                  movies.add(movie);
              }


              Gson gson = new GsonBuilder().setPrettyPrinting().create();
              String jsonString = gson.toJson(movies);

              try (FileWriter fileWriter = new FileWriter(file)) {
                  fileWriter.write(jsonString);
              }

          } catch (SQLException | IOException sqlException) {
              sqlException.printStackTrace();
          }
      }
        statusLabel.setText("Export data to "+file.getAbsolutePath());
        statusLabel.setVisible(true);
  }



  public void ListRecords() {
        tableViewMovies.getItems().clear();
        ///List<Movie> movies = new ArrayList<>();
      try {
          Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
          Statement statement = conn.createStatement();
          ResultSet resultSet = statement.executeQuery("SELECT * FROM movie");
          while (resultSet.next()) {
              String title = resultSet.getString("Title");
              int year = resultSet.getInt("Year");
              double sales = resultSet.getDouble("Sales");
                Movie movie = new Movie(title, year, sales);
                tableViewMovies.getItems().add(movie);
          }
      } catch (SQLException sqlException) {
          sqlException.printStackTrace();
      }
      statusLabel.setText("Movie table displayed");
        statusLabel.setVisible(true);
  }

private void loadJsonToDB(File file) {
    try (FileReader fr = new FileReader(file)) {
        cleartable();

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Movie[] m = gson.fromJson(fr, Movie[].class);

        for (Movie movie : m) {
            String title = movie.getTitle();
            int year = movie.getYear();
            double sales = movie.getSales();
            String sql = "INSERT INTO movie (Title, Year, Sales) VALUES (?, ?, ?)";

            try {
                Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
                PreparedStatement insertStatement = conn.prepareStatement(sql);
                insertStatement.setString(1, title);
                insertStatement.setInt(2, year);
                insertStatement.setDouble(3, sales);
                insertStatement.executeUpdate();
                conn.commit();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
private void cleartable() {
    String sql = "DELETE FROM movie";
    PreparedStatement preparedStatement = null;
    try {
        Connection conn = DriverManager.getConnection("jdbc:ucanaccess://.//MovieDB.accdb");
        preparedStatement = conn.prepareStatement(sql);
        int rowsDeleted = preparedStatement.executeUpdate();
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }
}
    private File OpenFileDialog () {
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setInitialDirectory(new File("."));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            System.out.println("File selected: " + file.getAbsolutePath());
            return file;
        } else {
            System.out.println("File selection cancelled.");
            return null;
        }
    }
    public void help(){
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("About Movie Database");
    alert.setHeaderText("Name and Integrity Statement");
    alert.setContentText("Kevin Paiz Ramos\n I certify that this submission is my own original work. ");
    alert.showAndWait();
    }
    public void exit(){
    System.exit(0);
    }
}//end of HelloController



