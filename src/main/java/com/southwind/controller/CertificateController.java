package com.southwind.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.southwind.entity.Certificate;
import com.southwind.service.CertificateService;
import com.southwind.util.CommonUtils;
import com.southwind.vo.CertificateVO;
import com.southwind.vo.PageVO;
import com.southwind.vo.ResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.core.io.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author admin
 * @since 2025-03-26
 */
@RestController
@RequestMapping("/certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/list")
    public List<CertificateVO> list(){
        List<Certificate> list = this.certificateService.list();
        List<CertificateVO> voList = new ArrayList<CertificateVO>();
        for (Certificate certificate : list) {
            CertificateVO certificateVO = new CertificateVO();
            BeanUtils.copyProperties(certificate, certificateVO);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            certificateVO.setDate(simpleDateFormat.format(certificate.getDate()));
            certificateVO.setPublish(certificate.getPublished());
            voList.add(certificateVO);
        }
        return voList;
    }

    @GetMapping(value = "/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> downloadImage() throws IOException {
        String imagePath = System.getProperty("user.dir")+"/src/main/resources/static/demo.png";
        Path path = Paths.get(imagePath);
        byte[] imageBytes = Files.readAllBytes(path);
        org.springframework.core.io.Resource resource = new ByteArrayResource(imageBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=image.jpg");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(resource);
    }

    @GetMapping("/load")
    public PageVO load(
            @RequestParam("page") Integer page,
            @RequestParam("size") Integer size,
            @RequestParam(value = "keyWord",required = false) String keyWord,
            @RequestParam(value = "type",required = false) String type){
        Page<Certificate> pageModel = new Page<>(page,size);
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(keyWord),type,keyWord);
        Page<Certificate> resultPage = this.certificateService.page(pageModel, queryWrapper);
        List<CertificateVO> list = new ArrayList<>();
        for (Certificate certificate : resultPage.getRecords()) {
            CertificateVO certificateVO = new CertificateVO();
            BeanUtils.copyProperties(certificate, certificateVO);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            certificateVO.setDate(simpleDateFormat.format(certificate.getDate()));
            if(certificate.getPublished()){
                certificateVO.setPublished("已颁发");
            } else {
                certificateVO.setPublished("未颁发");
            }
            list.add(certificateVO);
        }
        return new PageVO(list,resultPage.getSize(),resultPage.getTotal());
    }

    @PostMapping("/add")
    public Boolean add(@RequestBody Certificate certificate){
        certificate.setPublished(false);
        certificate.setDate(new Date());
        certificate.setColor(CommonUtils.getColor(certificate.getLevel()));
        return this.certificateService.save(certificate);
    }

    @PutMapping("/update")
    public Boolean update(@RequestBody Certificate certificate){
        return this.certificateService.updateById(certificate);
    }

    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable("id") Integer id){
        return this.certificateService.removeById(id);
    }
}

