package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        return userRepository.save(user).getId();

    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user = userRepository.findById(userId).get();

        if(user == null) return 0;

        int cnt=0;
        int agelimit = user.getAge();
        SubscriptionType type = user.getSubscription().getSubscriptionType();

        List<WebSeries> webSeries = webSeriesRepository.findAll();

        if(type==SubscriptionType.BASIC) {
            for (WebSeries webseries : webSeries) {
                if (webseries.getAgeLimit() <= agelimit && webseries.getSubscriptionType() == SubscriptionType.BASIC)
                    cnt++;
            }
        }
        else if(type == SubscriptionType.PRO){
            for (WebSeries webseries : webSeries) {
                if (webseries.getAgeLimit() <= agelimit &&
                        (webseries.getSubscriptionType() == SubscriptionType.BASIC ||
                                webseries.getSubscriptionType()==SubscriptionType.PRO))
                    cnt++;
            }
        }

        else cnt=webSeries.size();
        return cnt;
    }


}
