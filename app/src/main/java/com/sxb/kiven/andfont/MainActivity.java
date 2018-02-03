package com.sxb.kiven.andfont;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private File[] files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File floder = new File("system/fonts");
        if (floder.exists()){
            if (floder.isFile()){
                log("是文件");
            }else if (floder.isDirectory()){
                files = floder.listFiles();
                if (files != null && files.length > 0){
                    // 排序
                    Arrays.sort(files, new Comparator<File>() {
                        @Override
                        public int compare(File o1, File o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });

                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(new MyAdapter());

                }else {
                    log("是空文件夹");
                }
            }else {
                log("都不是");
            }
        }else {
            log("文件夹不存在");
        }



        File systemFontConfigLocation = new File("/system/etc/");
        File configFilename = new File(systemFontConfigLocation, "fonts.xml");
        /*File configFilename = new File(systemFontConfigLocation, "fallback_fonts.xml");*/
        try {
            /*for (File file : systemFontConfigLocation.listFiles()) {
                Log.i("sub", file.getAbsolutePath());
            }*/

            FileInputStream fin = new FileInputStream(configFilename);
            if (fin != null) {
                int length = fin.available();

                byte[] buffer = new byte[length];

                fin.read(buffer);

                Log.i("xml", new String(buffer));

                fin.close();
            }
        } catch (Exception e) {

        }
    }

    private void log(String info){
        Log.e("default_log", info);
    }

    class MyAdapter extends BaseAdapter{
        private AssetManager assetManager = null;
        private TTFParser ttfParser = null;
        public MyAdapter(){
            assetManager = getAssets();
            ttfParser = new TTFParser();
        }
        @Override
        public int getCount() {
            return files.length;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Object getItem(int position) {
            return files[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = null;
            if (convertView == null){
                textView = new TextView(getBaseContext());
                textView.setPadding(10, 10, 10, 10);
                textView.setBackgroundColor(Color.parseColor("#eeeeee"));
                textView.setTextColor(Color.parseColor("#000000"));

                convertView = textView;
            }else {
                textView = (TextView) convertView;
            }

            File file = (File) getItem(position);
            if (file.isFile()){
                if (file.getName().endsWith(".ttf") || file.getName().endsWith(".otf")){
                    try {
                        Typeface typeface = Typeface.createFromFile(file.getPath());
                        ttfParser.parse(file.getPath());

                        textView.setTypeface(typeface);
                        textView.setText(position + " " + file.getName() + "\n\n字体名称: " + ttfParser.getFontName()
                                + "\n\n familyName: " + ttfParser.getFontPropertie(TTFParser.FAMILY_NAME)
                                + "\n\n subFamilyName: " + ttfParser.getFontPropertie(TTFParser.FONT_SUBFAMILY_NAME)
                                + "\n\nHello word, 1234567890 !\n\n你好,世界!");
                    }catch (Exception e) {
                        textView.setText(file.getName() + "创建错误");
                    }
                }else {
                    textView.setText(file.getName() + " 不是TTF文件");
                }
            }else {
                textView.setText(file.getName() + " 不是文件");
            }

            return convertView;
        }
    }
}
