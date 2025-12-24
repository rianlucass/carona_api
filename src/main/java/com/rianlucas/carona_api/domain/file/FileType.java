package com.rianlucas.carona_api.domain.file;

public enum FileType {
    USER_PROFILE("users"),
    VEHICLE_PHOTO("vehicles");

    private final String directoryName;

    FileType(String directoryName) {
        this.directoryName = directoryName;
    }

    public String getDirectoryName() {
        return directoryName;
    }
}
