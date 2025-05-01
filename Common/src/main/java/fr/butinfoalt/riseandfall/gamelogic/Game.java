package fr.butinfoalt.riseandfall.gamelogic;

public class Game {
    private String name;
    private int day_time;
    private int nb_max_player;
    private int current_day;
    private boolean is_private;

    public Game(String name, int day_time, int nb_max_player, int current_day, boolean is_private) {
        this.name = name;
        this.day_time = day_time;
        this.nb_max_player = nb_max_player;
        this.current_day = current_day;
        this.is_private = is_private;
    }

    public Game(String name, int day_time, int nb_max_player, int current_day) {
        this.name = name;
        this.day_time = day_time;
        this.nb_max_player = nb_max_player;
        this.current_day = current_day;
        this.is_private = false;
    }

    public String getName() {
        return name;
    }

    public int getDayTime() {
        return day_time;
    }

    public int getNbMaxPlayer() {
        return nb_max_player;
    }

    public int getCurrentDay() {
        return current_day;
    }

    public boolean is_private() {
        return is_private;
    }
}
