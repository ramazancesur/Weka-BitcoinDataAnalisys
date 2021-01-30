package tr.edu.gazi.bilisim.veri.madencik.helper;

import tr.edu.gazi.bilisim.veri.madencik.customAnnotation.DataMatch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ramazan CESUR on 25.1.2021.
 */
public class CsvReader <T extends Serializable> {


    public static boolean setField(Object targetObject, String fieldName, Object fieldValue) {
        Field field;
        try {
            field = targetObject.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = null;
        }

        field.setAccessible(true);
        try {
            Class<?> type = field.getType();
            Property property = new Property<>(type);
            try {
                property.setValue(fieldValue.toString());
            } catch (NumberFormatException ex) {
                Double value = Double.parseDouble(fieldValue.toString().replace(".", "")) / 100;
                property.setValue(value.toString());

            }
            field.set(targetObject, property.value);
            return true;
        } catch (IllegalAccessException e) {
            return false;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return false;
    }

    public <T extends Serializable> List<T> reader(String readPath, String splitChareckter,
                                                   Class<T> clazz) {
        List<T> lstData = new LinkedList<>();
        String line = "";

        try (BufferedReader br = new BufferedReader(new FileReader(readPath))) {
            while ((line = br.readLine()) != null) {

                String[] readerArray = line.split(splitChareckter);
                int sayac = 0;
                T data = clazz.newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        if (field.isAnnotationPresent(DataMatch.class)) {
                            DataMatch dataMatch = field.getAnnotation(DataMatch.class);
                            int tempCount = 0;
                            if (dataMatch.index() != -1) {
                                tempCount = dataMatch.index();
                            } else {
                                tempCount = sayac;
                            }
                            if (readerArray[tempCount].contains("K")) {
                                String valueOfNoneK = readerArray[tempCount].replace(".", "").replace("K", "");
                                Double realValue = Double.valueOf(valueOfNoneK) * 1000;
                                readerArray[tempCount] = Double.toString(realValue);
                            }
                            setField(data, field.getName(), readerArray[tempCount]);
                        }
                        sayac++;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
                lstData.add(data);

            }

            return lstData;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
