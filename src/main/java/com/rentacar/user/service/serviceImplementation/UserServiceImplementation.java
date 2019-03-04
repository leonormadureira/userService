package com.rentacar.user.service.serviceImplementation;

import com.rentacar.user.domain.User;
import com.rentacar.user.dto.CarDto;
import com.rentacar.user.repository.UserRepository;
import com.rentacar.user.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Data
@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository user_repository;
    private RestTemplate restTemplate;


    @Autowired
    public UserServiceImplementation(UserRepository user_repository){this.user_repository = user_repository;}


    public List<CarDto> getCarsCurrentlyRentedByUserId(Long user_id){

        String url = "http://localhost:8080/carService/searchCars?user_id="+user_id;

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

    public CarDto rentCar (Long user_id) throws IOException {

        RestTemplate restTemplateGetCars = new RestTemplate();

        ResponseEntity<List<CarDto>> responseGetCars = restTemplateGetCars.exchange(
                "http://localhost:8080/carService?id=&&is_available=true",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CarDto>>(){});

        List<CarDto> listOfAvailableCars = responseGetCars.getBody();

        System.out.println("\nAvailable cars:\n" + listOfAvailableCars);

        System.out.println("\n Please enter de id of the car you wish to rent.");

        Long car_id = Long.valueOf(System.in.read());

        RestTemplate restTemplateRentCar = new RestTemplate();

        String rentCarURL = "http://localhost:8080/carService/rentcar?id=" + car_id + "&&user_id=" + user_id;

        return restTemplateRentCar.exchange(rentCarURL,HttpMethod.PUT,null,CarDto.class).getBody();

    }

    public CarDto releaseCar (Long user_id) throws IOException {

        getCarsCurrentlyRentedByUserId(user_id);

        System.out.println("\nCars currently rented by user: " + user_id + "\nPlease enter de id of the car you wish to release. ");

        Long car_id = Long.valueOf(System.in.read());

        RestTemplate restTemplateGetReleaseCar = new RestTemplate();

        String rentCarURL = "http://localhost:8080/carService/releasecar?id=" + car_id;

        return restTemplateGetReleaseCar.exchange(rentCarURL,HttpMethod.PUT,null,CarDto.class).getBody();

    }

}
