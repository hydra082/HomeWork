package org.example;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Cacheable
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.READ_WRITE)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.PERSIST)
    private List<Game> gameList = new ArrayList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @BatchSize(size = 2)
    private List<Game> gamesListBatch= new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    @Fetch(FetchMode.SELECT)
    private List<Game> gameListFetch = new ArrayList<>();

    public void addGameList(Game newGame)
    {
        this.gameList.add(newGame);
        this.gamesListBatch.add(newGame);
        this.gameListFetch.add(newGame);
    }

    public List<Game> getGameListFetch() {
        return gameListFetch;
    }

    public List<Game> getGameList()
    {
        return this.gameList;
    }

    public List<Game> getGamesListBatch() {
        return gamesListBatch;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
