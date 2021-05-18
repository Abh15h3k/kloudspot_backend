package com.example.project;

import com.example.project.Models.Dao.MyUserRepository;
import com.example.project.Models.Dao.VehicleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProjectRunner implements CommandLineRunner {

    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        // myUserRepository.deleteAll();
        // vehicleRepository.deleteAll();

        // MyUser myUser = new MyUser("1234", "user@project.com", "pass", new DriverLicense("123456", null),
        //         Arrays.asList(UserRole.USER), AccountStatus.ACTIVE, null, null, new ArrayList<>());
        // MyUser myAdmin = new MyUser("1235", "admin@project.com", "pass", new DriverLicense("123457", null),
        //         Arrays.asList(UserRole.ADMIN), AccountStatus.ACTIVE, null, null, new ArrayList<>());

        // Vehicle vehicle1 = new Vehicle("ka01mh1268", new GeoLocation(10.2, 15.2), new ArrayList<>(), "CAR", null, new ArrayList<>());
        // Vehicle vehicle2 = new Vehicle("ka01mh1355", new GeoLocation(10.2, 15.2), new ArrayList<>(), "BIKE", null, new ArrayList<>());

        // myUserRepository.save(myAdmin);
        // myUserRepository.save(myUser);

        // vehicleRepository.save(vehicle1);
        // vehicleRepository.save(vehicle2);
    }
}
