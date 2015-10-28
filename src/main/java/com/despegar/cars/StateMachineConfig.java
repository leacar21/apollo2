package com.despegar.cars;

import java.util.Date;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
    	config.withConfiguration()
    		.autoStartup(false).listener(listener());
//    		.beanFactory(new StaticListableBeanFactory())
//    		.taskExecutor(new SyncTaskExecutor())
//    		.taskScheduler(new ConcurrentTaskScheduler())
//    		.listener(new StateMachineListenerAdapter<States, Events>());
    }
	
    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
    	
        states.withStates()
        	.initial(States.INIT)
        	.state(States.TASK_GEN_ID, genIdAction(), null)
        	.state(States.TASK_RISK, riskAction(), null)
        	.choice(States.CHOICE_RISK)
        	.state(States.TASK_RISK_IN_PROGRESS, riskInProgressAction(), null)
        	.state(States.TASK_RISK_REJECT, riskRejectAction(), null)
        	.state(States.TASK_BOOKING, bookingAction(), null)
        	.choice(States.CHOICE_BOOKING)
        	.state(States.TASK_BOOKING_ERROR, bookingErrorAction(), null)
        	.state(States.TASK_VALIDATE_COUPON, validateCouponAction(), null)
        	.choice(States.CHOICE_VALIDATE_COUPON)
        	.state(States.TASK_OFFLINE, offlineAction(), null)
        	.state(States.TASK_VALIDATE_COUPON_OK, offlineAction(), null)
        	.fork(States.FORK_COLLECT_GEN_VOUCHER)
        	.state(States.TASKS_COLLECT_GEN_VOUCHER)
        	.join(States.JOIN_COLLECT_GEN_VOUCHER)
        	.choice(States.CHOICE_COLLECT_GEN_VOUCHER)
        	.state(States.TASK_NEW_CC, newCCAction(), null)
        	.state(States.TASK_FINALIZED, finalizedAction(), null)
        	.end(States.END)
        	.and()
	        .withStates()
	            .parent(States.TASKS_COLLECT_GEN_VOUCHER)
	            .initial(States.TASK_GEN_VOUCHER, genVoucherAction())
	            .end(States.TASK_END_GEN_VOUCHER)
	            .and()
	        .withStates()
	            .parent(States.TASKS_COLLECT_GEN_VOUCHER)
	            .initial(States.TASK_COLLECT, collectAction())
	            .end(States.TASK_END_COLLECT);
        
                
    }
    

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions
	        .withExternal()
		        .source(States.INIT).target(States.TASK_GEN_ID)
		        .event(Events.RUN)
		        .and()
            .withExternal()
                .source(States.TASK_GEN_ID).target(States.TASK_RISK)
                .and()
            .withExternal()
                .source(States.TASK_RISK).target(States.CHOICE_RISK)
                .and()
            .withChoice()
                .source(States.CHOICE_RISK)
                .first(States.TASK_BOOKING, tasksRiskAcceptChoiceGuard())
                .then(States.TASK_RISK_IN_PROGRESS, tasksRiskInProgressChoiceGuard())
                .then(States.TASK_RISK_REJECT, tasksRiskRejectChoiceGuard())
                .and()
	        .withExternal()
		        .source(States.TASK_BOOKING).target(States.CHOICE_BOOKING)
		        .and()   
		    .withChoice()
                .source(States.CHOICE_BOOKING)
                .first(States.TASK_VALIDATE_COUPON, tasksValidateCouponOKChoiceGuard())
                .last(States.TASK_BOOKING_ERROR)
                .and()
            .withExternal()
		        .source(States.TASK_VALIDATE_COUPON).target(States.CHOICE_VALIDATE_COUPON)
		        .and()
		    .withChoice()
                .source(States.CHOICE_VALIDATE_COUPON)
                .first(States.TASK_VALIDATE_COUPON_OK, tasksValidateCouponOKChoiceGuard())
                .then(States.TASK_OFFLINE, tasksValidateCouponErrorChoiceGuard())
                .and()
            .withExternal()
		        .source(States.TASK_VALIDATE_COUPON_OK).target(States.FORK_COLLECT_GEN_VOUCHER)
		        .and()
            .withFork()
                .source(States.FORK_COLLECT_GEN_VOUCHER).target(States.TASKS_COLLECT_GEN_VOUCHER)
                .and()
            .withExternal()
                .source(States.TASK_COLLECT).target(States.TASK_END_COLLECT)
                .and()
            .withExternal()
                .source(States.TASK_GEN_VOUCHER).target(States.TASK_END_GEN_VOUCHER)
                .and()
            .withJoin()
                .source(States.TASKS_COLLECT_GEN_VOUCHER).target(States.JOIN_COLLECT_GEN_VOUCHER)
                .and()
            .withExternal()
                .source(States.JOIN_COLLECT_GEN_VOUCHER).target(States.CHOICE_COLLECT_GEN_VOUCHER)
                .and()
            .withChoice()
                .source(States.CHOICE_COLLECT_GEN_VOUCHER)
                .first(States.TASK_FINALIZED, tasksCollectGenVoucherOKChoiceGuard())
                .then(States.TASK_NEW_CC, tasksCollectGenVoucherNewCCChoiceGuard())
                .then(States.TASK_OFFLINE, tasksCollectGenVoucherOfflineChoiceGuard())
                .and()
            .withExternal()
                .source(States.TASK_FINALIZED).target(States.END);
    }
    
    @Bean
    public StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("State change to " + to.getId());
            }
            
            @Override
        	public void stateEntered(State<States, Events> state) {
            	System.out.println("stateEntered " + new Date());
        	}

        	@Override
        	public void stateExited(State<States, Events> state) {
        		System.out.println("stateExited " + new Date());
        	}
        };
    }
    
    //---------------------------------------------------------
    
    
    @Bean
    public Guard<States, Events> tasksRiskAcceptChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return true;
//                Map<Object, Object> variables = context.getExtendedState().getVariables();
//                return !(ObjectUtils.nullSafeEquals(variables.get("T1"), true)
//                        && ObjectUtils.nullSafeEquals(variables.get("T2"), true)
//                        && ObjectUtils.nullSafeEquals(variables.get("T3"), true));
            }
        };
    }
    
    
    @Bean
    public Guard<States, Events> tasksRiskInProgressChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return false;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksRiskRejectChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return false;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksValidateCouponOKChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return true;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksValidateCouponErrorChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return false;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksCollectGenVoucherOKChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return true;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksCollectGenVoucherNewCCChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return false;
            }
        };
    }
    
    @Bean
    public Guard<States, Events> tasksCollectGenVoucherOfflineChoiceGuard() {
        return new Guard<States, Events>() {

            @Override
            public boolean evaluate(StateContext<States, Events> context) {
            	return false;
            }
        };
    }
    
    //---------------------------------------------------------
  //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> genIdAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    Boolean success = true;
			    variables.put("GEN_ID", success);
		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> riskAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    String risk = "ACCEPT";
			    variables.put("RISK", risk);

		    }
	    };
    }
    
    @Bean
    public Action<States, Events> riskRejectAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    String risk = "REJECT";
			    variables.put("RISK", risk);

		    }
	    };
    }
    
    @Bean
    public Action<States, Events> riskInProgressAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    String risk = "IN_PROGRESS";
			    variables.put("RISK", risk);

		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> bookingAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    boolean bookingOK = true;
			    variables.put("BOOKING", bookingOK);
		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> bookingErrorAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
		    }
	    };
    }
 
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> validateCouponAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    Boolean validateCouponOK = true;
			    variables.put("VALIDATE_COUPON", validateCouponOK);
		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> offlineAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
		    	
		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> newCCAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {

		    }
	    };
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> finalizedAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    Boolean finalized = true;
			    variables.put("FINALIZE", finalized);
		    }
	    };
    }
    
    
    //---------------------------------------------------------

    @Bean
    public Action<States, Events> collectAction() {
    	return new Action<States, Events>() {
    		@Override
    		public void execute(StateContext<States, Events> context) {
    			Map<Object, Object> variables = context.getExtendedState().getVariables();
    			Boolean collectOK = true;
    			Boolean newCC = true;
    			Boolean offline = true;
    			variables.put("COLLECT", collectOK);
    			variables.put("NEW_CC", newCC);
    			variables.put("OFFLINE", offline);
    		}
    	};
    }
    
    //---------------------------------------------------------
    
    @Bean
    public Action<States, Events> genVoucherAction() {
    	return new Action<States, Events>() {
		    @Override
		    public void execute(StateContext<States, Events> context) {
			    Map<Object, Object> variables = context.getExtendedState().getVariables();
			    Boolean genVoucherOK = true;
			    variables.put("GEN_VOUCHER", genVoucherOK);
		    }
	    };
    }
    
    //---------------------------------------------------------
    
}