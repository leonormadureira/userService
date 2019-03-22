package com.rentacar.user.controller;

import com.rentacar.user.domain.User;
import com.rentacar.user.dto.InputBody;
import com.rentacar.user.dto.CarDto;
import com.rentacar.user.service.serviceImplementation.UserServiceImplementation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Data
@RestController
public class UserController {

    public final UserServiceImplementation service;

    @Autowired
    public UserController(UserServiceImplementation service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/userService")
    public List<CarDto> getCarsCurrentlyRentedByUserId(@RequestBody InputBody requestBody) {

        return service.getCarsCurrentlyRentedByUserId(requestBody.getUser_id());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/userService")
    public User createNewUser(@RequestBody InputBody requestBody) {

        return service.createNewUser(requestBody.getFirst_name(), requestBody.getLast_name(), requestBody.getNif());

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/userService")
    public ResponseEntity deleteUser(@RequestBody InputBody requestBody) {

        service.deleteUser(requestBody.getUser_id());

        return ResponseEntity.ok(HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/userService/rentCar")
    public CarDto rentCar(@RequestBody InputBody requestBody) throws IOException  {

        return service.rentCar(requestBody.getUser_id(), requestBody.getCar_id());

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/userService/releaseCar")
    public CarDto releaseCar(@RequestBody InputBody requestBody) throws IOException {

        return service.releaseCar(requestBody.getUser_id());
    }

}
