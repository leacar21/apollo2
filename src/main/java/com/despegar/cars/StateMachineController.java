package com.despegar.cars;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@EnableAutoConfiguration
public class StateMachineController {
	
  @Autowired
  StateMachineFactory<States, Events> stateMachineFactory;
	
  @RequestMapping("/start")
  @ResponseBody
  String start() {
	  System.out.println(">>>>> State Machine Started >>>>>");
	  
	  StateMachine<States, Events> stateMachine =  stateMachineFactory.getStateMachine();
	  stateMachine.start();
	  
	  System.out.println(stateMachine.getState().getId().name());
	  
	  try{
		  stateMachine.sendEvent(Events.RUN);
	  } catch (Exception e) {
		  e.printStackTrace();
		  String name =  stateMachine.getState().getId().name();
		  System.out.println("Actual State: " + name);
	  }
	  
	  
	  boolean completed = stateMachine.isComplete();
	  
	  return "Started!! >>> Completed: " + completed;
  }

}
