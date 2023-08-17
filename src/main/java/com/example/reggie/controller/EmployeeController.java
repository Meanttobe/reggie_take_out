package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.reggie.common.R;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.EmployeeService;
import com.sun.net.httpserver.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /**
         * 1、将页面提交的密码password进行md5加密
         * 2、根据用户名username查询数据库
         * 3、查询不到该用户返回失败
         * 4、密码比对，密码错误返回登录失败
         * 5、查询员工状态，员工状态为0返回用户被禁用
         * 6、登录成功，将员工id存入session并返回登录成功提示
         */
        //1、将页面提交的密码password进行md5加密
        employeeService.test();
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据用户名username查询数据库
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername,username);
        Employee emp = employeeService.getOne(employeeLambdaQueryWrapper);

        //3、查询不到该用户返回失败
        if(emp == null){
            return R.error("用户不存在，登录失败");
        }

        //4、密码比对，密码错误返回登录失败
        if(!emp.getPassword().equals(password)){
            return R.error("密码错误，登陆失败");
        }

        //5、查询员工状态，员工状态为0返回用户被禁用
        if(emp.getStatus() == 0){
            return R.error("用户被禁用");
        }

        //6、登录成功，将员工id存入session并返回登录成功提示
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}
