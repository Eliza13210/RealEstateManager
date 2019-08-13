package com.openclassrooms.realestatemanager;

import com.openclassrooms.realestatemanager.models.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PointOfInterestsMatcher {


    /**
     * Update Result list with the results containing points of interest depending on type
     *
     * @param resultsToCheck
     * @return
     */
    public static List<Result> checkIfPointOfInterest(List<Result> resultsToCheck) {

        List<Result> matchedList = new ArrayList<>();

        String[] types = {"restaurant", "amusement_parc", "bus_station", "school", "train_station", "supermarket", "airport", "city_hall",
                "hospital", "subway_station", "pharmacy", "doctor"};

        List<String> listOfTypes = Arrays.asList(types);

        for (int i = 0; i < resultsToCheck.size(); i++) {

            for (int j = 0; j < listOfTypes.size(); j++) {

                if (resultsToCheck.get(i).getTypes().get(0).equals(listOfTypes.get(j))) {
                    matchedList.add(resultsToCheck.get(i));
                }

            }
        }
        return matchedList;
    }
}
