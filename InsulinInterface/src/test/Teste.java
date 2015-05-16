/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author David
 */
public class Teste {

    public static void main(String[] args) {
        ArrayList<Integer> vec = new ArrayList<Integer>();
        Map<Integer, Float> hashmap = new HashMap<>();
        
        
        vec.add(1);
        vec.add(1);
        vec.add(3);
        vec.add(3);

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

        int majorResult = 0;
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
        } else {
            System.out.println("Não houve maioria");
        }
    }
}
