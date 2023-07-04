package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @ Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay

        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        int amount=0;

        if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.BASIC){
            amount = 200*subscription.getNoOfScreensSubscribed() + 500;
        }
        else if(subscriptionEntryDto.getSubscriptionType()==SubscriptionType.PRO){
            amount = 250*subscription.getNoOfScreensSubscribed() + 800;
        }
        else amount = 350*subscription.getNoOfScreensSubscribed() + 1000;

        subscription.setTotalAmountPaid(amount);

        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        subscription.setUser(user);
        user.setSubscription(subscription);

        userRepository.save(user);


        return amount;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();

        if(user.getSubscription().getSubscriptionType()==SubscriptionType.ELITE)
            throw new Exception("Already the best Subscription");

        int extraprice=0;

        if(user.getSubscription().getSubscriptionType()==SubscriptionType.BASIC){
            Subscription subscription = user.getSubscription();
            int amount = subscription.getTotalAmountPaid();
            int screens = subscription.getNoOfScreensSubscribed();

            int extra = 250*screens + 800;
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(extra);
            extraprice = extra-amount;
            user.setSubscription(subscription);

            userRepository.save(user);
        }
        else{
            Subscription subscription = user.getSubscription();
            int amount = subscription.getTotalAmountPaid();
            int screens = subscription.getNoOfScreensSubscribed();

            int extra = 350*screens + 1000;
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(extra);
            extraprice = extra-amount;
            user.setSubscription(subscription);

            userRepository.save(user);
        }
        return extraprice;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptions = subscriptionRepository.findAll();
        int total=0;

        for(Subscription subscription: subscriptions)
            total+=subscription.getTotalAmountPaid();

        return total;
    }

}
