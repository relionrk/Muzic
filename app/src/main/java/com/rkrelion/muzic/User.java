package com.rkrelion.muzic;

public class User {
    private String name ;
    private String email ;

    private String genre_one ;
    private String genre_two ;
    private String genre_three ;

    public User() {
        //default empty constructor
    }

    public User(String name, String email, String genre_one, String genre_two, String genre_three) {
        this.name = name;
        this.email = email;
        this.genre_one = genre_one;
        this.genre_two = genre_two;
        this.genre_three = genre_three;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGenre_one() {
        return genre_one;
    }

    public void setGenre_one(String genre_one) {
        this.genre_one = genre_one;
    }

    public String getGenre_two() {
        return genre_two;
    }

    public void setGenre_two(String genre_two) {
        this.genre_two = genre_two;
    }

    public String getGenre_three() {
        return genre_three;
    }

    public void setGenre_three(String genre_three) {
        this.genre_three = genre_three;
    }
}
