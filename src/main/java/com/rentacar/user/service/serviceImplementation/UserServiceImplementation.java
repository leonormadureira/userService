package com.rentacar.user.service.serviceImplementation;

import com.google.gson.Gson;
import com.rentacar.user.domain.User;
import com.rentacar.user.dto.CarDto;
import com.rentacar.user.dto.InputBody;
import com.rentacar.user.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Data
@Service
@Slf4j
public class UserServiceImplementation {

    private final UserRepository user_repository;
    private RestTemplate restTemplate;

    @Value(value = "${car-service.host}")
    private String host;

    @Value(value = "${car-service.port}")
    private String port;


    @Autowired
    public UserServiceImplementation(UserRepository user_repository){this.user_repository = user_repository;}


    public List<CarDto> getCarsCurrentlyRentedByUserId(Long user_id){

        String url = "http://"+host+":"+port+"/carService/searchCars?user_id="+user_id;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<CarDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarDto>>(){});

        return response.getBody();
    }

    public User createNewUser(String firstName, String lastName, Long nif){

        User newUser = new User();

        newUser.setFirst_name(firstName);
        newUser.setLast_name(lastName);
        newUser.setNif(nif);

        return user_repository.save(newUser);

    }

    public void deleteUser (Long user_id){

        User userToDelete = user_repository.findUserByUserId(user_id);

        user_repository.delete(userToDelete);
    }

    public CarDto rentCar (Long user_id, Long car_id) throws IOException {

        CarDto inputBody = new CarDto();

        inputBody.setId(car_id);
        inputBody.setUser_id(user_id);

        Gson gson = new Gson();
        String json = gson.toJson(inputBody).replaceAll("(\\r|\\n)", "");;

        HttpHeaders requestHeaders = new HttpHeaders();

        RestTemplate restTemplateGetCars = new RestTemplate();
        requestHeaders.add(json, MediaType.APPLICATION_JSON_VALUE);

        HttpEntity<Json> requestEntity = new HttpEntity<>(requestHeaders);

        ResponseEntity<List<CarDto>> responseGetCars = restTemplateGetCars.exchange(
                "http://"+host+":"+port+"/carService",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<CarDto>>(){});

        List<CarDto> listOfCarsById = responseGetCars.getBody();

        if (listOfCarsById.size() == 0){

            return null;

        } else {

            CarDto searchedCar = listOfCarsById.get(0);

            if(searchedCar.getIs_available()){

                RestTemplate restTemplateRentCar = new RestTemplate();

                String rentCarURL = "http://"+host+":"+port+"/carService/rentcar";

                return restTemplateRentCar.exchange(rentCarURL,HttpMethod.PUT,requestEntity,CarDto.class).getBody();

            } return null;

        }

    }


    public CarDto releaseCar (Long user_id) throws IOException {

        getCarsCurrentlyRentedByUserId(user_id);

        System.out.println("\nCars currently rented by user: " + user_id + "\nPlease enter de id of the car you wish to release. ");

        Long car_id = Long.valueOf(System.in.read());

        RestTemplate restTemplateGetReleaseCar = new RestTemplate();

        String rentCarURL = "http://"+host+":"+port+"/carService/releasecar?id=" + car_id;

        return restTemplateGetReleaseCar.exchange(rentCarURL,HttpMethod.PUT,null,CarDto.class).getBody();

    }

}
