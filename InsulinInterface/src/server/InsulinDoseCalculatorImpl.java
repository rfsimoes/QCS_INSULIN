/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import javax.jws.WebService;
import org.apache.commons.math.stat.regression.SimpleRegression;

@WebService(endpointInterface = "server.InsulinDoseCalculator")
public class InsulinDoseCalculatorImpl implements InsulinDoseCalculator {

    @Override
    public int mealtimeInsulinDose(int carbohydrateAmount, int carbohydrateToInsulinRatio, int preMealBloodSugar, int targetBloodSugar, int personalSensitivity) {
        try {
            if (targetBloodSugar > preMealBloodSugar) {
                return 0;
            }
            int carbohydrateDose =(int) Math.round((double)carbohydrateAmount / carbohydrateToInsulinRatio / personalSensitivity *50);
            int highBloodSugarDose = (int) Math.round((double)(preMealBloodSugar - targetBloodSugar) / personalSensitivity);
            return carbohydrateDose + highBloodSugarDose;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public int backgroundInsulinDose(int bodyWeight) {
        try {
            return (int) Math.round(0.5 * 0.55 * bodyWeight);
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
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
