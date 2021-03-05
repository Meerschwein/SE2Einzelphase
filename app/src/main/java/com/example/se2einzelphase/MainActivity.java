package com.example.se2einzelphase;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView matrikelnummerInput = findViewById(R.id.matrikelnummerInput);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence matrikelnummer = matrikelnummerInput.getText();
                sendRequest(matrikelnummer);
            }
        });

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence matrikelnummer = matrikelnummerInput.getText();
                calculate(matrikelnummer);
            }
        });
    }

    private synchronized void displayMessage(String message) {
        final TextView serverResponseTextView = findViewById(R.id.serverResponse);
        serverResponseTextView.setText(message);
    }

    private void sendRequest(CharSequence message){
        new Thread(() -> {
            try {
                System.out.println("Starting Socket!");
                Socket client = new Socket("se2-isys.aau.at", 53212);

                DataOutputStream dout = new DataOutputStream(client.getOutputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

                byte[] messageAsBytes = (message.toString() + "\n").getBytes(StandardCharsets.UTF_8);

                System.out.println("Sending message: " + message + " .");
                dout.write(messageAsBytes);
                System.out.println("Sent message!");

                System.out.println("Waiting for response...");
                String response = br.readLine();
                System.out.println("Received response: " + response);

                displayMessage(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void calculate(CharSequence matrikelnummer){
        // 11912205 % 7 = 4
        char[] matrikelnummerAsChararray = matrikelnummer.toString().toCharArray();
        int numPrimes = 0;
        char[] matrikelnummerNoPrimes = new char[matrikelnummerAsChararray.length];

        for (char c : matrikelnummerAsChararray){
            if (!(c == '2' || c == '3' || c == '5' || c == '7')){
                matrikelnummerNoPrimes[numPrimes] = c;
                numPrimes++;
            }
        }
        matrikelnummerNoPrimes = Arrays.copyOfRange(matrikelnummerNoPrimes, 0, numPrimes);
        Arrays.sort(matrikelnummerNoPrimes);

        displayMessage(Arrays.toString(matrikelnummerNoPrimes));
    }
}