package com.rentacar.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {

    public Long user_id;
    public List<Long> car_id;
    public String first_name;
    public String last_name;

}
