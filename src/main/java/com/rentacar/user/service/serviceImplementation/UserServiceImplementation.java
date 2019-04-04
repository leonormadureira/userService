package com.rentacar.user.service.serviceImplementation;

import com.google.gson.Gson;
import com.rentacar.user.domain.User;
import com.rentacar.user.dto.CarDto;
import com.rentacar.user.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public UserServiceImplementation(UserRepository user_repository) {
        this.user_repository = user_repository;
    }


    public List<CarDto> getCarsCurrentlyRentedByUserId(Long user_id) {

        String url = "http://" + host + ":" + port + "/carService/searchCars?user_id=" + user_id;

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<CarDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarDto>>() {
                });

        return response.getBody();
    }

    public User createNewUser(String firstName, String lastName, Long nif) {

        User newUser = new User();

        newUser.setFirst_name(firstName);
        newUser.setLast_name(lastName);
        newUser.setNif(nif);

        return user_repository.save(newUser);
    }

    public void deleteUser(Long nif) {

        User userToDelete = user_repository.findUserByNif(nif);

        user_repository.delete(userToDelete);
    }

    public CarDto rentCar(Long user_id, Long car_id) throws IllegalArgumentException {

        RestTemplate restTemplateGetCars = new RestTemplate();

        HttpEntity<String> requestEntity = requestEntityCreation(user_id, car_id);

        ResponseEntity<List<CarDto>> responseGetCars = restTemplateGetCars.exchange(
                "http://" + host + ":" + port + "/carService?id=" + car_id,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarDto>>() {
                });

        List<CarDto> listOfCarsById = responseGetCars.getBody();

        if (listOfCarsById.size() == 0) {

            log.error("Car doesn't exist.");

            return null;

        } else {

            CarDto searchedCar = listOfCarsById.get(0);

            if (searchedCar.getIs_available()) {

                RestTemplate restTemplateRentCar = new RestTemplate();

                String rentCarURL = "http://" + host + ":" + port + "/carService/rentcar";

                return restTemplateRentCar.exchange(rentCarURL, HttpMethod.PUT, requestEntity, CarDto.class).getBody();

            } else {

                log.error("Car not available.");
                return null;
            }
        }

    }

    public HttpEntity requestEntityCreation(Long user_id, Long car_id) {
        CarDto inputBody = new CarDto();

        inputBody.setId(car_id);
        inputBody.setUser_id(user_id);

        Gson gson = new Gson();
        String json = gson.toJson(inputBody);

        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.add("Content-Type", "application/json");

        HttpEntity<String> requestEntity = new HttpEntity<>(json, requestHeaders);

        return requestEntity;
    }

    public CarDto releaseCar(Long user_id, Long car_id) {

        List<CarDto> listOfCars = getCarsCurrentlyRentedByUserId(user_id);
        int count = 0;

        for (CarDto carDto : listOfCars) {

            if (carDto.user_id == user_id) {

                RestTemplate restTemplateGetReleaseCar = new RestTemplate();

                String rentCarURL = "http://" + host + ":" + port + "/carService/releasecar";
                count++;

                log.info("Car " + car_id + " released.");

                HttpEntity<String> requestEntity = requestEntityCreation(user_id, car_id);

                RestTemplate restTemplateGetCars = new RestTemplate();

                return restTemplateGetReleaseCar.exchange(rentCarURL, HttpMethod.PUT, requestEntity, CarDto.class).getBody();
            }
        }

        if (count == 0) {

            log.info("There was a problem releasing the car.");
        }
        return null;
    }
}
