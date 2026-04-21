package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.service.NoticeService;
import com.southwind.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-25
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @GetMapping("/list")
    public List<Notice> list(){
        return noticeService.list();
    }

    @GetMapping("/load")
    public List<Notice> load(){
        Page<Notice> pageModel = new Page<>(1,3);
        return noticeService.page(pageModel).getRecords();
    }

    @GetMapping("/select")
    public PageVO select(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type
    ){
        Page<Notice> pageModel = new Page<>(page,size);
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        Page<Notice> resultPage = this.noticeService.page(pageModel, queryWrapper);
        return new PageVO(resultPage.getRecords(),resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody Notice notice){
        return this.noticeService.save(notice);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Notice notice){
        return this.noticeService.updateById(notice);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return this.noticeService.removeById(id);
    }
}

