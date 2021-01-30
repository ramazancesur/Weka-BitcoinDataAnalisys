package tr.edu.gazi.bilisim.veri.madencik;

import tr.edu.gazi.bilisim.veri.madencik.helper.CsvConverter;
import tr.edu.gazi.bilisim.veri.madencik.helper.CsvReader;
import tr.edu.gazi.bilisim.veri.madencik.helper.Helper;
import tr.edu.gazi.bilisim.veri.madencik.model.DataModel;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by Ramazan CESUR on 29.1.2021.
 */
public class Application {
    public static void main(String[] args) throws Exception {
        System.out.println("islem basladı");
        Helper helper = new Helper();

        CsvConverter<DataModel> converter = new CsvConverter<DataModel>();

        CsvReader reader = new CsvReader();
        List<DataModel> lstCleanData = new LinkedList<>();


        String absolitePath = helper.getAbsolitePath("csv/temizCoin.csv");

        List<DataModel> lstModel = reader.reader(absolitePath, ";", DataModel.class);
        lstModel.stream().forEach(dataModel -> {
            if (dataModel.getFark() < -20) {
                dataModel.setSinif(0);
            } else if (dataModel.getFark() < -10 && dataModel.getFark() >= -20) {
                dataModel.setSinif(1);
            } else if (dataModel.getFark() < 0 && dataModel.getFark() >= -10) {
                dataModel.setSinif(2);
            } else if (dataModel.getFark() < 10 && dataModel.getFark() >= 0) {
                dataModel.setSinif(3);
            } else if (dataModel.getFark() < 20 && dataModel.getFark() >= 10) {
                dataModel.setSinif(4);
            } else {
                dataModel.setSinif(5);
            }
            lstCleanData.add(dataModel);
        });


        for (int i = lstCleanData.size() - 1; i >= 0; i--) {
            if (i != 0) {
                DataModel currentModel = lstCleanData.get(i);
                DataModel prevModel = lstCleanData.get(i - 1);
                currentModel.setFark(prevModel.getFark());
                currentModel.setDusuk(prevModel.getDusuk());
                currentModel.setYuksek(prevModel.getYuksek());
                currentModel.setHacim(prevModel.getHacim());
                lstCleanData.set(i, currentModel);
            }
        }

        lstCleanData.forEach(dataModel -> {
            System.out.println(dataModel.toString());
        });

        String absPath = Application.class.getClassLoader().getResource("").getPath();
        File dataFile = null;
        try {
            dataFile = converter.listToCsvFile(lstCleanData, absPath, "testData.arff", DataModel.class,
                    DataModel.class.getDeclaredField("sinif"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }


        BufferedReader datafile = helper.readResourceFileAsBufferedReader("testData.arff");
        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);


        //do not use first and second
        Instance first = data.instance(0);
        Instance second = data.instance(1);


        //******************** Prediction ********************
        Classifier ibk = new J48();
        ibk.buildClassifier(data);

        double class1 = ibk.classifyInstance(first);
        double class2 = ibk.classifyInstance(second);


        System.out.println("first: " + class1 + "\nsecond: " + class2);


        //******************  MLP Model ******************
        //network variables
        String backPropOptions =
                "-L " + 0.1 //learning rate
                        + " -M " + 0 //momentum
                        + " -N " + 10000 //epoch
                        + " -V " + 0 //validation
                        + " -S " + 0 //seed
                        + " -E " + 0 //error
                        + " -H " + "3"; //hidden nodes.
        //e.g. use "3,3" for 2 level hidden layer with 3 nodes

        //network training
        MultilayerPerceptron mlp = new MultilayerPerceptron();
        mlp.setOptions(Utils.splitOptions(backPropOptions));
        mlp.buildClassifier(data);
        System.out.println("final weights:");
        System.out.println(mlp);

        IBk knn = new IBk(4);
        knn.buildClassifier(data);
        System.out.println("\n*********************  MLP Metrics: ********** ");
        Evaluation eval = new Evaluation(data);
        eval.evaluateModel(mlp, data);

        System.out.println(eval.toSummaryString("\nResults\n======\n", true));


        // eval.evaluateModel(knn,data);
        System.out.println("\n*********************  KNN Metrics: ********** ");
        eval.crossValidateModel(knn, data, 10, new Random(1));

        System.out.println(eval.toSummaryString("\nResults\n======\n", true));

        System.out.println("\n*********************  NAIVE BAYES Metrics: ********** ");

        NaiveBayes model = new NaiveBayes();
        model.buildClassifier(data);

        eval.crossValidateModel(knn, data, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", true));

        System.out.println("\n*********************  SVM Metrics: ********** ");
        SMO svm = new SMO();
        svm.buildClassifier(data);
        eval.crossValidateModel(knn, data, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", true));

        System.out.println("\n*********************   C4.5 Metrics: ********** ");
        J48 c45Tree = new J48();
        c45Tree.buildClassifier(data);
        eval.crossValidateModel(knn, data, 10, new Random(1));
        System.out.println(eval.toSummaryString("\nResults\n======\n", true));

    }
}
