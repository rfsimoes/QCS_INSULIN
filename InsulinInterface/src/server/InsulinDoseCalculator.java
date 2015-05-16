/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import org.apache.commons.math.stat.regression.SimpleRegression;

@WebService()
public class InsulinDoseCalculator implements InsulinDoseCalculatorInterface {

    @WebMethod
    public int mealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity) {
        try {
            if (targetBloodSugar > preMealBloodSugar) {
                return 0;
            }
            double carbohydrateDose =(double)carbohydrateAmount / carbohydrateToInsulinRatio / personalSensitivity *50.0;
            double highBloodSugarDose = (double)(preMealBloodSugar - targetBloodSugar) / personalSensitivity;
            return (int) Math.round(carbohydrateDose + highBloodSugarDose);
        } catch (Exception e) {
            return -1;
        }
    }

    @WebMethod
    public int backgroundInsulinDose(int bodyWeight) {
        try {
            return (int) Math.round(0.5 * 0.55 * bodyWeight);
        } catch (Exception e) {
            return -1;
        }
    }

    @WebMethod
    public int personalSensitivityToInsulin(int physicalActivityLevel, int[] physicalActivitySamples, int[] bloodSugarDropSamples) {
        try {
            if(physicalActivitySamples.length<2 || (physicalActivitySamples.length!=bloodSugarDropSamples.length) ){
                return -1;
            }
            SimpleRegression regression = new SimpleRegression();
            for(int i=0;i<physicalActivitySamples.length;i++){
                regression.addData(physicalActivitySamples[i], bloodSugarDropSamples[i]);
            }
            double alpha=regression.getIntercept();
            double beta = regression.getSlope();
            return (int) Math.round(alpha+beta*physicalActivityLevel);
        } catch (Exception e) {
            return -1;
        }
    }

}
