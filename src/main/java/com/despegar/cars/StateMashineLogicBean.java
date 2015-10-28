package com.despegar.cars;

import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;


@WithStateMachine
public class StateMashineLogicBean {
	
	
	@OnTransition(target = "TASK_GEN_ID")
    public void toState1() {
		System.out.println("TASK_GEN_ID");
    }

    @OnTransition(target = "TASK_RISK")
    public void toState2() {
    	System.out.println("TASK_RISK");
    }
    
    @OnTransition(target = "CHOICE_RISK")
    public void toState3() {
    	System.out.println("CHOICE_RISK");
    }
    
    @OnTransition(target = "TASK_RISK_IN_PROGRESS")
    public void toState4() {
    	System.out.println("TASK_RISK_IN_PROGRESS");
    }
    
    @OnTransition(target = "TASK_RISK_REJECT")
    public void toState5() {
    	System.out.println("TASK_RISK_REJECT");
    }
    
    @OnTransition(target = "TASK_BOOKING")
    public void toState6() {
    	System.out.println("TASK_BOOKING");
    }
    
    @OnTransition(target = "CHOICE_BOOKING")
    public void toState7() {
    	System.out.println("CHOICE_BOOKING");
    }
    
    @OnTransition(target = "TASK_BOOKING_ERROR")
    public void toState8() {
    	System.out.println("TASK_BOOKING_ERROR");
    }
    
    @OnTransition(target = "TASK_VALIDATE_COUPON")
    public void toState9() {
    	System.out.println("TASK_VALIDATE_COUPON");
    }
    
    @OnTransition(target = "CHOICE_VALIDATE_COUPON")
    public void toState10() {
    	System.out.println("CHOICE_VALIDATE_COUPON");
    }
    
    @OnTransition(target = "TASK_OFFLINE")
    public void toState11() {
    	System.out.println("TASK_OFFLINE");
    }

    @OnTransition(target = "FORK_COLLECT_GEN_VOUCHER")
    public void toState12() {
    	System.out.println("FORK_COLLECT_GEN_VOUCHER");
    }
    
    @OnTransition(target = "TASKS_COLLECT_GEN_VOUCHER")
    public void toState13() {
    	System.out.println("TASKS_COLLECT_GEN_VOUCHER");
    }
    
    @OnTransition(target = "TASK_COLLECT")
    public void toState14() {
    	System.out.println("TASK_COLLECT");
    }
    
    @OnTransition(target = "TASK_GEN_VOUCHER")
    public void toState15() {
    	System.out.println("TASK_GEN_VOUCHER");
    }

    @OnTransition(target = "TASK_END_COLLECT")
    public void toState16() {
    	System.out.println("TASK_END_COLLECT");
    }
    
    @OnTransition(target = "TASK_END_GEN_VOUCHER")
    public void toState17() {
    	System.out.println("TASK_END_GEN_VOUCHER");
    }
    
    @OnTransition(target = "JOIN_COLLECT_GEN_VOUCHER")
    public void toState18() {
    	System.out.println("JOIN_COLLECT_GEN_VOUCHER");
    }
    
    @OnTransition(target = "CHOICE_COLLECT_GEN_VOUCHER")
    public void toState19() {
    	System.out.println("CHOICE_COLLECT_GEN_VOUCHER");
    }
    
    @OnTransition(target = "TASK_NEW_CC")
    public void toState20() {
    	System.out.println("TASK_NEW_CC");
    }
    
    @OnTransition(target = "TASK_FINALIZED")
    public void toState21() {
    	System.out.println("TASK_FINALIZED");
    }
    
    @OnTransition(target = "END")
    public void toState22() {
    	System.out.println("END");
    }
    
    
    
}
