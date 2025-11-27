package com.jiudian.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.jiudian.manage.mapper.UserMapper;
import com.jiudian.manage.model.User;
import com.jiudian.manage.service.UserService;
import com.jiudian.manage.until.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
//service层对用户接口进行实现，调用mybatis的xml文件进行数据库操作
//就是调用mapper层给你写好的接口，去使用（就比如自己先定义好对象，赋好值，用mapper中的函数进行插入）
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;//使用的是userMapper接口的对象，然后调用mybatis的xml文件进行数据库操作，完成业务逻辑的处理

    @Override
    public User selectUser(int userid) {
        return userMapper.selectByPrimaryKey(userid);
    }

    public boolean addUser(String account, String password, int power){
        User user = new User();
        user.setUseraccount(account);
        user.setPassword(password);
        user.setPower(power);
        user.setIdnumber(UUIDUtil.generateShortUuid());
        int insert = userMapper.insertSelective(user);
        return insert>0?true:false;
    }

    @Override
    public boolean delUser(int userid) {
        int i = userMapper.deleteByPrimaryKey(userid);
        return i>0?true:false;
    }

    @Override
    public boolean alterUser(int userid, String password, String username, int age, int power, String IDnumber,String phonenumber) {
        User user = new User();
        user.setUserid(userid);
        //切记做字符比较之前，确保字符串不为null！！！！尼玛，否则会报空指针异常！！！！然后直接返回错误
        if(password!=null&&!password.equals("null")){
            user.setPassword(password);
        }
        if(username!=null&&!username.equals("null")){
            user.setUsername(username);
        }
        if(age!=-1){
            user.setAge(age);
        }
        if(power!=-1){
            user.setPower(power);
        }
        if(IDnumber!=null&&!IDnumber.equals("null")){
            user.setIdnumber(IDnumber);
        }
        if(phonenumber!=null&&!phonenumber.equals("null")){
            user.setPhonenumber(phonenumber);
        }
        int i = userMapper.updateByPrimaryKeySelective(user);
        return i>0?true:false;
    }

    @Override
    public boolean addSlary(int userid, double money) {
        User user = userMapper.selectByPrimaryKey(userid);
        Double money1 = user.getMoney();
        user.setMoney(money+money1);
        int i = userMapper.updateByPrimaryKey(user);
        return i>0?true:false;
    }

    @Override
    public List<User> getAllUser(int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return userMapper.getAllUser();
    }

    @Override
    public List<User> getUserByPower(int power,int pageNum,int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return userMapper.selectByPower(power);
    }

    @Override//验证登录信息，检查账户是否存在，检测密码是否正确
    public int[] login(String username, String password) {
        User user = userMapper.selectByAccount(username);//查帐户
        if(user!=null&&user.getPassword().equals(password)){
            //调用user类返回用户的账户和权限
            return new int[]{user.getUserid(),user.getPower()};
        }else{
            return null;
        }
    }

    @Override
    public boolean photo(int userid, String url) {
        User user = new User();
        user.setUserid(userid);
        user.setPhotourl(url);
        int i = userMapper.updateByPrimaryKeySelective(user);
        return i>0?true:false;
    }

    @Override
    public User current_login_user(String username, String password) {
        User user = userMapper.selectByAccount(username);//查帐户,返回User
        if(user!=null&&user.getPassword().equals(password))
            return user;
        else{
            return null;
        }
    }

    @Override
    public User getUserByAccount(String useraccount) {
        return userMapper.selectByAccount(useraccount);
    }

}
