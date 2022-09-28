package io.github.bruandreo.quarkussocial.domain.model;

import javax.persistence.*;

@Entity
@Table(name = "folowers")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User follower;

}
