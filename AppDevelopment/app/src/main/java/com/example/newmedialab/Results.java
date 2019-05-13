package com.example.newmedialab;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class Results extends AppCompatActivity {

    public Experiment exp = new Experiment("");
    public EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Intent i = getIntent();
        exp = (Experiment) i.getSerializableExtra("experiment");
        et = findViewById(R.id.editText);
        et.setText(exp.getName());
    }

    public void exportResults(View view) {
        String subdir = "/KineTest/Experiments/" + exp.name + "/";
        String subdir2 = "/KineTest/Exported/" + et.getText() + "/";
        File root = new File(Environment.getExternalStorageDirectory() + subdir);
        File export_root = new File(Environment.getExternalStorageDirectory() + subdir2);
        if (!export_root.exists()) export_root.mkdirs();
        try {
            if(android.os.Build.VERSION.SDK_INT >= 26){
                File export_root2 = new File(export_root + "/results.zip");
                pack(root.toPath(), export_root2.toPath());
            } else {
                FileUtils.copyDirectory(root, export_root);
            }
            Toast mToastToShow = Toast.makeText(this, "Exported results to: "+export_root.toString(), Toast.LENGTH_LONG);
            mToastToShow.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void pack(final Path folder, final Path zipFilePath) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(zipFilePath.toFile());
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {
            Files.walkFileTree(folder, new SimpleFileVisitor<Path>() {
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(file).toString()));
                    Files.copy(file, zos);
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }

                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    zos.putNextEntry(new ZipEntry(folder.relativize(dir).toString() + "/"));
                    zos.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }
}
