package org.example;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table(name = "game")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private static final long serialVersionUID = 1L;

    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    public void setUser(User newUser)
    {
        this.user = newUser;
    }
    public User getUser()
    {
        return user;
    }
    public Game(String newName)
    {
        this.name = newName;
    }

    public Game() {

    }

    public void setName(String newName) {
        this.name = newName;
    }

    public String getName() {
        return name;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
