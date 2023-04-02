package com.mycompany.app;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ParseTxt {

    public static List<ArrayList<String>> readFish(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String st;
        ArrayList<String> fishList = new ArrayList<String>();
        List<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
        while((st = br.readLine()) != null){
            if(st.startsWith("* ")){
                if(fishList.size() != 0){
                    ArrayList<String> temp = (ArrayList<String>) fishList.clone();
                    result.add(temp);
                    fishList.clear();
                }
                fishList.add(st.substring(2));
            } else {
                fishList.add(st.substring(6));
            }
        }

        result.add((ArrayList<String>) fishList.clone());

        return result;
    }

    public static List<String[]> readLongLat(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String st;
        String[] fishList = new String[3];
        List<String[]> result = new ArrayList<String[]>();
        while((st = br.readLine()) != null) {
            if(st.equals("")){
                if(fishList.length != 0){
                    result.add(fishList.clone());
                }
                continue;
            }
            if(st.contains(", ")){
                String[] longlat = st.split(", ");
                fishList[1] = longlat[0];
                fishList[2] = longlat[1];
                continue;
            }
            fishList[0] = st;
        }

        return result;
    }

    public static List<String[]> readFishStatus(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(path)));
        String st;
        List<String[]> result = new ArrayList<String[]>();
        while((st = br.readLine()) != null) {
            String[] data = st.split(": ");
            String[] fishList = new String[2];
            fishList[0] = data[0].toLowerCase();
            fishList[1] = "";
            for(int j = 1; j <  data.length; j++){
                fishList[1]+=data[j];
            }
//            fishList[1] = data[1];
            result.add(fishList);
        }
        for(String[] i: result){
            for(String j : i) {
                System.out.println(j);
            }
            System.out.print("\n");
        }
        return result;
    }


    public static List<String[]> readCustomFish(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        String st;
        List<String[]> result = new ArrayList<String[]>();
        while((st = br.readLine()) != null) {
            String[] fishList = st.split("/");
            result.add(fishList);
        }
        return result;
    }
}
