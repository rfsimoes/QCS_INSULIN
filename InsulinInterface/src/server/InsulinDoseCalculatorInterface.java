package server;
import javax.jws.WebService;
import javax.jws.WebMethod;

@WebService
public interface InsulinDoseCalculatorInterface {
    @WebMethod
    int mealtimeInsulinDose(int carbohydrateAmount,
                      int carbohydrateToInsulinRatio,
                      int preMealBloodSugar,
                      int targetBloodSugar,
                      int personalSensitivity);
    
    @WebMethod
    int backgroundInsulinDose(int bodyWeight);
    
    @WebMethod
    int personalSensitivityToInsulin(int physicalActivityLevel,
                               int[] physicalActivitySamples,
                               int[] bloodSugarDropSamples);

}
