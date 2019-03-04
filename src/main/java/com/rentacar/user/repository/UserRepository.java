package com.rentacar.user.repository;

import com.rentacar.user.domain.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository <User, Long> {

    @Query(value = "SELECT * FROM User WHERE user_id = ?1", nativeQuery = true)
    User findUserByUserId(Long user_id);
}