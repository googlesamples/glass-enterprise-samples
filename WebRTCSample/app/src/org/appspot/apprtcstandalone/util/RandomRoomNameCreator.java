package org.appspot.apprtcstandalone.util;

import java.util.Random;

public class RandomRoomNameCreator implements RoomNameCreator {

    private static final int NAME_LENGTH = 6;
    private static final int VALUE_RANGE = 10;

    @Override
    public String getRoomName() {
        Random random = new Random(System.currentTimeMillis());
        StringBuilder roomName = new StringBuilder();
        while(roomName.length() < NAME_LENGTH){
            roomName.append(String.valueOf(random.nextInt((VALUE_RANGE))));
        }
        return roomName.toString();
    }
}
