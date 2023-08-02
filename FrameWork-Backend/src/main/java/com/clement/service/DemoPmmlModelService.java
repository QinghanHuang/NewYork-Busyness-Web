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
            ClassPathResource pmmlResource = new ClassPathResource("model6.012B.pmml");
            PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(pmmlResource.getInputStream());
            EvaluatorBuilder evaluatorBuilder = new ModelEvaluatorBuilder(pmml);
            evaluator = evaluatorBuilder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int predict(double id,double month,double dayOfMonth,double dayOfWeek,double hour,double area,double perimeter) {
        // Convert the input data to the format expected by the Evaluator
        Map<String, Double> inputData = new HashMap<>();
        inputData.put("DOLocationID", id);
        inputData.put("dropoff_hour", hour);
        inputData.put("DOLocation_area", area);
        inputData.put("dropoff_day_of_week", dayOfWeek);
        inputData.put("dropoff_day_of_month", dayOfMonth);
        inputData.put("dropoff_month", month);
        inputData.put("DOLocation_perimeter", perimeter);

        // Evaluate the model with the input data
        Map<String, ?> result = evaluator.evaluate(inputData);
        // System.out.println(result);

        // Get the predicted output value (assuming the output is a single integer value)
        return (int) result.get("predicted_busyness");
    }

}
