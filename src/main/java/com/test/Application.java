
package com.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.StateMachine;

import java.util.Scanner;

@SpringBootApplication
public class Application{// implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	private StateMachine<String, String> stateMachine;


    /*
    @Override
	public void run(String... args) throws Exception {

		// 测试简单的单步流程 test.uml
//		runTestUML();

		// 测试二级审批请假流程 model.uml
		runModelUML1();
		runModelUML2();
		runModelUML3();

		logger.info("程序结束");

	}
	*/

	private void runTestUML() {
		stateMachine.start();

		sendSignal("E1");
	}

	private void runModelUML1() {
		stateMachine.stop();
		stateMachine.start();

		logger.info("执行成功路径-----------");

		sendSignal("submit");

		sendSignal("tlAudit");

		sendSignal("dmAudit");

		sendSignal("hrRecord");
	}
	private void runModelUML2() {
		stateMachine.stop();
		stateMachine.start();

		logger.info("执行失败路径1-----------");

		sendSignal("submit");

		sendSignal("tlReject");

		sendSignal("dmAudit");

		sendSignal("hrRecord");
	}
	private void runModelUML3() {
		stateMachine.stop();
		stateMachine.start();

		logger.info("执行失败路径2-----------");

		sendSignal("submit");

		sendSignal("tlAudit");

		sendSignal("dmReject");

		sendSignal("hrRecord");
	}
	private void sendSignal(String signal){

		logger.info("===========执行 {} ============",signal);

		logger.info("执行前状态:{}",stateMachine.getState().getId());

		stateMachine.sendEvent(signal);

		logger.info("执行后状态:{}",stateMachine.getState().getId());
	}

}


