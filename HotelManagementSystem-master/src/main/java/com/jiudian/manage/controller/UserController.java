package com.jiudian.manage.controller;

import com.jiudian.manage.model.User;
import com.jiudian.manage.service.impl.UserServiceImpl;
import com.jiudian.manage.until.ImageCode;
import com.jiudian.manage.until.State;
import com.jiudian.manage.until.StateSignal;
import com.sun.glass.ui.Accessible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Result;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    /**
     * 登录(已测试)
     * @param useraccount
     * @param password
     * @return
     */
    @RequestMapping(value = "/login.do")
    public Map login(@RequestParam String useraccount, @RequestParam String password,@RequestParam String icode,HttpSession session){
        StateSignal signal = new StateSignal();
        String code = (String) session.getAttribute(ImageCode.CODENAME);
        System.out.println("session: "+code+"   实际"+icode);
        if(icode!=null&&code!=null&&icode.equals(code)){
            int[] login = userService.login(useraccount, password);
            if(login!=null){
                //会生成类似JSON格式的响应
                signal.put(State.SuccessCode);
                signal.put(State.SuccessMessage);
                signal.put("userid",login[0]);
                signal.put("power",login[1]);//同时传入账户信息和权限
                User user = userService.current_login_user(useraccount, password);
                session.setAttribute("loginUser", user);
                System.out.println("登录成功，sessionID = " + session.getId());

            }else {
                signal.put(State.ErrorCode);
                signal.put(State.ErrorMessage);


            }
        }else{
            signal.put(State.ErrorCode);
            signal.put("message","验证码输入错误");
        }
        return signal.getResult();//返回整个HashMap对象，@RestController 自动将 Map 序列化为JSON格式返回给前端
    }

    @GetMapping("/createImage")
    public void createImage(@RequestParam String code, HttpServletResponse response, HttpSession session) throws IOException {
        ImageCode.createImage(response,session);
    }

    /**
     * 添加用户(已测试)
     * @param useraccount  用户名
     * @param password  密码
     * @param power     权限
     * @return
     */

    @RequestMapping(value = "/addUser.do")
    public Map addUser(@RequestParam String useraccount,
                       @RequestParam String password,
                       @RequestParam String power,
                       HttpSession session) {

        StateSignal signal = new StateSignal();

        User currentUser = (User) session.getAttribute("loginUser");
        System.out.println("添加用户时 sessionID = " + session.getId());

        if (currentUser == null) {
            signal.put(State.ErrorCode);
            signal.put("message", "未登录或登录已失效");
            System.out.println("卡在这里");
            return signal.getResult();
        }


        //service层的login就是用来验证你输入的账户和密码是否匹配
        int []login=userService.login(useraccount, password);
        if(login==null)//用户为空时
        {
            signal.put(State.ErrorCode);
            signal.put("message", "用户不存在");
            return signal.getResult();
        }
        int newUserPower = login[1];
        System.out.println("当前用户权限: " + currentUser.getPower()
                + ", 新用户权限: " + newUserPower);


        // 数字越小，权限越高
        // 只有“更高权限（数值更小）”才能创建“更低权限（数值更大）”的账号
        // 所以：当前权限 >= 新账号权限 -> 拒绝
        if (currentUser.getPower() > newUserPower) {
            signal.put(State.ErrorCode);
            signal.put("message", "无权限！");
            return signal.getResult();
        }
        //到这里用户肯定存在
        User newUser= userService.getUserByAccount(useraccount);
        if(newUser==null)
        {
            signal.put(State.ErrorCode);
            signal.put("message", "无权限！");
            System.out.println("卡在这里newUser");
            return signal.getResult();
        }
        boolean add = userService.alterUser(newUser.getUserid(),password,newUser.getUsername(),newUser.getAge(),Integer.parseInt(power),newUser.getIdnumber(),newUser.getPhonenumber());
        if(add){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }

        return signal.getResult();
    }








    //开始添加用户
//        boolean add = userService.addUser(useraccount,password,Integer.parseInt(power));
//        StateSignal signal = new StateSignal();
//        if(add){
//            signal.put(State.SuccessCode);
//            signal.put(State.SuccessMessage);
//        }else {
//            signal.put(State.ErrorCode);
//            signal.put(State.ErrorMessage);
//        }
//        return signal.getResult();

    /**
     * 修改用户数据(已测试)
     * @param userid
     * @param password
     * @param username
     * @param age
     * @param power
     * @param IDnumber
     * @return
     */
    @RequestMapping(value = "/updateUser.do")
    public Map updateUser(@RequestParam int userid,@RequestParam(required = false,defaultValue = "null") String password,@RequestParam(required = false,defaultValue = "null") String username,@RequestParam(required = false,defaultValue = "-1") int age,@RequestParam(required = false,defaultValue = "-1") int power,@RequestParam(required = false,defaultValue = "null") String IDnumber,@RequestParam(required = false,defaultValue = "null") String phonenumber){
        boolean upd = userService.alterUser(userid, password, username, age, power, IDnumber,phonenumber);
        StateSignal signal = new StateSignal();
        if(upd){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }
        return signal.getResult();
    }

    /**
     * 删除用户(已测试)
     * @param userid    用户id
     * @return
     */
    @RequestMapping(value = "/delUser.do")
    public Map delUser(@RequestParam("userid")Integer userid){
        boolean del = userService.delUser(userid);
        StateSignal signal = new StateSignal();
        if(del){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }
        return signal.getResult();
    }

    /**
     * 获取用户列表(已测试)
     * @return
     */
    @RequestMapping(value = "/getAllUser.do")
    public Map getAllUser(@RequestParam int pageNum,@RequestParam int pageSize){
        List<User> allUser = userService.getAllUser(pageNum,pageSize);
        StateSignal signal = new StateSignal();
        if(allUser!=null){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
            signal.put("List",allUser);
            signal.put("pageNum",pageNum);
            signal.put("pageSize",pageSize);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }

        return  signal.getResult();
    }

    /**
     * 获取对应权限的用户列表
     * @param power
     * @return
     */
    @RequestMapping(value = "/getUserByPower.do")
    public Map getUserByPower(@RequestParam int power,@RequestParam int pageNum,@RequestParam int pageSize){
        List<User> Users = userService.getUserByPower(power,pageNum,pageSize);
        StateSignal signal = new StateSignal();
        if(Users!=null){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
            signal.put("List",Users);
            signal.put("pageNum",pageNum);
            signal.put("pageSize",pageSize);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }
        return  signal.getResult();
    }

    /**
     * 通过userid获取信息
     * @param userid 用户id
     * @return
     */
    @RequestMapping(value = "/getUserById.do")
    public Map getUserById(@RequestParam int userid){
        User user = userService.selectUser(userid);
        StateSignal signal = new StateSignal();
        if(user!=null){
            signal.put(State.SuccessCode);
            signal.put(State.SuccessMessage);
            signal.put("user",user);
        }else {
            signal.put(State.ErrorCode);
            signal.put(State.ErrorMessage);
        }
        return  signal.getResult();
    }


}
