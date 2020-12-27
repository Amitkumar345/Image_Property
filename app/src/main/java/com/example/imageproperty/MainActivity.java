package com.example.imageproperty;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.clarifai.channel.ClarifaiChannel;
import com.clarifai.credentials.ClarifaiCallCredentials;
import com.clarifai.grpc.api.Concept;
import com.clarifai.grpc.api.Data;
import com.clarifai.grpc.api.Image;
import com.clarifai.grpc.api.Input;
import com.clarifai.grpc.api.MultiOutputResponse;
import com.clarifai.grpc.api.PostModelOutputsRequest;
import com.clarifai.grpc.api.V2Grpc;
import com.clarifai.grpc.api.status.StatusCode;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showDetails(View view) throws MalformedURLException {
        EditText editText = (EditText) findViewById(R.id.image_link);
        String link = editText.getText().toString();

        V2Grpc.V2BlockingStub stub = V2Grpc.newBlockingStub(ClarifaiChannel.INSTANCE.getInsecureGrpcChannel())
                .withCallCredentials(new ClarifaiCallCredentials("01bf4e501a184761876a06c03bd2d6ef"));

        MultiOutputResponse response = stub.postModelOutputs(
                PostModelOutputsRequest.newBuilder()
                        .setModelId("aaa03c23b3724a16a56b629203edc62c")
                        .addInputs(
                                Input.newBuilder().setData(
                                        Data.newBuilder().setImage(
                                                Image.newBuilder().setUrl(link)
                                        )
                                )
                        )
                        .build()
        );
        if (response.getStatus().getCode() != StatusCode.SUCCESS) {
            throw new RuntimeException("Request failed, status: " + response.getStatus());
        }

        List<String> list= new ArrayList<>();

        for (Concept c : response.getOutputs(0).getData().getConceptsList()) {
           list.add( String.format("%12s: %,.2f", c.getName(), c.getValue()) );
        }

        Intent intent = new Intent(MainActivity.this, ResultActivity.class);
        intent.putExtra("LIST", (Serializable) list);
        startActivity(intent);
    }

}