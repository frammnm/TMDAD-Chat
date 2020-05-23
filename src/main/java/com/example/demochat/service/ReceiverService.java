package com.example.demochat.service;


import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import java.lang.Math;


import com.example.demochat.model.Message;

public class ReceiverService {

//    public static final String RECEIVE_METHOD_NAME = "receiveMessage";

    private List<Message> messagePopulation;

    private Map<String, Integer> wordCount;

    private Map<String, Integer> sentToCount;

    private Map<String, Integer> sentFromCount;


    public List<Map<String, Double>> calculateZScores(){

        List<Map<String, Double>> resultSet = new ArrayList<Map<String, Double>>();

        Map<String, Double> wordScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {

            String currentWord = entry.getKey();
            double currentWordScore = calculateZScore(currentWord, wordCount);

            wordScores.put(currentWord, currentWordScore);

        }

        resultSet.add(wordScores);

        Map<String, Double> sentScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : sentToCount.entrySet()) {

            String currentWord = entry.getKey();
            double currentWordScore = calculateZScore(currentWord, sentToCount);

            sentScores.put(currentWord, currentWordScore);

        }

        resultSet.add(sentScores);

        Map<String, Double> fromScores = new HashMap<>();
        for (Map.Entry<String, Integer> entry : sentFromCount.entrySet()) {

            String currentWord = entry.getKey();
            double currentWordScore = calculateZScore(currentWord, sentFromCount);

            fromScores.put(currentWord, currentWordScore);

        }

        resultSet.add(fromScores);


        return resultSet;
    }


    public double calculateZScore(String word, Map<String, Integer> population) {

        int populationSize = population.size();

        int sumPopulation = 0;
        for (Map.Entry<String, Integer> entry : population.entrySet()) {

            String currentWord = entry.getKey();
            Integer currentWordCount = entry.getValue();

            sumPopulation = sumPopulation + currentWordCount;

        }

        double averagePopulation = (double) sumPopulation / populationSize;
        List<Double> stdList = new ArrayList<Double>();
        double stdSumPopulation = (double) 0;

        for (Map.Entry<String, Integer> entry : population.entrySet()) {

            String currentWord = entry.getKey();
            Integer currentWordCount = entry.getValue();

            double std = Math.pow(currentWordCount - averagePopulation, 2);

            stdList.add(std);
            stdSumPopulation = stdSumPopulation + std;
        }

        double stdPopulation = Math.sqrt(stdSumPopulation / populationSize);

        return ((population.get(word) - averagePopulation) / stdPopulation);
    }

    private Map<String, Integer> countWords(String text, Map<String, Integer> map, boolean split) {

        if (split) {
            for(String word : text.split("\\W")) {

                if (word.isEmpty()) { continue; }

                if (map.containsKey(word)) {
                    map.put(word, map.get(word) + 1);
                } else {
                    map.put(word, 1);
                }
            }
        } else {

            if (map.containsKey(text)) {
                map.put(text, map.get(text) + 1);
            } else {
                map.put(text, 1);
            }
        }


        return map;
    }

    public void receiveMessage(Message m) {

        System.out.println(m.getBody());
        System.out.println(m.getSent_from());
        System.out.println(m.getSent_to());

        String body = m.getBody().toLowerCase();
        String from = m.getSent_from().toLowerCase();
        String to = m.getSent_to().toLowerCase();

        wordCount = countWords(body, wordCount, true);
        sentToCount = countWords(from, sentToCount, false);
        sentFromCount = countWords(to, sentFromCount, false);

        messagePopulation.add(m);

//        Map<String,Object> map = new HashMap<>();
//        map.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);

    }
}