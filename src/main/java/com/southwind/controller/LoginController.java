package com.southwind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.southwind.entity.Admin;
import com.southwind.entity.Student;
import com.southwind.form.LoginForm;
import com.southwind.service.AdminService;
import com.southwind.service.StudentService;
import com.southwind.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private StudentService studentService;

    @GetMapping("/login")
    public ResultVO login(LoginForm loginForm) {
        if (loginForm.getType().equals("admin")) {
            QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", loginForm.getUsername());
            Admin admin = this.adminService.getOne(queryWrapper);
            if(admin == null){
                return new ResultVO(-1,"用户名错误",null);
            }
            if(!admin.getPassword().equals(loginForm.getPassword())){
                return new ResultVO(-1,"密码错误",null);
            }
            return new ResultVO(0,"",admin);
        } else {
            QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", loginForm.getUsername());
            Student student = this.studentService.getOne(queryWrapper);
            if(student == null){
                return new ResultVO(-1,"用户名错误",null);
            }
            if(!student.getPassword().equals(loginForm.getPassword())){
                return new ResultVO(-1,"密码错误",null);
            }
            return new ResultVO(0,"",student);
        }
    }
}
