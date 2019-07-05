package edinc.elm327_bluetooth_connector;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.text.format.Time.*;
import static edinc.elm327_bluetooth_connector.Bluetooth_Chat_ELM327.STATE_MESSAGE_RECEIVED;

public class Bluetooth_Chat_ELM327 extends AppCompatActivity {

    Button listen, send, list;
    ListView chat;
    TextView trama,status,textView2,Set,tVRpm,RPMTxt,tVVel,VELTxt,units,latT,TLAT,lngT,TLNG;
    EditText command, Car_Number;
    Switch switch1;

    String Initialization ="ATZ";
    String Setup_Line = "ATL0";
    String Echo = "ATE0"; //Echo off
    String Order;


    String LAT,LNG,DATE,TIME,rpm;
    public int n =1;
    public int C = 1;
    public int Car_ID = 1;
    int RPM;
    public  String Help ="";
    public  String Help2 ="";
    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;
    double lat1, lat2,lng1,lng2;

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;

    int REQUEST_ENABLED_BLUETOOTH = 1;

    private static final String APP_NAME = "Bluetooth_Chat_ELM327";
    private static final UUID MY_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static String DB = "jdbc:mysql://testdb.cgnwvpmwp6tr.us-east-2.rds.amazonaws.com/Test";
    private static String user = "Test";
    private static String password = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bhat_elm327);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewByIdes();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled())
        {
            Intent enabledIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabledIntent,REQUEST_ENABLED_BLUETOOTH);
        }

        implementListeners();
        Current_Location();
    }

    private void findViewByIdes() {
        listen = findViewById(R.id.listen);
        send = findViewById(R.id.send);
        list = findViewById(R.id.list);
        status = findViewById(R.id.status);
        chat = findViewById(R.id.chat);
        command = findViewById(R.id.command);

        textView2 = findViewById(R.id.textView2);

        Car_Number = findViewById(R.id.Car_Number);
        TLNG = findViewById(R.id.TLNG);
        TLAT = findViewById(R.id.TLAT);

        VELTxt = findViewById(R.id.VELTxt);
        switch1 = findViewById(R.id.switch1);


    }

    private void implementListeners() {
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<BluetoothDevice> bt = bluetoothAdapter.getBondedDevices();
                String[] strings = new String[bt.size()];
                btArray = new BluetoothDevice[bt.size()];
                int index = 0;

                if (bt.size() > 0) {
                    for (BluetoothDevice device : bt) {
                        btArray[index] = device;
                        strings[index] = device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, strings);
                    chat.setAdapter(arrayAdapter);
                }
            }
        });

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setTextColor(Color.GREEN);
                status.setText("Connected");
            }
        });

        chat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                status.setTextColor(Color.GREEN);
                status.setText("Connected");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status.setTextColor(Color.GREEN);
                status.setText("Connected");
            }
        });
    }
/*
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setTextColor(Color.GREEN);
                    status.setText("Connected");
                    break;

                case STATE_CONNECTING:
                    status.setTextColor(Color.GREEN);
                    status.setText("Connected");
                    break;

                case STATE_CONNECTED:
                    status.setTextColor(Color.GREEN);
                    status.setText("Connected");
                    //Begin();
                    //Echo();
                    //Line();
                    break;

                case STATE_CONNECTION_FAILED:
                    status.setTextColor(Color.GREEN);
                    status.setText("Connected");

                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    if(n == 1) {
                        Help = tempMsg;
                        Help = Help.trim();
                        trama.setText(Help);
                        n = 2;
                        if(Help.length()>10){
                            String aux = Help.substring(Help.length()-5,Help.length());
                            aux = aux.replace(" ","");
                            /*
                            if(C == 2) {
                                aux = aux.substring(aux.length() - 2, aux.length());
                                VELTxt.setText(aux);
                                C = 1;
                            }else{if(C == 1) {

                                RPM = Integer.parseInt(aux, 16) / 4;
                                RPMTxt.setText(String.valueOf(RPM));
                                C = 2;
                            //}
                        }
                    }else{ if(n == 2){
                        Help2 = tempMsg;
                        Set.setText(Help2);
                        n = 1;
                        if(Help2.length()>10){
                            String aux = Help2.substring(Help2.length()-5,Help2.length());
                            aux = aux.replace(" ","");
                            /*
                            if(C == 2) {
                                aux = aux.substring(aux.length()-2,aux.length());
                                VELTxt.setText(aux);
                                C = 1;
                            }else {
                                if (C == 1) {

                                    RPM = Integer.parseInt(aux, 16) / 4;
                                    RPMTxt.setText(String.valueOf(RPM));
                                    C = 2;
                               // }
                            //}
                            //}
                        }
                        }
                    }

                    break;
            }
            return true;
        }
    });
*/
    /*
    Handler handler_begin = new Handler();
    public void Begin(){
        Order = Initialization;
        handler_begin.postDelayed(Cleaner,1000);
    }

    public void Echo(){
        Order = Echo;
        handler_begin.postDelayed(Cleaner,2000);
    }

    public void Line(){
        Order = Setup_Line;
        handler_begin.postDelayed(Cleaner,3000);
    }

    private  Runnable Cleaner = new Runnable() {
        @Override
        public void run() {
            sendReceive.write(Order.getBytes());
        }
    };
    */


//////////////////////////////////////////   LOCATION   ////////////////////////////////////////////
public void Start_Location() {
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    final boolean GPSenable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    if (!GPSenable) {
        Intent LocationIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(LocationIntent);
    }
}

    public void Refresh_Position(Location location) {
        if (location != null) {
            if(n>1) {
                lat2 = lat1;
                lng2 = lng1;
                lat1 = location.getLatitude();
                lng1 = location.getLongitude();
                Distancia distancia = new Distancia(lat1,lat2,lng1,lng2);
                double D = distancia.haversine();
                int vel = (int) Math.round(D*3500/2.1);
                VELTxt.setText(String.valueOf(vel));
                RPM = vel;
            }else {
                lat1 = location.getLatitude();
                lng1 = location.getLongitude();
            }
            LAT = String.valueOf(lat1);
            LNG = String.valueOf(lng1);
            TLAT.setText(LAT);
            TLNG.setText(LNG);
            n++;
            Fecha();
            //RPMTxt.setText(rpm);
            Send obj = new Send();
            obj.execute("");
            /*
            if(switch1.isChecked()) {
                if (C == 1) {
                    String COMMAND = "010C";
                    sendReceive.write(COMMAND.getBytes());
                } else {
                    if (C == 2) {
                        String COMMAND = "010D";
                        sendReceive.write(COMMAND.getBytes());
                    }
                }
            }
            */


        }
    }

    /*
   private Timer mTimer1;
    private TimerTask mTt1;
    private Handler mTimerHandler= new Handler();

    private void stopTimer() {
        if(mTimer1 != null){
            mTimer1.cancel();
            mTimer1.purge();
        }
    }
    private void startTimer(){
        mTimer1= new Timer();
        mTt1=new TimerTask() {
            @Override
            public void run() {
                mTimerHandler.post(new Runnable() {
                public void run(){
                    String COMMAND = "010C";
                    if(switch1.isChecked()){
                        sendReceive.write(COMMAND.getBytes());
                    }
                }
                });
            }
        };

        mTimer1.schedule(mTt1,0,2000);
    }

    */

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            Refresh_Position(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            String m = "GPS Enable";
        }

        @Override
        public void onProviderDisabled(String provider) {
            Start_Location();
        }
    };


    private void Current_Location() {

        Start_Location();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Refresh_Position(location);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, locationListener);
    }


///////////////////////////////////////   DB CONNECTION   //////////////////////////////////////////

    public void Fecha(){
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String dia =Integer.toString(today.monthDay);
        String mes = Integer.toString(today.month+1);
        String year = Integer.toString(today.year);
        String date = (year+"-"+mes+"-"+dia);
        DATE = date;
        String hora = Integer.toString(today.hour);
        String minuto = Integer.toString(today.minute);
        String seg = Integer.toString(today.second);
        String time = (hora+":"+minuto+":"+seg);
        TIME = time;
    }

    private class Send extends AsyncTask<String, String, String> {
        String msg = "";

        @Override
        protected String doInBackground(String... strings) {
            try{
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(DB,user,password);
                if(connection == null){
                    msg = "Null Connection";

                }else{
                    String query = "INSERT INTO Datos (Longitud,Latitud,Fecha,Carro,Parametro) VALUES ('"+LNG+"','"+LAT+"','"+DATE+" "+TIME+"','"+Car_ID+"','"+String.valueOf(RPM)+"')";
                    Statement statement = connection.createStatement();
                    statement.executeUpdate(query);
                    msg = "Inserted";

                }
                connection.close();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                msg = "Class Error";
            } catch (SQLException e) {
                e.printStackTrace();
                msg = "SQL Sentence Error";
            }
            return msg;
        }
        @Override
        protected void onPostExecute(String msg)
        {
            status.setText(msg);
        }
    }

/////////////////////////////////////////    MENU    ///////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bluetooth__chat__elm327, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Car_ID = Integer.parseInt(String.valueOf(Car_Number.getText()));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
