package com.test;


import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SMController {


    @Autowired
    private StateMachinePersister<String, String, String> persister;

    @Autowired
    private StateMachine<String, String> stateMachine;


    @RequestMapping("/new")
    @ResponseBody
    public BaseResponse newLeave(@RequestBody LeaveRequest leave){
        BaseResponse result = new BaseResponse();
        stateMachine.start();

        result.message = "新建请假申请成功";
        result.success = true;
        result.data = leave;

        return result;

    }

    @RequestMapping("/apply")
    @ResponseBody
    public BaseResponse apply(@RequestBody JSONObject params){
        String leaveId = params.getAsString("leaveId");

        return sendEvent("submit",leaveId);

    }

    @RequestMapping("/tlApprove")
    @ResponseBody
    public BaseResponse tlApprove(@RequestBody JSONObject params) {

        String id = params.getAsString("leaveId");
        boolean agree = params.getAsNumber("agree").intValue() != 0;

        return sendEvent(agree ? "tlAudit" : "tlReject", id);
    }

    @RequestMapping("/dmApprove")
    @ResponseBody
    public BaseResponse dmApprove(@RequestBody JSONObject params) {

        String id = params.getAsString("leaveId");
        boolean agree = params.getAsNumber("agree").intValue() != 0;

        return sendEvent(agree ? "dmAudit" : "dmReject", id);

    }

    @RequestMapping("/hrRecord")
    @ResponseBody
    public BaseResponse hrRecord(@RequestBody JSONObject params) {

        String id = params.getAsString("leaveId");

        return sendEvent("hrRecord",id);

    }

    @RequestMapping("/getState")
    @ResponseBody
    public BaseResponse getState(@RequestBody JSONObject params){
        String leaveId = params.getAsString("leaveId");

        BaseResponse result = new BaseResponse();

        try{
            persister.restore(stateMachine,leaveId);

            result.success = true;
            String state = stateMachine.getState().getId();

            result.data = state;

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            stateMachine.stop();
            return result;
        }

    }

    private BaseResponse sendEvent(String event,String leaveId){
        BaseResponse result = new BaseResponse();

        if(leaveId == null || leaveId.length()==0){
            result.success = false;
            result.message = "leaveId 不能为空";
            return result;
        }

        try {
            // 根据业务 id 获取状态
            persister.restore(stateMachine,leaveId);

            result.success = stateMachine.sendEvent(event);
            // 持久化状态机
            if (result.success) {
                persister.persist(stateMachine, leaveId);
            }
            JSONObject data = new JSONObject();

            result.message = result.success ? "执行成功":"执行失败";
            result.message = result.message + "，当前状态为："+stateMachine.getState().getId();
            data.put("leaveId",leaveId);
            data.put("event",event);
            data.put("state",stateMachine.getState().getId());
            result.data = data;
        } catch (Exception e) {
            e.printStackTrace();
            result.message = e.getMessage();
        }finally {
            stateMachine.stop();
            return result;
        }
    }

}

