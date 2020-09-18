package com.example.demo.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class TestController {
    private static AtomicBoolean flag = new AtomicBoolean(false);

    @GetMapping(value = "/test")
    public String test() {
        return "test";
    }

    public static void main(String[] args) {
        String word = "计算机学院-软件工程-小A,计算机学院-网络技术-小B,金融学院-国际金融-小C,计算机学院-软件工程-小D";
        String[] bigSplit = word.split(",");
        Set<String> rootSet = new HashSet<>();
        JSONArray jsonArray = new JSONArray();
        for (String split : bigSplit) {
            String[] everyWord = split.split("-");
            for (int i = 0; i < everyWord.length; i++) {
                if (i > 0) {
                    JSONObject json = new JSONObject();
                    String parent = everyWord[i - 1];
                    String value = everyWord[i];
                    json.put("item", value);
                    json.put("parent", parent);
                    jsonArray.add(json);
                } else {
                    rootSet.add(everyWord[i]);
                }
            }
        }
        //[{"item":"计算机学院","children":[
        // {"item":"软件工程","children":[
        // {"item":"小A","children":[]}
        // ]}
        // ]}]
        JSONArray out = new JSONArray();
        rootSet.forEach(root -> {
            JSONObject tree = collectChildren(root, jsonArray);
            out.add(tree);
        });
        System.out.println(out.toJSONString());
    }

    private static JSONObject collectChildren(String item, JSONArray jsonArray) {
        JSONObject tree = new JSONObject();
        JSONArray children = new JSONArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject child = jsonArray.getJSONObject(i);
            String parent = child.getString("parent");
            if (parent.equals(item)) {
                child = collectChildren(child.getString("item"), jsonArray);
                children.add(child);
            }
        }
        tree.put("item",item);
        if (children.size() > 0) {
            tree.put("children", children);
        }
        return tree;
    }

    private static void readMultiThread() {
        LocalDateTime localDateTime = LocalDateTime.now();
        ThreadPoolExecutor threadPoolExecutor =
                new ThreadPoolExecutor(20, 20, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue());
        File file = new File("C:\\Users\\xinqi\\Desktop\\test.txt");
        try {
            BufferedReader inputStream = new BufferedReader(new FileReader(file));
            for (int i = 0; i < 20; i++) {
                threadPoolExecutor.submit(() -> {
                    try {
                        readThread(inputStream, threadPoolExecutor, localDateTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void readThread(BufferedReader reader, ThreadPoolExecutor threadPoolExecutor, LocalDateTime localDateTime) throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line + ">>>>当前线程：" + Thread.currentThread().getName());
        }
        if (line == null && !flag.get()) {
            flag.set(true);
            threadPoolExecutor.shutdown();
            Thread.sleep(1000);
            reader.close();
            Duration duration = Duration.between(localDateTime, LocalDateTime.now());
            System.out.println(duration.toMillis());
        }
    }

    private static void read() {
        File file = new File("C:\\Users\\xinqi\\Desktop\\test.txt");
        byte[] bytes = new byte[1024];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            while (inputStream.read(bytes) > 0) {
                System.out.println(new String(bytes));
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void write() {
        String test = "测试";
        File file = new File("C:\\Users\\xinqi\\Desktop\\test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
            fileWriter.write(test);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
