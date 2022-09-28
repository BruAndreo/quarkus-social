package io.github.bruandreo.quarkussocial.rest.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class FollowerRequest {

    @NotNull(message = "Follower Id can not be null")
    @Min(value = 0L, message = "Follower Id can not be Zero or minor")
    private Long followerId;

}
