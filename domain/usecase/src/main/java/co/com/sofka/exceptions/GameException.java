package co.com.sofka.exceptions;

import lombok.Getter;

public class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}
