/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voter;

import server.InsulinDoseCalculator;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import server.InsulinDoseCalculatorInterface;

/**
 *
 * @author David
 */
public class Voter {

    Voter thisInstance;
    int[] results;
    static final int MEALTIME_INSULINE_STANDART = 0;
    static final int MEALTIME_INSULINE_PERSONALIZED = 1;
    static final int BACKGROUND_INSULINE = 2;

    ArrayList<WebService> webServiceList;
    public int majorResult;

    public Voter() {
        thisInstance = this;
        webServiceList = new ArrayList<WebService>();
        webServiceList.add(new WebService("http://localhost:8080/InsulinDoseCalculator?wsdl", "http://server/", "InsulinDoseCalculatorService"));
        webServiceList.add(new WebService("http://qcs01.dei.uc.pt:8080/InsulinDoseCalculator?wsdl", "http://server/", "InsulinDoseCalculatorService"));
        webServiceList.add(new WebService("http://liis-lab.dei.uc.pt:8080/Server?wsdl", "http://server/", "InsulinDoseCalculatorService"));
        webServiceList.add(new WebService("http://qcs06.dei.uc.pt:8080/insulin?wsdl", "http://server/", "InsulinDoseCalculatorService"));
        webServiceList.add(new WebService("http://qcs07.dei.uc.pt:8080/insulin?wsdl", "http://server/", "InsulinDoseCalculatorService"));
    }

    public ArrayList<Integer> vec;
    Map<Integer, Float> hashmap;
    boolean finishedCalculations;

    private int getService(int method) {
        //save results in array
        vec = new ArrayList<>();
        finishedCalculations = false;
        // save results in hashmap
        hashmap = new HashMap<>();

        ExecutorService fixedPool = Executors.newFixedThreadPool(webServiceList.size());

        // Create a Runnable class
        class Worker implements Runnable {

            String wsdl, namespace, webService;

            Worker(String wsdl, String namespace, String webService) {
                this.wsdl = wsdl;
                this.namespace = namespace;
                this.webService = webService;
            }

            @Override
            public void run() {
                int retries = 0;
                while (true) {
                    try {
                        URL url = new URL(wsdl);

                        //1st argument service URI, refer to wsdl document above
                        //2nd argument is service name, refer to wsdl document above
                        QName qname = new QName(namespace, webService);
                        Service service = Service.create(url, qname);
                        InsulinDoseCalculatorInterface calculator = service.getPort(new QName(namespace, "InsulinDoseCalculatorPort"), InsulinDoseCalculatorInterface.class);
                        if (method == MEALTIME_INSULINE_STANDART) {
                            int result = calculator.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
                            System.out.println("Insuline Dose: " + result + "  (" + wsdl + ")");
                            if (!finishedCalculations) {
                                synchronized (thisInstance) {
                                    vec.add(result);
                                }
                            }
                        } else if (method == MEALTIME_INSULINE_PERSONALIZED) {
                            personalSensitivity = calculator.personalSensitivityToInsulin(physicalActivityLevel, physicalActivitySamples, bloodSugarDropSamples);
                            System.out.println("Personal Sensivity: " + personalSensitivity + "  (" + wsdl + ")");
                            //Personal Sensivity must be between 15 and 100 mg/dl
                            if (!(personalSensitivity >= 15 && personalSensitivity <= 100)) {
                                break;
                            }
                            int result = calculator.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
                            System.out.println("Insuline Dose: " + result + "  (" + wsdl + ")");
                            if (!finishedCalculations) {
                                synchronized (thisInstance) {
                                    vec.add(result);
                                }
                            }
                        } else if (method == BACKGROUND_INSULINE) {
                            int result = calculator.backgroundInsulinDose(bodyWeight);
                            System.out.println("Insuline Dose: " + result + "  (" + wsdl + ")");
                            if (!finishedCalculations) {
                                synchronized (thisInstance) {
                                    vec.add(result);
                                }
                            }
                        }
                        return;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        try {
                            retries += 1;
                            if (retries > 8) {
                                return;
                            }
                            //We retry the connection every 500ms
                            Thread.sleep(500);
                        } catch (InterruptedException ex1) {
                            System.out.println("A working thread just had a timeout");
                            return;
                            //Logger.getLogger(Voter.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                }
            }
        };

        for (WebService webServiceList1 : webServiceList) {
            Worker worker = new Worker(webServiceList1.wsdl, webServiceList1.namespace, webServiceList1.serviceName);
            fixedPool.submit(worker);
        }
        fixedPool.shutdown();
        try {
            System.out.println("\n------------------\n");
            System.out.println("waiting for pool to finish");
            if (fixedPool.awaitTermination(4, TimeUnit.SECONDS)) {
                System.out.println("Every thread finished with success");
            } else {
                System.out.println("Some threads still working. Shutting them down");
                fixedPool.shutdownNow();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Voter.class.getName()).log(Level.SEVERE, null, ex);
        }
        finishedCalculations = true;
        System.out.println("tamanho do vec: " + vec.size());
        if (vec.size() <= 1) {
            System.out.println("houve demasiados timeouts");
            return -2;
        }
        //AQUI LEVA O CÓDIGO DO VOTADOR. OS RESULTADOS OBTIDOS ESTÃO NO VEC
        for (int i = 0; i < vec.size(); i++) {
            System.out.println("Resultado:" + vec.get(i));
            // Se o valor não estiver no hasmap, metemo-lo lá com o contador a 1.0001
            if (!hashmap.containsKey(vec.get(i))) {
                hashmap.put(vec.get(i), 1.0001f);
            } else {
                hashmap.put(vec.get(i), hashmap.get(vec.get(i)) + 1.0001f);
            }
            if (!hashmap.containsKey(vec.get(i) - 1)) {
                hashmap.put(vec.get(i) - 1, 1f);
            } else {
                hashmap.put(vec.get(i) - 1, hashmap.get(vec.get(i) - 1) + 1);
            }
            if (!hashmap.containsKey(vec.get(i) + 1)) {
                hashmap.put(vec.get(i) + 1, 1f);
            } else {
                hashmap.put(vec.get(i) + 1, hashmap.get(vec.get(i) + 1) + 1);
            }
        }

        // Ir buscar o resultado maioritário
        float majorCount = 0;
        majorResult=0;
        boolean maiority = false;
        for (int key : hashmap.keySet()) {
            if (vec.contains(key) && hashmap.get(key) > majorCount) {
                majorCount = hashmap.get(key);
                majorResult = key;
                maiority = true;
            } else if (hashmap.get(key) == majorCount) {
                maiority = false;
            }
        }
        if (maiority) {
            System.out.println("Resutado maioritário: " + majorResult);
            return majorResult;
        } else {
            System.out.println("Não houve maioria");
            return -1;
        }
    }

    private int carbohydrateAmount,
            carbohydrateToInsulinRatio,
            preMealBloodSugar,
            targetBloodSugar,
            personalSensitivity;

    public int mealtimeInsulinDoseCalculationUsingStandardInsulinSensitivity(int carbohydrateAmount,
            int carbohydrateToInsulinRatio,
            int preMealBloodSugar,
            int targetBloodSugar,
            int personalSensitivity) {
        this.carbohydrateAmount = carbohydrateAmount;
        this.carbohydrateToInsulinRatio = carbohydrateToInsulinRatio;
        this.preMealBloodSugar = preMealBloodSugar;
        this.targetBloodSugar = targetBloodSugar;
        this.personalSensitivity = personalSensitivity;

        return getService(MEALTIME_INSULINE_STANDART);
    }

    private int bodyWeight;

    public int backgroundInsulinDoseCalculation(int bodyWeight) {
        this.bodyWeight = bodyWeight;

        return getService(BACKGROUND_INSULINE);
    }

    private int physicalActivityLevel;
    private int[] physicalActivitySamples;
    private int[] bloodSugarDropSamples;

    public int personalSensitivityToInsulinCalculation(int carbohydrateAmount,
            int carbohydrateToInsulinRatio,
            int preMealBloodSugar,
            int targetBloodSugar,
            int physicalActivityLevel,
            int[] physicalActivitySamples,
            int[] bloodSugarDropSamples) {
        this.carbohydrateAmount = carbohydrateAmount;
        this.carbohydrateToInsulinRatio = carbohydrateToInsulinRatio;
        this.preMealBloodSugar = preMealBloodSugar;
        this.targetBloodSugar = targetBloodSugar;
        this.physicalActivityLevel = physicalActivityLevel;
        this.physicalActivitySamples = physicalActivitySamples;
        this.bloodSugarDropSamples = bloodSugarDropSamples;

        return getService(MEALTIME_INSULINE_PERSONALIZED);
    }

    class WebService {

        String wsdl;
        String namespace;
        String serviceName;

        WebService(String wsdl, String namespace, String serviceName) {
            this.wsdl = wsdl;
            this.namespace = namespace;
            this.serviceName = serviceName;
        }
    }
}
