package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Notice;
import com.southwind.entity.Tag;
import com.southwind.mapper.TagMapper;
import com.southwind.service.TagService;
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
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;
    @Autowired
    private TagMapper tagMapper;

    @GetMapping("/list")
    public List<Tag> list(){
        return tagService.list();
    }

    @GetMapping("/select")
    public PageVO select(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size
    ){
        Page<Tag> pageModel = new Page<>(page,size);
        Page<Tag> resultPage = this.tagService.page(pageModel,null);
        return new PageVO(resultPage.getRecords(),resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody Tag tag){
        return this.tagService.save(tag);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Tag tag){
        return this.tagService.updateById(tag);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        this.tagMapper.clearTagsByTagId(id);
        return this.tagService.removeById(id);
    }
}

