package com.example.reversi_po;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {
    private ImageView[][] boardDisplay;
    private double customScale;
    private Board b = new Board();
    public ImageView current;
    private Button startGame;
    private Handler handler = new Handler();
    private static int fullLatency;
    private static int singleLatency = 400;
    private  String pawnDesign;
    int blackPawn;
    int whitePawn;
    String whiteName;
    String blackName;
    private CountDownTimer countDownTimer;
    long timeLeft;
    long timeLimit;
    int minutes;
    int seconds;
    String timeLeftFormat;
    TextView timeShow;

    private Intent intent;
    private Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = getIntent().getExtras();
        customScale = (bundle.getInt("scale")/100.0);
        pawnDesign = bundle.getString("pawn");
        timeLimit = bundle.getLong("time");

        if (pawnDesign.equals("Classic")) {
            blackName = "black_pawn";
            whiteName = "white_pawn";
        } else if (pawnDesign.equals("Pirate")) {
            blackName = "black_pirate";
            whiteName = "white_pirate";
        } else if (pawnDesign.equals("Game of Thrones")) {
            blackName = "black_got";
            whiteName = "white_got";
        }

        blackPawn =  getResources().getIdentifier(blackName, "drawable", getPackageName());
        whitePawn =  getResources().getIdentifier(whiteName, "drawable", getPackageName());

        timeLeft = timeLimit;
        timeShow = findViewById(R.id.timer);

        current = findViewById(R.id.current);

        boardDisplay = new ImageView[8][8];
        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        final double SCALE = displaymetrics.widthPixels / 1080.0;
        int size = (int) (1080* SCALE * customScale);
        int margin = (1080 - size) / 2;
        ImageView boardBackground = findViewById(R.id.Board);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.setMargins(margin, (int) (300*SCALE), 0, 0);
        boardBackground.setLayoutParams(params);

        int left, height, resID;
        String fieldID;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                fieldID = "Field" + i + j;
                resID = getResources().getIdentifier(fieldID, "id", getPackageName());
                boardDisplay[i][j] = findViewById(resID);
                params = new FrameLayout.LayoutParams((int) (130 * SCALE * customScale), (int) (130 * SCALE * customScale));
                if (customScale == 0.9) {
                    left = (int) (margin + 5 + (134 * i) * SCALE * customScale);
                    height = (int) (1280 * customScale - (134 * j) * SCALE * customScale);
                } else if (customScale == 0.8) {
                    left = (int) (margin + 5 + (134 * i) * SCALE * customScale);
                    height = (int) (1052 - (134 * j) * SCALE * customScale);
                } else if (customScale == 0.5){
                    left = (int) (margin + 5 + (134 * i) * SCALE * customScale);
                    height = (int) ((1280 - 134 * j) * SCALE * customScale);
                } else {
                    left = 0;
                    height = 0;
                }
                params.setMargins(left, height, 0, 0);
                boardDisplay[i][j].setLayoutParams(params);
                final int x = i;
                final int y = j;
                boardDisplay[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (fullLatency == 0) {
                            fullLatency = singleLatency;
                            if (b.getCurrentPlayer().getColor() == COLOR.BLACK) {
                                chessAction(x, y);
                            }
                            if (b.getCurrentPlayer().getColor() == COLOR.WHITE) {
                                int row = 0;
                                int col = 0;
                                for (int i = 0; i < 8; i++) {
                                    for (int j = 0; j < 8; j++) {
                                        if (b.getBoard()[i][j] == COLOR.EMPTY && b.chkSlot(i, j)) {
                                            row = i;
                                            col = j;
                                            break;
                                        }
                                    }
                                }
                                chessAction(row, col);
                                fullLatency = 0;
                            }
                        }
                    }
                });
            }
        }
        startGame = findViewById(R.id.newGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullLatency = 0;
                b.startGame();
                printBoard();
                current.setImageResource(blackPawn);
                showPossibleMove();
                timeLeft = timeLimit;
                countDownTimer = new CountDownTimer(timeLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft = millisUntilFinished;
                        minutes = (int) (timeLeft / 1000) / 60;
                        seconds = (int) (timeLeft / 1000) % 60;
                        timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
                        timeShow.setText(timeLeftFormat);
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(MainActivity.this, R.string.no_time, Toast.LENGTH_LONG).show();
                        fullLatency = 0;
                        b.startGame();
                        printBoard();
                        current.setImageResource(blackPawn);
                        showPossibleMove();
                        timeLeft = timeLimit;
                        countDownTimer.start();
                    }
                }.start();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        fullLatency = 0;
        b.startGame();
        printBoard();
        current.setImageResource(blackPawn);
        showPossibleMove();
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                minutes = (int) (timeLeft / 1000) / 60;
                seconds = (int) (timeLeft / 1000) % 60;
                timeLeftFormat = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
                timeShow.setText(timeLeftFormat);
            }

            @Override
            public void onFinish() {
                Toast.makeText(MainActivity.this, R.string.no_time, Toast.LENGTH_LONG).show();
                fullLatency = 1;
            }
        }.start();
    }

    public void chessAction(int row, int col) {

        if(b.checkEnd() != 0 && HasPossibleMove()) {
            if(b.chkSlot(row, col)) {
                // Flip chess
                b.flip(row, col);
                b.placeChess(row, col);
                if (b.getCurrentPlayer().getColor() == COLOR.WHITE) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            printBoard();

                            // Change turn and check if the next player has possible move
                            if (b.checkEnd() != 0) {
                                b.nextTurn();
                                buttonChange(current);

                                if (!HasPossibleMove()) {
                                    Toast.makeText(MainActivity.this, R.string.no_move, Toast.LENGTH_LONG).show();
                                    b.nextTurn();
                                    buttonChange(current);
                                    if (!HasPossibleMove()) {
                                        showWinMessage();
                                        countDownTimer.cancel();
                                    }
                                }
                            }

                            // Show win message for the last play
                            if (b.checkEnd() == 0) {
                                showWinMessage();
                                countDownTimer.cancel();

                            }
                            showPossibleMove();
                        }
                    }, fullLatency);
                }
                if (b.getCurrentPlayer().getColor() == COLOR.BLACK) {
                    printBoard();

                    // Change turn and check if the next player has possible move
                    if (b.checkEnd() != 0) {
                        b.nextTurn();
                        buttonChange(current);

                        if (!HasPossibleMove()) {
                            Toast.makeText(MainActivity.this, R.string.no_move, Toast.LENGTH_LONG).show();
                            b.nextTurn();
                            buttonChange(current);
                            if (!HasPossibleMove()) {
                                showWinMessage();
                                countDownTimer.cancel();
                            }
                        }
                    }

                    // Show win message for the last play
                    if (b.checkEnd() == 0) {
                        showWinMessage();
                        countDownTimer.cancel();
                    }
                }

            }
        } else if(b.checkEnd() == 0) {
            Toast.makeText(MainActivity.this, R.string.restart, Toast.LENGTH_SHORT).show();
        }

    }

    public void buttonChange(ImageView img_button) {
        if (b.getCurrentPlayer().getColor() == COLOR.BLACK) {
            img_button.setImageResource(blackPawn);
        } else if (b.getCurrentPlayer().getColor() == COLOR.WHITE) {
            img_button.setImageResource(whitePawn);
        } else {
            img_button.setImageResource(R.drawable.empty);
        }
    }

    public void printBoard() {
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                if(b.getBoard()[i][j] == COLOR.BLACK) {
                    boardDisplay[i][j].setImageResource(blackPawn);
                } else if(b.getBoard()[i][j] == COLOR.WHITE) {
                    boardDisplay[i][j].setImageResource(whitePawn);
                } else if(b.getBoard()[i][j] == COLOR.EMPTY) {
                    boardDisplay[i][j].setImageResource(R.drawable.empty);
                }
            }
        }
    }

    public boolean HasPossibleMove() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if(b.chkSlot(i, j)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void showPossibleMove() {
        for(int row = 0; row < 8; row++) {
            for(int col = 0; col < 8; col++) {
                if(b.getBoard()[row][col] == COLOR.EMPTY && b.chkSlot(row, col)) {
                    if(b.getCurrentPlayer().getColor() == COLOR.BLACK) {
                        boardDisplay[row][col].setImageResource(R.drawable.black_pawn_pos);
                    } else if(b.getCurrentPlayer().getColor() == COLOR.WHITE) {
                        boardDisplay[row][col].setImageResource(R.drawable.white_pawn_pos);
                    }
                }
            }
        }
    }

    public void showWinMessage() {
        if (b.count(COLOR.BLACK) > b.count(COLOR.WHITE)) {
            Toast.makeText(MainActivity.this, R.string.black_wins, Toast.LENGTH_LONG).show();
        } else if (b.count(COLOR.BLACK) < b.count(COLOR.WHITE)) {
            Toast.makeText(MainActivity.this, R.string.white_wins, Toast.LENGTH_LONG).show();
        } else if (b.count(COLOR.BLACK) == b.count(COLOR.WHITE)) {
            Toast.makeText(MainActivity.this, R.string.ties, Toast.LENGTH_LONG).show();
        }
    }
}

