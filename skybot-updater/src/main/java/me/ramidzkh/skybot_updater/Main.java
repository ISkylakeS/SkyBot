package me.ramidzkh.skybot_updater;

import okhttp3.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    
    public static final String java;
    public static final File file = new File("skybot.jar");

    static {
        String j = System.getProperty("java");
        if(j == null) j = "C:\\Program Files\\Java\\jre1.8.0_131\\bin\\java.exe";
        
        java = j;
    }

    public static void main(String[] args) throws IOException {
        if(!file.exists())
            try {
                handleDownloadFile();
                System.out.println("Successfully downloaded latest JAR file!");
            } catch (IOException e) {
                System.err.println("Couldn't download latest JAR file");
                e.printStackTrace();
                
                System.exit(1);
            }
        
        ProcessHandler handler;
        
        while(true) {
            try {
                // Fix here
                handler = new ProcessHandler();
                
                handler.bind();

                while(handler.process.isAlive());
                
                int exit = handler.returnCode();
                
                if(exit == 0x5454) {
                    handleDownloadFile();
                    System.out.println("Successfully downloaded latest JAR file!");
                } else {
                    System.out.printf("Program exited with exit code %s. Goodbye!\n", exit);
                    break;
                }
            } catch (IOException e) {
                System.err.printf("Failed starting SkyBot (%s -jar %s)\n", java, file.getName());
                System.exit(1);
            }
        }
    }

    public static void handleDownloadFile()
    throws IOException {
        if(file.exists())
            file.delete();
        
        GithubRequester.downloadLatest(new OkHttpClient(), new FileOutputStream(file));
    }
}