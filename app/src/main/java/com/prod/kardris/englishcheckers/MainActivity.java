package com.prod.kardris.englishcheckers;
//import java.awt.Color;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Vibrator;
import android.widget.Toast;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.R.attr.button;

public class MainActivity extends AppCompatActivity {
    private final String TAG="button";
    private final int SIZE=8;
    private Button[][] buttons;
    public int[] inputVars;
    private final InputStream realSystemIn=System.in;
    private final PipedOutputStream systemInOut=new PipedOutputStream();;
    private EnglishCheckers gameCheckers=null;
    private AtomicBoolean lock;
    private Thread gameLoop;
    private int _chosenStrategy;
    private int _gameType;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            TextView status=(TextView)findViewById(R.id.status);
            status.setText((String)msg.obj);
        }
    };

    public Handler getHandler(){
        return handler;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//this must be initialized after context has been created.

        lock=new AtomicBoolean();
        lock.set(false);

        buttons=new Button[SIZE][SIZE];
        inputVars=new int[2];
        final MainActivity thisRef = this;
        int countButtons=0;

        gameCheckers=new EnglishCheckers(this,lock);

        for(int i = 0;  i <= SIZE-1;  ++i) {
            for (int j = 0; j <= SIZE - 1; ++j) {
                Button button = (Button) findViewById(getResources().getIdentifier("gButton" + countButtons, "id",
                        this.getPackageName()));

                //findViewById(R.id.gButton0)
                final int ii = realI(i);
                final int jj = realJ(j);

                buttons[i][j] = button;
                final int iI=i;// needed for paiting the canvas in yellow while selecting.
                final int jJ=j;
                //container.add(buttons[i][j]);

                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vib.vibrate(50);
                        buttons[iI][jJ].setBackgroundColor(Color.YELLOW);
                        inputVars[0]=ii;
                        inputVars[1]=jj;

                        synchronized (lock){
                            lock.set(true);
                            lock.notify(); //inttereput the thread.
                        }

                        //Log.i(TAG, "onClick: "+ii+","+jj);

                    }
                });
                countButtons++;
            }
        }

        Log.i(TAG,"entering checkers loop");
        _gameType=0; //for player vs pc.
        _chosenStrategy=EnglishCheckers.DEFENSIVE;
        gameLoop =new Thread(gameCheckers.loop(_gameType,_chosenStrategy));//default: interactive and defensive.
        gameLoop.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "hello", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_game) {
           // gameLoop.notify();
            gameLoop.interrupt();
            //reset inputs:
           // inputVars[0]=-1;
            //inputVars[1]=-1;
            lock.set(false);
            gameLoop= new Thread(gameCheckers.loop(_gameType,_chosenStrategy));
            gameLoop.start();
            Toast.makeText(getApplicationContext(), "A new game has been started", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.choose_strategy) {
           // public static final int RANDOM			= 1;
           // public static final int DEFENSIVE		= 2;
          //  public static final int SIDES				= 3;
            CharSequence strategies[] = new CharSequence[] {"RANDOM", "(D)DEFENSIVE", "SIDES"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose strategies:");
            builder.setItems(strategies, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case 0: _chosenStrategy=EnglishCheckers.RANDOM; break;
                        case 1: _chosenStrategy=EnglishCheckers.DEFENSIVE; break;
                        case 2: _chosenStrategy=EnglishCheckers.SIDES; break;
                        default:
                            System.err.println("weird value at onClick in onOptionsItemSelected");
                    }
                    Toast.makeText(getApplicationContext(), "strategy has been changed..", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "restart game to effect", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();

            return true;
        }
        if (id == R.id.pvp) {
            if(_gameType==0){
                _gameType=1;
                Toast.makeText(getApplicationContext(), "player against player", Toast.LENGTH_SHORT).show();
            }
            else{
                _gameType=0;
                Toast.makeText(getApplicationContext(), "player against computer", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), "restart the game to effect", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showBoard(int array[][]) {
        for(int i=0; i<=SIZE-1; i++){
            for(int j=0; j<=SIZE-1; j++)
            {
                int boardVal = array[realI(i)][realJ(j)];

                int color=Color.GRAY;
                switch (boardVal) {
                    case EnglishCheckers.EMPTY:
                        if ((realI(i)+realJ(j)) % 2 == 0)
                            color =Color.DKGRAY;
                        else
                            color = Color.LTGRAY;
                        break;
                    case EnglishCheckers.RED:      color = 0xff8b131d;      break;
                    case EnglishCheckers.BLUE:      color = 0xFF10756c;      break;
                    case EnglishCheckers.RED * 2:   color = 0xFF8b1359;   break;
                    case EnglishCheckers.BLUE * 2:   color = 0xFF138b45;      break;
                    case EnglishCheckers.MARK:      color = 0xffffcc66;   break;
                    default:
                        System.err.println("Unknown value at position " + realI(i) + "," + realJ(j) + "!");
                }
                //System.out.println("in show board ");
                if (! buttons[i][j].getBackground().equals(color)) {
                    //buttons[i][j].call
                    //buttons[i][j].setBackgroundColor(color);
                    setColor(buttons[i][j],color);
                   //buttons[i][j].
                }
            }
        }
    }
    private int realI(int i) {
        return SIZE-1-i;
    }

    private int realJ(int j) {
        return j;
    }
    private void setColor(final Button btn,final int color){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn.setBackgroundColor(color);
            }
        });
    }
}
