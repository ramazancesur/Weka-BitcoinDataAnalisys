package tr.edu.gazi.bilisim.veri.madencik.helper;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Created by Ramazan CESUR on 25.1.2021.
 */
public class CsvConverter<T extends Serializable> {

    private Helper helper = new Helper();

    public File listToCsvFile(List<T> lstData, String path,
                              String fileName, Class<T> clazz, Field resultField) {
        Set<Object> setResult = new HashSet<>();
        final String[] inwritedString = {"@RELATİON " + fileName.replace(".", "") + "\n"};

        List<Object> result = new LinkedList<>();
        List<String> lstHeader = new LinkedList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            resultField.setAccessible(true);
            if (!resultField.equals(field)) {
                inwritedString[0] = inwritedString[0].concat(" @ATTRIBUTE " + field.getName() + "  REAL" + "\n");
            } else {
                // its occured be data lstData
                lstData.stream()
                        .forEach(x -> {
                            try {
                                Object resultData = resultField.get(x);
                                setResult.add(resultData);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });


                //YAPacağını biliyorum bana güveniyor sadece
                //:D
                String classes = "{";

                int sayac = 0;
                for (Object data : setResult) {
                    if (sayac == 0) {
                        classes = classes.concat(data.toString());
                        sayac++;
                    } else {
                        classes = classes.concat("," + data.toString());
                    }
                }

                classes += "}";

                classes = "\n @ATTRIBUTE class\t" + classes;

                inwritedString[0] += classes;
            }


        }
        inwritedString[0] += "\n @DATA  \n";
        int[] sayac = {1};
        lstData
                .forEach(data -> {
                    List<String> lstStringData = new LinkedList<String>();
                    Field[] fields = data.getClass().getDeclaredFields();
                    sayac[0]++;
                    for (Field field : fields) {
                        field.setAccessible(true);
                        try {
                            Object value = field.get(data);
                            if (value != null) {
                                lstStringData.add(value.toString());
                            } else {
                                lstStringData.add("?");
                            }

                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    int counter = 0;
                    for (String dataString : lstStringData) {
                        if (counter == 0) {
                            inwritedString[0] += dataString;
                        } else if (counter == lstStringData.size() - 1) {
                            inwritedString[0] += "," + dataString + "\n";
                        } else {
                            inwritedString[0] += "," + dataString;
                        }
                        counter++;
                    }
                });

        String filePath = (path + "/" + fileName);
        return helper.writeFile(filePath, inwritedString[0]);
    }


    public void createArffFile(File csvFile, String wekaArffFileName) throws IOException {
        CSVLoader loader = new CSVLoader();

        loader.setSource(new File(csvFile.getAbsolutePath()));
        Instances data = loader.getDataSet();

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(wekaArffFileName));
        saver.setDestination(new File(wekaArffFileName));
        saver.writeBatch();
    }

}
