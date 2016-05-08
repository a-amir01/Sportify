package gspot.com.sportify.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by amir on 5/6/16.
 */
public class GatheringTypeProvider {

    public static HashMap<String, List<String>> getDataHashMap() {
        HashMap<String, List<String>> countriesHashMap = new HashMap<String, List<String>>();

        List<String> zimbabweList = new ArrayList<String>();
        List<String> southAfricaList = new ArrayList<String>();
        List<String> zambiaList = new ArrayList<String>();

        for (int i = 0; i < GatheringTypes.zimbabweArray.length; i++) {
            zimbabweList.add(GatheringTypes.zimbabweArray[i]);
        }

        for (int i = 0; i < GatheringTypes.south_africaArray.length; i++) {
            southAfricaList.add(GatheringTypes.south_africaArray[i]);
        }

        for (int i = 0; i < GatheringTypes.zambiaArray.length; i++) {
            zambiaList.add(GatheringTypes.zambiaArray[i]);
        }

        countriesHashMap.put("Zimbabwe", zimbabweList);
        countriesHashMap.put("South Africa", southAfricaList);
        countriesHashMap.put("Zambia", zambiaList);

        return countriesHashMap;
    }
}
