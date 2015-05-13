/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package voter;

import java.net.MalformedURLException;
import server.InsulinDoseCalculator;

import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import jdk.nashorn.internal.objects.NativeArray;

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

    public Voter() {
        thisInstance=this;
        webServiceList = new ArrayList<WebService>();
        webServiceList.add(new WebService("http://localhost:8080/InsulinDoseCalculator?wsdl", "http://server/", "InsulinDoseCalculatorImplService"));
        webServiceList.add(new WebService("http://qcs01.dei.uc.pt:8080/InsulinDoseCalculator?wsdl", "http://server/", "InsulinDoseCalculatorService"));
        webServiceList.add(new WebService("http://liis-lab.dei.uc.pt:8080/Server?wsdl", "http://server/", "InsulinDoseCalculatorService"));
    }

    ArrayList<Integer> vec;
    private int getService(int method) {
        //save results in array
        vec = new ArrayList<>();
        
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

                try {
                    
                    URL url = new URL(wsdl);
                    
                    //1st argument service URI, refer to wsdl document above
                    //2nd argument is service name, refer to wsdl document above
                    QName qname = new QName(namespace, webService);
                    Service service = Service.create(url, qname);
                    InsulinDoseCalculator calculator = service.getPort(InsulinDoseCalculator.class);
                    if(method==MEALTIME_INSULINE_STANDART){
                        int result = calculator.mealtimeInsulinDose(carbohydrateAmount, carbohydrateToInsulinRatio, preMealBloodSugar, targetBloodSugar, personalSensitivity);
                        System.out.println(wsdl);
                        System.out.println(result);
                        synchronized(thisInstance){
                            vec.add(result);
                        }                        
                    }
                    //FALTA ADICINAR OS OUTROS METODOS
                    else if(method==MEALTIME_INSULINE_PERSONALIZED){
                        
                    }
                    else if(method==BACKGROUND_INSULINE){
                        
                    }
                } catch (MalformedURLException ex) {
                    System.out.println("Malformed URL");
                    Logger.getLogger(Voter.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        };
        
        for (WebService webServiceList1 : webServiceList) {
            Worker worker = new Worker(webServiceList1.wsdl, webServiceList1.namespace, webServiceList1.serviceName);
            fixedPool.submit(worker);
        }
        fixedPool.shutdown();
        try {
            System.out.println("waiting for pool to finish");
            fixedPool.awaitTermination(4, TimeUnit.SECONDS);
            
            fixedPool.shutdownNow();
        } catch (InterruptedException ex) {
            Logger.getLogger(Voter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //AQUI LEVA O CÓDIGO DO VOTADOR. OS RESULTADOS OBTIDOS ESTÃO NO VEC
        for(int i=0;i<vec.size();i++){
            System.out.println("Resultado:" + vec.get(i));
        }
        
        //ISTO VAI DEVOLVER O RESULTADO DA VOTAÇÃO
        return 1;
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

    public static void main(String[] args) throws Exception {

        Voter voter = new Voter();
        voter.mealtimeInsulinDoseCalculationUsingStandardInsulinSensitivity( 60, 12, 200, 100, 25);
        
        System.out.println("bye bye");
        
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
