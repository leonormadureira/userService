package com.rentacar.user.service;

import com.rentacar.user.domain.User;
import com.rentacar.user.dto.CarDto;

import java.io.IOException;
import java.util.List;

public interface UserService {

    /**
     * Retrieves a list of car_ids that the user provided in request is currently renting.
     *
     * @param user_id
     * @return List<Long>
     */
    List<CarDto> getCarsCurrentlyRentedByUserId(Long user_id);

    /**
     * Creates a new user.
     *
     * @param firstName
     * @param lastName
     * @param nif
     * @return User - data of the user
     */
    User createNewUser(String firstName, String lastName, Long nif);

    /**
     * Deletes a user.
     *
     * @param user_id
     */
    void deleteUser (Long user_id);

    /**
     * Allows a user to rent a car.
     *
     * @param user_id
     * @return CarDto
     */
    CarDto rentCar (Long user_id) throws IOException;

    /**
     * Allows a user to release a car.
     *
     * @param user_id
     * @return CarDto
     */
    CarDto releaseCar (Long user_id) throws IOException;


}
