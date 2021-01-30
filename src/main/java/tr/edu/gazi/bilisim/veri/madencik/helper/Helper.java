package tr.edu.gazi.bilisim.veri.madencik.helper;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.namics.commons.random.RandomData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.reflections.Reflections;
import tr.edu.gazi.bilisim.veri.madencik.model.Model;
import weka.classifiers.AbstractClassifier;
import weka.core.Instances;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


/**
 * Created by Ramazan CESUR on 25.1.2021.
 */
public class Helper {
    private static Instances data;

    public ArrayList<Class<? extends AbstractClassifier>> getAllClassForPackageName(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends AbstractClassifier>> subTypes = reflections.getSubTypesOf(AbstractClassifier.class);
        ArrayList<Class<? extends AbstractClassifier>> lstSubTypes = new ArrayList(subTypes);
        return lstSubTypes;
    }


    public BufferedReader readResourceFileAsBufferedReader(String resource) {
        URL url = com.google.common.io.Resources.getResource(resource);
        try {
            CharSource charSource = com.google.common.io.Resources.asCharSource(url, Charsets.UTF_8);
            return charSource.openBufferedStream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getAbsolitePath(String resourcePath) {
        URL url = Resources.getResource(resourcePath);
        return url.getPath();
    }

    public String getAbsolitePath() {
        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            String decodedPath = URLDecoder.decode(path, "UTF-8");
            return decodedPath;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    public Instances getDataInstance(String filePath) {
        if (data == null) {
            BufferedReader datafile = this.readResourceFileAsBufferedReader(filePath);
            try {
                data = new Instances(datafile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }

    public <T extends Object> T createDummyData(Class<T> clazz) {
        return RandomData.random(clazz);
    }

    public void writeExcel(String path, List<Model> lstModel) {
        //Blank workbook
        XSSFWorkbook workbook = new XSSFWorkbook();

        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Employee Data");


        int rownum = 0;
        for (Model model : lstModel) {
            //create a row of excelsheet
            Row row = sheet.createRow(rownum++);

            int cellnum = 0;

            Cell cell = row.createCell(cellnum++);
            cell.setCellValue((String) model.getData());
        }
        try {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(path));
            workbook.write(out);
            out.close();
            System.out.println("howtodoinjava_demo.xlsx written successfully on disk.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String jsonDataDownload(String pageUrl) {
        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;

        String dataUrl = "";
        // sana bir ödev ... nın anlamına bir bak nerde kullanılıyor
        try {
            url = new URL(pageUrl);
            is = url.openStream();  // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            while ((line = br.readLine()) != null) {
                dataUrl += "\n" + line;
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException ioe) {
                // nothing to see here
            }
        }
        System.out.println(dataUrl);
        return dataUrl;
    }

    public String readResourceFileAsString(String resource) {
        URL url = com.google.common.io.Resources.getResource(resource);
        try {
            return com.google.common.io.Resources.toString(url, Charsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

    }

    public File writeFile(String filePath, String content) {

        // (path + "/" + fileName
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filePath), "utf-8"))) {

            writer.write(content);
            System.out.println("işlem başarılı...");
            return new File(filePath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Instances getWekaData(String dataPath, int resultColumns) {
        BufferedReader datafile = this.readResourceFileAsBufferedReader(dataPath);
        Instances data = null;
        try {
            data = new Instances(datafile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (resultColumns != -1) {
            data.setClassIndex(resultColumns);
        } else {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }


    public Properties loadProperties(String filename) {
        URL url = Resources.getResource(filename);
        final ByteSource byteSource = Resources.asByteSource(url);
        final Properties props = new Properties();

        try (InputStream inputStream = byteSource.openBufferedStream()) {
            props.load(inputStream);
            props.list(System.out);
        } catch (IOException e) {
            System.out.println("openBufferedStream failed! " +e.getMessage());
        }
        return props;
    }


    public Properties getRestrictProp(String propsFile, String restrictElement) {
        Properties props = new Properties();

        Properties properties = this.loadProperties(propsFile);
        properties.entrySet().stream()
                .filter(entrySet -> entrySet.getKey().toString().contains(restrictElement))
                .forEach(x -> {
                    props.put(x.getKey(), x.getValue());
                });
        return props;
    }


}
