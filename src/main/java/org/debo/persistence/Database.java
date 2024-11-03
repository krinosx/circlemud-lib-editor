package org.debo.persistence;

import org.debo.lib.world.wld.Room;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Database {

    private static Database instance;
    private final DB db;
    private HTreeMap<String, String> roomDb;
    private HTreeMap<String, String> settingsDb;


    private Database(){

        db = DBMaker
                .fileDB("session.db")
                .fileMmapEnable()
                .transactionEnable()
                .closeOnJvmShutdown()
                .make();
    }

    public static Database getInstance() {
        if(instance == null){
            instance = new Database();
        }
        return instance;
    }

    public HTreeMap<String, String> getSettingsDatabase(){
        if(settingsDb == null){
            settingsDb = db
                    .hashMap("settings", Serializer.STRING, Serializer.STRING)
                    .createOrOpen();
        }
        return settingsDb;
    }


    public HTreeMap<String, String> getRoomDatabase(){
        if(roomDb == null) {
            roomDb = db
                    .hashMap("room", Serializer.STRING, Serializer.STRING)
                    .createOrOpen();
        }
        return roomDb;
    }

    void closeAll(){
        if(roomDb!= null && !roomDb.isClosed()){
            roomDb.close();
        }
        if(settingsDb != null && !settingsDb.isClosed()){
            settingsDb.close();
        }
    }


    public void commit() {
        if(db!=null && !db.isClosed()) {
            db.commit();
        }
    }
}
