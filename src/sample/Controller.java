package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextField firstName;
    public TextField lastName;
    public TextField email;
    public TextField phoneNumber;
    public TextField employeeId;
    public ComboBox<Integer> restaurantId;
    public ComboBox<Integer> positionId;
    public ComboBox<String> city;
    public TextField district;
    public TextField street;
    public TextField building;
    public TextField gate;
    public TextField floorStory;
    public TextField door;
    public TableView<ModelTable> table;
    public TableColumn<ModelTable, String> col_employeeId;
    public TableColumn<ModelTable, String> col_restaurantId;
    public TableColumn<ModelTable, String> col_firstName;
    public TableColumn<ModelTable, String> col_lastName;
    public TableColumn<ModelTable, String> col_email;
    public TableColumn<ModelTable, String> col_phoneNumber;
    public TableColumn<ModelTable, String> col_hireDate;
    public TableColumn<ModelTable, String> col_positionId;
    public TableColumn<ModelTable, String> col_addressId;


    ObservableList<ModelTable> observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table();
    }

    public void hire() {
        reFilled();
        boolean filled = true;

        if (restaurantId.getSelectionModel().isEmpty()) {
            filled = notFilled(restaurantId);
        }
        if (firstName.getText().isEmpty()) {
            filled = notFilled(firstName);
        }
        if (lastName.getText().isEmpty()) {
            filled = notFilled(lastName);
        }
        if (email.getText().isEmpty()) {
            filled = notFilled(email);
        }
        if (phoneNumber.getText().isEmpty()) {
            filled = notFilled(phoneNumber);
        }
        if (positionId.getSelectionModel().isEmpty()) {
            filled = notFilled(positionId);
        }
        if (city.getSelectionModel().isEmpty()) {
            filled = notFilled(city);
        }
        if (district.getText().isEmpty()) {
            filled = notFilled(district);
        }
        if (street.getText().isEmpty()) {
            filled = notFilled(street);
        }
        if (building.getText().isEmpty()) {
            filled = notFilled(building);
        }
        if (gate.getText().isEmpty()) {
            filled = notFilled(gate);
        }
        if (floorStory.getText().isEmpty()) {
            filled = notFilled(floorStory);
        }
        if (door.getText().isEmpty()) {
            filled = notFilled(door);
        }

        if (filled) {
            try {
                reFilled();
                Connection connection= Connect.getConnection();
                assert connection != null;

                PreparedStatement addAddress = connection.prepareStatement("INSERT INTO AddressData VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
                addAddress.setObject(1, city.getValue());
                addAddress.setObject(2, city.getValue());
                addAddress.setObject(3, district.getText());
                addAddress.setObject(4, street.getText());
                addAddress.setObject(5, building.getText());
                addAddress.setObject(6, gate.getText());
                addAddress.setObject(7, floorStory.getText());
                addAddress.setObject(8, door.getText());
                addAddress.execute();
                int addressId;

                try (ResultSet generatedKeys = addAddress.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        addressId = generatedKeys.getInt(1);
                    }
                    else {
                        throw new SQLException("Fail");
                    }
                }
                CallableStatement hire = connection.prepareCall("{call hire(?, ?, ?, ?, ?, ?, ?)}");
                hire.setObject(1, restaurantId.getValue());
                hire.setObject(2, firstName.getText());
                hire.setObject(3, lastName.getText());
                hire.setObject(4, email.getText());
                hire.setObject(5, phoneNumber.getText());
                hire.setObject(6, positionId.getValue());
                hire.setObject(7, addressId);
                hire.execute();
                refresh();
                removeText();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void raise() {
        reFilled();
        boolean filled = true;

        if (employeeId.getText().isEmpty()) {
            filled = notFilled(employeeId);
        }
        if (positionId.getSelectionModel().isEmpty()) {
            filled = notFilled(positionId);
        }

        if (filled) {
            try {
                reFilled();
                Connection connection= Connect.getConnection();
                assert connection != null;

                CallableStatement raise = connection.prepareCall("{call raise(?, ?)}");
                raise.setObject(1, employeeId.getText());
                raise.setObject(2, positionId.getValue());
                raise.execute();
                refresh();
                removeText();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void fire() {
        reFilled();
        boolean filled = true;

        if (employeeId.getText().isEmpty()) {
            filled = notFilled(employeeId);
        }

        if (filled) {
            try {
                reFilled();
                Connection connection= Connect.getConnection();
                assert connection != null;

                CallableStatement fire = connection.prepareCall("{call fire(?)}");
                fire.setObject(1, employeeId.getText());
                fire.execute();
                refresh();
                removeText();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void table() {
        try {
            Connection connection= Connect.getConnection();
            assert connection != null;
            ResultSet resultSet = connection.createStatement().executeQuery("select * from Employee");

            while (resultSet.next()) {
                observableList.add(new ModelTable(resultSet.getString("employeeId"), resultSet.getString("restaurantId"),
                        resultSet.getString("firstName"), resultSet.getString("lastName"),
                        resultSet.getString("email"), resultSet.getString("phoneNumber"),
                        resultSet.getString("hireDate"), resultSet.getString("positionId"), resultSet.getString("addressId")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        col_employeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        col_restaurantId.setCellValueFactory(new PropertyValueFactory<>("restaurantId"));
        col_firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        col_lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        col_email.setCellValueFactory(new PropertyValueFactory<>("email"));
        col_phoneNumber.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        col_hireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        col_positionId.setCellValueFactory(new PropertyValueFactory<>("positionId"));
        col_addressId.setCellValueFactory(new PropertyValueFactory<>("addressId"));

        table.setItems(observableList);
    }

    public void refresh() {
        table.getItems().clear();
        table();
    }

    public boolean notFilled(Control c) {
        c.setStyle("-fx-border-color: red; -fx-border-radius: 3px");
        return false;
    }

    public void reFilled() {
        restaurantId.setStyle(null);
        firstName.setStyle(null);
        lastName.setStyle(null);
        email.setStyle(null);
        phoneNumber.setStyle(null);
        positionId.setStyle(null);
        city.setStyle(null);
        district.setStyle(null);
        street.setStyle(null);
        building.setStyle(null);
        gate.setStyle(null);
        floorStory.setStyle(null);
        door.setStyle(null);
        employeeId.setStyle(null);
    }

    public void removeText() {
        restaurantId.setValue(null);
        firstName.clear();
        lastName.clear();
        email.clear();
        phoneNumber.clear();
        positionId.setValue(null);
        city.setValue(null);
        district.clear();
        street.clear();
        building.clear();
        gate.clear();
        floorStory.clear();
        door.clear();
        employeeId.clear();
    }
}
