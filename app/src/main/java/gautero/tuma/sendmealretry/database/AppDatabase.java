package gautero.tuma.sendmealretry.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gautero.tuma.sendmealretry.model.Pedido;
import gautero.tuma.sendmealretry.model.Plato;

@Database(entities = {Plato.class, Pedido.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase database;
    public static String databaseName = "dataBase";

    private static final int NUMBER_OF_THREADS = 1;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public abstract PlatoDao platoDao();
    public abstract PedidoDao pedidoDao();

    public synchronized static AppDatabase getInstance(Context context){

        if(database == null){

            database = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, databaseName)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return database;

    }
}
