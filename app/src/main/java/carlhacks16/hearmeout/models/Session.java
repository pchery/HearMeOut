package carlhacks16.hearmeout.models;

/**
 * Created by paulchery on 4/9/16.
 */
public class Session {

    private int volume;
    private int speed;
    private int fillers;
    private int movements;

    public Session(){
        this.volume = 0;
        this.speed = 0;
        this.fillers = 0;
        this.movements = 0;
    }

    public Session(int volume, int speed, int fillers, int movements ){
        this.volume = volume;
        this.speed = speed;
        this.movements = movements;
        this. fillers = fillers;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getFillers() {
        return fillers;
    }

    public void setFillers(int fillers) {
        this.fillers = fillers;
    }

    public int getMovements() {
        return movements;
    }

    public void setMovements(int movements) {
        this.movements = movements;
    }
}
