package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Car {
    public String registrationNo;

    public Car(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }
}
class ParkingSpot{
    private int spotNumber;
    private boolean available;
    private Car car;
    public ParkingSpot(int spotNumber){
        this.spotNumber=spotNumber;
        this.available=true;
        this.car=null;
    }

    public int getSpotNumber() {
        return spotNumber;
    }
    public Car getCar(){
        return car;
    }
    public void occupy(Car car) {
        this.car=car;
        this.available=false;
    }
    public void vacate(){
        this.car=null;
        this.available=true;
    }

    public boolean isAvailable() {
        return available;
    }
}
class ParkingLot extends ParkingMng{
    private List<ParkingSpot> spots;
    public ParkingLot(int cap){
        this.spots=new ArrayList<>();
        for(int i=0;i<cap;i++){
            spots.add(new ParkingSpot(i));
        }
    }
    public void parkCar(Car car) throws SQLException{
        for(ParkingSpot spot :spots){
            if(spot.isAvailable()){
                String query ="Insert into parkingData(vehicle,spotNo) values(?,?)";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,car.getRegistrationNo());
                preparedStatement.setInt(2,spot.getSpotNumber());
                preparedStatement.executeUpdate();
                spot.occupy(car);
                System.out.println("Car with "+car.getRegistrationNo() +"is Parked at "+spot.getSpotNumber());
                return;
            }
        }
        System.out.println("Parking is Full");
    }
    public boolean removeCar(String licencePlate) throws SQLException {
        for(ParkingSpot spot:spots) {
            if (!spot.isAvailable() && spot.getCar().getRegistrationNo().equalsIgnoreCase(licencePlate)) {
                spot.vacate();
                String query="Delete from parkingData where vehicle=?";
                preparedStatement=connection.prepareStatement(query);
                preparedStatement.setString(1,licencePlate);
                int n=preparedStatement.executeUpdate();
                System.out.println("Car with number " + licencePlate + " removed from parking " + spot.getSpotNumber());
                return true;
            }
        }
            System.out.println("Car with " +licencePlate+ "not found");
            return false;
    }


}
public class ParkingMng{
    private static final String JDBC_url="jdbc:mysql://localhost:3306/spark";
    private static final String USERNAME="admin";
    private static final String Password="Admin123@";
    public static Connection connection;
    public static PreparedStatement preparedStatement;

    public static void main(String[] args) {
        try {
            connection= DriverManager.getConnection(JDBC_url,USERNAME,Password);
            String query="Create table if not exists parkingData(id INT AUTO_INCREMENT PRIMARY KEY,"+
                    " vehicle varchar(30),"
                    +"spotNo int"
                    +")";
            preparedStatement=connection.prepareStatement(query);
            preparedStatement.execute();
            System.out.println("Table created");
            ParkingLot parkingLot=new ParkingLot(5);
            Car car1=new Car("UP50 1122");
            Car car2=new Car("UP50 1234");
            parkingLot.parkCar(car1);
//            parkingLot.parkCar(car2);
//            parkingLot.removeCar("UP50 1122");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
