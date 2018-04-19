package com.example.kuldeep.openpose;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    Mat mat1;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    mat1=new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.gallery);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);//
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),8866);
            }
        });

    }

    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==8866)
        {
            if(resultCode==RESULT_OK)
            {
                if(data!=null)
                {
                    Uri uri = data.getData();
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(uri);
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    Bitmap bmp32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Utils.bitmapToMat(bmp32, mat1);
                    Log.i("HE",data.getData()+"");
                    ArrayList<String> BODY_PARTS = new ArrayList<String>();
                    BODY_PARTS.add("Nose");
                    BODY_PARTS.add("Neck");BODY_PARTS.add("RShoulder");BODY_PARTS.add("RElbow");BODY_PARTS.add("RWrist");
                    BODY_PARTS.add("LShoulder");BODY_PARTS.add("LElbow");BODY_PARTS.add("LWrist");BODY_PARTS.add("RHip");
                    BODY_PARTS.add("RKnee");BODY_PARTS.add("RAnkle");BODY_PARTS.add("LHip");BODY_PARTS.add("LKnee");BODY_PARTS.add("LAnkle");
                    BODY_PARTS.add("REye");BODY_PARTS.add("LEye");BODY_PARTS.add( "REar");BODY_PARTS.add( "LEar");BODY_PARTS.add("Background");
                    String[][] POSE_PAIRS = new String[17][2];
                    POSE_PAIRS[0][0] = "Neck";POSE_PAIRS[0][1] = "RShoulder";
                    POSE_PAIRS[1][0] = "Neck";POSE_PAIRS[1][1] = "LShoulder";
                    POSE_PAIRS[2][0] = "RShoulder";POSE_PAIRS[2][1] = "RElbow";
                    POSE_PAIRS[3][0] = "RElbow";POSE_PAIRS[3][1] = "RWrist";
                    POSE_PAIRS[4][0] = "LShoulder";POSE_PAIRS[4][1] = "LElbow";
                    POSE_PAIRS[5][0] = "LElbow";POSE_PAIRS[5][1] = "LWrist";
                    POSE_PAIRS[6][0] = "Neck";POSE_PAIRS[6][1] = "RHip";
                    POSE_PAIRS[7][0] = "RHip";POSE_PAIRS[7][1] = "RKnee";
                    POSE_PAIRS[8][0] = "RKnee";POSE_PAIRS[8][1] = "RAnkle";
                    POSE_PAIRS[9][0] = "Neck";POSE_PAIRS[9][1] = "LHip";
                    POSE_PAIRS[10][0] = "LHip";POSE_PAIRS[10][1] = "LKnee";
                    POSE_PAIRS[11][0] = "LKnee";POSE_PAIRS[11][1] = "LAnkle";
                    POSE_PAIRS[12][0] = "Neck";POSE_PAIRS[12][1] = "Nose";
                    POSE_PAIRS[13][0] = "Nose";POSE_PAIRS[13][1] = "REye";
                    POSE_PAIRS[14][0] = "REye";POSE_PAIRS[14][1] = "REar";
                    POSE_PAIRS[15][0] = "Nose";POSE_PAIRS[15][1] = "LEye";
                    POSE_PAIRS[16][0] = "LEye";POSE_PAIRS[16][1] = "LEar";
                    //String input = getPath("COCO_val2014_000000000589.jpg", this);
                    String proto = getPath("openpose_pose_coco.prototxt", this);
                    String model = getPath("pose_iter_440000.caffemodel", this);
                    int inWidth = 368;
                    int inHeight = 368;
                    Log.d("DEBUG_CAMERA",data.getDataString());
                    //VideoCapture videoCapture = new VideoCapture(input);
                    //Log.d("DEBUG_CAMERA",videoCapture.toString());
                    Net net = Dnn.readNetFromCaffe(proto, model);

                    //Boolean hasFrame = videoCapture.read(mat1);
                        int frameWidth = mat1.width();
                        int frameHeight = mat1.height();
                        Log.d("DEBUG",frameHeight + " " + frameWidth + " " + mat1);
                        Mat inp = Dnn.blobFromImage(mat1, 1, new Size(368, 368), new Scalar(0,0,0),false,false);
                        Log.d("See Hererere", inp.height() + " " + inp.width() + " " + inp.channels());
                        net.setInput(inp);
                        Mat out = net.forward();
                        Log.d("OUTPUT", out + "");

                        Point[] points = new Point[BODY_PARTS.size()];
                        for(int i=0;i<BODY_PARTS.size();i++)
                        {
                            MatOfDouble doubles = new MatOfDouble(out.get(0,i));
                            Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(doubles);
                            Double conf = minMaxLocResult.maxVal;
                            Point point = minMaxLocResult.maxLoc;
                            double x = (frameWidth*point.x)/out.cols();
                            double y = (frameHeight*point.y)/out.rows();
                            if(conf>0.1){points[i]=point;}
                            else points[i]=null;

                        }
                        for(int i=0;i<points.length;i++)
                        {

                            Log.d("LOOOOOO",points[i] + "");
                        }

//                        for(int j=0;j<POSE_PAIRS.length;j++)
//                        {
//                            String partfrom = POSE_PAIRS[j][0];
//                            String partto = POSE_PAIRS[j][1];
//                            int idfrom = BODY_PARTS.indexOf(partfrom);
//                            int idto = BODY_PARTS.indexOf(partto);
//                            if (points[idfrom]!=null && points[idto]!=null)
//                            {
//                                Imgproc.line(mat1,points[idfrom],points[idto],new Scalar(0,255,0),3);
//                                Imgproc.ellipse(mat1,points[idfrom],new Size(3,3),0,0,360,new Scalar(0,0,255),FILLED);
//                                Imgproc.ellipse(mat1,points[idto],new Size(3,3),0,0,360,new Scalar(0,0,255),FILLED);
//                            }
//                        }
                        ImageView imageView1 = findViewById(R.id.imageView);
                        Bitmap bitmap1 = ((BitmapDrawable)imageView1.getDrawable()).getBitmap();
                        Utils.matToBitmap(mat1,bitmap1);
                        imageView.setImageBitmap(bitmap1);
                    }

                }
            }
        }


    private static String getPath(String file, Context context) {
        AssetManager assetManager = context.getAssets();
        BufferedInputStream inputStream = null;
        try {
            // Read data from assets.
            inputStream = new BufferedInputStream(assetManager.open(file));
            byte[] data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
            // Create copy file in storage.
            File outFile = new File(context.getFilesDir(), file);
            FileOutputStream os = new FileOutputStream(outFile);
            os.write(data);
            os.close();
            // Return a path to file which may be read in common way.
            return outFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.i("TAG", "Failed to upload a file");
        }
        return "";
    }
}
