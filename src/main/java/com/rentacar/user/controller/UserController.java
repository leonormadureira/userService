package com.rentacar.user.controller;

import com.rentacar.user.domain.User;
import com.rentacar.user.dto.CarDto;
import com.rentacar.user.service.serviceImplementation.UserServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class UserController {

    public final UserServiceImplementation service;

    @Autowired
    public UserController(UserServiceImplementation service){this.service = service;}

    @RequestMapping(method = RequestMethod.GET, path = "/userService")
    public List<CarDto> getCarsCurrentlyRentedByUserId(Long user_id){

        return service.getCarsCurrentlyRentedByUserId(user_id);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/userService")
    public User createNewUser(String firstName, String lastName, Long nif){

        return service.createNewUser(firstName,lastName,nif);

    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/userService")
    public ResponseEntity deleteUser (Long user_id){

        service.deleteUser(user_id);

        return ResponseEntity.ok(HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/userService/rentCar")
    public CarDto rentCar (Long user_id) throws IOException {

        return service.rentCar(user_id);

    }

    @RequestMapping(method = RequestMethod.PUT, path = "/userService/releaseCar")
    public CarDto releaseCar (Long user_id) throws IOException {

        return service.releaseCar(user_id);
    }

}
