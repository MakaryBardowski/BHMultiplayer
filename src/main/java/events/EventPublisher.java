/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package events;

import java.util.ArrayList;

/**
 *
 * @author 48793
 */
public abstract class EventPublisher {
    private final ArrayList<EventSubscriber> subscribers = new ArrayList(1);
    
    public final void addEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.add(eventSubscriber);
    }
    
    public void removeEventSubscriber(EventSubscriber eventSubscriber){
        subscribers.remove(eventSubscriber);
    }
    
    public void notifyEventSubscribers(GameEvent gameEvent){
        for(var subscriber : subscribers){
            subscriber.receiveEventNotification(gameEvent);
        }
    }
    
}
