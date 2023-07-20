package com.clement.service;

import org.dmg.pmml.PMML;
import org.jpmml.evaluator.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DemoPmmlModelService {

    private Evaluator evaluator;

    public DemoPmmlModelService() {
        // Load PMML model from the resource folder
        try {
            ClassPathResource pmmlResource = new ClassPathResource("test_model.pmml");
            PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(pmmlResource.getInputStream());
            EvaluatorBuilder evaluatorBuilder = new ModelEvaluatorBuilder(pmml);
            evaluator = evaluatorBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int predict(int input1, int input2) {
        // Convert the input data to the format expected by the Evaluator
        Map<String, Integer> inputData = new HashMap<>();
        inputData.put("X1", input1);
        inputData.put("X2", input2);

        // Evaluate the model with the input data
        Map<String, ?> result = evaluator.evaluate(inputData);

        // Get the predicted output value (assuming the output is a single integer value)
        int prediction = ((Number) result.get("y")).intValue();
        return prediction;
    }
}
