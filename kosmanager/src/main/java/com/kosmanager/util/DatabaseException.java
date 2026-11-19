package com.kosmanager.util;

public final class DatabaseException extends RuntimeException {

    //constructor untuk membuat exception dgn pesan kesalahan
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
