package julian.com.memorizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;


import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView resultimage;
    Button analyser;
    TextView result;
    Bitmap returnimage;
    FirebaseVisionImage image;
    String resultText;
    Button scan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        analyser=findViewById(R.id.analyser);
        resultimage=findViewById(R.id.resultimage);
        result=findViewById(R.id.resulttext);
        scan=findViewById(R.id.scanbut);

        analyser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                 dispatchTakePictureIntent();
                 cropimageintent();

            }



        });


        scan.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                     analyseusinggooglevisionapi();
                              }

         });

    }

    private void cropimageintent() {

    }

    private void analyseusinggooglevisionapi() {
    //    Bitmap bitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),returnimage);
        TextRecognizer textRecognizer=new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational())
        {
            Toast.makeText(this, "no text found", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Frame frame=new Frame.Builder().setBitmap(returnimage).build();
            SparseArray<TextBlock> items=textRecognizer.detect(frame);
            StringBuilder sb=new StringBuilder();

            for (int i=0;i<items.size();++i)
            {
                TextBlock myitem=items.valueAt(i);
                sb.append(myitem.getValue());
                sb.append("\n");
            }
            result.setText(sb);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            resultimage.setImageBitmap(imageBitmap);
            returnimage=imageBitmap;

        }
    }
    private void detecttextfromimage() {
        final FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(returnimage);
        FirebaseVisionTextDetector detector=FirebaseVision.getInstance().getVisionTextDetector();
        detector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                displaytextfromimage(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"error"+e,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displaytextfromimage(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.Block> blocks=firebaseVisionText.getBlocks();
        if (blocks.size() == 0)
        {
            Toast.makeText(MainActivity.this,"no text found",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks())
            {
                String Text=block.getText();
                result.setText(Text);
            }
        }
    }

}
