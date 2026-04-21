package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.entity.Student;
import com.southwind.service.StudentService;
import com.southwind.vo.PageVO;
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

    @PutMapping("/update")
    public Boolean update(@RequestBody Student student){
        return this.studentService.updateById(student);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return this.studentService.removeById(id);
    }
}

