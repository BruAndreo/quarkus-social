package io.github.bruandreo.quarkussocial.domain.repository;

import io.github.bruandreo.quarkussocial.domain.model.Follower;
import io.github.bruandreo.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {

    public boolean follows(User follower, User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("follower", follower);
        params.put("user", user);

        var result = find("follower = :follower and user = :user", params)
                .firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findFollowersByUser(Long userId) {
        return find("user.id", userId).list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();

        delete("follower.id = :followerId and user.id = :userId", params);

    }
}
