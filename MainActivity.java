package com.example.talkingnotepad;

import android.app.*;
import android.speech.tts.TextToSpeech;
import android.speech.RecognizerIntent;
import android.os.*;
import android.widget.*;
import android.view.*;
import java.util.*;
import android.content.Intent;
import java.io.*;
import android.net.Uri;

public class MainActivity extends Activity implements View.OnClickListener
{
    private final int OPEN_FILE=1;
    private final int SAVE_FILE=2;
    private final int RECORD_VOICE=3;
    Button btnOpen,btnSave,btnSpeak,btnRecord,
            btnVoiceCommand,btnClear,btnHelp,btnAbout;
    EditText txtFileContents;
    TextToSpeech tts;
    boolean voiceCommandMode=false,recording=false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnOpen=(Button)findViewById(R.id.btnOpen);
        btnSave=(Button)findViewById(R.id.btnSave);
        btnSpeak=(Button)findViewById(R.id.btnSpeak);
        btnRecord=(Button)findViewById(R.id.btnRecord);
        btnVoiceCommand=(Button)findViewById(R.id.btnVoiceCommand);
        btnClear=(Button)findViewById(R.id.btnClear);
        btnHelp=(Button)findViewById(R.id.btnHelp);
        btnAbout=(Button)findViewById(R.id.btnAbout);
        txtFileContents=(EditText)findViewById(R.id.txtFileContents);
        btnOpen.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnSpeak.setOnClickListener(this);
        btnRecord.setOnClickListener(this);
        btnVoiceCommand.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnHelp.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
    }
    public void open()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent,OPEN_FILE);
    }
    public void save()
    {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE,"newfile.txt");
        startActivityForResult(intent,SAVE_FILE);
    }
    public void speak()
    {
        if(txtFileContents.getText().toString().trim().length()==0)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("Nothing to speak. Please type or record some text.");
            builder.setIcon(R.drawable.error);
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        else
        {
            tts=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener()
            {
                public void onInit(int status)
                {
                    if(status!=TextToSpeech.ERROR)
                    {
                        tts.setLanguage(Locale.US);
                        String str=txtFileContents.getText().toString();
                        tts.speak(str,TextToSpeech.QUEUE_ADD,null);
                    }
                }
            });
        }
    }
    public void record()
    {
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if(voiceCommandMode && !recording)
        {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak a command to be executed...");
        }
        else
        {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say something to record...");
        }
        startActivityForResult(intent,RECORD_VOICE);
    }
    public void clear()
    {
        txtFileContents.setText("");
    }
    public void help()
    {
        StringBuffer strHelp=new StringBuffer();
        strHelp.append("Following Voice Commands are supported: \n\n");
        strHelp.append("Open - Shows open file dialog box\n");
        strHelp.append("Save - Shows save file dialog box\n");
        strHelp.append("Speak - Reads file contents\n");
        strHelp.append("Record - Allows voice recording\n");
        strHelp.append("Clear - Clears file contents\n");
        strHelp.append("Help - Shows this help screen\n");
        strHelp.append("About - Shows About screen\n");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Help");
        builder.setMessage(strHelp.toString());
        builder.setIcon(R.drawable.help);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void about()
    {
        ImageView iv=new ImageView(this);
        StringBuffer strAbout=new StringBuffer();
        strAbout.append("Developed by Ishika Bansal\n");
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("About TalkingNotepad Android App");
        builder.setMessage(strAbout.toString());
        builder.setView(iv);
        builder.setIcon(R.drawable.about);
        AlertDialog dialog=builder.create();
        dialog.show();
    }
    public void onClick(View v)
    {
        voiceCommandMode=false;
        recording=false;
        Button b=(Button)v;
        if(b.getId()==R.id.btnOpen)
        {
            open();
        }
        if(b.getId()==R.id.btnSave)
        {
            save();
        }
        if(b.getId()==R.id.btnSpeak)
        {
            speak();
        }
        if(b.getId()==R.id.btnRecord)
        {
            record();
        }
        if(b.getId()==R.id.btnVoiceCommand)
        {
            voiceCommandMode=true;
            record();
        }
        if(b.getId()==R.id.btnClear)
        {
            clear();
        }
        if(b.getId()==R.id.btnHelp)
        {
            help();
        }
        if(b.getId()==R.id.btnAbout)
        {
            about();
        }
    }
    @Override
    protected void onPause()
    {
        if(tts!=null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case OPEN_FILE:
                if (resultCode == RESULT_OK)
                {
                    try
                    {
                        Uri uri = data.getData();
                        InputStream inputStream =
                                getContentResolver().openInputStream(uri);
                        BufferedReader reader =
                                new BufferedReader(new InputStreamReader(
                                        inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String currentline = "";
                        while ((currentline = reader.readLine()) != null) {
                            stringBuilder.append(currentline + "\n");
                        }
                        txtFileContents.setText(stringBuilder.toString().trim());
                        inputStream.close();
                        reader.close();
                    }
                    catch(Exception ex)
                    {
                        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                        builder.setCancelable(true);
                        builder.setTitle("Error");
                        builder.setMessage(ex.getMessage());
                        builder.setIcon(R.drawable.error);
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                }
                break;
            case SAVE_FILE:
                if(resultCode==RESULT_OK)
                {
                    try
                    {
                        Uri uri = data.getData();
                        ParcelFileDescriptor pfd =
                                this.getContentResolver().
                                        openFileDescriptor(uri, "w");

                        FileOutputStream fileOutputStream =
                                new FileOutputStream(
                                        pfd.getFileDescriptor());

                        String textContent = txtFileContents.getText().toString();
                        fileOutputStream.write(textContent.getBytes());
                        fileOutputStream.close();
                        pfd.close();
                    }
                    catch(Exception ex)
                    {
                        AlertDialog.Builder builder=new AlertDialog.Builder(this);
                        builder.setCancelable(true);
                        builder.setTitle("ERROR");
                        builder.setMessage(ex.getMessage());
                        builder.setIcon(R.drawable.error);
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                }
                break;
            case RECORD_VOICE:
                if(resultCode==RESULT_OK)
                {
                    ArrayList<String> result=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(voiceCommandMode)
                    {
                        String command=result.get(0);
                        if(command.toUpperCase().equals("OPEN")||
                                command.toUpperCase().startsWith("OP")||command.toUpperCase().startsWith("OB"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Open Command",Toast.LENGTH_SHORT).show();
                            open();
                        }
                        else if(command.toUpperCase().equals("SAVE")||
                                command.toUpperCase().startsWith("SA")||command.toUpperCase().startsWith("SE"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Save Command",Toast.LENGTH_SHORT).show();
                            save();
                        }
                        else if(command.toUpperCase().equals("SPEAK")||command.toUpperCase().startsWith("SPA")||
                                command.toUpperCase().startsWith("SPE")||command.toUpperCase().startsWith("SPI"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Speak Command",Toast.LENGTH_SHORT).show();
                            speak();
                        }
                        else if(command.toUpperCase().equals("RECORD")||
                                command.toUpperCase().startsWith("REC")||command.toUpperCase().startsWith("RAC")
                                ||command.toUpperCase().startsWith("RAK")||command.toUpperCase().startsWith("REK"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Record Command",Toast.LENGTH_SHORT).show();
                            recording=true;
                            record();
                        }
                        else if(command.toUpperCase().equals("CLEAR")||command.toUpperCase().equals("KLEAR")||
                                command.toUpperCase().startsWith("CLA")||command.toUpperCase().startsWith("CLE")||
                                command.toUpperCase().startsWith("CLI")||command.toUpperCase().startsWith("KLA")||
                                command.toUpperCase().startsWith("KLE")||command.toUpperCase().startsWith("KLI"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Clear Command",Toast.LENGTH_SHORT).show();
                            clear();
                        }
                        else if(command.toUpperCase().equals("HELP")||
                                command.toUpperCase().startsWith("HAL")||command.toUpperCase().startsWith("HEL")
                                ||command.toUpperCase().startsWith("HIL")||command.toUpperCase().startsWith("HUL"))
                        {
                            Toast.makeText(getBaseContext(),"Executing Help Command",Toast.LENGTH_SHORT).show();
                            help();
                        }
                        else if(command.toUpperCase().equals("ABOUT")||
                                command.toUpperCase().startsWith("ABA")||command.toUpperCase().startsWith("ABO"))
                        {
                            Toast.makeText(getBaseContext(),"Executing About Command",Toast.LENGTH_SHORT).show();
                            about();
                        }
                        else
                        {
                            Toast.makeText(getBaseContext(),"Unrecognized command",Toast.LENGTH_SHORT).show();
                            help();
                        }
                        voiceCommandMode=false;
                    }
                    else
                    {
                        String existing = txtFileContents.getText().toString();
                        txtFileContents.setText(existing + " "+ result.get(0));
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}