package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.entity.Student;
import com.southwind.service.StudentService;
import com.southwind.vo.PageVO;
import com.southwind.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public PageVO list(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type
    ){
        Page<Student> pageModel = new Page<>(page,size);
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        Page<Student> resultPage = this.studentService.page(pageModel, queryWrapper);
        return new PageVO(resultPage.getRecords(),resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody Student student){
        return this.studentService.save(student);
    }

    @PostMapping("/register")
    public ResultVO register(@RequestBody Student student){
        if (StringUtils.isBlank(student.getUsername()) || StringUtils.isBlank(student.getPassword())) {
            return new ResultVO(-1, "用户名和密码不能为空", null);
        }
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", student.getUsername());
        Student exists = this.studentService.getOne(queryWrapper);
        if (exists != null) {
            return new ResultVO(-1, "用户名已存在", null);
        }
        if (StringUtils.isBlank(student.getName())) {
            student.setName(student.getUsername());
        }
        if (StringUtils.isBlank(student.getGender())) {
            student.setGender("未知");
        }
        if (StringUtils.isBlank(student.getAge())) {
            student.setAge("18");
        }
        if (StringUtils.isBlank(student.getAddress())) {
            student.setAddress("未填写学校");
        }
        Boolean saved = this.studentService.save(student);
        if (Boolean.TRUE.equals(saved)) {
            return new ResultVO(0, "注册成功", null);
        }
        return new ResultVO(-1, "注册失败，请稍后重试", null);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Student student){
        return this.studentService.updateById(student);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return this.studentService.removeById(id);
    }
}

