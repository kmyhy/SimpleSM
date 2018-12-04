package com.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.model.DefaultStateMachineComponentResolver;
import org.springframework.statemachine.config.model.StateMachineComponentResolver;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.statemachine.uml.UmlStateMachineModelFactory;


@Configuration
@EnableStateMachine
public class SMConfiger extends StateMachineConfigurerAdapter<String, String> {

    @Autowired
    private SMPersister persister;

    @Bean
    public StateMachinePersister<String,String,String> stateMachinePersist(){
        return new DefaultStateMachinePersister<>(persister);
    }

    @Override
    public void configure(StateMachineModelConfigurer<String, String> model) throws Exception {
        model
                .withModel()
                .factory(modelFactory());
    }

    @Bean
    public StateMachineModelFactory<String, String> modelFactory() {
        // 测试单步流程
//        UmlStateMachineModelFactory factory = new UmlStateMachineModelFactory(
//                "classpath:test.uml");
        // 测试二级审批请假流程
        UmlStateMachineModelFactory factory = new UmlStateMachineModelFactory(
                "classpath:model.uml");
        factory.setStateMachineComponentResolver(stateMachineComponentResolver());
        return factory;
    }

    @Bean
    public StateMachineComponentResolver<String, String> stateMachineComponentResolver() {
        DefaultStateMachineComponentResolver<String, String> resolver = new DefaultStateMachineComponentResolver<>();
        resolver.registerAction("myAction", myAction());
        resolver.registerGuard("myGuard", myGuard());
        return resolver;
    }

    public Action<String, String> myAction() {
        return new Action<String, String>() {

            @Override
            public void execute(StateContext<String, String> context) {
            }
        };
    }

    public Guard<String, String> myGuard() {
        return new Guard<String, String>() {

            @Override
            public boolean evaluate(StateContext<String, String> context) {
                return false;
            }
        };
    }
}
