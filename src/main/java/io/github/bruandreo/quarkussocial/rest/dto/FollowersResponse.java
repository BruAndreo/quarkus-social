package io.github.bruandreo.quarkussocial.rest.dto;

import io.github.bruandreo.quarkussocial.domain.model.Follower;
import lombok.Data;

@Data
public class FollowersResponse {

    private Long id;
    private String name;

    public FollowersResponse() {}

    public FollowersResponse(Follower follower) {
        this(follower.getFollower().getId(), follower.getFollower().getName());
    }

    public FollowersResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
