package gautero.tuma.sendmealretry.Actividades;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import gautero.tuma.sendmealretry.R;
import gautero.tuma.sendmealretry.asyncTaskLab03.ConfirmarPedidoTask;
import gautero.tuma.sendmealretry.database.OnPedidoResultCallback;
import gautero.tuma.sendmealretry.database.PedidoDao;
import gautero.tuma.sendmealretry.database.PedidoRepository;
import gautero.tuma.sendmealretry.database.PlatoDao;
import gautero.tuma.sendmealretry.model.Pedido;
import gautero.tuma.sendmealretry.model.Plato;

public class PedidoActivity extends AppCompatActivity implements OnPedidoResultCallback {

    Application context = this.getApplication();

    OnPedidoResultCallback callback =  this;

    private static final int CODIGO_VER_PLATOS = 421;
    private static final int CODIGO_SELECT_UBI = 422;
    Button sp, cp, seleccionarUbi;
    String lista = "";
    List<Plato> listaPlatos = new ArrayList<Plato>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        Toolbar tb = findViewById(R.id.toolbarPedido);

        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                finish();
                return false;
            }
        });

        seleccionarUbi = findViewById(R.id.buttonUbicación);

        seleccionarUbi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PedidoActivity.this, MapActivity.class);
                startActivityForResult(i, CODIGO_SELECT_UBI);


            }
        });


        sp = findViewById(R.id.buttonAddPlato);
        sp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(PedidoActivity.this, SelectPlato.class);
                int a = 1;
                i.putExtra("addCode", a);
                startActivityForResult(i,CODIGO_VER_PLATOS);

            }

        });

        cp = findViewById(R.id.buttonConfirmar);
        cp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se lanza tarea Asincrónica
                ConfirmarPedidoTask ctask = new ConfirmarPedidoTask(PedidoActivity.this);
                ctask.execute(lista);
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Pedido pedido = new Pedido();

        if(requestCode==CODIGO_VER_PLATOS){

            //en teoria aca recupero el objeto plato que seleccione
            Gson gson = new Gson();
            Plato ob = gson.fromJson(getIntent().getStringExtra("myjson"), Plato.class);
            listaPlatos.add(ob);

            // insertar el pedido en la db
            TextView mail = findViewById(R.id.editTextTextEmailAddress3);
            String email = mail.toString();
            TextView direc = findViewById(R.id.editTextTextPersonName4);
            String dir = direc.toString();
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch del = findViewById(R.id.switch2);
            boolean delivery;
            delivery = del.isChecked();
            pedido.setDelivery(delivery);
            pedido.setDireccion(dir);
            pedido.setEmail(email);
            pedido.setPlatos(listaPlatos);
            PedidoRepository repository = new PedidoRepository(context, callback);
            repository.insertar(pedido);

            // Lista que muestra los nombres de los platos que se agregan al pedido
            String platoName =
                    data.getExtras().getString("plato");
            TextView tv = findViewById(R.id.listaP);
            lista += platoName + ", ";
            tv.setText(lista);

        }

        // Data del MAPA
        if(requestCode==CODIGO_VER_PLATOS){

            LatLng ubi = Objects.requireNonNull(getIntent().getExtras()).getParcelable("ubi");
            LatLng ubiResto = Objects.requireNonNull(getIntent().getExtras()).getParcelable("ubiResto");

           // pedido.setUbi(ubi);
           // pedido.setUbiResto(ubiResto);

        }

    }

    @Override
    public void onResult(List<Pedido> pedidos) {

    }
}